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
import {Sector, SectorView} from '../../core/models/sector.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss',
})

export class ProfileComponent implements OnInit {
  sectors: SectorView[] = [];
  form: FormGroup;
  loading = false;
  message = '';
  errorMessage = '';

  constructor(private fb: FormBuilder, private api: ApiService) {
    this.form = this.fb.group({
      name: ['', Validators.required],
      sectorIds: [[], Validators.required],
    });
  }

  ngOnInit(): void {
    this.api.getSectors().subscribe({
      next: (sectors) => {
        this.sectors = this.buildHierarchicalList(sectors);
      },
      error: (err) => {
        console.error('Failed to load sectors', err);
        this.errorMessage = 'Failed to load sectors';
      },
    });
  }

  private buildHierarchicalList(sectors: Sector[]): SectorView[] {
    const byParent = new Map<number | null, Sector[]>();

    for (const s of sectors) {
      const pid = s.parentId ?? null;
      if (!byParent.has(pid)) {
        byParent.set(pid, []);
      }
      byParent.get(pid)!.push(s);
    }

    const result: SectorView[] = [];

    const dfs = (parentId: number | null, level: number) => {
      const children = byParent.get(parentId) || [];
      children.sort((a, b) => a.name.localeCompare(b.name));

      for (const child of children) {
        const displayName =
          '\u00A0\u00A0\u00A0\u00A0'.repeat(level) + child.name;

        result.push({
          ...child,
          level,
          displayName,
        });

        dfs(child.id, level + 1);
      }
    };

    dfs(null, 0);

    return result;
  }

  get nameCtrl(): FormControl {
    return this.form.get('name') as FormControl;
  }

  get sectorIdsCtrl(): FormControl {
    return this.form.get('sectorIds') as FormControl;
  }

  save(): void {
    if (this.form.invalid) {
      return;
    }

    const payload = {
      username: this.nameCtrl.value,
      sectorIds: this.sectorIdsCtrl.value as number[],
    };

    this.loading = true;
    this.errorMessage = '';
    this.message = '';

    this.api.saveUserSelection(payload).subscribe({
      next: () => {
        this.loading = false;
        this.message = 'Selection saved';
      },
      error: (err: any) => {
        this.loading = false;
        console.error('Failed to save selection', err);
        this.errorMessage = 'Failed to save selection';
      },
    });
  }


  restore(): void {
    const username = this.nameCtrl.value?.trim();
    if (!username) {
      this.errorMessage = 'Please enter a name to restore selection';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.message = '';

    this.api.getUserSelection(username).subscribe({
      next: (data) => {
        this.loading = false;
        // data: { username: string; sectorIds: number[] }
        this.form.patchValue({
          name: data.username,
          sectorIds: data.sectorIds,
        });
        this.message = 'Selection restored';
      },
      error: (err) => {
        this.loading = false;
        console.error('Failed to restore selection', err);
        this.errorMessage = 'Failed to restore selection';
      },
    });
  }

}
