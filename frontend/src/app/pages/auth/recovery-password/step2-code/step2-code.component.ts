import { Component, EventEmitter, OnDestroy, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormControl, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../../../services/auth.service';
import { RecoveryStateService } from '../../../../services/recovery-state.service';
import { interval, Subscription } from 'rxjs';

@Component({
  selector: 'app-step2-code',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSnackBarModule,
  ],
  templateUrl: './step2-code.component.html',
  styleUrls: ['./step2-code.component.scss'],
})
export class Step2CodeComponent implements OnInit, OnDestroy {
  @Output() next = new EventEmitter<void>();
  @Output() back = new EventEmitter<void>();

  codeControl = new FormControl('', [Validators.required, Validators.pattern(/^\d{6}$/)]);
  isLoading = false;

  countdown = 600;
  private timer$?: Subscription;

  constructor(
    private authService: AuthService,
    private recoveryState: RecoveryStateService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.timer$ = interval(1000).subscribe(() => {
      if (this.countdown > 0) {
        this.countdown--;
      }
    });
  }

  ngOnDestroy(): void {
    this.timer$?.unsubscribe();
  }

  get countdownFormatted(): string {
    const m = Math.floor(this.countdown / 60)
      .toString()
      .padStart(2, '0');
    const s = (this.countdown % 60).toString().padStart(2, '0');
    return `${m}:${s}`;
  }

  get email(): string {
    return this.recoveryState.getEmail();
  }

  get maskedEmail(): string {
    const raw = this.email.trim();
    if (!raw || !raw.includes('@')) {
      return raw || '—';
    }
    const [local, domain] = raw.split('@');
    if (!local.length) {
      return `***@${domain}`;
    }
    return `${local[0]}***@${domain}`;
  }

  onSubmit(): void {
    if (this.codeControl.invalid) {
      this.codeControl.markAsTouched();
      return;
    }

    this.isLoading = true;

    this.authService.verifyRecoveryCode(this.email, this.codeControl.value!).subscribe({
      next: (response) => {
        this.recoveryState.setResetToken(response.resetToken);
        this.isLoading = false;
        this.next.emit();
      },
      error: (err) => {
        this.isLoading = false;
        const msg =
          err.status === 401
            ? 'Código inválido ou expirado. Verifique e tente novamente.'
            : err.status === 429
              ? 'Muitas tentativas. Solicite um novo código.'
              : 'Erro ao verificar código.';
        this.snackBar.open(msg, 'Fechar', { duration: 5000, verticalPosition: 'top' });
      },
    });
  }
}
