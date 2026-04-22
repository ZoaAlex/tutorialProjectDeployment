package iusjc_planning.planning_service.generation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import iusjc_planning.planning_service.dto.ClasseResponse;
import iusjc_planning.planning_service.dto.DisponibiliteEnseignantDTO;
import iusjc_planning.planning_service.feign.DisponibiliteClient;

/**
 * Générateur d'emploi du temps class-centrique.
 * 
 * Logique :
 * 1. Regrouper les cours par classe.
 * 2. Pour chaque classe :
 *    a. Récupérer la salle de la classe.
 *    b. Calculer le score de priorité de chaque cours et les mettre dans une pile (Stack).
 *    c. Tenter de placer les cours de la pile sur les créneaux disponibles.
 *    d. Lors d'un placement, marquer le créneau de l'enseignant comme utilisé.
 */
@Service
public class EmploiDuTempsGenerator {

    private static final Logger log = LoggerFactory.getLogger(EmploiDuTempsGenerator.class);
    private static final int DUREE_CRENEAU_HEURES = 2;

    private final DisponibiliteClient disponibiliteClient;
    private final ScoringService scoringService;

    public EmploiDuTempsGenerator(DisponibiliteClient disponibiliteClient,
                                   ScoringService scoringService) {
        this.disponibiliteClient = disponibiliteClient;
        this.scoringService = scoringService;
    }

    public ResultatGeneration generer(Map<Long, ClasseResponse> classesMap,
                                      List<CoursInfo> tousLesCours) {

        log.info("=== Début de la génération de l'emploi du temps (Approche par Classe) ===");

        GrilleHebdomadaire grille = new GrilleHebdomadaire();
        List<ResultatGeneration.PlacementEffectue> placements = new ArrayList<>();

        List<CoursInfo> coursNonPlaces = new ArrayList<>();
        // Charger toutes les disponibilités actives depuis le service externe
        List<Long> enseignantIds = tousLesCours.stream()
                .map(CoursInfo::getEnseignantId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        
        Map<Long, Set<CreneauHoraire>> disposActivesParProf = chargerEtExtraireDispos(enseignantIds);

        // Mettre à jour le volume de disponibilité initial dans CoursInfo
        tousLesCours.forEach(c -> {
            Set<CreneauHoraire> dispos = disposActivesParProf.getOrDefault(c.getEnseignantId(), Collections.emptySet());
            c.setVolumeDisponibilite(dispos.size() * DUREE_CRENEAU_HEURES);
        });

        // Grouper les cours par classe
        Map<Long, List<CoursInfo>> coursParClasse = tousLesCours.stream()
                .collect(Collectors.groupingBy(CoursInfo::getClasseId));

        // Itérer sur chaque classe pour générer son planning
        for (Long classeId : coursParClasse.keySet()) {
            ClasseResponse classeInfo = classesMap.get(classeId);
            log.debug("Classe Info: {} {} {} {}", classeInfo.getNom(),classeInfo.getCode(), classeInfo.getCodeSalle(), classeInfo.getSalleId());
            if (classeInfo == null) {
                log.warn("Classe {} non trouvée dans les données référentielles, sautée.", classeId);
                continue;
            }

            List<CoursInfo> coursDeLaClasse = coursParClasse.get(classeId);
            for (CoursInfo coursInfo : coursDeLaClasse) {
                log.debug("cours info: {}", coursInfo);
            }
            Long salleId = classeInfo.getSalleId();
            String nomSalle = classeInfo.getCodeSalle();

            log.info("Traitement de la classe : {} (Salle : {})", classeInfo.getNom(), nomSalle);

            // Créer une pile de cours triée par score (du plus petit au plus grand pour que le pop soit le plus prioritaire)
            // Note: ScoringService.calculerScore renvoie un score plus élevé pour les cours plus prioritaires.
            // Donc on trie par score ascendant pour que stack.pop() donne le score max.
            Stack<CoursInfo> pileDeCours = new Stack<>();
            
            // Calculer les scores initiaux pour la pile
            List<CoursInfo> coursTries = new ArrayList<>(coursDeLaClasse);
            coursTries.sort(Comparator.comparingDouble(c -> {
                // Créer un candidat fictif pour le calcul du score (sans créneau ni salle spécifique pour l'instant)
                // Ou simplement utiliser une version simplifiée du score.
                // Ici on va utiliser le score volume (dispo) comme priorité principale.
                return scoringService.calculerScore(new CreneauCandidat(c, null, null), Collections.emptyList(), grille);
            }));

            for (CoursInfo c : coursTries) {
                pileDeCours.push(c);
                log.debug("Cours info: {}", c.getNom());
            }

            // Tenter de placer les cours de la pile
            while (!pileDeCours.isEmpty()) {
                CoursInfo coursActuel = pileDeCours.pop();

                boolean place = false;
                // Trouver un créneau pour ce cours
                List<CreneauHoraire> creneauxPossibles = grille.getTousLesCreneaux();
                
                for (CreneauHoraire creneau : creneauxPossibles) {
                    if (testerEtPlacer(coursActuel, creneau, salleId, nomSalle, disposActivesParProf, grille, placements)) {
                        place = true;
                        // Si le cours n'est pas fini, on pourrait le remettre dans la pile ou continuer.
                        // On continue pour essayer de placer une autre séance si possible, 
                        // mais on doit revoir la pile pour respecter les priorités globales.
                        break; // On a placé une séance, on repasse par la pile pour le prochain choix
                    }
                }

                if (!place) {
                    coursNonPlaces.add(coursActuel);
                    log.debug("Impossible de placer une séance pour le cours '{}' de la classe {}", coursActuel.getNom(), classeInfo.getNom());
                }
            }
        }

        // Identifier les cours non placés
        List<ResultatGeneration.CoursNonPlace> nonPlaces = coursNonPlaces.stream()
                .filter(c -> !c.estTermine())
                .map(c -> new ResultatGeneration.CoursNonPlace(
                        c.getCoursId(), c.getNom(), c.getEnseignantId(), c.getVolumeRestant(),
                        "Pas de créneau ou de disponibilité compatible"
                ))
                .collect(Collectors.toList());

        return new ResultatGeneration( placements, nonPlaces);
    }

    private boolean testerEtPlacer(CoursInfo cours, CreneauHoraire creneau, Long salleId, String nomSalle,
                                   Map<Long, Set<CreneauHoraire>> disposActivesParProf,
                                   GrilleHebdomadaire grille,
                                   List<ResultatGeneration.PlacementEffectue> placements) {
        
        Long enseignantId = cours.getEnseignantId();
        
        // 1. Vérifier si l'enseignant est disponible sur ce créneau (dans sa liste de dispos initiales)
        Set<CreneauHoraire> disposProf = disposActivesParProf.getOrDefault(enseignantId, Collections.emptySet());
        log.debug("disposProf.contains(creneau): {}", disposProf.contains(creneau));
        if (!disposProf.contains(creneau)) return false;

        // 2. Vérifier si l'enseignant est libre dans la grille (pas d'autre cours déjà placé)
        log.debug("grille.estLibrePourEnseignant(enseignantId, creneau): {}", grille.estLibrePourEnseignant(enseignantId, creneau));
        if (!grille.estLibrePourEnseignant(enseignantId, creneau)) return false;

        // 3. Vérifier si la classe est libre
        log.debug("grille.estLibrePourClasse(cours.getClasseId(), creneau) : {}",grille.estLibrePourClasse(cours.getClasseId(), creneau));
        if (!grille.estLibrePourClasse(cours.getClasseId(), creneau)) return false;

        // 4. Vérifier si la salle (celle de la classe) est libre
        log.debug(" grille.estLibrePourSalle(salleId, creneau) : {}",grille.estLibrePourSalle(salleId, creneau));
        if (!grille.estLibrePourSalle(salleId, creneau)) return false;

        // Tout est OK -> Placement
        grille.enregistrerPlacement(cours.getClasseId(), enseignantId, salleId, creneau);
        cours.enregistrerPlacement();
        
        // Marquer la disponibilité comme utilisée (enlever du Set pour cet enseignant)
        disposProf.remove(creneau);
        // Mettre à jour le volume de dispo dans tous les objets CoursInfo de cet enseignant
        // (Optionnel selon l'implémentation du score, mais cohérent)
        cours.setVolumeDisponibilite(disposProf.size() * DUREE_CRENEAU_HEURES);

        placements.add(new ResultatGeneration.PlacementEffectue(
                cours.getCoursId(),
                cours.getNom(),
                enseignantId,
                cours.getClasseId(),
                salleId,
                nomSalle,
                creneau.getJour().getNom(),
                creneau.getHeureDebut().toString(),
                creneau.getHeureFin().toString(),
                1.0 // Score simplifié
        ));

        return true;
    }

    private Map<Long, Set<CreneauHoraire>> chargerEtExtraireDispos(List<Long> enseignantIds) {
        Map<Long, Set<CreneauHoraire>> result = new HashMap<>();
        GrilleHebdomadaire grilleRef = new GrilleHebdomadaire();
        
        for (Long id : enseignantIds) {
            List<DisponibiliteEnseignantDTO> disposDto = disponibiliteClient.getDisponibilitesActives(id);
            
            Set<CreneauHoraire> creneauxProf = new HashSet<>();
            for (CreneauHoraire creneau : grilleRef.getTousLesCreneaux()) {
                boolean couvert = disposDto.stream()
                        .anyMatch(d -> d.getJour() == creneau.getJour()
                                     && !d.getHeureDebut().isAfter(creneau.getHeureDebut())
                                     && !d.getHeureFin().isBefore(creneau.getHeureFin()));
                if (couvert) {
                    creneauxProf.add(creneau);
                }
            }
            result.put(id, creneauxProf);
        }
        return result;
    }
}
