import { Routes } from '@angular/router';
import { ProfileComponent } from './features/profile/profile.component';
import { AdminSectorsComponent } from './features/admin-sectors/admin-sectors.component';

export const routes: Routes = [
  { path: '', component: ProfileComponent },
  { path: 'admin', component: AdminSectorsComponent },
  { path: '**', redirectTo: '' }
];
