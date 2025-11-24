import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
  FormControl,
} from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { Sector } from '../../core/models/sector.model';

@Component({
  selector: 'app-admin-sectors',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin-sectors.component.html',
  styleUrl: './admin-sectors.component.scss',
})
export class AdminSectorsComponent implements OnInit {
  sectors: Sector[] = [];
  loading = false;
  errorMessage = '';
  successMessage = '';
  selectedSector: Sector | null = null;
  showFormPanel = false;

  form: FormGroup;

  constructor(private fb: FormBuilder, private api: ApiService) {
    this.form = this.fb.group({
      name: ['', Validators.required],
      parentId: [null],
    });
  }

  ngOnInit(): void {
    this.loadSectors();
  }

  get nameCtrl(): FormControl {
    return this.form.get('name') as FormControl;
  }

  get parentIdCtrl(): FormControl {
    return this.form.get('parentId') as FormControl;
  }

  loadSectors(): void {
    this.loading = true;
    this.errorMessage = '';
    this.api.getSectors().subscribe({
      next: (sectors) => {
        this.sectors = sectors.sort((a, b) => a.name.localeCompare(b.name));
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load sectors', err);
        this.errorMessage = 'Failed to load sectors';
        this.loading = false;
      },
    });
  }

  startCreate(): void {
    this.selectedSector = null;
    this.form.reset({
      name: '',
      parentId: null,
    });
    this.showFormPanel = true;
    this.successMessage = '';
    this.errorMessage = '';
  }

  startEdit(sector: Sector): void {
    this.selectedSector = sector;
    this.form.reset({
      name: sector.name,
      parentId: sector.parentId ?? null,
    });
    this.showFormPanel = true;
    this.successMessage = '';
    this.errorMessage = '';
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const payload = {
      name: this.nameCtrl.value,
      parentId: this.parentIdCtrl.value ?? null,
    };

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    if (this.selectedSector) {
      this.api.updateSector(this.selectedSector.id, payload).subscribe({
        next: () => {
          this.successMessage = 'Sector updated';
          this.loading = false;
          this.loadSectors();
        },
        error: (err) => {
          console.error('Failed to update sector', err);
          this.errorMessage = 'Failed to update sector';
          this.loading = false;
        },
      });
    } else {
      this.api.createSector(payload).subscribe({
        next: () => {
          this.successMessage = 'Sector created';
          this.loading = false;
          this.loadSectors();
          this.startCreate();
        },
        error: (err) => {
          console.error('Failed to create sector', err);
          this.errorMessage = 'Failed to create sector';
          this.loading = false;
        },
      });
    }
    this.showFormPanel = false;
  }
  cancelForm(): void {
    this.form.reset({
      name: '',
      parentId: null,
    });
    this.selectedSector = null;
    this.showFormPanel = false;
  }

  delete(sector: Sector): void {
    if (!confirm(`Delete sector "${sector.name}"?`)) {
      return;
    }
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.api.deleteSector(sector.id).subscribe({
      next: () => {
        this.successMessage = 'Sector deleted';
        this.loading = false;
        this.loadSectors();
        if (this.selectedSector?.id === sector.id) {
          this.startCreate();
        }
      },
      error: (err) => {
        console.error('Failed to delete sector', err);
        this.errorMessage = 'Failed to delete sector';
        this.loading = false;
      },
    });
  }


  getParentName(sector: Sector): string {
    if (!sector.parentId) {
      return '—';
    }
    const parent = this.sectors.find((s) => s.id === sector.parentId);
    return parent ? parent.name : '#' + sector.parentId;
  }
}
