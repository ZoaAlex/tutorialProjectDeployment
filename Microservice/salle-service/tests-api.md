# Tests API - Salles Service

## Prérequis
- Tous les services doivent être démarrés
- MySQL avec la base `sallesdb` initialisée
- API Gateway sur le port 8080

## Tests via API Gateway (Port 8080)

### 1. Test de santé des services

```bash
# Salles Service
curl http://localhost:8080/api/salles/health

# Matériels Service  
curl http://localhost:8080/api/materiels/health

# Réservations Service
curl http://localhost:8080/api/reservations/health
```

### 2. Tests SALLES

#### 2.1 Récupérer toutes les salles
```bash
curl -X GET http://localhost:8080/api/salles
```

#### 2.2 Récupérer une salle par ID
```bash
curl -X GET http://localhost:8080/api/salles/1
```

#### 2.3 Récupérer les salles d'une école
```bash
curl -X GET http://localhost:8080/api/salles/ecole/1
```

#### 2.4 Créer une nouvelle salle
```bash
curl -X POST http://localhost:8080/api/salles \
  -H "Content-Type: application/json" \
  -H "X-User-Id: admin" \
  -d "{
    \"codeSalle\": \"B101\",
    \"nom\": \"Salle de TP\",
    \"capacite\": 40,
    \"typeSalle\": \"LABORATOIRE\",
    \"description\": \"Salle de travaux pratiques\",
    \"emplacement\": \"Bâtiment B, 1er étage\",
    \"ecoleId\": 1,
    \"etage\": 1,
    \"batiment\": \"Bâtiment B\",
    \"surface\": 85.0,
    \"accessibleHandicap\": true,
    \"climatisee\": true,
    \"wifiDisponible\": true
  }"
```

#### 2.5 Rechercher des salles disponibles
```bash
curl -X GET "http://localhost:8080/api/salles/disponibles?dateDebut=2024-12-20T08:00:00&dateFin=2024-12-20T10:00:00&ecoleId=1"
```

#### 2.6 Rechercher des salles par critères
```bash
curl -X POST http://localhost:8080/api/salles/recherche \
  -H "Content-Type: application/json" \
  -d "{
    \"ecoleId\": 1,
    \"capaciteMin\": 30,
    \"statut\": \"LIBRE\",
    \"wifiDisponible\": true
  }"
```

#### 2.7 Mettre à jour une salle
```bash
curl -X PUT http://localhost:8080/api/salles/1 \
  -H "Content-Type: application/json" \
  -H "X-User-Id: admin" \
  -d "{
    \"id\": 1,
    \"codeSalle\": \"A101\",
    \"nom\": \"Amphithéâtre A - Rénové\",
    \"capacite\": 220,
    \"typeSalle\": \"AMPHITHEATRE\",
    \"statut\": \"LIBRE\",
    \"description\": \"Grand amphithéâtre principal rénové\",
    \"emplacement\": \"Bâtiment A, Rez-de-chaussée\",
    \"ecoleId\": 1,
    \"etage\": 0,
    \"batiment\": \"Bâtiment A\",
    \"surface\": 250.0,
    \"accessibleHandicap\": true,
    \"climatisee\": true,
    \"wifiDisponible\": true
  }"
```

#### 2.8 Changer le statut d'une salle
```bash
curl -X PATCH "http://localhost:8080/api/salles/1/statut?statut=MAINTENANCE" \
  -H "X-User-Id: admin"
```

#### 2.9 Obtenir les statistiques des salles
```bash
curl -X GET http://localhost:8080/api/salles/statistiques
```

### 3. Tests MATÉRIEL

#### 3.1 Récupérer tout le matériel
```bash
curl -X GET http://localhost:8080/api/materiels
```

#### 3.2 Récupérer le matériel d'une salle
```bash
curl -X GET http://localhost:8080/api/materiels/salle/1
```

#### 3.3 Créer un nouveau matériel
```bash
curl -X POST http://localhost:8080/api/materiels \
  -H "Content-Type: application/json" \
  -H "X-User-Id: admin" \
  -d "{
    \"nom\": \"Vidéoprojecteur\",
    \"type\": \"PROJECTEUR\",
    \"description\": \"Projecteur HD pour présentations\",
    \"quantite\": 1,
    \"quantiteFonctionnelle\": 1,
    \"marque\": \"Epson\",
    \"modele\": \"EB-2250U\",
    \"salleId\": 2
  }"
```

#### 3.4 Récupérer le matériel en panne
```bash
curl -X GET http://localhost:8080/api/materiels/panne
```

#### 3.5 Récupérer le matériel nécessitant maintenance
```bash
curl -X GET http://localhost:8080/api/materiels/maintenance/requise
```

#### 3.6 Changer l'état d'un matériel
```bash
curl -X PATCH "http://localhost:8080/api/materiels/1/etat?etat=EN_MAINTENANCE" \
  -H "X-User-Id: admin"
```

#### 3.7 Programmer une maintenance
```bash
curl -X PATCH "http://localhost:8080/api/materiels/1/maintenance?dateMaintenance=2024-12-25T10:00:00" \
  -H "X-User-Id: admin"
```

#### 3.8 Déplacer un matériel vers une autre salle
```bash
curl -X PATCH http://localhost:8080/api/materiels/1/deplacer/3 \
  -H "X-User-Id: admin"
```

### 4. Tests RÉSERVATIONS

#### 4.1 Récupérer toutes les réservations
```bash
curl -X GET http://localhost:8080/api/reservations
```

#### 4.2 Créer une nouvelle réservation
```bash
curl -X POST http://localhost:8080/api/reservations \
  -H "Content-Type: application/json" \
  -H "X-User-Id: user1" \
  -d "{
    \"dateDebut\": \"2024-12-20T14:00:00\",
    \"dateFin\": \"2024-12-20T16:00:00\",
    \"motif\": \"Réunion pédagogique\",
    \"description\": \"Réunion de coordination des enseignants\",
    \"utilisateurId\": 1,
    \"salleId\": 6,
    \"nombreParticipants\": 15,
    \"materielRequis\": \"Projecteur, Tableau blanc\",
    \"commentaires\": \"Prévoir café et eau\",
    \"priorite\": 2,
    \"recurrente\": false
  }"
```

#### 4.3 Récupérer les réservations en attente
```bash
curl -X GET http://localhost:8080/api/reservations/en-attente
```

#### 4.4 Récupérer les réservations actives
```bash
curl -X GET http://localhost:8080/api/reservations/actives
```

#### 4.5 Récupérer les réservations d'une salle
```bash
curl -X GET http://localhost:8080/api/reservations/salle/1
```

#### 4.6 Récupérer les réservations d'un utilisateur
```bash
curl -X GET http://localhost:8080/api/reservations/utilisateur/1
```

#### 4.7 Valider une réservation
```bash
curl -X PATCH "http://localhost:8080/api/reservations/1/valider?validateurId=2" \
  -H "X-User-Id: admin"
```

#### 4.8 Rejeter une réservation
```bash
curl -X PATCH "http://localhost:8080/api/reservations/1/rejeter?motifRejet=Salle+non+disponible" \
  -H "X-User-Id: admin"
```

#### 4.9 Annuler une réservation
```bash
curl -X PATCH http://localhost:8080/api/reservations/1/annuler \
  -H "X-User-Id: user1"
```

#### 4.10 Obtenir les statistiques des réservations
```bash
curl -X GET http://localhost:8080/api/reservations/statistiques
```

### 5. Tests ÉCOLES (via Planning Service)

#### 5.1 Récupérer toutes les écoles
```bash
curl -X GET http://localhost:8080/api/ecoles
```

#### 5.2 Récupérer une école par ID
```bash
curl -X GET http://localhost:8080/api/ecoles/1
```

#### 5.3 Récupérer les écoles actives
```bash
curl -X GET http://localhost:8080/api/ecoles/actives
```

#### 5.4 Récupérer toutes les universités
```bash
curl -X GET http://localhost:8080/api/ecoles/universites
```

## Tests de scénarios complets

### Scénario 1 : Créer une salle avec matériel et réservation

```bash
# 1. Créer une salle
SALLE_ID=$(curl -X POST http://localhost:8080/api/salles \
  -H "Content-Type: application/json" \
  -H "X-User-Id: admin" \
  -d '{"codeSalle":"TEST01","nom":"Salle Test","capacite":30,"typeSalle":"SALLE_COURS","ecoleId":1}' \
  | jq -r '.id')

# 2. Ajouter du matériel
curl -X POST http://localhost:8080/api/materiels \
  -H "Content-Type: application/json" \
  -H "X-User-Id: admin" \
  -d "{\"nom\":\"Projecteur Test\",\"type\":\"PROJECTEUR\",\"quantite\":1,\"salleId\":$SALLE_ID}"

# 3. Créer une réservation
curl -X POST http://localhost:8080/api/reservations \
  -H "Content-Type: application/json" \
  -H "X-User-Id: user1" \
  -d "{\"dateDebut\":\"2024-12-25T10:00:00\",\"dateFin\":\"2024-12-25T12:00:00\",\"motif\":\"Test\",\"utilisateurId\":1,\"salleId\":$SALLE_ID}"
```

### Scénario 2 : Test de conflit de réservation

```bash
# 1. Créer une première réservation
curl -X POST http://localhost:8080/api/reservations \
  -H "Content-Type: application/json" \
  -H "X-User-Id: user1" \
  -d '{"dateDebut":"2024-12-26T14:00:00","dateFin":"2024-12-26T16:00:00","motif":"Réunion 1","utilisateurId":1,"salleId":1}'

# 2. Tenter de créer une réservation en conflit (devrait échouer)
curl -X POST http://localhost:8080/api/reservations \
  -H "Content-Type: application/json" \
  -H "X-User-Id: user2" \
  -d '{"dateDebut":"2024-12-26T15:00:00","dateFin":"2024-12-26T17:00:00","motif":"Réunion 2","utilisateurId":2,"salleId":1}'
```

## Résultats attendus

- ✅ Tous les endpoints doivent retourner un code 200 (succès) ou 201 (créé)
- ✅ Les données doivent être cohérentes entre les appels
- ✅ Les conflits de réservation doivent être détectés (code 400)
- ✅ Les validations doivent fonctionner (école inexistante = erreur 404)
- ✅ Les statistiques doivent refléter les données créées
