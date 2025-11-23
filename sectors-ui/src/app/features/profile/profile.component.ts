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
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss',
})
export class ProfileComponent implements OnInit {
  sectors: Sector[] = [];
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
      next: (sectors) => (this.sectors = sectors),
      error: (err) => {
        console.error('Failed to load sectors', err);
        this.errorMessage = 'Failed to load sectors';
      },
    });
  }

  get nameCtrl(): FormControl {
    return this.form.get('name') as FormControl;
  }

  get sectorIdsCtrl(): FormControl {
    return this.form.get('sectorIds') as FormControl;
  }

  save(): void {
    console.log('save', this.form.value);
  }

  restore(): void {
    console.log('restore', this.nameCtrl.value);
  }
}
