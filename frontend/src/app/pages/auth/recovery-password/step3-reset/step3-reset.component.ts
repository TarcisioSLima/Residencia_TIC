import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  ReactiveFormsModule,
  FormGroup,
  FormControl,
  Validators,
  AbstractControl,
} from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../../services/auth.service';
import { RecoveryStateService } from '../../../../services/recovery-state.service';

@Component({
  selector: 'app-step3-reset',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
  ],
  templateUrl: './step3-reset.component.html',
  styleUrls: ['./step3-reset.component.scss'],
})
export class Step3ResetComponent {
  form = new FormGroup(
    {
      newPassword: new FormControl('', [Validators.required, Validators.minLength(8)]),
      confirmPassword: new FormControl('', Validators.required),
    },
    { validators: passwordsMatchValidator }
  );

  isLoading = false;
  success = false;

  constructor(
    private authService: AuthService,
    private recoveryState: RecoveryStateService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    const resetToken = this.recoveryState.getResetToken();
    const newPassword = this.form.value.newPassword!;

    this.authService.resetPassword(resetToken, newPassword).subscribe({
      next: () => {
        this.recoveryState.clear();
        this.isLoading = false;
        this.success = true;
        setTimeout(() => this.router.navigate(['/auth/login']), 3000);
      },
      error: (err) => {
        this.isLoading = false;
        const msg =
          err.status === 401
            ? 'Token expirado. Por favor, reinicie o processo de recuperação.'
            : err.status === 400
              ? 'A nova senha não atende aos requisitos de segurança.'
              : 'Erro ao redefinir senha.';
        this.snackBar.open(msg, 'Fechar', { duration: 6000, verticalPosition: 'top' });
        if (err.status === 401) {
          this.recoveryState.clear();
          this.router.navigate(['/auth/recovery-password']);
        }
      },
    });
  }
}

function passwordsMatchValidator(group: AbstractControl) {
  const pw = group.get('newPassword')?.value;
  const confirm = group.get('confirmPassword')?.value;
  return pw === confirm ? null : { passwordsMismatch: true };
}
