import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { Sector, SaveSectorRequest } from '../../core/models/sector.model';

@Component({
  selector: 'app-admin-sectors',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin-sectors.component.html',
  styleUrl: './admin-sectors.component.scss'
})
export class AdminSectorsComponent implements OnInit {

  sectors: Sector[] = [];
  loading = false;
  errorMessage = '';
  successMessage = '';

  // selected sector for editing, null when creating new
  selectedSector: Sector | null = null;

  constructor(
    private fb: FormBuilder,
    private api: ApiService
  ) {}

  // @ts-ignore
  sectorForm = this.fb.group({
    name: ['', Validators.required],
    parentId: [null as number | null]
  });

  ngOnInit(): void {
    this.loadSectors();
  }

  loadSectors(): void {
    this.loading = true;
    this.errorMessage = '';
    this.api.getSectors().subscribe({
      next: sectors => {
        // sort sectors by name
        this.sectors = sectors.sort((a, b) => a.name.localeCompare(b.name));
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load sectors';
        this.loading = false;
      }
    });
  }

  startCreate(): void {
    this.selectedSector = null;
    this.sectorForm.reset({
      name: '',
      parentId: null
    });
    this.successMessage = '';
    this.errorMessage = '';
  }

  startEdit(sector: Sector): void {
    this.selectedSector = sector;
    this.sectorForm.reset({
      name: sector.name,
      parentId: sector.parentId ?? null
    });
    this.successMessage = '';
    this.errorMessage = '';
  }

  submit(): void {
    if (this.sectorForm.invalid) {
      this.sectorForm.markAllAsTouched();
      return;
    }

    const payload: SaveSectorRequest = {
      name: this.sectorForm.value.name!,
      parentId: this.sectorForm.value.parentId ?? null
    };

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    if (this.selectedSector) {
      // update
      this.api.updateSector(this.selectedSector.id, payload).subscribe({
        next: () => {
          this.successMessage = 'Sector updated';
          this.loading = false;
          this.loadSectors();
        },
        error: () => {
          this.errorMessage = 'Failed to update sector';
          this.loading = false;
        }
      });
    } else {
      // create
      this.api.createSector(payload).subscribe({
        next: () => {
          this.successMessage = 'Sector created';
          this.loading = false;
          this.loadSectors();
          this.startCreate();
        },
        error: () => {
          this.errorMessage = 'Failed to create sector';
          this.loading = false;
        }
      });
    }
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
      error: () => {
        this.errorMessage = 'Failed to delete sector';
        this.loading = false;
      }
    });
  }

  // getter for easy access to form controls
  get f() {
    return this.sectorForm.controls;
  }

  getParentName(sector: Sector): string {
    if (!sector.parentId) {
      return '—';
    }
    const parent = this.sectors.find(s => s.id === sector.parentId);
    return parent ? parent.name : '#' + sector.parentId;
  }

}
