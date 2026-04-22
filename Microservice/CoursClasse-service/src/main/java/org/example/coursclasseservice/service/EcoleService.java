package org.example.coursclasseservice.service;

import jakarta.persistence.*;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.example.coursclasseservice.dto.EcoleDto;
import org.example.coursclasseservice.exception.ResourceNotFoundException;
import org.example.coursclasseservice.mapper.EcoleMapper;
import org.example.coursclasseservice.model.*;
import org.example.coursclasseservice.model.Enumeration.StatutCours;
import org.example.coursclasseservice.repository.ClasseRepository;
import org.example.coursclasseservice.repository.EcoleRepository;
import org.example.coursclasseservice.repository.FiliereRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EcoleService {

    private final EcoleRepository ecoleRepository;
    private final ClasseRepository classeRepository;
    private final FiliereRepository filiereRepository;

    @Autowired
    public EcoleService(EcoleRepository ecoleRepository, ClasseRepository classeRepository,
            FiliereRepository filiereRepository) {
        this.ecoleRepository = ecoleRepository;
        this.classeRepository = classeRepository;
        this.filiereRepository = filiereRepository;
    }

    public List<EcoleDto> getAllEcoles() {
        return ecoleRepository.findAll().stream()
                .map(EcoleMapper::toDto)
                .collect(Collectors.toList());
    }

    public EcoleDto getEcoleById(Long id) {
        Ecole ecole = ecoleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ecole not found with id: " + id));
        return EcoleMapper.toDto(ecole);
    }

    @Transactional
    public EcoleDto createEcole(EcoleDto ecoleDto) {
        List<Classe> classes = new ArrayList<>();
        if (ecoleDto.getClasseIds() != null && !ecoleDto.getClasseIds().isEmpty()) {
            classes = classeRepository.findAllById(ecoleDto.getClasseIds());
        }

        List<Filiere> filieres = new ArrayList<>();
        if (ecoleDto.getFiliereIds() != null && !ecoleDto.getFiliereIds().isEmpty()) {
            filieres = filiereRepository.findAllById(ecoleDto.getFiliereIds());
        }

        Ecole ecole = EcoleMapper.toEntity(ecoleDto, classes, filieres);
        Ecole savedEcole = ecoleRepository.save(ecole);
        return EcoleMapper.toDto(savedEcole);
    }

    @Transactional
    public EcoleDto updateEcole(Long id, EcoleDto ecoleDto) {
        if (!ecoleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ecole not found with id: " + id);
        }
        ecoleDto.setId(id);

        List<Classe> classes = new ArrayList<>();
        if (ecoleDto.getClasseIds() != null && !ecoleDto.getClasseIds().isEmpty()) {
            classes = classeRepository.findAllById(ecoleDto.getClasseIds());
        }

        List<Filiere> filieres = new ArrayList<>();
        if (ecoleDto.getFiliereIds() != null && !ecoleDto.getFiliereIds().isEmpty()) {
            filieres = filiereRepository.findAllById(ecoleDto.getFiliereIds());
        }

        Ecole ecole = EcoleMapper.toEntity(ecoleDto, classes, filieres);
        Ecole updatedEcole = ecoleRepository.save(ecole);
        return EcoleMapper.toDto(updatedEcole);
    }

    @Transactional
    public void deleteEcole(Long id) {
        if (!ecoleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ecole not found with id: " + id);
        }
        ecoleRepository.deleteById(id);
    }
    private  Ecole mapRowToEcole(Row row) {

        String code = row.getCell(0).getStringCellValue();

        String nom = row.getCell(1).getStringCellValue();

        String description = row.getCell(2).getStringCellValue();
        Ecole ecole = new Ecole();
        ecole.setCode(code);
        ecole.setNom(nom);
        ecole.setDescription(description);

        return ecole;

    }
    public ResponseEntity<?> importerEcole(MultipartFile file){
        List<Ecole> ecoleList = new ArrayList<>();
        List<ErreurImport> erreurs = new ArrayList<>();

        try(Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                try{
                    Ecole ecole = mapRowToEcole(row);
                    ecoleList.add(ecole);
                } catch (Exception e) {
                    erreurs.add(new ErreurImport(row.getRowNum() + 1, e.getMessage()));
                }
            }

            if (!erreurs.isEmpty()) {
                return ResponseEntity.badRequest().body(erreurs);
            }

            ecoleRepository.saveAll(ecoleList);
            return ResponseEntity.ok(ecoleList.size()+" Cours importés");

        }catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage() + "Erreur lecture fichier");
        }
    }

}
