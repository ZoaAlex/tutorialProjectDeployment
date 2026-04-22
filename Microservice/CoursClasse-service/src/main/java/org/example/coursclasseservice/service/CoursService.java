package org.example.coursclasseservice.service;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.example.coursclasseservice.dto.CoursDto;
import org.example.coursclasseservice.exception.ResourceNotFoundException;
import org.example.coursclasseservice.feign.EnseignantfeignClient;
import org.example.coursclasseservice.mapper.CoursMapper;
import org.example.coursclasseservice.model.Cours;
import org.example.coursclasseservice.model.Enumeration.StatutCours;
import org.example.coursclasseservice.model.Ue;
import org.example.coursclasseservice.repository.ClasseRepository;
import org.example.coursclasseservice.repository.CoursRepository;
import org.example.coursclasseservice.repository.UeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoursService {

    private static final Logger log = LoggerFactory.getLogger(CoursService.class);

    private final EnseignantfeignClient enseignantfeignClient;
    private final CoursRepository coursRepository;
    private final UeRepository ueRepository;

    private static class ValidationResult {
        final Ue ue;

        ValidationResult(Ue ue) {
            this.ue = ue;
        }
    }

    private ValidationResult validate(CoursDto coursDto) {
        Ue ue = null;
        if (coursDto.ueId() != null && coursDto.ueId() != 0) {
            ue = ueRepository.findById(coursDto.ueId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ue not found with id: " + coursDto.ueId()));
        }
        // Validation enseignant : ignoree si user-service indisponible
        if (coursDto.enseignantEmail() != null) {
            try {
                if (!enseignantfeignClient.existEmail(coursDto.enseignantEmail())) {
                    throw new ResourceNotFoundException("Enseignant not found with Email: " + coursDto.enseignantEmail());
                }
            } catch (ResourceNotFoundException e) {
                throw e;
            } catch (Exception e) {
                log.warn("user-service indisponible, validation enseignant ignoree : {}", e.getMessage());
            }
        }
        if (ue == null) {
            throw new IllegalArgumentException("UE is required for a Cours");
        }
        return new ValidationResult(ue);
    }

    @Autowired
    public CoursService(CoursRepository coursRepository, ClasseRepository classeRepository,
            UeRepository ueRepository, EnseignantfeignClient enseignantfeignClient) {
        this.coursRepository = coursRepository;
        this.ueRepository = ueRepository;
        this.enseignantfeignClient = enseignantfeignClient;

    }

    public List<CoursDto> getAllCours() {
        return coursRepository.findAllWithDetails().stream()
                .map(this::enrichCoursDto)
                .collect(Collectors.toList());
    }

    public CoursDto getCoursById(Long id) {
        Cours cours = coursRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cours not found with id: " + id));
        return enrichCoursDto(cours);
    }

    private CoursDto enrichCoursDto(Cours entity) {

        return CoursMapper.toDto(entity);
    }

    @Transactional
    public CoursDto createCours(CoursDto coursDto) {
        ValidationResult result = validate(coursDto);
        Cours cours = CoursMapper.toEntity(coursDto, result.ue);
        Cours saved = coursRepository.save(cours);
        return enrichCoursDto(coursRepository.findByIdWithDetails(saved.getId()).orElse(saved));
    }

    @Transactional
    public CoursDto updateCours(CoursDto coursDto) {
        if (!coursRepository.existsById(coursDto.id())) {
            throw new ResourceNotFoundException("Cours not found with id: " + coursDto.id());
        }
        ValidationResult result = validate(coursDto);
        Cours cours = CoursMapper.toEntity(coursDto, result.ue);
        Cours saved = coursRepository.save(cours);
        return enrichCoursDto(coursRepository.findByIdWithDetails(saved.getId()).orElse(saved));
    }

    @Transactional
    public void deleteCours(Long id) {
        if (!coursRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cours not found with id: " + id);
        }
        coursRepository.deleteById(id);
    }

    public List<CoursDto> getCoursAPlanifier() {
        return coursRepository.findCoursAPlanifier().stream()
                .map(this::enrichCoursDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CoursDto mettreAJourHeuresEffectuees(Long id, int heures) {
        Cours cours = coursRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cours not found with id: " + id));
        int nouvellesHeures = cours.getNbreheurefait() + heures;
        if (nouvellesHeures > cours.getVolumeHoraire()) {
            log.warn("Cours {} : depassement volume horaire", id);
            nouvellesHeures = cours.getVolumeHoraire();
        }
        cours.setNbreheurefait(nouvellesHeures);
        return enrichCoursDto(coursRepository.save(cours));
    }



    private Cours mapRowToCours(Row row){
        String nom = row.getCell(0).getStringCellValue();
        String ueCode = row.getCell(1).getStringCellValue();
        int volumeHoraire = (int) row.getCell(2).getNumericCellValue();
        int nbreheurefait = (int) row.getCell(3).getNumericCellValue();
        String enseignantEmail= row.getCell(4).getStringCellValue();

        Ue ue = ueRepository.findByCodeUe(ueCode)
                .orElseThrow(() -> new RuntimeException("UE non trouvée avec le code : " + ueCode));
        if (!enseignantfeignClient.existEmail(enseignantEmail)) {
            throw new ResourceNotFoundException("Enseignant not found with Email: " + enseignantEmail);
        }
        Cours cours = new Cours();
        cours.setNom(nom);
        cours.setUe(ue);
        cours.setVolumeHoraire(volumeHoraire);
        cours.setNbreheurefait(nbreheurefait);
        cours.setEnseignantemail(enseignantEmail);
        cours.setStatutCours(StatutCours.en_attente);

        return cours;
    }


    public ResponseEntity<?> importerCours(MultipartFile file) {
        List<Cours> coursList = new ArrayList<>();
        List<ErreurImport> erreurs = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                try {
                    Cours cours = mapRowToCours(row);
                    coursList.add(cours);
                } catch (Exception e) {
                    erreurs.add(new ErreurImport(row.getRowNum() + 1, e.getMessage()));
                }
            }

            if (!erreurs.isEmpty()) {
                return ResponseEntity.badRequest().body(erreurs);
            }

            coursRepository.saveAll(coursList);
            return ResponseEntity.ok(coursList.size() + " Cours importés");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage() + "Erreur lecture fichier");
        }
    }




}
