import openpyxl

wb = openpyxl.Workbook()
ws = wb.active
ws.title = "Feuil1"

# En-tête (ligne 1, ignorée par le backend)
ws.append(["Nom", "UeCode", "VolumeHoraire", "NbreHeureFait", "EnseignantEmail"])

# ⚠️  Remplace les emails par ceux de tes enseignants réels en base
# 50 cours répartis sur les 20 UE (2-3 cours par UE)
E1 = "jean@iusjc.com"
E2 = "alexzoa70@gmail.com"
E3 = "enseignant@iusjc.com"

cours = [
    # ── GL — UE-GL-001 : Algorithmique et Structures de Données
    ("Algorithmique - Introduction",                  "UE-GL-001", 30, 0, E1),
    ("Algorithmique - Structures de Données",         "UE-GL-001", 30, 0, E1),
    ("Algorithmique - Complexité",                    "UE-GL-001", 20, 0, E1),

    # ── GL — UE-GL-002 : Programmation Orientée Objet
    ("POO - Concepts de Base",                        "UE-GL-002", 30, 0, E2),
    ("POO - Design Patterns",                         "UE-GL-002", 20, 0, E2),
    ("POO - Projet Java",                             "UE-GL-002", 20, 0, E2),

    # ── GL — UE-GL-003 : Bases de Données
    ("Bases de Données - Modélisation",               "UE-GL-003", 25, 0, E3),
    ("Bases de Données - SQL Avancé",                 "UE-GL-003", 25, 0, E3),

    # ── RS — UE-RS-001 : Architecture des Réseaux
    ("Réseaux - Modèle OSI",                          "UE-RS-001", 25, 0, E1),
    ("Réseaux - Protocoles TCP/IP",                   "UE-RS-001", 25, 0, E1),
    ("Réseaux - Routage et Commutation",              "UE-RS-001", 20, 0, E1),

    # ── RS — UE-RS-002 : Administration Systèmes Linux
    ("Linux - Administration de Base",                "UE-RS-002", 30, 0, E2),
    ("Linux - Scripts Shell",                         "UE-RS-002", 20, 0, E2),

    # ── RS — UE-RS-003 : Sécurité Informatique
    ("Sécurité - Cryptographie",                      "UE-RS-003", 25, 0, E3),
    ("Sécurité - Ethical Hacking",                    "UE-RS-003", 25, 0, E3),

    # ── GE — UE-GE-001 : Comptabilité Générale
    ("Comptabilité - Principes Fondamentaux",         "UE-GE-001", 30, 0, E1),
    ("Comptabilité - Bilan et Résultat",              "UE-GE-001", 25, 0, E1),

    # ── GE — UE-GE-002 : Management des Organisations
    ("Management - Théories des Organisations",       "UE-GE-002", 25, 0, E2),
    ("Management - Leadership",                       "UE-GE-002", 20, 0, E2),

    # ── GE — UE-GE-003 : Finance d'Entreprise
    ("Finance - Analyse Financière",                  "UE-GE-003", 30, 0, E3),
    ("Finance - Gestion de Trésorerie",               "UE-GE-003", 25, 0, E3),

    # ── MKT — UE-MKT-001 : Fondamentaux du Marketing
    ("Marketing - Étude de Marché",                   "UE-MKT-001", 25, 0, E1),
    ("Marketing - Mix Marketing",                     "UE-MKT-001", 25, 0, E1),
    ("Marketing - Comportement Consommateur",         "UE-MKT-001", 20, 0, E1),

    # ── MKT — UE-MKT-002 : Marketing Digital
    ("Marketing Digital - SEO/SEA",                   "UE-MKT-002", 25, 0, E2),
    ("Marketing Digital - Réseaux Sociaux",           "UE-MKT-002", 20, 0, E2),

    # ── GC — UE-GC-001 : Mécanique des Structures
    ("Structures - Statique",                         "UE-GC-001", 30, 0, E3),
    ("Structures - Résistance des Matériaux",         "UE-GC-001", 30, 0, E3),

    # ── GC — UE-GC-002 : Matériaux de Construction
    ("Matériaux - Béton Armé",                        "UE-GC-002", 25, 0, E1),
    ("Matériaux - Acier et Bois",                     "UE-GC-002", 20, 0, E1),

    # ── GC — UE-GC-003 : Topographie et Géodésie
    ("Topographie - Levés de Terrain",                "UE-GC-003", 25, 0, E2),
    ("Topographie - SIG",                             "UE-GC-003", 20, 0, E2),

    # ── EM — UE-EM-001 : Électrotechnique
    ("Électrotechnique - Circuits AC/DC",             "UE-EM-001", 30, 0, E3),
    ("Électrotechnique - Machines Électriques",       "UE-EM-001", 30, 0, E3),
    ("Électrotechnique - Électronique de Puissance",  "UE-EM-001", 20, 0, E3),

    # ── EM — UE-EM-002 : Mécanique Appliquée
    ("Mécanique - Cinématique",                       "UE-EM-002", 25, 0, E1),
    ("Mécanique - Dynamique",                         "UE-EM-002", 25, 0, E1),

    # ── MED — UE-MED-001 : Anatomie et Physiologie
    ("Anatomie - Système Locomoteur",                 "UE-MED-001", 30, 0, E2),
    ("Anatomie - Système Cardiovasculaire",           "UE-MED-001", 30, 0, E2),
    ("Physiologie - Fonctions Vitales",               "UE-MED-001", 25, 0, E2),

    # ── MED — UE-MED-002 : Biochimie Médicale
    ("Biochimie - Métabolisme",                       "UE-MED-002", 25, 0, E3),
    ("Biochimie - Enzymologie",                       "UE-MED-002", 25, 0, E3),

    # ── PHA — UE-PHA-001 : Pharmacologie Générale
    ("Pharmacologie - Pharmacocinétique",             "UE-PHA-001", 30, 0, E1),
    ("Pharmacologie - Pharmacodynamie",               "UE-PHA-001", 25, 0, E1),
    ("Pharmacologie - Toxicologie",                   "UE-PHA-001", 20, 0, E1),

    # ── PHA — UE-PHA-002 : Chimie Pharmaceutique
    ("Chimie Pharma - Synthèse Organique",            "UE-PHA-002", 30, 0, E2),
    ("Chimie Pharma - Analyse Médicament",            "UE-PHA-002", 25, 0, E2),
    ("Chimie Pharma - Formulation",                   "UE-PHA-002", 20, 0, E3),
]

for c in cours:
    ws.append(c)

wb.save("cours_import.xlsx")
print(f"Fichier généré : cours_import.xlsx ({len(cours)} cours)")
