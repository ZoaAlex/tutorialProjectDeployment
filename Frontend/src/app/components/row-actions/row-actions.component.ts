import { Component, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-row-actions',
  standalone: true,
  template: `
    <button class="btn btn-sm btn-outline-primary me-2" (click)="edit.emit()">
      <i class="fas fa-edit"></i>
    </button>
    <button class="btn btn-sm btn-outline-danger" (click)="delete.emit()">
      <i class="fas fa-trash"></i>
    </button>
  `
})
export class RowActionsComponent {
  @Output() edit = new EventEmitter<void>();
  @Output() delete = new EventEmitter<void>();
}
