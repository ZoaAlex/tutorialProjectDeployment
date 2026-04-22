package org.example.coursclasseservice.service;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.example.coursclasseservice.dto.UeDto;
import org.example.coursclasseservice.exception.ResourceNotFoundException;
import org.example.coursclasseservice.mapper.UeMapper;
import org.example.coursclasseservice.model.Classe;
import org.example.coursclasseservice.model.Cours;
import org.example.coursclasseservice.model.Ue;
import org.example.coursclasseservice.repository.ClasseRepository;
import org.example.coursclasseservice.repository.CoursRepository;
import org.example.coursclasseservice.repository.UeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UeService {

    private static final Logger log = LoggerFactory.getLogger(UeService.class);

    private final UeRepository ueRepository;
    private final CoursRepository coursRepository;
    private final ClasseRepository classeRepository;

    @Autowired
    public UeService(UeRepository ueRepository, CoursRepository coursRepository,
                     ClasseRepository classeRepository) {
        this.ueRepository = ueRepository;
        this.coursRepository = coursRepository;
        this.classeRepository = classeRepository;
    }

    public List<UeDto> getAllUes() {
        return ueRepository.findAll().stream()
                .map(UeMapper::toDto)
                .collect(Collectors.toList());
    }

    public UeDto getUeById(Long id) {
        Ue ue = ueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UE non trouvee avec id: " + id));
        return UeMapper.toDto(ue);
    }

    @Transactional
    public UeDto createUe(UeDto ueDto) {
        return saveOrUpdateUe(ueDto);
    }

    @Transactional
    public UeDto updateUe(Long id, UeDto ueDto) {
        if (!ueRepository.existsById(id)) {
            throw new ResourceNotFoundException("UE non trouvee avec id: " + id);
        }
        ueDto.setId(id);
        return saveOrUpdateUe(ueDto);
    }

    private UeDto saveOrUpdateUe(UeDto ueDto) {
        if (ueDto.getClasseId() == null) {
            throw new IllegalArgumentException("L ID de la classe est obligatoire pour creer une UE");
        }
        Classe classe = classeRepository.findById(ueDto.getClasseId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Classe non trouvee avec id: " + ueDto.getClasseId()));

        List<Cours> cours = new ArrayList<>();
        if (ueDto.getCoursIds() != null && !ueDto.getCoursIds().isEmpty()) {
            cours = coursRepository.findAllById(ueDto.getCoursIds());
        }

        Ue ue = UeMapper.toEntity(ueDto, cours, classe);
        Ue savedUe = ueRepository.save(ue);
        log.info("UE sauvegardee : id={}, code={}", savedUe.getId(), savedUe.getCodeUe());
        return UeMapper.toDto(savedUe);
    }

    @Transactional
    public void deleteUe(Long id) {
        if (!ueRepository.existsById(id)) {
            throw new ResourceNotFoundException("UE non trouvee avec id: " + id);
        }
        ueRepository.deleteById(id);
    }

    private Ue mapRowToUe(Row row) {
        String codeUe = row.getCell(0).getStringCellValue();
        String intitule = row.getCell(1).getStringCellValue();
        String classeCode = row.getCell(2).getStringCellValue();

        Classe classe = classeRepository.findByCode(classeCode)
                .orElseThrow(() -> new RuntimeException("Classe non trouvée avec le code : " + classeCode));

        Ue ue = new Ue();
        ue.setCodeUe(codeUe);
        ue.setIntitule(intitule);
        ue.setClasse(classe);
        return ue;
    }

    public ResponseEntity<?> importerUe(MultipartFile file) {
        List<Ue> ueList = new ArrayList<>();
        List<ErreurImport> erreurs = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                try {
                    Ue ue = mapRowToUe(row);
                    ueList.add(ue);
                } catch (Exception e) {
                    erreurs.add(new ErreurImport(row.getRowNum() + 1, e.getMessage()));
                }
            }

            if (!erreurs.isEmpty()) {
                return ResponseEntity.badRequest().body(erreurs);
            }

            ueRepository.saveAll(ueList);
            return ResponseEntity.ok(ueList.size() + " UE importées");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage() + " Erreur lecture fichier");
        }
    }
}