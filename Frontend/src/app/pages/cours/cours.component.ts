import { Component, signal, computed, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SidebarComponent } from '../../components/sidebar/sidebar.component';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { ImportExcelComponent } from '../../components/import-excel/import-excel.component';
import { CrudActionsComponent } from '../../components/crud-actions/crud-actions.component';
import { RowActionsComponent } from '../../components/row-actions/row-actions.component';
import { CoursClasseService } from '../../services/cours-classe.service';
import { SalleService } from '../../services/salle.service';
import { AuthService } from '../../services/auth.service';
import { Cours, StatutCours, TypeCours } from '../../models/cours.model';
import { Classe } from '../../models/classe.model';
import { Ecole } from '../../models/ecole.model';
import { Filiere } from '../../models/filiere.model';
import { Ue } from '../../models/ue.model';
import { Etudiant } from '../../models/etudiant.model';
import { GroupeEtudiant } from '../../models/groupe-etudiant.model';
import { resultat } from '../../models/result.model';

@Component({
  selector: 'app-cours',
  standalone: true,
  imports: [CommonModule, FormsModule, SidebarComponent, NavbarComponent, ImportExcelComponent],
  templateUrl: './cours.component.html'
})



export class CoursComponent implements OnInit {
  private coursClasseService = inject(CoursClasseService);
  private salleService = inject(SalleService);
  authService = inject(AuthService);

  cours = signal<Cours[]>([]);
  classes = signal<Classe[]>([]);
  ecoles = signal<Ecole[]>([]);
  filieres = signal<Filiere[]>([]);
  ues = signal<Ue[]>([]);
  salles = signal<any[]>([]);
  etudiants = signal<Etudiant[]>([]);
  groupes = signal<GroupeEtudiant[]>([]);
  result = signal<resultat[]>([]);

  // ici on essaye de récupérer les UE correspondant à un cours bien précis.
  uesForCours = signal<Ue[]>([]);

  searchTerm = signal('');
  filterType = signal('');

  // Modals state
  showModal = signal(false); // For Cours
  showModalClasse = signal(false);
  showModalEcole = signal(false);
  showModalUe = signal(false);
  showModalFiliere = signal(false);
  showModalEtudiant = signal(false);
  showModalGroupe = signal(false);
  showModalEtudiantsGroupe = signal(false);
  groupeSelectionne = signal<GroupeEtudiant | null>(null);
  etudiantsDuGroupe = signal<Etudiant[]>([]);

  editMode = signal(false);
  activeTab = signal('cours');


  filteredCours = computed(() => {
    const term = this.searchTerm().toLowerCase();
    const type = this.filterType();

    // 1. Filter courses
    let filteredResults = this.cours();
    if (term) {
      filteredResults = filteredResults.filter(c =>
        c.nom?.toLowerCase().includes(term) ||
        c.id.toString().includes(term)
      );
    }
    if (type) {
      filteredResults = filteredResults.filter(c => c.type === type);
    }

    // 2. Map courses to their corresponding UE to create the 'resultat' structure
    return filteredResults.map(c => {
      const ue = this.ues().find(u => u.id === c.ueId);
      return {
        r1: c,
        r2: ue || { id: 0, codeUe: 'N/A', intitule: 'Inconnu' } as Ue
      } as resultat;
    });
  });

  filteredClasses = computed(() => {
    return this.classes().map(cl => {
      const filiere = this.filieres().find(f => f.id === cl.filiereId);
      return {
        r1: cl,
        r2: filiere || { id: 0, nom: 'Inconnue', code: 'N/A' } as Filiere
      };
    });
  });

  currentCours = signal<Cours>(this.getEmptyCours());
  currentClasse = signal<Classe>(this.getEmptyClasse());
  currentEcole = signal<Ecole>(this.getEmptyEcole());
  currentUe = signal<Ue>(this.getEmptyUe());
  currentFiliere = signal<Filiere>(this.getEmptyFiliere());
  currentEtudiant = signal<Etudiant>(this.getEmptyEtudiant());
  currentGroupe = signal<GroupeEtudiant>(this.getEmptyGroupe());

  ngOnInit() {
    this.loadAll();
  }

  loadAll() {
    this.coursClasseService.getAllCours().subscribe(data => this.cours.set(data));
    this.coursClasseService.getAllClasses().subscribe(data => this.classes.set(data));
    this.coursClasseService.getAllEcoles().subscribe(data => this.ecoles.set(data));
    this.coursClasseService.getAllFilieres().subscribe(data => this.filieres.set(data));
    this.coursClasseService.getAllUes().subscribe(data => this.ues.set(data));
    this.salleService.getAllSalles().subscribe(data => this.salles.set(data));
    this.coursClasseService.getAllEtudiants().subscribe(data => this.etudiants.set(data));
    this.coursClasseService.getAllGroupes().subscribe(data => this.groupes.set(data));
  }

  getUeCode(ueId?: number): string {
    if (!ueId) return 'N/A';
    const ue = this.ues().find(u => u.id === ueId);
    return ue ? ue.codeUe : 'Chargement...';
  }

  getClasseNom(classeId?: number): string {
    if (!classeId) return 'N/A';
    const cl = this.classes().find(c => c.id === classeId);
    return cl ? cl.nom : 'N/A';
  }


  getEmptyCours(): Cours {
    return {
      nom: '',
      id: 0,
      statutCours: StatutCours.EN_ATTENTE,
      classeId: 0,
      codeClasse: '',
      ueId: 0,
      nbreheurefait: 0,
      volumeHoraire: 0,
      enseignantEmail: '',
      effectifClasse: 0
    };
  }

  getEmptyClasse(): Classe {
    return { id: 0, nom: '', code: '', effectif: 0, ecoleId: 0, filiereId: 0 };
  }

  getEmptyEcole(): Ecole {
    return { id: 0, nom: '', code: '', description: '' };
  }

  getEmptyUe(): Ue {
    return { id: 0, codeUe: '', intitule: '', coursIds: [], classeId: 0 };
  }

  getEmptyFiliere(): Filiere {
    return { id: 0, nom: '', code: '', ecoleId: 0, classes: [] };
  }

  getEmptyEtudiant(): Etudiant {
    return { matricule: '', nom: '', prenom: '', sex: 'MASCULINE' as any, classeId: 0, ecoleId: 0 };
  }

  getEmptyGroupe(): GroupeEtudiant {
    return { nom: '', description: '', effectif: 0 };
  }

  openModal(cours?: Cours): void {
    this.editMode.set(!!cours);
    this.currentCours.set(cours ? { ...cours } : this.getEmptyCours());
    this.showModal.set(true);
  }

  closeModal(): void { this.showModal.set(false); }

  saveCours(): void {
    const c = this.currentCours();
    const handleSuccess = (savedCours: Cours) => {
      this.loadAll();
      this.closeModal();
      console.log(savedCours);
    };

    if (this.editMode()) {
      this.coursClasseService.updateCours(c.id!, c).subscribe({
        next: (res) => handleSuccess(res),
        error: (err) => alert('Erreur lors de la mise à jour du cours')
      });
    } else {
      this.coursClasseService.createCours(c).subscribe({
        next: (res) => handleSuccess(res),
        error: (err) => alert('Erreur lors de la création du cours')
      });
    }
  }

  deleteCours(id: number): void {
    if (confirm('Supprimer ce cours?')) {
      this.coursClasseService.deleteCours(id).subscribe({
        next: () => this.loadAll(),
        error: (err) => {
          console.error('Erreur suppression cours', err);
          alert('Impossible de supprimer le cours. Il est peut-être lié à d\'autres enregistrements.');
        }
      });
    }
  }

  // Generic Modal Logic (Simplified for readability)
  openModalEntity(type: 'classe' | 'ecole' | 'ue' | 'filiere' | 'etudiant' | 'groupe', item?: any): void {
    this.editMode.set(!!item);
    if (type === 'classe') {
      this.currentClasse.set(item ? { ...item } : this.getEmptyClasse());
      this.showModalClasse.set(true);
    } else if (type === 'ecole') {
      this.currentEcole.set(item ? { ...item } : this.getEmptyEcole());
      this.showModalEcole.set(true);
    } else if (type === 'ue') {
      this.currentUe.set(item ? { ...item } : this.getEmptyUe());
      this.showModalUe.set(true);
    } else if (type === 'filiere') {
      this.currentFiliere.set(item ? { ...item } : this.getEmptyFiliere());
      this.showModalFiliere.set(true);
    } else if (type === 'etudiant') {
      this.currentEtudiant.set(item ? { ...item } : this.getEmptyEtudiant());
      this.showModalEtudiant.set(true);
    } else if (type === 'groupe') {
      this.currentGroupe.set(item ? { ...item } : this.getEmptyGroupe());
      this.showModalGroupe.set(true);
    }
  }

  closeModalEntity(type: string): void {
    if (type === 'classe') this.showModalClasse.set(false);
    else if (type === 'ecole') this.showModalEcole.set(false);
    else if (type === 'ue') this.showModalUe.set(false);
    else if (type === 'filiere') this.showModalFiliere.set(false);
    else if (type === 'etudiant') this.showModalEtudiant.set(false);
    else if (type === 'groupe') this.showModalGroupe.set(false);
  }

  saveClasse(): void {
    const item = this.currentClasse();
    if (this.editMode()) {
      this.coursClasseService.updateClasse(item.id!, item).subscribe(() => { this.loadAll(); this.closeModalEntity('classe'); });
    } else {
      this.coursClasseService.createClasse(item).subscribe(() => { this.loadAll(); this.closeModalEntity('classe'); });
    }
  }

  deleteClasse(id: number): void {
    if (confirm('Supprimer cette classe?')) {
      this.coursClasseService.deleteClasse(id).subscribe({
        next: () => this.loadAll(),
        error: (err) => {
          console.error('Erreur suppression classe', err);
          alert('Impossible de supprimer la classe. Elle contient peut-être encore des étudiants ou des cours.');
        }
      });
    }
  }

  saveEcole(): void {
    const item = this.currentEcole();
    if (this.editMode()) {
      this.coursClasseService.updateEcole(item.id!, item).subscribe(() => { this.loadAll(); this.closeModalEntity('ecole'); });
    } else {
      this.coursClasseService.createEcole(item).subscribe(() => { this.loadAll(); this.closeModalEntity('ecole'); });
    }
  }

  deleteEcole(id: number): void {
    if (confirm('Supprimer cette école?')) {
      this.coursClasseService.deleteEcole(id).subscribe({
        next: () => this.loadAll(),
        error: (err) => {
          console.error('Erreur suppression école', err);
          alert('Impossible de supprimer l\'école. Elle est peut-être liée à d\'autres entités (étudiants, filières, etc.).');
        }
      });
    }
  }

  saveUe(): void {
    const item = this.currentUe();
    if (this.editMode()) {
      this.coursClasseService.updateUe(item.id!, item).subscribe(() => { this.loadAll(); this.closeModalEntity('ue'); });
    } else {
      this.coursClasseService.createUe(item).subscribe(() => { this.loadAll(); this.closeModalEntity('ue'); });
    }
  }

  deleteUe(id: number): void {
    if (confirm('Supprimer cette UE?')) {
      this.coursClasseService.deleteUe(id).subscribe({
        next: () => this.loadAll(),
        error: (err) => {
          console.error('Erreur suppression UE', err);
          alert('Impossible de supprimer l\'UE. Elle est peut-être encore associée à des cours.');
        }
      });
    }
  }

  saveFiliere(): void {
    const item = this.currentFiliere();
    if (this.editMode()) {
      this.coursClasseService.updateFiliere(item.id!, item).subscribe(() => { this.loadAll(); this.closeModalEntity('filiere'); });
    } else {
      this.coursClasseService.createFiliere(item).subscribe(() => { this.loadAll(); this.closeModalEntity('filiere'); });
    }
  }

  deleteFiliere(id: number): void {
    if (confirm('Supprimer cette filière?')) {
      this.coursClasseService.deleteFiliere(id).subscribe({
        next: () => this.loadAll(),
        error: (err) => {
          console.error('Erreur suppression filière', err);
          alert('Impossible de supprimer la filière. Elle contient peut-être encore des classes.');
        }
      });
    }
  }

  saveEtudiant(): void {
    const item = this.currentEtudiant();
    if (this.editMode()) {
      this.coursClasseService.updateEtudiant(item.id!, item).subscribe(() => { this.loadAll(); this.closeModalEntity('etudiant'); });
    } else {
      this.coursClasseService.createEtudiant(item).subscribe(() => { this.loadAll(); this.closeModalEntity('etudiant'); });
    }
  }

  deleteEtudiant(id: number): void {
    if (confirm('Supprimer cet étudiant?')) {
      this.coursClasseService.deleteEtudiant(id).subscribe({
        next: () => this.loadAll(),
        error: (err) => {
          console.error('Erreur suppression étudiant', err);
          alert('Impossible de supprimer l\'étudiant.');
        }
      });
    }
  }

  saveGroupe(): void {
    const item = this.currentGroupe();
    if (this.editMode()) {
      this.coursClasseService.updateGroupe(item.id!, item).subscribe(() => { this.loadAll(); this.closeModalEntity('groupe'); });
    } else {
      this.coursClasseService.createGroupe(item).subscribe(() => { this.loadAll(); this.closeModalEntity('groupe'); });
    }
  }

  deleteGroupe(id: number): void {
    if (confirm('Supprimer ce groupe?')) {
      this.coursClasseService.deleteGroupe(id).subscribe({
        next: () => this.loadAll(),
        error: (err) => {
          console.error('Erreur suppression groupe', err);
          alert('Impossible de supprimer le groupe.');
        }
      });
    }
  }

  voirEtudiantsGroupe(groupe: GroupeEtudiant): void {
    this.groupeSelectionne.set(groupe);
    const ids: number[] = (groupe as any).etudiantIds ?? [];
    this.etudiantsDuGroupe.set(this.etudiants().filter(e => ids.includes(e.id!)));
    this.showModalEtudiantsGroupe.set(true);
  }
}
