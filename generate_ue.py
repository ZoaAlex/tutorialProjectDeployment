import openpyxl

wb = openpyxl.Workbook()
ws = wb.active
ws.title = "Feuil1"

# En-tête (ligne 1, ignorée par le backend)
ws.append(["CodeUe", "Intitule", "ClasseCode"])

# 20 UE réparties sur les 8 filières (GL, RS, GE, MKT, GC, EM, MED, PHA)
# Chaque UE est assignée à une classe L1 (les cours s'appliquent généralement par niveau)
ues = [
    # GL — Génie Logiciel
    ("UE-GL-001", "Algorithmique et Structures de Données",   "ESTIC-GL-L1"),
    ("UE-GL-002", "Programmation Orientée Objet",             "ESTIC-GL-L1"),
    ("UE-GL-003", "Bases de Données",                         "ESTIC-GL-L2"),
    # RS — Réseaux et Systèmes
    ("UE-RS-001", "Architecture des Réseaux",                 "ESTIC-RS-L1"),
    ("UE-RS-002", "Administration Systèmes Linux",            "ESTIC-RS-L1"),
    ("UE-RS-003", "Sécurité Informatique",                    "ESTIC-RS-L2"),
    # GE — Gestion Entreprises
    ("UE-GE-001", "Comptabilité Générale",                    "ESGC-GE-L1"),
    ("UE-GE-002", "Management des Organisations",             "ESGC-GE-L1"),
    ("UE-GE-003", "Finance d'Entreprise",                     "ESGC-GE-L2"),
    # MKT — Marketing
    ("UE-MKT-001", "Fondamentaux du Marketing",               "ESGC-MKT-L1"),
    ("UE-MKT-002", "Marketing Digital",                       "ESGC-MKT-L2"),
    # GC — Génie Civil
    ("UE-GC-001", "Mécanique des Structures",                 "ESIG-GC-L1"),
    ("UE-GC-002", "Matériaux de Construction",                "ESIG-GC-L1"),
    ("UE-GC-003", "Topographie et Géodésie",                  "ESIG-GC-L2"),
    # EM — Électromécanique
    ("UE-EM-001", "Électrotechnique",                         "ESIG-EM-L1"),
    ("UE-EM-002", "Mécanique Appliquée",                      "ESIG-EM-L2"),
    # MED — Sciences Médicales
    ("UE-MED-001", "Anatomie et Physiologie",                 "ESSP-MED-L1"),
    ("UE-MED-002", "Biochimie Médicale",                      "ESSP-MED-L2"),
    # PHA — Pharmacie
    ("UE-PHA-001", "Pharmacologie Générale",                  "ESSP-PHA-L1"),
    ("UE-PHA-002", "Chimie Pharmaceutique",                   "ESSP-PHA-L2"),
]

for ue in ues:
    ws.append(ue)

wb.save("ue_import.xlsx")
print(f"Fichier généré : ue_import.xlsx ({len(ues)} UE)")
