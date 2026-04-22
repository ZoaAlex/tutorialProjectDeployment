import { Component, Input, Output, EventEmitter, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CoursClasseService, ImportEntityType } from '../../services/cours-classe.service';

@Component({
  selector: 'app-import-excel',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="d-inline-flex align-items-center gap-2">
      <!-- Bouton déclencheur -->
      <label class="btn btn-outline-success btn-sm mb-0" [class.disabled]="loading()">
        <i class="fas fa-file-excel me-1"></i>
        {{ loading() ? 'Import...' : 'Importer Excel' }}
        <input type="file" accept=".xlsx,.xls" class="d-none" (change)="onFileSelected($event)" [disabled]="loading()">
      </label>

      <!-- Feedback succès -->
      @if (successMsg()) {
        <span class="badge bg-success">
          <i class="fas fa-check me-1"></i>{{ successMsg() }}
        </span>
      }

      <!-- Feedback erreur -->
      @if (errorMsg()) {
        <span class="badge bg-danger" [title]="errorMsg()!">
          <i class="fas fa-exclamation-triangle me-1"></i>Erreur import
        </span>
      }
    </div>
  `
})
export class ImportExcelComponent {
  @Input({ required: true }) entityType!: ImportEntityType;
  @Output() imported = new EventEmitter<void>();

  loading = signal(false);
  successMsg = signal<string | null>(null);
  errorMsg = signal<string | null>(null);

  constructor(private coursClasseService: CoursClasseService) {}

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    // Reset
    input.value = '';
    this.successMsg.set(null);
    this.errorMsg.set(null);
    this.loading.set(true);

    this.coursClasseService.importerExcel(this.entityType, file).subscribe({
      next: (msg) => {
        this.successMsg.set(msg);
        this.loading.set(false);
        this.imported.emit();
        // Efface le message après 4s
        setTimeout(() => this.successMsg.set(null), 4000);
      },
      error: (err) => {
        const detail = err.error ?? err.message ?? 'Erreur inconnue';
        this.errorMsg.set(typeof detail === 'string' ? detail : JSON.stringify(detail));
        this.loading.set(false);
        setTimeout(() => this.errorMsg.set(null), 6000);
      }
    });
  }
}
