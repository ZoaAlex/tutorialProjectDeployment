import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ImportExcelComponent } from '../import-excel/import-excel.component';
import { ImportEntityType } from '../../services/cours-classe.service';

@Component({
  selector: 'app-crud-actions',
  standalone: true,
  imports: [CommonModule, ImportExcelComponent],
  template: `
    <div class="d-flex justify-content-between align-items-center flex-wrap gap-2">
      @if (excelFormat) {
        <small class="text-muted">
          <i class="fas fa-info-circle me-1"></i>
          Format Excel : <code>{{ excelFormat }}</code>
        </small>
      } @else {
        <span></span>
      }
      <div class="d-flex gap-2 align-items-center">
        @if (importType) {
          <app-import-excel [entityType]="importType" (imported)="imported.emit()"></app-import-excel>
        }
        @if (createLabel) {
          <button class="btn btn-primary" (click)="create.emit()">
            <i class="fas fa-plus me-2"></i>{{ createLabel }}
          </button>
        }
      </div>
    </div>
  `
})
export class CrudActionsComponent {
  @Input() createLabel?: string;
  @Input() importType?: ImportEntityType;
  @Input() excelFormat?: string;
  @Output() create = new EventEmitter<void>();
  @Output() imported = new EventEmitter<void>();
}
