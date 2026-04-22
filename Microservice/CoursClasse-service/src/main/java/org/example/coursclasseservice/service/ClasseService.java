package org.example.coursclasseservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.example.coursclasseservice.dto.ClasseDto;
import org.example.coursclasseservice.dto.SalleAttributCoursdto;
import org.example.coursclasseservice.exception.ResourceNotFoundException;
import org.example.coursclasseservice.feign.SalleFeignClient;
import org.example.coursclasseservice.mapper.ClasseMapper;
import org.example.coursclasseservice.model.Classe;
import org.example.coursclasseservice.model.Etudiant;
import org.example.coursclasseservice.model.Filiere;
import org.example.coursclasseservice.repository.ClasseRepository;
import org.example.coursclasseservice.repository.EtudiantRepository;
import org.example.coursclasseservice.repository.FiliereRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ClasseService {

    private static final Logger log = LoggerFactory.getLogger(ClasseService.class);

    private final ClasseRepository classeRepository;
    private final EtudiantRepository etudiantRepository;
    private final FiliereRepository filiereRepository;
    private final SalleFeignClient salleFeignClient;

    @Autowired
    public ClasseService(ClasseRepository classeRepository,
            EtudiantRepository etudiantRepository, FiliereRepository filiereRepository,
            SalleFeignClient salleFeignClient) {
        this.classeRepository = classeRepository;
        this.etudiantRepository = etudiantRepository;
        this.filiereRepository = filiereRepository;
        this.salleFeignClient = salleFeignClient;
    }

    public List<ClasseDto> getAllClasses() {
        return classeRepository.findAll().stream()
                .map(this::enrichClasseDto)
                .collect(Collectors.toList());
    }

    public ClasseDto getClasseById(Long id) {
        Classe classe = classeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Classe not found with id: " + id));
        return enrichClasseDto(classe);
    }

    private ClasseDto enrichClasseDto(Classe entity) {
        ClasseDto dto = ClasseMapper.toDto(entity);
        if (entity.getSalleId() != null && entity.getSalleId() != 0) {
            try {
                SalleAttributCoursdto salleAttribut = salleFeignClient.getSalleById(entity.getSalleId());
                if (salleAttribut != null) {
                    dto.setCodeSalle(salleAttribut.getCodeSalle() != null ? salleAttribut.getCodeSalle() : "Salle " + entity.getSalleId());
                }
            } catch (Exception e) {
                log.warn("Erreur lors de la récupération des infos salle pour la classe {} : {}", entity.getId(), e.getMessage());
            }
        }
        return dto;
    }

    @Transactional
    public ClasseDto createClasse(ClasseDto classeDto) {
        List<Etudiant> etudiants = new ArrayList<>();
        if (classeDto.getEtudiantIds() != null && !classeDto.getEtudiantIds().isEmpty()) {
            etudiants = etudiantRepository.findAllById(classeDto.getEtudiantIds());
        }

        Filiere filiere = null;
        if (classeDto.getFiliereId() != null) {
            filiere = filiereRepository.findById(classeDto.getFiliereId())
                    .orElseThrow(() -> new ResourceNotFoundException("Filiere not found with id: " + classeDto.getFiliereId()));
        }

        Classe classe = ClasseMapper.toEntity(classeDto, etudiants, filiere);
        Classe savedClasse = classeRepository.save(classe);
        return enrichClasseDto(savedClasse);
    }

    @Transactional
    public ClasseDto updateClasse(Long id, ClasseDto classeDto) {
        Classe existingClasse = classeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Classe not found with id: " + id));

        existingClasse.setNom(classeDto.getNom());
        existingClasse.setCode(classeDto.getCode());
        existingClasse.setEffectif(classeDto.getEffectif());
        existingClasse.setSalleId(classeDto.getSalleId());

        if (classeDto.getFiliereId() != null) {
            Filiere filiere = filiereRepository.findById(classeDto.getFiliereId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Filiere not found with id: " + classeDto.getFiliereId()));
            existingClasse.setFiliere(filiere);
        }

        if (classeDto.getEtudiantIds() != null) {
            List<Etudiant> etudiants = etudiantRepository.findAllById(classeDto.getEtudiantIds());
            existingClasse.setEtudiants(etudiants);
        }

        Classe updatedClasse = classeRepository.save(existingClasse);
        return enrichClasseDto(updatedClasse);
    }

    @Transactional
    public void deleteClasse(Long id) {
        if (!classeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Classe not found with id: " + id);
        }
        classeRepository.deleteById(id);
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) return true;
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            org.apache.poi.ss.usermodel.Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != org.apache.poi.ss.usermodel.CellType.BLANK) {
                String val = cell.toString().trim();
                if (!val.isEmpty()) return false;
            }
        }
        return true;
    }

    private Classe mapRowToClasse(Row row) {
        String nom = row.getCell(0).getStringCellValue();
        String code = row.getCell(1).getStringCellValue();
        int effectif = (int) row.getCell(2).getNumericCellValue();
        // Index 3 was EcoleCode in Excel, we ignore it now
        String filiereCode = row.getCell(3).getStringCellValue();

        Filiere filiere = filiereRepository.findByCode(filiereCode)
                .orElseThrow(() -> new RuntimeException("Filière non trouvée avec le code : " + filiereCode));

        Classe classe = new Classe();
        classe.setNom(nom);
        classe.setCode(code);
        classe.setEffectif(effectif);
        classe.setFiliere(filiere);
        return classe;
    }

    public ResponseEntity<?> importerClasse(MultipartFile file) {
        List<Classe> classeList = new ArrayList<>();
        List<ErreurImport> erreurs = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                if (isRowEmpty(row)) continue;
                try {
                    Classe classe = this.mapRowToClasse(row);
                    classeList.add(classe);
                } catch (Exception e) {
                    erreurs.add(new ErreurImport(row.getRowNum() + 1, e.getMessage()));
                }
            }

            if (!erreurs.isEmpty()) {
                return ResponseEntity.badRequest().body(erreurs);
            }

            classeRepository.saveAll(classeList);
            return ResponseEntity.ok(classeList.size() + " Classes importées");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage() + " Erreur lecture fichier");
        }
    }
}
