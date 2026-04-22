# Stratégie de génération automatique de l'emploi du temps
## Approche retenue : Semaine glissante + Scoring pondéré (5+4)

---

## Vue d'ensemble

L'idée centrale est simple : **on ne génère pas tout l'emploi du temps d'un coup**.
On avance semaine par semaine, et pour chaque semaine on place les cours en leur
attribuant un score. Le cours avec le meilleur score obtient le créneau en cas de conflit.

---

## Les données dont on a besoin (entrées)

Avant de lancer la génération pour une semaine donnée, il faut collecter :

1. **La liste des cours à planifier** (depuis `coursclasse-service`)
   - Pour chaque cours : `id`, `volumeHoraire`, `nbreheurefait`, `enseignantId`, `classeId`, `ueId`
   - Le **volume restant** = `volumeHoraire - nbreheurefait` → c'est la métrique de priorité

2. **Les disponibilités des enseignants pour la semaine** (depuis `planning-service`)
   - Pour chaque enseignant : quels jours, quelles plages horaires, quel type de dispo

3. **Les salles disponibles** (depuis `salles-service`)
   - Capacité, type de salle (amphi, labo, etc.), réservations déjà existantes

4. **Les classes et leurs effectifs** (depuis `coursclasse-service`)
   - Pour vérifier que la salle choisie peut accueillir la classe

---

## Étape 1 : Construire la grille de créneaux de la semaine

Une semaine = 5 jours (lundi → vendredi).
Chaque jour est découpé en **créneaux horaires** (ex: 8h-10h, 10h-12h, 14h-16h, 16h-18h).

On construit donc une grille vide :

```
Lundi 8h-10h    | libre
Lundi 10h-12h   | libre
Lundi 14h-16h   | libre
...
Vendredi 16h-18h | libre
```

Chaque case de la grille peut accueillir **un cours par classe** (une classe ne peut pas
avoir deux cours en même temps) et **un cours par enseignant** (un enseignant ne peut pas
être à deux endroits en même temps).

---

## Étape 2 : Identifier les créneaux valides pour chaque cours

Pour chaque cours à placer, on parcourt tous les créneaux de la grille et on vérifie :

- L'enseignant du cours est-il **disponible** sur ce créneau ? (vérification dans les dispos)
- La classe du cours est-elle **libre** sur ce créneau ? (pas déjà un autre cours)
- L'enseignant est-il **libre** sur ce créneau ? (pas déjà affecté à un autre cours)
- Existe-t-il une **salle disponible** avec une capacité suffisante pour l'effectif de la classe ?

Si toutes ces conditions sont vraies → le créneau est **candidat** pour ce cours.

---

## Étape 3 : Détecter les conflits de disponibilité

Un **conflit de disponibilité** se produit quand, sur un même créneau, plusieurs cours
ont leur enseignant disponible. Il faut décider lequel placer en premier.

Exemple concret :
- Cours A : Maths, volume restant = 2h, enseignant X dispo lundi 8h-10h
- Cours B : Physique, volume restant = 8h, enseignant Y dispo lundi 8h-10h
- Cours C : Anglais, volume restant = 1h, enseignant Z dispo lundi 8h-10h

→ Trois cours peuvent potentiellement occuper lundi 8h-10h.
→ On doit choisir lequel placer **en priorité** sur ce créneau.

---

## Étape 4 : Calculer le score de chaque cours candidat

Pour chaque cours candidat sur un créneau donné, on calcule un score :

```
score = scoreVolume * 40
      + scoreConcurrence * 30
      + tauxRemplissageSalle * 20
      + scoreFragmentation * 10
```

**Détail de chaque composante :**

---

### `scoreVolume * 40` — Priorité sur le volume restant (poids 40%)

C'est la règle principale : **un cours avec peu d'heures restantes passe en premier**.

```
scoreVolume = 2 / volumeRestant
```

- Cours A (2h restantes) : 2/2 = 1.0 → score partiel = 40
- Cours B (4h restantes) : 2/4 = 0.5 → score partiel = 20
- Cours C (8h restantes) : 2/8 = 0.25 → score partiel = 10

**→ Cours A passe en premier, puis B, puis C.**

---

### `scoreConcurrence * 30` — Pression de concurrence sur le créneau (poids 30%)

C'est ici que ta règle métier s'applique : **si un enseignant est le seul disponible
sur un créneau, son cours a moins d'urgence à être placé maintenant** car il aura
d'autres opportunités. En revanche, **si plusieurs enseignants sont disponibles sur
le même créneau, il faut choisir le plus urgent et laisser les autres pour plus tard**.

On calcule pour chaque créneau le **nombre d'enseignants concurrents** (enseignants
d'autres cours également disponibles sur ce même créneau) :

```
scoreConcurrence = nombreEnseignantsConcurrents / totalEnseignantsDisposSemaine
```

- Créneau lundi 8h : 3 enseignants dispo → scoreConcurrence élevé pour le cours avec
  le moins de volume restant → il passe en premier, les autres attendent
- Créneau mardi 14h : 1 seul enseignant dispo (prof de Maths) → scoreConcurrence = 0
  → Maths peut être placé ici sans pénaliser personne, mais son scoreVolume décide

**Exemple concret :**

Lundi 8h-10h : enseignant Maths (8h restantes) ET enseignant Physique (2h restantes)
sont tous les deux disponibles.

- Maths : scoreVolume = 2/8 = 0.25, scoreConcurrence élevé (concurrent présent)
  → score total orienté vers Physique
- Physique : scoreVolume = 2/2 = 1.0, scoreConcurrence élevé
  → **Physique gagne ce créneau**

Maths sera placé sur un autre créneau où il est seul ou face à des cours avec
encore moins de volume restant.

---

### `tauxRemplissageSalle * 20` — Optimisation des salles (poids 20%)

On préfère une salle dont la capacité est proche de l'effectif de la classe.
Exemple : classe de 30 étudiants → une salle de 35 places est mieux qu'une de 200.

```
tauxRemplissage = effectifClasse / capaciteSalle  (valeur entre 0 et 1)
```

---

### `scoreFragmentation * 10` — Cohérence de l'emploi du temps (poids 10%)

On favorise les créneaux qui regroupent les cours sur les mêmes jours plutôt que
de les éparpiller sur toute la semaine. Un emploi du temps avec des journées pleines
est préférable à un emploi du temps avec un cours par jour.

```
scoreFragmentation = coursDejaPlacésCeJour / totalCreneauxJour
```

Si la classe a déjà 2 cours placés le lundi → placer un 3ème cours le lundi a un
meilleur score de fragmentation que de l'ouvrir sur un nouveau jour vide.

---

## Étape 5 : Placer les cours dans l'ordre des scores

Un cours peut être planifié **plusieurs fois dans la même semaine** tant que :
- Son `volumeRestant > 0`
- Son enseignant est disponible sur le créneau concerné
- La classe n'a pas déjà un cours sur ce créneau
- L'enseignant n'est pas déjà affecté à un autre cours sur ce créneau
- Une salle adaptée est disponible sur ce créneau

**Algorithme de placement :**

1. Construire la liste de toutes les paires `(cours, créneau)` valides pour la semaine
2. Calculer le score de chaque paire
3. Trier par score décroissant
4. Prendre la paire avec le meilleur score
5. Placer ce cours sur ce créneau :
   - Marquer le créneau comme occupé pour cette classe
   - Marquer le créneau comme occupé pour cet enseignant
   - Incrémenter `nbreheurefait` de 2h pour ce cours
6. **Ne pas retirer le cours de la liste** — s'il a encore du `volumeRestant > 0`, il reste candidat pour d'autres créneaux de la même semaine
7. Recalculer les scores des paires restantes (la grille a changé, certains créneaux sont maintenant occupés)
8. Répéter à partir de l'étape 4 jusqu'à ce que :
   - Tous les cours aient `volumeRestant = 0`, **ou**
   - Il n'existe plus aucune paire `(cours, créneau)` valide pour la semaine

**Exemple concret :**

Cours Maths, `volumeRestant = 6h`, enseignant dispo lundi, mercredi, vendredi matin :
- Tour 1 : score le plus élevé → placé lundi 8h-10h → `volumeRestant` passe à 4h
- Tour 2 : toujours candidat → placé mercredi 8h-10h → `volumeRestant` passe à 2h
- Tour 3 : toujours candidat → placé vendredi 8h-10h → `volumeRestant` passe à 0h
- Tour 4 : `volumeRestant = 0` → Maths est retiré définitivement de la liste

**Garde-fou contre la surcharge :**
Un cours ne peut pas être placé deux fois sur le **même créneau** (même jour, même heure).
C'est garanti par la contrainte "classe déjà occupée sur ce créneau".

---

## Étape 6 : Gérer les cours non placés

Il peut arriver qu'un cours n'ait aucun créneau valide sur la semaine courante :
- L'enseignant n'a aucune disponibilité cette semaine
- Toutes les salles adaptées sont occupées
- La classe est déjà pleine pour la semaine

Dans ce cas : **reporter le cours à la semaine suivante** et le noter comme "non planifié".
On peut aussi générer une alerte pour l'administrateur.

---

## Étape 7 : Passer à la semaine suivante

Une fois la semaine N traitée :
- Mettre à jour `nbreheurefait` pour chaque cours placé
- Les cours dont `volumeHoraire == nbreheurefait` sont **terminés** → ne plus les planifier
- Recommencer à l'étape 1 pour la semaine N+1

---

## Architecture du code à produire (dans `planning-service`)

```
planning-service/
└── src/main/java/iusjc_planning/planning_service/
    ├── generation/
    │   ├── EmploiDuTempsGenerator.java      ← orchestrateur principal
    │   ├── CreneauCandidat.java             ← représente un créneau valide pour un cours
    │   ├── ScoringService.java              ← calcule le score d'un (cours, créneau)
    │   ├── ConflitDetector.java             ← détecte les conflits de disponibilité
    │   └── GrilleHebdomadaire.java          ← la grille de la semaine (état des créneaux)
    ├── feign/
    │   ├── CoursClient.java                 ← appel vers coursclasse-service
    │   └── SalleClient.java                 ← appel vers salles-service
    └── controller/
        └── GenerationController.java        ← endpoint POST /generate?semaine=2026-W12
```

---

## Flux d'exécution résumé

```
POST /generate?semaine=2026-W12
        │
        ▼
1. Récupérer tous les cours avec volume restant > 0  (CoursClient)
2. Récupérer les dispos enseignants pour la semaine  (DisponibiliteRepository)
3. Récupérer les salles disponibles                  (SalleClient)
4. Construire la GrilleHebdomadaire (vide)
        │
        ▼
5. Pour chaque cours :
   → Trouver les créneaux candidats (dispo enseignant + classe libre + salle dispo)
   → Calculer le score de chaque (cours, créneau)
        │
        ▼
6. Trier par score décroissant
7. Placer les cours un par un (meilleur score en premier)
8. Mettre à jour la grille après chaque placement
        │
        ▼
9. Sauvegarder l'emploi du temps généré
10. Mettre à jour nbreheurefait via CoursClient
11. Retourner le résultat (placés + non placés)
```

---

## Points de vigilance

---

### 1. Vérification de l'état des microservices avant génération

Avant de lancer la génération, le `planning-service` doit s'assurer que tous les acteurs
sont opérationnels. Concrètement, au démarrage de la requête `/generate`, on effectue
un **health check** sur chaque service impliqué :

| Service | Ce qu'il fournit | Vérification |
|---|---|---|
| `coursclasse-service` | Cours, classes, UE, filières, effectifs | Appel Feign `/actuator/health` ou endpoint dédié |
| `salles-service` | Salles disponibles, capacités, réservations | Idem |
| `planning-service` (lui-même) | Disponibilités enseignants | Déjà local |
| `user-service` | Informations enseignants (nom, id) | Idem |

Si l'un de ces services ne répond pas → **arrêter la génération immédiatement** et retourner
une erreur claire : `"Génération impossible : coursclasse-service indisponible"`.

Il faut aussi vérifier que les **données sont cohérentes** avant de commencer :
- Il existe au moins un cours avec `volumeRestant > 0`
- Il existe au moins une disponibilité enseignant pour la semaine demandée
- Il existe au moins une salle disponible

---

### 2. Structure des créneaux horaires

**Jours ouvrés : Lundi → Samedi**

**Plage journalière : 8h00 → 17h00** avec une pause déjeuner de 12h00 à 13h00.

**Durée standard d'un créneau : 2h**

La grille journalière est donc fixe et identique pour chaque jour :

```
Créneau 1 :  8h00 → 10h00   (2h)
Créneau 2 : 10h00 → 12h00   (2h)
--- PAUSE DÉJEUNER 12h00-13h00 ---
Créneau 3 : 13h00 → 15h00   (2h)
Créneau 4 : 15h00 → 17h00   (2h)
```

Soit **4 créneaux par jour × 6 jours = 24 créneaux par semaine** par classe.

La pause de 12h-13h est une **contrainte dure** : aucun cours ne peut être placé sur
cet intervalle, quelles que soient les disponibilités déclarées.

---

### 3. Gestion du volume restant — règle simplifiée

**Garantie métier : tous les `volumeHoraire` sont des multiples de 2h** (2h, 4h, 6h, 8h...).

Cela signifie que `volumeRestant` (`volumeHoraire - nbreheurefait`) sera **toujours un
multiple de 2h** tant qu'on décrémente correctement de 2h à chaque placement.

Il n'y a donc **jamais de créneau partiel** à gérer. Chaque placement consomme exactement
2h et le volume restant reste toujours un multiple de 2h jusqu'à 0.

**Les seuls cas possibles :**

**Cas 1 : `volumeRestant >= 2h`**
→ On place le cours sur un créneau de 2h.
→ On incrémente `nbreheurefait` de 2h après placement.
→ Si `volumeRestant` était exactement 2h → le cours est maintenant terminé.

**Cas 2 : `volumeRestant = 0`**
→ Ce cours est terminé, on ne le planifie plus jamais.

**Conséquence sur le comblement :**
La notion de "comblement" disparaît complètement. Chaque créneau de 2h est occupé
par **un seul cours** pour une classe donnée. Si aucun cours n'est à placer sur un
créneau → ce créneau reste libre (heure libre pour la classe).

**Validation à l'entrée :**
Avant de lancer la génération, vérifier que pour chaque cours :
`volumeHoraire % 2 == 0` et `nbreheurefait % 2 == 0`
Si ce n'est pas le cas → rejeter le cours avec une alerte à l'administrateur.

---

### 4. Idempotence

Si on relance la génération pour la même semaine, il faut d'abord supprimer les créneaux
déjà générés pour cette semaine avant de recommencer, et remettre les `nbreheurefait`
à leur valeur précédente.

---

### 5. Volume restant = 0

Ne jamais planifier un cours dont `volumeHoraire == nbreheurefait`. Ce cours est terminé.
