package org.example.coursclasseservice.service;

import org.example.coursclasseservice.dto.EtudiantDto;
import org.example.coursclasseservice.exception.ResourceNotFoundException;
import org.example.coursclasseservice.mapper.EtudiantMapper;
import org.example.coursclasseservice.model.Classe;
import org.example.coursclasseservice.model.Etudiant;
import org.example.coursclasseservice.model.GroupeEtudiant;
import org.example.coursclasseservice.repository.ClasseRepository;
import org.example.coursclasseservice.repository.EtudiantRepository;
import org.example.coursclasseservice.repository.GroupeEtudiantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.example.coursclasseservice.model.Enumeration.Sexe;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EtudiantService {

    private final EtudiantRepository etudiantRepository;
    private final ClasseRepository classeRepository;
    private final GroupeEtudiantRepository groupeEtudiantRepository;

    @Autowired
    public EtudiantService(EtudiantRepository etudiantRepository, ClasseRepository classeRepository,
            GroupeEtudiantRepository groupeEtudiantRepository) {
        this.etudiantRepository = etudiantRepository;
        this.classeRepository = classeRepository;
        this.groupeEtudiantRepository = groupeEtudiantRepository;
    }

    public List<EtudiantDto> getAllEtudiants() {
        return etudiantRepository.findAll().stream()
                .map(EtudiantMapper::toDto)
                .collect(Collectors.toList());
    }

    public EtudiantDto getEtudiantById(Long id) {
        Etudiant etudiant = etudiantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Etudiant not found with id: " + id));
        return EtudiantMapper.toDto(etudiant);
    }

    @Transactional
    public EtudiantDto createEtudiant(EtudiantDto etudiantDto) {
        if (etudiantRepository.existsByMatricule(etudiantDto.getMatricule())) {
            // Potentially throw BusinessException here or handle gracefully
        }

        Classe classe = null;
        if (etudiantDto.getClasseId() != null) {
            classe = classeRepository.findById(etudiantDto.getClasseId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Classe not found with id: " + etudiantDto.getClasseId()));
        }

        List<GroupeEtudiant> groupes = new ArrayList<>();
        if (etudiantDto.getGroupeIds() != null && !etudiantDto.getGroupeIds().isEmpty()) {
            groupes = groupeEtudiantRepository.findAllById(etudiantDto.getGroupeIds());
        }

        Etudiant etudiant = EtudiantMapper.toEntity(etudiantDto, classe, groupes);
        Etudiant savedEtudiant = etudiantRepository.save(etudiant);
        return EtudiantMapper.toDto(savedEtudiant);
    }

    @Transactional
    public EtudiantDto updateEtudiant(Long id, EtudiantDto etudiantDto) {
        if (!etudiantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Etudiant not found with id: " + id);
        }
        etudiantDto.setId(id);

        Classe classe = null;
        if (etudiantDto.getClasseId() != null) {
            classe = classeRepository.findById(etudiantDto.getClasseId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Classe not found with id: " + etudiantDto.getClasseId()));
        }

        List<GroupeEtudiant> groupes = new ArrayList<>();
        if (etudiantDto.getGroupeIds() != null && !etudiantDto.getGroupeIds().isEmpty()) {
            groupes = groupeEtudiantRepository.findAllById(etudiantDto.getGroupeIds());
        }

        Etudiant etudiant = EtudiantMapper.toEntity(etudiantDto, classe, groupes);
        Etudiant updatedEtudiant = etudiantRepository.save(etudiant);
        return EtudiantMapper.toDto(updatedEtudiant);
    }

    @Transactional
    public void deleteEtudiant(Long id) {
        if (!etudiantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Etudiant not found with id: " + id);
        }
        etudiantRepository.deleteById(id);
    }

    private Etudiant mapRowToEtudiant(Row row) {
        String matricule = row.getCell(0).getStringCellValue();
        String nom = row.getCell(1).getStringCellValue();
        String prenom = row.getCell(2).getStringCellValue();
        String sexeStr = row.getCell(3).getStringCellValue();
        String classeCode = row.getCell(4).getStringCellValue();

        Classe classe = classeRepository.findByCode(classeCode)
                .orElseThrow(() -> new RuntimeException("Classe non trouvée avec le code : " + classeCode));

        Etudiant etudiant = new Etudiant();
        etudiant.setMatricule(matricule);
        etudiant.setNom(nom);
        etudiant.setPrenom(prenom);
        etudiant.setSex(Sexe.valueOf(sexeStr.toUpperCase()));
        etudiant.setClasse(classe);
        return etudiant;
    }

    public ResponseEntity<?> importerEtudiant(MultipartFile file) {
        List<Etudiant> etudiantList = new ArrayList<>();
        List<ErreurImport> erreurs = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                try {
                    Etudiant etudiant = mapRowToEtudiant(row);
                    etudiantList.add(etudiant);
                } catch (Exception e) {
                    erreurs.add(new ErreurImport(row.getRowNum() + 1, e.getMessage()));
                }
            }

            if (!erreurs.isEmpty()) {
                return ResponseEntity.badRequest().body(erreurs);
            }

            etudiantRepository.saveAll(etudiantList);
            return ResponseEntity.ok(etudiantList.size() + " Étudiants importés");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage() + " Erreur lecture fichier");
        }
    }
}
