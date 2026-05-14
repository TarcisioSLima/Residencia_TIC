import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormControl, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../../../services/auth.service';
import { RecoveryStateService } from '../../../../services/recovery-state.service';

@Component({
  selector: 'app-step1-email',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSnackBarModule,
  ],
  templateUrl: './step1-email.component.html',
  styleUrls: ['./step1-email.component.scss'],
})
export class Step1EmailComponent {
  @Output() next = new EventEmitter<void>();

  emailControl = new FormControl('', [Validators.required, Validators.email]);
  isLoading = false;

  constructor(
    private authService: AuthService,
    private recoveryState: RecoveryStateService,
    private snackBar: MatSnackBar
  ) {}

  onSubmit(): void {
    if (this.emailControl.invalid) {
      this.emailControl.markAsTouched();
      return;
    }

    this.isLoading = true;
    const email = this.emailControl.value!;

    this.authService.requestRecovery(email).subscribe({
      next: () => {
        this.recoveryState.setEmail(email);
        this.isLoading = false;
        this.next.emit();
      },
      error: (err) => {
        this.isLoading = false;
        const msg =
          err.status === 429
            ? 'Muitas tentativas. Tente novamente em alguns minutos.'
            : 'Se o e-mail existir em nossa base, você receberá um código em breve.';
        this.snackBar.open(msg, 'Fechar', { duration: 5000, verticalPosition: 'top' });
        if (err.status !== 429) {
          this.recoveryState.setEmail(email);
          this.next.emit();
        }
      },
    });
  }
}
