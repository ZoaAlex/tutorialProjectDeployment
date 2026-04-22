-- =====================================================
-- SCHEMA POUR PLANNING SERVICE - GESTION ENSEIGNANTS
-- =====================================================

-- Création de la base de données
CREATE DATABASE IF NOT EXISTS planningdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE planningdb;

-- =====================================================
-- TABLE UNIVERSITES
-- =====================================================
CREATE TABLE IF NOT EXISTS universites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(200) NOT NULL,
    code VARCHAR(100) UNIQUE,
    description TEXT,
    adresse VARCHAR(300),
    telephone VARCHAR(20),
    email VARCHAR(150),
    statut ENUM('ACTIVE', 'INACTIVE', 'EN_MAINTENANCE') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_code (code),
    INDEX idx_statut (statut)
);

-- =====================================================
-- TABLE ECOLES
-- =====================================================
CREATE TABLE IF NOT EXISTS ecoles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(200) NOT NULL,
    code VARCHAR(50) UNIQUE,
    description TEXT,
    adresse VARCHAR(300),
    telephone VARCHAR(20),
    email VARCHAR(150),
    statut ENUM('ACTIVE', 'INACTIVE', 'EN_RENOVATION', 'FERMEE_TEMPORAIREMENT') NOT NULL DEFAULT 'ACTIVE',
    universite_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (universite_id) REFERENCES universites(id) ON DELETE CASCADE,
    
    INDEX idx_code (code),
    INDEX idx_statut (statut),
    INDEX idx_universite (universite_id)
);

-- =====================================================
-- TABLE ENSEIGNANTS
-- =====================================================
CREATE TABLE IF NOT EXISTS enseignants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    telephone VARCHAR(20),
    specialite VARCHAR(150),
    grade VARCHAR(100),
    departement VARCHAR(200),
    statut ENUM('ACTIF', 'INACTIF', 'EN_CONGE', 'SUSPENDU') NOT NULL DEFAULT 'ACTIF',
    user_service_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_email (email),
    INDEX idx_statut (statut),
    INDEX idx_specialite (specialite),
    INDEX idx_departement (departement),
    INDEX idx_user_service_id (user_service_id)
);

-- =====================================================
-- TABLE AFFECTATIONS ENSEIGNANTS
-- =====================================================
CREATE TABLE IF NOT EXISTS affectations_enseignants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    enseignant_id BIGINT NOT NULL,
    ecole_id BIGINT NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE,
    type_affectation ENUM('PERMANENTE', 'TEMPORAIRE', 'VACATAIRE', 'INVITE', 'ECHANGE') NOT NULL,
    commentaire TEXT,
    statut ENUM('ACTIVE', 'SUSPENDUE', 'TERMINEE', 'ANNULEE') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (enseignant_id) REFERENCES enseignants(id) ON DELETE CASCADE,
    FOREIGN KEY (ecole_id) REFERENCES ecoles(id) ON DELETE CASCADE,
    
    INDEX idx_enseignant (enseignant_id),
    INDEX idx_ecole (ecole_id),
    INDEX idx_dates (date_debut, date_fin),
    INDEX idx_statut (statut),
    
    UNIQUE KEY unique_affectation_active (enseignant_id, ecole_id, statut)
);

-- =====================================================
-- TABLE COURS ECOLES
-- =====================================================
CREATE TABLE IF NOT EXISTS cours_ecoles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom_cours VARCHAR(200) NOT NULL,
    code_cours VARCHAR(50),
    description TEXT,
    jour ENUM('LUNDI', 'MARDI', 'MERCREDI', 'JEUDI', 'VENDREDI', 'SAMEDI', 'DIMANCHE') NOT NULL,
    heure_debut TIME NOT NULL,
    heure_fin TIME NOT NULL,
    salle VARCHAR(100),
    type_cours ENUM('COURS_MAGISTRAL', 'TRAVAUX_DIRIGES', 'TRAVAUX_PRATIQUES', 'SEMINAIRE', 'CONFERENCE', 'EXAMEN', 'SOUTENANCE') NOT NULL,
    nombre_heures_par_semaine INT NOT NULL DEFAULT 1,
    commentaire TEXT,
    statut ENUM('ACTIF', 'SUSPENDU', 'ANNULE', 'REPORTE', 'TERMINE') NOT NULL DEFAULT 'ACTIF',
    ecole_id BIGINT NOT NULL,
    enseignant_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (ecole_id) REFERENCES ecoles(id) ON DELETE CASCADE,
    FOREIGN KEY (enseignant_id) REFERENCES enseignants(id) ON DELETE CASCADE,
    
    INDEX idx_enseignant_jour (enseignant_id, jour),
    INDEX idx_ecole_statut (ecole_id, statut),
    INDEX idx_jour_heure (jour, heure_debut, heure_fin),
    INDEX idx_salle_jour (salle, jour, ecole_id),
    
    -- Contrainte pour éviter les heures incohérentes
    CONSTRAINT chk_heures_cours CHECK (heure_debut < heure_fin)
);

-- =====================================================
-- TABLE DISPONIBILITES ENSEIGNANTS
-- =====================================================
CREATE TABLE IF NOT EXISTS disponibilites_enseignants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    enseignant_id BIGINT NOT NULL,
    jour ENUM('LUNDI', 'MARDI', 'MERCREDI', 'JEUDI', 'VENDREDI', 'SAMEDI', 'DIMANCHE') NOT NULL,
    heure_debut TIME NOT NULL,
    heure_fin TIME NOT NULL,
    type ENUM('MATIN', 'APRES_MIDI', 'SOIR', 'JOURNEE_COMPLETE', 'PERSONNALISE') NOT NULL,
    commentaire TEXT,
    est_disponible BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (enseignant_id) REFERENCES enseignants(id) ON DELETE CASCADE,
    
    INDEX idx_enseignant_jour (enseignant_id, jour),
    INDEX idx_enseignant_disponible (enseignant_id, est_disponible),
    INDEX idx_jour_heure (jour, heure_debut, heure_fin),
    
    -- Contrainte pour éviter les heures incohérentes
    CONSTRAINT chk_heures_dispo CHECK (heure_debut < heure_fin)
);

-- =====================================================
-- TABLE CONFLITS PLANNING
-- =====================================================
CREATE TABLE IF NOT EXISTS conflits_planning (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cours_id BIGINT NOT NULL,
    cours_conflit_id BIGINT,
    enseignant_id BIGINT NOT NULL,
    type_conflit ENUM('CHEVAUCHEMENT_HORAIRE', 'DOUBLE_AFFECTATION', 'INDISPONIBILITE_ENSEIGNANT', 'CONFLIT_SALLE', 'SURCHARGE_HORAIRE') NOT NULL,
    description TEXT,
    statut ENUM('DETECTE', 'EN_COURS_RESOLUTION', 'RESOLU', 'IGNORE', 'REPORTE') NOT NULL DEFAULT 'DETECTE',
    date_detection TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_resolution TIMESTAMP NULL,
    solution_appliquee TEXT,
    
    FOREIGN KEY (cours_id) REFERENCES cours_ecoles(id) ON DELETE CASCADE,
    FOREIGN KEY (cours_conflit_id) REFERENCES cours_ecoles(id) ON DELETE CASCADE,
    FOREIGN KEY (enseignant_id) REFERENCES enseignants(id) ON DELETE CASCADE,
    
    INDEX idx_cours (cours_id),
    INDEX idx_enseignant (enseignant_id),
    INDEX idx_statut (statut),
    INDEX idx_type_conflit (type_conflit),
    INDEX idx_date_detection (date_detection)
);

-- =====================================================
<<<<<<< Updated upstream
-- DONNÉES D'EXEMPLE
-- =====================================================
=======
-- TABLE REGLES RESOLUTION AUTOMATIQUE
-- =====================================================
CREATE TABLE IF NOT EXISTS regles_resolution_auto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    description TEXT,
    type_conflit_cible ENUM('CHEVAUCHEMENT_HORAIRE', 'DOUBLE_AFFECTATION', 'INDISPONIBILITE_ENSEIGNANT', 'CONFLIT_SALLE', 'SURCHARGE_HORAIRE') NOT NULL,
    action_resolution ENUM('DECALER_COURS_SUIVANT', 'DECALER_COURS_PRECEDENT', 'CHANGER_SALLE', 'REDUIRE_DUREE', 'DIVISER_CRENEAU', 'NOTIFIER_ADMIN', 'SUSPENDRE_COURS', 'PROPOSER_ALTERNATIVES') NOT NULL,
    est_active BOOLEAN NOT NULL DEFAULT TRUE,
    priorite INT NOT NULL DEFAULT 1,
    duree_max_conflit_minutes INT,
    delai_max_resolution_minutes INT,
    seulement_meme_enseignant BOOLEAN DEFAULT TRUE,
    seulement_meme_ecole BOOLEAN DEFAULT FALSE,
    autoriser_changement_salle BOOLEAN DEFAULT TRUE,
    autoriser_changement_horaire BOOLEAN DEFAULT TRUE,
    autoriser_changement_enseignant BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_type_conflit_actif (type_conflit_cible, est_active),
    INDEX idx_priorite (priorite),
    INDEX idx_action (action_resolution)
);

-- =====================================================
-- DONNÉES D'EXEMPLE POUR NOTIFICATIONS ET RÈGLES
-- =====================================================

-- Insertion des règles de résolution automatique par défaut
INSERT INTO regles_resolution_auto (nom, description, type_conflit_cible, action_resolution, est_active, priorite, duree_max_conflit_minutes, delai_max_resolution_minutes, autoriser_changement_horaire) VALUES
('Décalage automatique cours courts', 'Décale automatiquement les cours de moins de 2h après 10 minutes', 'CHEVAUCHEMENT_HORAIRE', 'DECALER_COURS_SUIVANT', TRUE, 3, 120, 10, TRUE),
('Changement salle automatique', 'Change automatiquement la salle en cas de conflit après 5 minutes', 'CONFLIT_SALLE', 'CHANGER_SALLE', TRUE, 5, NULL, 5, TRUE),
('Notification indisponibilité', 'Notifie l\'admin en cas d\'indisponibilité enseignant', 'INDISPONIBILITE_ENSEIGNANT', 'NOTIFIER_ADMIN', TRUE, 1, NULL, 0, FALSE),
('Suspension cours problématiques', 'Suspend les cours qui créent trop de conflits après 30 minutes', 'CHEVAUCHEMENT_HORAIRE', 'SUSPENDRE_COURS', FALSE, 1, NULL, 30, FALSE);
>>>>>>> Stashed changes

-- Insertion de l'université Saint-Jean
INSERT INTO universites (nom, code, description, adresse, telephone, email, statut) VALUES
('Université Saint-Jean Chrysostome', 'IUSJC', 'Université privée spécialisée dans les sciences et technologies', '123 Avenue Saint-Jean, Kinshasa', '+243123456789', 'info@iusjc.edu', 'ACTIVE');

-- Insertion des écoles
INSERT INTO ecoles (nom, code, description, adresse, telephone, email, statut, universite_id) VALUES
('École Supérieure d\'Informatique et de Gestion', 'ESIG', 'Formation en informatique et gestion', '123 Avenue Saint-Jean, Kinshasa', '+243123456790', 'esig@iusjc.edu', 'ACTIVE', 1),
('École Supérieure de Gestion Commerciale', 'ESGC', 'Formation en commerce et gestion', '125 Avenue Saint-Jean, Kinshasa', '+243123456791', 'esgc@iusjc.edu', 'ACTIVE', 1),
('École de Médecine', 'MEDECINE', 'Formation médicale et paramédicale', '127 Avenue Saint-Jean, Kinshasa', '+243123456792', 'medecine@iusjc.edu', 'ACTIVE', 1),
('École d\'Ingénierie', 'INGENIERIE', 'Formation en ingénierie et sciences appliquées', '129 Avenue Saint-Jean, Kinshasa', '+243123456793', 'ingenierie@iusjc.edu', 'ACTIVE', 1);

-- Insertion d'enseignants d'exemple
INSERT INTO enseignants (nom, prenom, email, telephone, specialite, grade, departement, statut, user_service_id) VALUES
('Dupont', 'Jean', 'jean.dupont@iusjc.edu', '+33123456789', 'Mathématiques', 'Professeur', 'Sciences Exactes', 'ACTIF', 1),
('Martin', 'Sophie', 'sophie.martin@iusjc.edu', '+33123456790', 'Informatique', 'Maître de Conférences', 'Sciences Informatiques', 'ACTIF', 2),
('Bernard', 'Pierre', 'pierre.bernard@iusjc.edu', '+33123456791', 'Physique Quantique', 'Professeur Titulaire', 'Sciences Physiques', 'ACTIF', 3),
('Johnson', 'Emily', 'emily.johnson@iusjc.edu', '+33123456792', 'Anglais', 'Professeur Agrégé', 'Langues Étrangères', 'ACTIF', 4),
('Dubois', 'Marie', 'marie.dubois@iusjc.edu', '+33123456793', 'Chimie Organique', 'Maître de Conférences', 'Sciences Chimiques', 'ACTIF', 5),
('Mbala', 'Joseph', 'joseph.mbala@iusjc.edu', '+243987654321', 'Gestion d\'Entreprise', 'Professeur', 'Sciences de Gestion', 'ACTIF', 6),
('Kasongo', 'Marie', 'marie.kasongo@iusjc.edu', '+243987654322', 'Médecine Générale', 'Docteur', 'Médecine', 'ACTIF', 7);

-- Insertion des affectations enseignants
INSERT INTO affectations_enseignants (enseignant_id, ecole_id, date_debut, date_fin, type_affectation, commentaire, statut) VALUES
-- Jean Dupont enseigne à ESIG et INGENIERIE
(1, 1, '2024-01-01', NULL, 'PERMANENTE', 'Enseignant permanent en mathématiques', 'ACTIVE'),
(1, 4, '2024-01-01', NULL, 'PERMANENTE', 'Cours de mathématiques appliquées', 'ACTIVE'),

-- Sophie Martin à ESIG uniquement
(2, 1, '2024-01-01', NULL, 'PERMANENTE', 'Responsable département informatique', 'ACTIVE'),

-- Pierre Bernard à INGENIERIE
(3, 4, '2024-01-01', NULL, 'PERMANENTE', 'Professeur de physique', 'ACTIVE'),

-- Emily Johnson à ESIG et ESGC (langues)
(4, 1, '2024-01-01', NULL, 'PERMANENTE', 'Cours d\'anglais technique', 'ACTIVE'),
(4, 2, '2024-01-01', NULL, 'PERMANENTE', 'Anglais commercial', 'ACTIVE'),

-- Marie Dubois à INGENIERIE
(5, 4, '2024-01-01', NULL, 'PERMANENTE', 'Laboratoire de chimie', 'ACTIVE'),

-- Joseph Mbala à ESGC
(6, 2, '2024-01-01', NULL, 'PERMANENTE', 'Gestion et commerce', 'ACTIVE'),

-- Marie Kasongo à MEDECINE
(7, 3, '2024-01-01', NULL, 'PERMANENTE', 'Médecine générale', 'ACTIVE');

-- Insertion de cours d'exemple
INSERT INTO cours_ecoles (nom_cours, code_cours, description, jour, heure_debut, heure_fin, salle, type_cours, nombre_heures_par_semaine, commentaire, statut, ecole_id, enseignant_id) VALUES
-- Cours de Jean Dupont (ESIG)
('Mathématiques I', 'MATH101', 'Algèbre et analyse', 'LUNDI', '08:00:00', '10:00:00', 'A101', 'COURS_MAGISTRAL', 2, 'Cours fondamental', 'ACTIF', 1, 1),
('Mathématiques II', 'MATH102', 'Géométrie et statistiques', 'MERCREDI', '10:00:00', '12:00:00', 'A102', 'COURS_MAGISTRAL', 2, 'Suite du cours MATH101', 'ACTIF', 1, 1),

-- Cours de Jean Dupont (INGENIERIE) - CONFLIT POTENTIEL
('Mathématiques Appliquées', 'MATH201', 'Mathématiques pour ingénieurs', 'LUNDI', '09:00:00', '11:00:00', 'B201', 'COURS_MAGISTRAL', 2, 'Cours spécialisé', 'SUSPENDU', 4, 1),

-- Cours de Sophie Martin (ESIG)
('Programmation Java', 'INFO101', 'Introduction à Java', 'MARDI', '08:00:00', '10:00:00', 'LAB1', 'TRAVAUX_PRATIQUES', 3, 'TP en laboratoire', 'ACTIF', 1, 2),
('Base de Données', 'INFO201', 'Conception BDD', 'JEUDI', '14:00:00', '16:00:00', 'A201', 'COURS_MAGISTRAL', 2, 'Théorie des BDD', 'ACTIF', 1, 2),

-- Cours d'Emily Johnson (ESIG et ESGC)
('Anglais Technique', 'ANG101', 'Anglais pour informaticiens', 'VENDREDI', '08:00:00', '10:00:00', 'A301', 'COURS_MAGISTRAL', 2, 'Anglais spécialisé', 'ACTIF', 1, 4),
('Anglais Commercial', 'ANG201', 'Anglais des affaires', 'VENDREDI', '10:30:00', '12:30:00', 'B301', 'COURS_MAGISTRAL', 2, 'Communication commerciale', 'ACTIF', 2, 4),

-- Cours de Joseph Mbala (ESGC)
('Gestion d\'Entreprise', 'GEST101', 'Principes de gestion', 'LUNDI', '14:00:00', '16:00:00', 'C101', 'COURS_MAGISTRAL', 2, 'Cours fondamental', 'ACTIF', 2, 6),
('Marketing', 'MARK101', 'Stratégies marketing', 'MERCREDI', '14:00:00', '17:00:00', 'C102', 'SEMINAIRE', 3, 'Études de cas', 'ACTIF', 2, 6);

-- Insertion de disponibilités d'exemple pour Jean Dupont (ID: 1)
INSERT INTO disponibilites_enseignants (enseignant_id, jour, heure_debut, heure_fin, type, commentaire, est_disponible) VALUES
-- Lundi - CONFLIT avec cours ESIG et INGENIERIE
(1, 'LUNDI', '08:00:00', '12:00:00', 'MATIN', 'Disponible mais avec cours', TRUE),

-- Mardi
(1, 'MARDI', '09:00:00', '11:00:00', 'MATIN', 'Libre pour consultations', TRUE),
(1, 'MARDI', '13:30:00', '15:30:00', 'APRES_MIDI', 'Encadrement projets', TRUE),

-- Mercredi - cours à 10h-12h
(1, 'MERCREDI', '08:00:00', '10:00:00', 'MATIN', 'Libre avant cours', TRUE),
(1, 'MERCREDI', '14:00:00', '17:00:00', 'APRES_MIDI', 'Libre après cours', TRUE),

-- Jeudi
(1, 'JEUDI', '10:00:00', '12:00:00', 'MATIN', 'Réunions pédagogiques', TRUE),
(1, 'JEUDI', '14:00:00', '17:00:00', 'APRES_MIDI', 'Consultations étudiants', TRUE),

-- Vendredi
(1, 'VENDREDI', '08:30:00', '12:30:00', 'MATIN', 'Recherche et préparation', TRUE);

-- Insertion de disponibilités pour Sophie Martin (ID: 2)
INSERT INTO disponibilites_enseignants (enseignant_id, jour, heure_debut, heure_fin, type, commentaire, est_disponible) VALUES
-- Lundi
(2, 'LUNDI', '09:00:00', '12:00:00', 'MATIN', 'Préparation cours', TRUE),
(2, 'LUNDI', '13:00:00', '17:00:00', 'APRES_MIDI', 'Encadrement projets', TRUE),

-- Mardi - cours 8h-10h
(2, 'MARDI', '10:30:00', '12:00:00', 'MATIN', 'Libre après TP', TRUE),
(2, 'MARDI', '14:00:00', '16:00:00', 'APRES_MIDI', 'Consultations', TRUE),

-- Mercredi
(2, 'MERCREDI', '10:00:00', '12:00:00', 'MATIN', 'Réunions département', TRUE),

-- Jeudi - cours 14h-16h
(2, 'JEUDI', '09:00:00', '12:00:00', 'MATIN', 'Libre avant cours', TRUE),
(2, 'JEUDI', '16:30:00', '18:00:00', 'APRES_MIDI', 'Libre après cours', TRUE),

-- Vendredi
(2, 'VENDREDI', '13:00:00', '17:00:00', 'APRES_MIDI', 'Projets étudiants', TRUE);

-- Détection automatique de conflits (sera fait par l'application)
INSERT INTO conflits_planning (cours_id, cours_conflit_id, enseignant_id, type_conflit, description, statut) VALUES
(1, 3, 1, 'CHEVAUCHEMENT_HORAIRE', 'Conflit horaire entre Mathématiques I (ESIG) et Mathématiques Appliquées (INGENIERIE) le LUNDI de 08:00 à 12:00', 'DETECTE');

-- =====================================================
-- VUES UTILES
-- =====================================================

-- Vue pour obtenir le planning complet des enseignants avec écoles
CREATE OR REPLACE VIEW vue_planning_complet AS
SELECT 
    e.id as enseignant_id,
    CONCAT(e.prenom, ' ', e.nom) as nom_complet,
    e.email,
    e.specialite,
    ec.nom as ecole_nom,
    ec.code as ecole_code,
    c.nom_cours,
    c.code_cours,
    c.jour,
    c.heure_debut,
    c.heure_fin,
    c.salle,
    c.type_cours,
    c.statut as statut_cours,
    TIMEDIFF(c.heure_fin, c.heure_debut) as duree,
    (SELECT COUNT(*) FROM conflits_planning cp WHERE cp.cours_id = c.id AND cp.statut = 'DETECTE') as nb_conflits
FROM enseignants e
JOIN cours_ecoles c ON e.id = c.enseignant_id
JOIN ecoles ec ON c.ecole_id = ec.id
WHERE e.statut = 'ACTIF' AND c.statut = 'ACTIF'
ORDER BY e.nom, e.prenom, 
         FIELD(c.jour, 'LUNDI', 'MARDI', 'MERCREDI', 'JEUDI', 'VENDREDI', 'SAMEDI', 'DIMANCHE'),
         c.heure_debut;

-- Vue pour les conflits actifs
CREATE OR REPLACE VIEW vue_conflits_actifs AS
SELECT 
    cp.id,
    cp.type_conflit,
    cp.description,
    cp.statut,
    cp.date_detection,
    CONCAT(e.prenom, ' ', e.nom) as enseignant_nom,
    c1.nom_cours as cours_principal,
    ec1.nom as ecole_principale,
    c2.nom_cours as cours_conflit,
    ec2.nom as ecole_conflit
FROM conflits_planning cp
JOIN enseignants e ON cp.enseignant_id = e.id
JOIN cours_ecoles c1 ON cp.cours_id = c1.id
JOIN ecoles ec1 ON c1.ecole_id = ec1.id
LEFT JOIN cours_ecoles c2 ON cp.cours_conflit_id = c2.id
LEFT JOIN ecoles ec2 ON c2.ecole_id = ec2.id
WHERE cp.statut IN ('DETECTE', 'EN_COURS_RESOLUTION')
ORDER BY cp.date_detection DESC;

-- Vue pour les statistiques par école
CREATE OR REPLACE VIEW vue_stats_ecoles AS
SELECT 
    ec.id,
    ec.nom as ecole_nom,
    ec.code as ecole_code,
    COUNT(DISTINCT ae.enseignant_id) as nb_enseignants_affectes,
    COUNT(DISTINCT c.id) as nb_cours_total,
    COUNT(DISTINCT CASE WHEN c.statut = 'ACTIF' THEN c.id END) as nb_cours_actifs,
    COUNT(DISTINCT CASE WHEN cp.statut = 'DETECTE' THEN cp.id END) as nb_conflits_non_resolus,
    SUM(c.nombre_heures_par_semaine) as total_heures_semaine
FROM ecoles ec
LEFT JOIN affectations_enseignants ae ON ec.id = ae.ecole_id AND ae.statut = 'ACTIVE'
LEFT JOIN cours_ecoles c ON ec.id = c.ecole_id
LEFT JOIN conflits_planning cp ON c.id = cp.cours_id
WHERE ec.statut = 'ACTIVE'
GROUP BY ec.id, ec.nom, ec.code;

-- =====================================================
-- PROCÉDURES STOCKÉES UTILES
-- =====================================================

DELIMITER //

-- Procédure pour obtenir les enseignants disponibles pour un créneau dans une école
CREATE PROCEDURE GetEnseignantsDisponiblesPourEcole(
    IN p_ecole_id BIGINT,
    IN p_jour ENUM('LUNDI', 'MARDI', 'MERCREDI', 'JEUDI', 'VENDREDI', 'SAMEDI', 'DIMANCHE'),
    IN p_heure_debut TIME,
    IN p_heure_fin TIME
)
BEGIN
    SELECT DISTINCT
        e.id,
        CONCAT(e.prenom, ' ', e.nom) as nom_complet,
        e.email,
        e.specialite,
        e.grade,
        d.heure_debut as dispo_debut,
        d.heure_fin as dispo_fin,
        d.commentaire as dispo_commentaire
    FROM enseignants e
    INNER JOIN affectations_enseignants ae ON e.id = ae.enseignant_id
    INNER JOIN disponibilites_enseignants d ON e.id = d.enseignant_id
    WHERE e.statut = 'ACTIF'
      AND ae.ecole_id = p_ecole_id
      AND ae.statut = 'ACTIVE'
      AND ae.date_debut <= CURRENT_DATE
      AND (ae.date_fin IS NULL OR ae.date_fin >= CURRENT_DATE)
      AND d.jour = p_jour
      AND d.est_disponible = TRUE
      AND d.heure_debut <= p_heure_debut
      AND d.heure_fin >= p_heure_fin
      AND NOT EXISTS (
          SELECT 1 FROM cours_ecoles c 
          WHERE c.enseignant_id = e.id 
            AND c.jour = p_jour 
            AND c.statut = 'ACTIF'
            AND NOT (c.heure_fin <= p_heure_debut OR c.heure_debut >= p_heure_fin)
      )
    ORDER BY e.nom, e.prenom;
END //

-- Procédure pour détecter tous les conflits d'un enseignant
CREATE PROCEDURE DetecterConflitsEnseignant(
    IN p_enseignant_id BIGINT
)
BEGIN
    SELECT 
        'CHEVAUCHEMENT_HORAIRE' as type_conflit,
        c1.nom_cours as cours1,
        c2.nom_cours as cours2,
        ec1.nom as ecole1,
        ec2.nom as ecole2,
        c1.jour,
        c1.heure_debut,
        c1.heure_fin,
        'Conflit entre deux cours' as description
    FROM cours_ecoles c1
    JOIN cours_ecoles c2 ON c1.enseignant_id = c2.enseignant_id 
        AND c1.id != c2.id
        AND c1.jour = c2.jour
        AND c1.statut = 'ACTIF' 
        AND c2.statut = 'ACTIF'
        AND NOT (c1.heure_fin <= c2.heure_debut OR c1.heure_debut >= c2.heure_fin)
    JOIN ecoles ec1 ON c1.ecole_id = ec1.id
    JOIN ecoles ec2 ON c2.ecole_id = ec2.id
    WHERE c1.enseignant_id = p_enseignant_id
    
    UNION ALL
    
    SELECT 
        'INDISPONIBILITE_ENSEIGNANT' as type_conflit,
        c.nom_cours as cours1,
        NULL as cours2,
        ec.nom as ecole1,
        NULL as ecole2,
        c.jour,
        c.heure_debut,
        c.heure_fin,
        'Enseignant non disponible' as description
    FROM cours_ecoles c
    JOIN ecoles ec ON c.ecole_id = ec.id
    WHERE c.enseignant_id = p_enseignant_id
      AND c.statut = 'ACTIF'
      AND NOT EXISTS (
          SELECT 1 FROM disponibilites_enseignants d
          WHERE d.enseignant_id = p_enseignant_id
            AND d.jour = c.jour
            AND d.est_disponible = TRUE
            AND d.heure_debut <= c.heure_debut
            AND d.heure_fin >= c.heure_fin
      );
END //

DELIMITER ;

-- =====================================================
-- INDEX SUPPLÉMENTAIRES POUR PERFORMANCE
-- =====================================================

-- Index composés pour les recherches fréquentes
CREATE INDEX idx_enseignant_statut_specialite ON enseignants(statut, specialite);
CREATE INDEX idx_cours_enseignant_jour_statut ON cours_ecoles(enseignant_id, jour, statut);
CREATE INDEX idx_affectation_enseignant_ecole_active ON affectations_enseignants(enseignant_id, ecole_id, statut);
CREATE INDEX idx_disponibilite_enseignant_jour_actif ON disponibilites_enseignants(enseignant_id, jour, est_disponible);

COMMIT;