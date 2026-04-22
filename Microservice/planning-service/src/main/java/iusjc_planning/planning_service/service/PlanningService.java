package iusjc_planning.planning_service.service;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import iusjc_planning.planning_service.dto.ClasseResponse;
import iusjc_planning.planning_service.dto.CoursClientDTO;
import iusjc_planning.planning_service.dto.EnseignantResponse;
import iusjc_planning.planning_service.feign.ClasseClient;
import iusjc_planning.planning_service.feign.CoursClient;
import iusjc_planning.planning_service.feign.UserClient;
import iusjc_planning.planning_service.generation.CoursInfo;
import iusjc_planning.planning_service.generation.EmploiDuTempsGenerator;
import iusjc_planning.planning_service.generation.ResultatGeneration;
import iusjc_planning.planning_service.model.EmploiDuTemps;
import iusjc_planning.planning_service.repository.EmploiDuTempsRepository;
@Service
public class PlanningService {

    private static final Logger log = LoggerFactory.getLogger(PlanningService.class);

    private final EmploiDuTempsGenerator generator;
    private final EmploiDuTempsRepository repository;
    private final ClasseClient classeClient;
    private final CoursClient coursClient;
    private final UserClient userClient;

    public PlanningService(EmploiDuTempsGenerator generator,
                           EmploiDuTempsRepository repository,
                           ClasseClient classeClient,
                           CoursClient coursClient,
                           UserClient userClient) {
        this.generator = generator;
        this.repository = repository;
        this.classeClient = classeClient;
        this.coursClient = coursClient;
        this.userClient = userClient;
    }

    /**
     * Génère l'emploi du temps complet et persiste les résultats dans PostgreSQL.
     */
    @Transactional
    public ResultatGeneration genererTout() {
        log.info("Lancement de la génération globale...");

        // 1. Récupérer les classes
        List<ClasseResponse> classes = classeClient.getAllClasses();
        Map<Long, ClasseResponse> classesMap = classes.stream()
                .collect(Collectors.toMap(ClasseResponse::getId, c -> c));

        // 2. Récupérer les cours à planifier
        List<CoursClientDTO> coursDtos = coursClient.getCoursAPlanifier();
        List<CoursInfo> coursInfos = coursDtos.stream()
                .map(dto -> {
                    // Résoudre l'enseignantId depuis l'email fourni par coursclasse-service
                    Long enseignantId = null;
                    if (dto.getEnseignantEmail() != null && !dto.getEnseignantEmail().isBlank()) {
                        try {
                            EnseignantResponse ens = userClient.getEnseignantByEmail(dto.getEnseignantEmail());
                            if (ens != null) enseignantId = ens.getId();
                            log.debug("Enseignant {}", ens);
                        } catch (Exception e) {
                            log.warn("Impossible de résoudre l'enseignant pour l'email {} : {}", dto.getEnseignantEmail(), e.getMessage());
                        }
                    }
                    return new CoursInfo(
                            dto.getId(),
                            dto.getNom(),
                            dto.getVolumeHoraire(),
                            dto.getNbreheurefait(),
                            enseignantId,
                            dto.getClasseId(),
                            dto.getEffectifClasse(),
                            0
                    );
                })
                .collect(Collectors.toList());

        // 3. Appeler le générateur
        ResultatGeneration resultat = generator.generer(classesMap, coursInfos);

        // 4. Persister les résultats (Effacer les anciens si nécessaire - à confirmer)
        // L'utilisateur a dit "oui on change" pour stockage database, on va tout réécrire.
        repository.deleteAll();

        List<EmploiDuTemps> entites = resultat.getPlacements().stream()
                .map(p -> new EmploiDuTemps(
                        p.getCoursId(),
                        p.getNomCours(),
                        p.getEnseignantId(),
                        p.getClasseId(),
                        p.getSalleId(),
                        p.getNomSalle(),
                        p.getJour(),
                        LocalTime.parse(p.getHeureDebut()),
                        LocalTime.parse(p.getHeureFin())
                ))
                .collect(Collectors.toList());

        repository.saveAll(entites);

        // 5. Mettre à jour les heures effectuées via Feign
        resultat.getPlacements().forEach(p -> {
            coursClient.mettreAJourHeuresEffectuees(p.getCoursId(), 2);
        });

        log.info("Génération terminée avec {} placements enregistrés.", entites.size());
        return resultat;
    }

    /**
     * Récupère tous les emplois du temps persistés en base.
     * Optionnellement filtrés par classeId.
     */
    public List<EmploiDuTemps> getEmploiDuTemps(Long classeId) {
        if (classeId != null) {
            return repository.findByClasseId(classeId);
        }
        return repository.findAll();
    }
}
