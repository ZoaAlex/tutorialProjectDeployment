package org.example.coursclasseservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.example.coursclasseservice.dto.FiliereDto;
import org.example.coursclasseservice.exception.ResourceNotFoundException;
import org.example.coursclasseservice.mapper.FiliereMapper;
import org.example.coursclasseservice.model.Classe;
import org.example.coursclasseservice.model.Ecole;
import org.example.coursclasseservice.model.Filiere;
import org.example.coursclasseservice.repository.ClasseRepository;
import org.example.coursclasseservice.repository.EcoleRepository;
import org.example.coursclasseservice.repository.FiliereRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FiliereService {

    private final FiliereRepository filiereRepository;
    private final EcoleRepository ecoleRepository;
    private final ClasseRepository classeRepository;

    @Autowired
    public FiliereService(FiliereRepository filiereRepository, EcoleRepository ecoleRepository,
            ClasseRepository classeRepository) {
        this.filiereRepository = filiereRepository;
        this.ecoleRepository = ecoleRepository;
        this.classeRepository = classeRepository;
    }

    public List<FiliereDto> getAllFilieres() {
        return filiereRepository.findAll().stream()
                .map(FiliereMapper::toDto)
                .collect(Collectors.toList());
    }

    public FiliereDto getFiliereById(Long id) {
        Filiere filiere = filiereRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Filiere not found with id: " + id));
        return FiliereMapper.toDto(filiere);
    }

    @Transactional
    public FiliereDto createFiliere(FiliereDto filiereDto) {
        Ecole ecole = null;
        if (filiereDto.getEcoleId() != null) {
            ecole = ecoleRepository.findById(filiereDto.getEcoleId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Ecole not found with id: " + filiereDto.getEcoleId()));
        }
        List<Classe> classes = new ArrayList<>();
        if (filiereDto.getClasseIds() != null && !filiereDto.getClasseIds().isEmpty()) {
            classes = classeRepository.findAllById(filiereDto.getClasseIds());
        }
        Filiere filiere = FiliereMapper.toEntity(filiereDto, ecole, classes);
        return FiliereMapper.toDto(filiereRepository.save(filiere));
    }

    @Transactional
    public FiliereDto updateFiliere(Long id, FiliereDto filiereDto) {
        if (!filiereRepository.existsById(id)) {
            throw new ResourceNotFoundException("Filiere not found with id: " + id);
        }
        filiereDto.setId(id);
        Ecole ecole = null;
        if (filiereDto.getEcoleId() != null) {
            ecole = ecoleRepository.findById(filiereDto.getEcoleId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Ecole not found with id: " + filiereDto.getEcoleId()));
        }
        List<Classe> classes = new ArrayList<>();
        if (filiereDto.getClasseIds() != null && !filiereDto.getClasseIds().isEmpty()) {
            classes = classeRepository.findAllById(filiereDto.getClasseIds());
        }
        Filiere filiere = FiliereMapper.toEntity(filiereDto, ecole, classes);
        return FiliereMapper.toDto(filiereRepository.save(filiere));
    }

    @Transactional
    public void deleteFiliere(Long id) {
        if (!filiereRepository.existsById(id)) {
            throw new ResourceNotFoundException("Filiere not found with id: " + id);
        }
        filiereRepository.deleteById(id);
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

    private Filiere mapRowToFiliere(Row row) {
        String code = row.getCell(0).getStringCellValue();
        String nom = row.getCell(1).getStringCellValue();
        String ecoleCode = row.getCell(2).getStringCellValue();

        Ecole ecole = ecoleRepository.findByCode(ecoleCode)
                .orElseThrow(() -> new RuntimeException("Ecole non trouvée avec le code : " + ecoleCode));

        Filiere filiere = new Filiere();
        filiere.setCode(code);
        filiere.setNom(nom);
        filiere.setEcole(ecole);
        return filiere;
    }

    public ResponseEntity<?> importerFiliere(MultipartFile file) {
        List<Filiere> filiereList = new ArrayList<>();
        List<ErreurImport> erreurs = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                if (isRowEmpty(row)) continue;

                try {
                    Filiere filiere = mapRowToFiliere(row);
                    filiereList.add(filiere);
                } catch (Exception e) {
                    erreurs.add(new ErreurImport(row.getRowNum() + 1, e.getMessage()));
                }
            }

            if (!erreurs.isEmpty()) {
                return ResponseEntity.badRequest().body(erreurs);
            }

            filiereRepository.saveAll(filiereList);
            return ResponseEntity.ok(filiereList.size() + " Filières importées");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage() + " Erreur lecture fichier");
        }
    }
}
