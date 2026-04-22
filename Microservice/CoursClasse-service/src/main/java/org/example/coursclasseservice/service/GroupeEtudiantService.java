package org.example.coursclasseservice.service;

import org.example.coursclasseservice.dto.GroupeEtudiantDto;
import org.example.coursclasseservice.exception.ResourceNotFoundException;
import org.example.coursclasseservice.mapper.GroupeEtudiantMapper;
import org.example.coursclasseservice.model.Etudiant;
import org.example.coursclasseservice.model.GroupeEtudiant;
import org.example.coursclasseservice.repository.EtudiantRepository;
import org.example.coursclasseservice.repository.GroupeEtudiantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.example.coursclasseservice.model.Classe;
import org.example.coursclasseservice.repository.ClasseRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupeEtudiantService {

    private final GroupeEtudiantRepository groupeEtudiantRepository;
    private final EtudiantRepository etudiantRepository;
    private final ClasseRepository classeRepository;

    @Autowired
    public GroupeEtudiantService(GroupeEtudiantRepository groupeEtudiantRepository,
            EtudiantRepository etudiantRepository, ClasseRepository classeRepository) {
        this.groupeEtudiantRepository = groupeEtudiantRepository;
        this.etudiantRepository = etudiantRepository;
        this.classeRepository = classeRepository;
    }

    public List<GroupeEtudiantDto> getAllGroupes() {
        return groupeEtudiantRepository.findAll().stream()
                .map(GroupeEtudiantMapper::toDto)
                .collect(Collectors.toList());
    }

    public GroupeEtudiantDto getGroupeById(Long id) {
        GroupeEtudiant groupe = groupeEtudiantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GroupeEtudiant not found with id: " + id));
        return GroupeEtudiantMapper.toDto(groupe);
    }

    @Transactional
    public GroupeEtudiantDto createGroupe(GroupeEtudiantDto groupeDto) {
        List<Etudiant> etudiants = new ArrayList<>();
        if (groupeDto.getEtudiantIds() != null && !groupeDto.getEtudiantIds().isEmpty()) {
            etudiants = etudiantRepository.findAllById(groupeDto.getEtudiantIds());
        }

        List<Classe> classes = new ArrayList<>();
        if (groupeDto.getClasseIds() != null && !groupeDto.getClasseIds().isEmpty()) {
            classes = classeRepository.findAllById(groupeDto.getClasseIds());
        }

        GroupeEtudiant groupe = GroupeEtudiantMapper.toEntity(groupeDto, etudiants, classes);
        GroupeEtudiant savedGroupe = groupeEtudiantRepository.save(groupe);
        return GroupeEtudiantMapper.toDto(savedGroupe);
    }

    @Transactional
    public GroupeEtudiantDto updateGroupe(Long id, GroupeEtudiantDto groupeDto) {
        if (!groupeEtudiantRepository.existsById(id)) {
            throw new ResourceNotFoundException("GroupeEtudiant not found with id: " + id);
        }
        groupeDto.setId(id);

        List<Etudiant> etudiants = new ArrayList<>();
        if (groupeDto.getEtudiantIds() != null && !groupeDto.getEtudiantIds().isEmpty()) {
            etudiants = etudiantRepository.findAllById(groupeDto.getEtudiantIds());
        }

        List<Classe> classes = new ArrayList<>();
        if (groupeDto.getClasseIds() != null && !groupeDto.getClasseIds().isEmpty()) {
            classes = classeRepository.findAllById(groupeDto.getClasseIds());
        }

        GroupeEtudiant groupe = GroupeEtudiantMapper.toEntity(groupeDto, etudiants, classes);
        GroupeEtudiant updatedGroupe = groupeEtudiantRepository.save(groupe);
        return GroupeEtudiantMapper.toDto(updatedGroupe);
    }

    @Transactional
    public void deleteGroupe(Long id) {
        if (!groupeEtudiantRepository.existsById(id)) {
            throw new ResourceNotFoundException("GroupeEtudiant not found with id: " + id);
        }
        groupeEtudiantRepository.deleteById(id);
    }

    private GroupeEtudiant mapRowToGroupe(Row row) {
        String nom = row.getCell(0).getStringCellValue();
        String description = row.getCell(1).getStringCellValue();
        int effectif = (int) row.getCell(2).getNumericCellValue();
        String classeCodesStr = row.getCell(3).getStringCellValue();

        List<Classe> classes = new ArrayList<>();
        if (classeCodesStr != null && !classeCodesStr.isEmpty()) {
            String[] codes = classeCodesStr.split(",");
            for (String code : codes) {
                Classe classe = classeRepository.findByCode(code.trim())
                        .orElseThrow(() -> new RuntimeException("Classe non trouvée avec le code : " + code.trim()));
                classes.add(classe);
            }
        }

        GroupeEtudiant groupe = new GroupeEtudiant();
        groupe.setNom(nom);
        groupe.setDescription(description);
        groupe.setEffectif(effectif);
        groupe.setClasses(classes);
        return groupe;
    }

    public ResponseEntity<?> importerGroupe(MultipartFile file) {
        List<GroupeEtudiant> groupeList = new ArrayList<>();
        List<ErreurImport> erreurs = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                try {
                    GroupeEtudiant groupe = mapRowToGroupe(row);
                    groupeList.add(groupe);
                } catch (Exception e) {
                    erreurs.add(new ErreurImport(row.getRowNum() + 1, e.getMessage()));
                }
            }

            if (!erreurs.isEmpty()) {
                return ResponseEntity.badRequest().body(erreurs);
            }

            groupeEtudiantRepository.saveAll(groupeList);
            return ResponseEntity.ok(groupeList.size() + " Groupes importés");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage() + " Erreur lecture fichier");
        }
    }
}
