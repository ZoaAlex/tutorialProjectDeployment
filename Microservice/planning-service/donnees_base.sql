-- Données de base pour Planning Service IUSJC
USE planningdb;

-- Insertion de l'université Saint-Jean
INSERT IGNORE INTO universites (id, nom, code, description, adresse, telephone, email, statut) VALUES
(1, 'Université Saint-Jean Chrysostome', 'IUSJC', 'Université privée spécialisée dans les sciences et technologies', '123 Avenue Saint-Jean, Kinshasa', '+243123456789', 'info@iusjc.edu', 'ACTIVE');

-- Insertion des écoles
INSERT IGNORE INTO ecoles (id, nom, code, description, adresse, telephone, email, statut, universite_id) VALUES
(1, 'École Supérieure d\'Informatique et de Gestion', 'ESIG', 'Formation en informatique et gestion', '123 Avenue Saint-Jean, Kinshasa', '+243123456790', 'esig@iusjc.edu', 'ACTIVE', 1),
(2, 'École Supérieure de Gestion Commerciale', 'ESGC', 'Formation en commerce et gestion', '125 Avenue Saint-Jean, Kinshasa', '+243123456791', 'esgc@iusjc.edu', 'ACTIVE', 1),
(3, 'École de Médecine', 'MEDECINE', 'Formation médicale et paramédicale', '127 Avenue Saint-Jean, Kinshasa', '+243123456792', 'medecine@iusjc.edu', 'ACTIVE', 1),
(4, 'École d\'Ingénierie', 'INGENIERIE', 'Formation en ingénierie et sciences appliquées', '129 Avenue Saint-Jean, Kinshasa', '+243123456793', 'ingenierie@iusjc.edu', 'ACTIVE', 1);

-- Insertion d'enseignants d'exemple
INSERT IGNORE INTO enseignants (id, nom, prenom, email, telephone, specialite, grade, departement, statut) VALUES
(1, 'Dupont', 'Jean', 'jean.dupont@iusjc.edu', '+33123456789', 'Mathématiques', 'Professeur', 'Sciences Exactes', 'ACTIF'),
(2, 'Martin', 'Sophie', 'sophie.martin@iusjc.edu', '+33123456790', 'Informatique', 'Maître de Conférences', 'Sciences Informatiques', 'ACTIF'),
(3, 'Bernard', 'Pierre', 'pierre.bernard@iusjc.edu', '+33123456791', 'Physique Quantique', 'Professeur Titulaire', 'Sciences Physiques', 'ACTIF');

-- Insertion des affectations enseignants
INSERT IGNORE INTO affectations_enseignants (enseignant_id, ecole_id, date_debut, type_affectation, statut) VALUES
(1, 1, '2024-01-01', 'PERMANENTE', 'ACTIVE'),
(1, 4, '2024-01-01', 'PERMANENTE', 'ACTIVE'),
(2, 1, '2024-01-01', 'PERMANENTE', 'ACTIVE'),
(3, 4, '2024-01-01', 'PERMANENTE', 'ACTIVE');

-- Insertion de disponibilités d'exemple
INSERT IGNORE INTO disponibilites_enseignants (enseignant_id, jour, heure_debut, heure_fin, type, commentaire, est_disponible) VALUES
(1, 'LUNDI', '08:00:00', '12:00:00', 'MATIN', 'Disponible pour cours', TRUE),
(1, 'MARDI', '09:00:00', '11:00:00', 'MATIN', 'Libre pour consultations', TRUE),
(1, 'MERCREDI', '08:00:00', '10:00:00', 'MATIN', 'Libre avant cours', TRUE),
(2, 'LUNDI', '09:00:00', '12:00:00', 'MATIN', 'Préparation cours', TRUE),
(2, 'MARDI', '10:30:00', '12:00:00', 'MATIN', 'Libre après TP', TRUE),
(3, 'LUNDI', '14:00:00', '17:00:00', 'APRES_MIDI', 'Recherche', TRUE);

-- Insertion des règles de résolution automatique
INSERT IGNORE INTO regles_resolution_auto (nom, description, type_conflit_cible, action_resolution, est_active, priorite, duree_max_conflit_minutes, delai_max_resolution_minutes, autoriser_changement_horaire) VALUES
('Décalage automatique cours courts', 'Décale automatiquement les cours de moins de 2h après 10 minutes', 'CHEVAUCHEMENT_HORAIRE', 'DECALER_COURS_SUIVANT', TRUE, 3, 120, 10, TRUE),
('Changement salle automatique', 'Change automatiquement la salle en cas de conflit après 5 minutes', 'CONFLIT_SALLE', 'CHANGER_SALLE', TRUE, 5, NULL, 5, TRUE),
('Notification indisponibilité', 'Notifie l\'admin en cas d\'indisponibilité enseignant', 'INDISPONIBILITE_ENSEIGNANT', 'NOTIFIER_ADMIN', TRUE, 1, NULL, 0, FALSE);

COMMIT;