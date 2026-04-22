import { Component, signal, computed, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { SidebarComponent } from '../../components/sidebar/sidebar.component';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { SalleService } from '../../services/salle.service';
import { Salle, CreateSalleRequest, StatutSalle, TypeSalle, StatistiquesSalles } from '../../models/salle.model';

@Component({
    selector: 'app-salles',
    standalone: true,
    imports: [CommonModule, FormsModule, ReactiveFormsModule, SidebarComponent, NavbarComponent],
    templateUrl: './salles.component.html',
    styleUrls: ['./salles.component.scss']
})
export class SallesComponent implements OnInit {
    private salleService = inject(SalleService);
    private fb = inject(FormBuilder);

    // ── État principal ──────────────────────────────────────────
    salles = signal<Salle[]>([]);
    stats = signal<StatistiquesSalles | null>(null);
    isLoading = signal(false);
    errorMessage = signal('');
    successMessage = signal('');

    // ── Filtres ─────────────────────────────────────────────────
    searchTerm = signal('');
    filterType = signal<TypeSalle | ''>('');
    filterStatut = signal<StatutSalle | ''>('');
    viewMode = signal<'grid' | 'list'>('grid');

    // ── Modal ───────────────────────────────────────────────────
    showModal = signal(false);
    editMode = signal(false);
    currentSalle = signal<Salle | null>(null);
    isSaving = signal(false);

    // ── Enums exposés au template ───────────────────────────────
    StatutSalle = StatutSalle;
    TypeSalle = TypeSalle;
    typesSalle = Object.values(TypeSalle);
    statutsSalle = Object.values(StatutSalle);

    // ── Formulaire réactif ──────────────────────────────────────
    salleForm: FormGroup = this.fb.group({
        codeSalle:         ['', [Validators.required, Validators.maxLength(20)]],
        nom:               ['', [Validators.required, Validators.minLength(3)]],
        capacite:          [30, [Validators.required, Validators.min(1), Validators.max(1000)]],
        typeSalle:         [TypeSalle.SALLE_COURS, Validators.required],
        batiment:          [''],
        etage:             [null],
        surface:           [null],
        emplacement:       [''],
        description:       [''],
        wifiDisponible:    [true],
        climatisee:        [false],
        accessibleHandicap:[false]
    });

    // ── Computed ────────────────────────────────────────────────
    filteredSalles = computed(() => {
        let result = this.salles();
        const term = this.searchTerm().toLowerCase().trim();
        if (term) {
            result = result.filter(s =>
                s.nom.toLowerCase().includes(term) ||
                s.codeSalle.toLowerCase().includes(term) ||
                s.batiment?.toLowerCase().includes(term) ||
                s.nomEcole?.toLowerCase().includes(term) ||
                s.emplacement?.toLowerCase().includes(term)
            );
        }
        if (this.filterType()) result = result.filter(s => s.typeSalle === this.filterType());
        if (this.filterStatut()) result = result.filter(s => s.statut === this.filterStatut());
        return result;
    });

    sallesLibres = computed(() => this.salles().filter(s => s.statut === StatutSalle.LIBRE).length);
    sallesOccupees = computed(() => this.salles().filter(s => s.statut === StatutSalle.OCCUPEE).length);
    sallesMaintenance = computed(() => this.salles().filter(s =>
        s.statut === StatutSalle.MAINTENANCE || s.statut === StatutSalle.HORS_SERVICE).length);

    ngOnInit() {
        this.loadSalles();
    }

    // ── Chargement ──────────────────────────────────────────────
    loadSalles() {
        this.isLoading.set(true);
        this.errorMessage.set('');
        this.salleService.getAllSalles().subscribe({
            next: (data) => { this.salles.set(data); this.isLoading.set(false); },
            error: () => {
                this.errorMessage.set('Impossible de charger les salles. Vérifiez que le salles-service est démarré (port 8084).');
                this.isLoading.set(false);
            }
        });
    }

    // ── Modal ───────────────────────────────────────────────────
    openModal(salle?: Salle) {
        this.editMode.set(!!salle);
        this.currentSalle.set(salle ?? null);
        if (salle) {
            this.salleForm.patchValue({
                codeSalle: salle.codeSalle,
                nom: salle.nom,
                capacite: salle.capacite,
                typeSalle: salle.typeSalle,
                batiment: salle.batiment ?? '',
                etage: salle.etage ?? null,
                surface: salle.surface ?? null,
                emplacement: salle.emplacement ?? '',
                description: salle.description ?? '',
                wifiDisponible: salle.wifiDisponible ?? false,
                climatisee: salle.climatisee ?? false,
                accessibleHandicap: salle.accessibleHandicap ?? false
            });
        } else {
            this.salleForm.reset({
                capacite: 30, typeSalle: TypeSalle.SALLE_COURS,
                wifiDisponible: true, climatisee: false, accessibleHandicap: false
            });
        }
        this.showModal.set(true);
    }

    closeModal() {
        this.showModal.set(false);
        this.salleForm.markAsUntouched();
    }

    saveSalle() {
        if (this.salleForm.invalid) { this.salleForm.markAllAsTouched(); return; }
        this.isSaving.set(true);
        const v = this.salleForm.value;

        const request: CreateSalleRequest = {
            codeSalle: v.codeSalle,
            nom: v.nom,
            capacite: v.capacite,
            typeSalle: v.typeSalle,
            batiment: v.batiment || undefined,
            etage: v.etage ?? undefined,
            surface: v.surface ?? undefined,
            emplacement: v.emplacement || undefined,
            description: v.description || undefined,
            wifiDisponible: v.wifiDisponible,
            climatisee: v.climatisee,
            accessibleHandicap: v.accessibleHandicap
        };

        const op = this.editMode() && this.currentSalle()?.id
            ? this.salleService.updateSalle(this.currentSalle()!.id!, { ...this.currentSalle()!, ...request })
            : this.salleService.creerSalle(request);

        op.subscribe({
            next: () => {
                this.isSaving.set(false);
                this.closeModal();
                this.loadSalles();
                this.showSuccess(this.editMode() ? 'Salle modifiée avec succès' : 'Salle créée avec succès');
            },
            error: (err) => {
                this.isSaving.set(false);
                this.errorMessage.set(err.error?.message || 'Erreur lors de la sauvegarde');
            }
        });
    }

    changerStatut(salle: Salle, statut: StatutSalle) {
        this.salleService.changerStatutSalle(salle.id!, statut).subscribe({
            next: () => { this.loadSalles(); this.showSuccess('Statut mis à jour'); },
            error: (err) => this.errorMessage.set(err.error?.message || 'Erreur changement statut')
        });
    }

    deleteSalle(salle: Salle) {
        if (!confirm(`Supprimer la salle "${salle.nom}" ? Cette action est irréversible.`)) return;
        this.salleService.deleteSalle(salle.id!).subscribe({
            next: () => { this.loadSalles(); this.showSuccess('Salle supprimée'); },
            error: (err) => this.errorMessage.set(err.error?.message || 'Impossible de supprimer (réservations actives ?)')
        });
    }

    // ── Helpers UI ──────────────────────────────────────────────
    showSuccess(msg: string) {
        this.successMessage.set(msg);
        setTimeout(() => this.successMessage.set(''), 3500);
    }

    getStatutConfig(statut: StatutSalle): { label: string; badge: string; dot: string } {
        const map: Record<StatutSalle, { label: string; badge: string; dot: string }> = {
            [StatutSalle.LIBRE]:        { label: 'Libre',        badge: 'badge-libre',        dot: 'dot-libre' },
            [StatutSalle.OCCUPEE]:      { label: 'Occupée',      badge: 'badge-occupee',      dot: 'dot-occupee' },
            [StatutSalle.MAINTENANCE]:  { label: 'Maintenance',  badge: 'badge-maintenance',  dot: 'dot-maintenance' },
            [StatutSalle.HORS_SERVICE]: { label: 'Hors service', badge: 'badge-hors-service', dot: 'dot-hors-service' }
        };
        return map[statut] ?? { label: statut, badge: 'badge-secondary', dot: '' };
    }

    getTypeConfig(type: TypeSalle): { label: string; icon: string } {
        const map: Record<TypeSalle, { label: string; icon: string }> = {
            [TypeSalle.AMPHITHEATRE]:       { label: 'Amphithéâtre',      icon: 'fa-theater-masks' },
            [TypeSalle.SALLE_COURS]:        { label: 'Salle de cours',    icon: 'fa-chalkboard-teacher' },
            [TypeSalle.LABORATOIRE]:        { label: 'Laboratoire',       icon: 'fa-microscope' },
            [TypeSalle.SALLE_INFORMATIQUE]: { label: 'Salle informatique',icon: 'fa-laptop' },
            [TypeSalle.SALLE_CONFERENCE]:   { label: 'Salle de conférence',icon: 'fa-users' },
            [TypeSalle.BIBLIOTHEQUE]:       { label: 'Bibliothèque',      icon: 'fa-book' },
            [TypeSalle.BUREAU]:             { label: 'Bureau',            icon: 'fa-briefcase' },
            [TypeSalle.AUTRE]:              { label: 'Autre',             icon: 'fa-door-open' }
        };
        return map[type] ?? { label: type, icon: 'fa-door-open' };
    }

    hasError(field: string): boolean {
        const ctrl = this.salleForm.get(field);
        return !!(ctrl?.invalid && ctrl?.touched);
    }
}
