import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, RouterModule,
    MatFormFieldModule, MatInputModule,
    MatButtonModule, MatIconModule, MatProgressSpinnerModule, MatSnackBarModule,
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent {
  hidePassword = true;
  isLoading = false;

  form = new FormGroup({
    username: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', Validators.required),
  });

  constructor(
    private router: Router,
    private authService: AuthService,
    private snackBar: MatSnackBar,
  ) {}

  onSubmit(): void {
    if (this.form.invalid) return;
    this.isLoading = true;
    const { username, password } = this.form.value;
    this.authService.login({ email: username!, senha: password! }).subscribe({
      next: () => {
        this.isLoading = false;
        this.router.navigate([this.authService.isAdmin() ? '/admin/admin-panel' : '/minha-conta']);
      },
      error: () => {
        this.snackBar.open('E-mail ou senha inválidos', 'Fechar', { duration: 3000 });
        this.isLoading = false;
      },
    });
  }
}
