import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

export interface StepperStep {
  label: string;
}

@Component({
  selector: 'app-progress-stepper',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="stepper">
      <div class="step-slot"
           *ngFor="let step of steps; let i = index; let last = last; let first = first"
           [class.done]="i < safeCurrentStep"
           [class.active]="i === safeCurrentStep"
           [class.pending]="i > safeCurrentStep">
        <div class="step-track">
          <div class="half-line"
               [class.filled]="i <= safeCurrentStep"
               [style.visibility]="first ? 'hidden' : 'visible'"></div>
          <div class="step-circle">
            <span *ngIf="i < safeCurrentStep">✓</span>
            <span *ngIf="i >= safeCurrentStep">{{ i + 1 }}</span>
          </div>
          <div class="half-line"
               [class.filled]="i < safeCurrentStep"
               [style.visibility]="last ? 'hidden' : 'visible'"></div>
        </div>
        <span class="step-label">{{ step.label }}</span>
      </div>
    </div>
  `,
  styles: [`
    .stepper {
      display: flex;
      align-items: flex-start;
      margin-bottom: 20px;
    }
    .step-slot {
      flex: 1;
      display: flex;
      flex-direction: column;
      align-items: center;
    }
    .step-track {
      display: flex;
      align-items: center;
      width: 100%;
    }
    .half-line {
      flex: 1;
      height: 3px;
      border-radius: 2px;
      background: var(--color-border);
    }
    .half-line.filled { background: var(--color-primary); }
    .step-circle {
      width: 28px; height: 28px;
      border-radius: 50%;
      display: flex; align-items: center; justify-content: center;
      font-size: 12px; font-weight: 700;
      flex-shrink: 0;
    }
    .step-slot.done .step-circle, .step-slot.active .step-circle {
      background: var(--color-primary); color: white;
    }
    .step-slot.pending .step-circle {
      background: var(--color-border); color: var(--color-text-muted);
    }
    .step-label {
      font-size: 11px;
      color: var(--color-text-muted);
      margin-top: 6px;
      text-align: center;
      line-height: 1.3;
    }
    .step-slot.active .step-label { color: var(--color-primary); font-weight: 600; }
  `]
})
export class ProgressStepperComponent {
  @Input() steps: StepperStep[] = [];
  @Input() currentStep = 0;

  get safeCurrentStep(): number {
    return Math.max(0, Math.min(this.currentStep, this.steps.length));
  }
}
