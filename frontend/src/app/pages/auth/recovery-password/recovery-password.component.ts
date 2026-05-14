import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavigationEnd, Router, RouterModule } from '@angular/router';
import { Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';
import { Step1EmailComponent } from './step1-email/step1-email.component';
import { Step2CodeComponent } from './step2-code/step2-code.component';
import { Step3ResetComponent } from './step3-reset/step3-reset.component';
import { ProgressStepperComponent, StepperStep } from '../../../shared/stepper/progress-stepper.component';

@Component({
  selector: 'app-recovery-password',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    Step1EmailComponent,
    Step2CodeComponent,
    Step3ResetComponent,
    ProgressStepperComponent,
  ],
  templateUrl: './recovery-password.component.html',
  styleUrls: ['./recovery-password.component.scss'],
})
export class RecoveryPasswordComponent implements OnInit, OnDestroy {
  currentStep: 1 | 2 | 3 = 1;

  steps: StepperStep[] = [
    { label: 'E-mail' },
    { label: 'Código' },
    { label: 'Nova Senha' },
  ];

  private navSub?: Subscription;

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.navSub = this.router.events
      .pipe(filter((e): e is NavigationEnd => e instanceof NavigationEnd))
      .subscribe((e) => {
        const url = e.urlAfterRedirects.split('?')[0];
        if (url === '/auth/recovery-password' || url.endsWith('/auth/recovery-password')) {
          this.currentStep = 1;
        }
      });
  }

  ngOnDestroy(): void {
    this.navSub?.unsubscribe();
  }

  goToStep(step: 1 | 2 | 3): void {
    this.currentStep = step;
  }
}
