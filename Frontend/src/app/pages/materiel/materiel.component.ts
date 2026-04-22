import { Component, signal, computed, inject, OnInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { SidebarComponent } from '../../components/sidebar/sidebar.component';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { SalleService } from '../../services/salle.service';
import { Salle } from '../../models/salle.model';
import { Materiel, CreateMaterielRequest, TypeMateriel, EtatMateriel } from '../../models/materiel.model';

@Component({
    selector: 'app-materiel',
    standalone: true,
    imports: [CommonModule, FormsModule, ReactiveFormsModule, SidebarComponent, NavbarComponent],
    templateUrl: './materiel.component.html',
    styleUrls: ['./materiel.component.scss']
})
export class MaterielComponent implements OnInit {
    private salleService = inject(SalleService);
    private fb = inject(FormBuilder);

    // ── État ────────────────────────────────────────────────────
    salles = signal<Salle[]>([]);
    materielList = signal<Materiel[]>([]);
    isLoadingSalles = signal(false);
    isLoadingMateriel = signal(false);
    selectedSalle = signal<Salle | null>(null);
    errorMessage = signal('');
    successMessage = signal('');

    // ── Filtres ─────────────────────────────────────────────────
    searchTerm = signal('');
    filterType = signal<TypeMateriel | ''>('');
    filterEtat = signal<EtatMateriel | ''>('');
    searchSalle = signal('');

    // ── Modal ───────────────────────────────────────────────────
    showModal = signal(false);
    selectedMateriel = signal<Materiel | null>(null);
    isSaving = signal(false);

    // ── Enums ───────────────────────────────────────────────────
    EtatMateriel = EtatMateriel;
    TypeMateriel = TypeMateriel;
    typesMaterielList = Object.values(TypeMateriel);
    etatsMaterielList = Object.values(EtatMateriel);

    // ── Formulaire ──────────────────────────────────────────────
    materielForm: FormGroup = this.fb.group({
        nom:                    ['', [Validators.required, Validators.minLength(2)]],
        type:                   ['', Validators.required],
        description:            [''],
        quantite:               [1, [Validators.required, Validators.min(1)]],
        quantiteFonctionnelle:  [null],
        marque:                 [''],
        modele:                 [''],
        numeroSerie:            [''],
        dateAcquisition:        [''],
        dateProchaineMaintenance: [''],
        observations:           [''],
        salleId:                [null, Validators.required]
    });

    // ── Computed ────────────────────────────────────────────────
    filteredSalles = computed(() => {
        const term = this.searchSalle().toLowerCase().trim();
        if (!term) return this.salles();
        return this.salles().filter(s =>
            s.nom.toLowerCase().includes(term) ||
            s.codeSalle.toLowerCase().includes(term) ||
            s.nomEcole?.toLowerCase().includes(term)
        );
    });

    filteredMateriel = computed(() => {
        let result = this.materielList();
        const term = this.searchTerm().toLowerCase().trim();
        if (term) result = result.filter(m =>
            m.nom.toLowerCase().includes(term) ||
            m.marque?.toLowerCase().includes(term) ||
            m.modele?.toLowerCase().includes(term) ||
            m.numeroSerie?.toLowerCase().includes(term)
        );
        if (this.filterType()) result = result.filter(m => m.type === this.filterType());
        if (this.filterEtat()) result = result.filter(m => m.etat === this.filterEtat());
        return result;
    });

    materielEnPanne = computed(() => this.materielList().filter(m => m.etat === EtatMateriel.EN_PANNE).length);
    materielMaintenance = computed(() => this.materielList().filter(m => m.etat === EtatMateriel.EN_MAINTENANCE).length);
    materielMaintenanceRequise = computed(() => this.materielList().filter(m => m.maintenanceRequise).length);

    ngOnInit() { this.chargerSalles(); }

    // ── Chargement ──────────────────────────────────────────────
    chargerSalles() {
        this.isLoadingSalles.set(true);
        this.salleService.getAllSalles().subscribe({
            next: (data) => { this.salles.set(data); this.isLoadingSalles.set(false); },
            error: () => { this.errorMessage.set('Impossible de charger les salles.'); this.isLoadingSalles.set(false); }
        });
    }

    selectSalle(salle: Salle) {
        this.selectedSalle.set(salle);
        this.searchTerm.set(''); this.filterType.set(''); this.filterEtat.set('');
        this.chargerMateriel(salle.id!);
    }

    deselectionnerSalle() {
        this.selectedSalle.set(null);
        this.materielList.set([]);
    }

    chargerMateriel(salleId: number) {
        this.isLoadingMateriel.set(true);
        this.salleService.getMaterielParSalle(salleId).subscribe({
            next: (data) => { this.materielList.set(data); this.isLoadingMateriel.set(false); },
            error: () => { this.errorMessage.set('Impossible de charger le matériel.'); this.isLoadingMateriel.set(false); }
        });
    }

    // ── Modal ───────────────────────────────────────────────────
    openModal(materiel?: Materiel) {
        this.selectedMateriel.set(materiel ?? null);
        if (materiel) {
            this.materielForm.patchValue({
                nom: materiel.nom, type: materiel.type,
                description: materiel.description ?? '',
                quantite: materiel.quantite,
                quantiteFonctionnelle: materiel.quantiteFonctionnelle ?? null,
                marque: materiel.marque ?? '', modele: materiel.modele ?? '',
                numeroSerie: materiel.numeroSerie ?? '',
                dateAcquisition: materiel.dateAcquisition ?? '',
                dateProchaineMaintenance: materiel.dateProchaineMaintenance ?? '',
                observations: materiel.observations ?? '',
                salleId: materiel.salleId
            });
        } else {
            this.materielForm.reset({ quantite: 1, salleId: this.selectedSalle()!.id });
        }
        this.showModal.set(true);
    }

    closeModal() {
        this.showModal.set(false);
        this.materielForm.markAsUntouched();
    }

    /** Convertit une date "yyyy-MM-dd" en "yyyy-MM-ddT00:00:00" attendu par LocalDateTime */
    private toLocalDateTime(date: string | null | undefined): string | undefined {
        if (!date) return undefined;
        return date.includes('T') ? date : `${date}T00:00:00`;
    }

    saveMateriel() {
        if (this.materielForm.invalid) { this.materielForm.markAllAsTouched(); return; }
        this.isSaving.set(true);
        const v = this.materielForm.value;
        const selected = this.selectedMateriel();

        if (selected) {
            const updated: Materiel = {
                ...selected,
                ...v,
                dateAcquisition: this.toLocalDateTime(v.dateAcquisition),
                dateProchaineMaintenance: this.toLocalDateTime(v.dateProchaineMaintenance)
            };
            this.salleService.updateMateriel(selected.id, updated).subscribe({
                next: () => { this.afterSave('Matériel mis à jour'); },
                error: (err) => { this.isSaving.set(false); this.errorMessage.set(err.error?.message || 'Erreur mise à jour'); }
            });
        } else {
            const request: CreateMaterielRequest = {
                nom: v.nom, type: v.type,
                description: v.description || undefined,
                quantite: v.quantite,
                quantiteFonctionnelle: v.quantiteFonctionnelle || undefined,
                marque: v.marque || undefined, modele: v.modele || undefined,
                numeroSerie: v.numeroSerie || undefined,
                dateAcquisition: this.toLocalDateTime(v.dateAcquisition),
                dateProchaineMaintenance: this.toLocalDateTime(v.dateProchaineMaintenance),
                observations: v.observations || undefined,
                salleId: v.salleId
            };
            this.salleService.creerMateriel(request).subscribe({
                next: () => { this.afterSave('Matériel ajouté'); },
                error: (err) => { this.isSaving.set(false); this.errorMessage.set(err.error?.message || 'Erreur création'); }
            });
        }
    }

    afterSave(msg: string) {
        this.isSaving.set(false);
        this.closeModal();
        this.chargerMateriel(this.selectedSalle()!.id!);
        this.showSuccess(msg);
    }

    changerEtat(materiel: Materiel, etat: EtatMateriel) {
        this.salleService.changerEtatMateriel(materiel.id, etat).subscribe({
            next: () => { this.chargerMateriel(this.selectedSalle()!.id!); this.showSuccess('État mis à jour'); },
            error: (err) => this.errorMessage.set(err.error?.message || 'Erreur changement état')
        });
    }

    deleteMateriel(materiel: Materiel) {
        if (!confirm(`Supprimer "${materiel.nom}" ?`)) return;
        this.salleService.deleteMateriel(materiel.id).subscribe({
            next: () => { this.chargerMateriel(this.selectedSalle()!.id!); this.showSuccess('Matériel supprimé'); },
            error: (err) => this.errorMessage.set(err.error?.message || 'Erreur suppression')
        });
    }

    // ── Helpers UI ──────────────────────────────────────────────
    showSuccess(msg: string) {
        this.successMessage.set(msg);
        setTimeout(() => this.successMessage.set(''), 3500);
    }

    getEtatConfig(etat: EtatMateriel): { label: string; cls: string; icon: string } {
        const map: Record<EtatMateriel, { label: string; cls: string; icon: string }> = {
            [EtatMateriel.FONCTIONNEL]:    { label: 'Fonctionnel',    cls: 'etat-fonctionnel',    icon: 'fa-check-circle' },
            [EtatMateriel.EN_PANNE]:       { label: 'En panne',       cls: 'etat-panne',          icon: 'fa-times-circle' },
            [EtatMateriel.EN_MAINTENANCE]: { label: 'Maintenance',    cls: 'etat-maintenance',    icon: 'fa-tools' }
        };
        return map[etat] ?? { label: etat, cls: '', icon: 'fa-circle' };
    }

    getTypeConfig(type: TypeMateriel): { label: string; icon: string } {
        const map: Record<TypeMateriel, { label: string; icon: string }> = {
            [TypeMateriel.PROJECTEUR]:         { label: 'Projecteur',         icon: 'fa-video' },
            [TypeMateriel.ORDINATEUR]:         { label: 'Ordinateur',         icon: 'fa-laptop' },
            [TypeMateriel.TABLEAU_INTERACTIF]: { label: 'Tableau interactif', icon: 'fa-chalkboard' },
            [TypeMateriel.MICRO]:              { label: 'Microphone',         icon: 'fa-microphone' },
            [TypeMateriel.HAUT_PARLEUR]:       { label: 'Haut-parleur',       icon: 'fa-volume-up' },
            [TypeMateriel.CAMERA]:             { label: 'Caméra',             icon: 'fa-camera' },
            [TypeMateriel.ECRAN]:              { label: 'Écran',              icon: 'fa-desktop' },
            [TypeMateriel.CLIMATISATION]:      { label: 'Climatisation',      icon: 'fa-snowflake' },
            [TypeMateriel.WIFI]:               { label: 'WiFi',               icon: 'fa-wifi' },
            [TypeMateriel.AUTRE]:              { label: 'Autre',              icon: 'fa-box' }
        };
        return map[type] ?? { label: type, icon: 'fa-box' };
    }

    getQuantiteFonctionnelle(m: Materiel): number {
        return m.quantiteFonctionnelle ?? m.quantite;
    }

    hasError(field: string): boolean {
        const ctrl = this.materielForm.get(field);
        return !!(ctrl?.invalid && ctrl?.touched);
    }
}
