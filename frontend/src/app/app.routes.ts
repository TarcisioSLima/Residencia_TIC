import { Routes } from '@angular/router';
import { LoginComponent } from './pages/auth/login/login.component';
import { RegisterComponent } from './pages/auth/register/register.component';
import { RecoveryPasswordComponent } from './pages/auth/recovery-password/recovery-password.component';
import { PreferencesComponent } from './pages/preferences/cities/cities-preferences.component';
import { UserPreferencesComponent } from './pages/preferences/user/user-preferences.component';
import { AdminPanelComponent } from './pages/admin/admin-panel/admin-panel.component';
import { LogsViewComponent } from './pages/admin/logs-view/logs-view.component';
import { EnviosComponent } from './pages/admin/envios/envios.component';
import { adminGuard } from './guards/admin.guard';
import { authGuard } from './guards/auth.guard';
import { noAuthGuard } from './guards/no-auth.guard';
import { ManageProfileComponent } from './pages/manage-profile/manage-profile.component';
import { SetupCompleteComponent } from './pages/setup-complete/setup-complete.component';
import { MinhaContaComponent } from './pages/minha-conta/minha-conta.component';

export const routes: Routes = [
  {
    path: 'auth',
    children: [
      { path: 'login', component: LoginComponent, canActivate: [noAuthGuard] },
      { path: 'register', component: RegisterComponent, canActivate: [noAuthGuard] },
      { path: 'recovery-password', component: RecoveryPasswordComponent, canActivate: [noAuthGuard] },
      { path: '', redirectTo: 'login', pathMatch: 'full' }
    ]
  },
  {
    path: 'preferences',
    canActivate: [authGuard],
    children: [
      { path: 'user', component: UserPreferencesComponent },
      { path: 'cities', component: PreferencesComponent },
      { path: '', redirectTo: 'cities', pathMatch: 'full' }
    ]
  },
  {
    path: 'admin',
    children: [
      { path: 'admin-panel', component: AdminPanelComponent, canActivate: [adminGuard] },
      { path: 'envios', component: EnviosComponent, canActivate: [adminGuard] },
      { path: 'logs', component: LogsViewComponent, canActivate: [adminGuard] },
      { path: '', redirectTo: 'admin-panel', pathMatch: 'full' }
    ]
  },
  {
    path: 'manage-profile',
    canActivate: [authGuard],
    children: [
      { path: '', component: ManageProfileComponent }
    ]
  },
  { path: 'setup/complete', component: SetupCompleteComponent, canActivate: [authGuard] },
  { path: 'minha-conta', component: MinhaContaComponent, canActivate: [authGuard] },
  { path: '', redirectTo: '/auth/login', pathMatch: 'full' },
  { path: '**', redirectTo: '/auth/login' }
];
