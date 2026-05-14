import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProgressStepperComponent } from './progress-stepper.component';

describe('ProgressStepperComponent', () => {
  let fixture: ComponentFixture<ProgressStepperComponent>;
  let component: ProgressStepperComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProgressStepperComponent],
    }).compileComponents();
    fixture = TestBed.createComponent(ProgressStepperComponent);
    component = fixture.componentInstance;
    component.steps = [{ label: 'E-mail' }, { label: 'Código' }, { label: 'Nova Senha' }];
    component.currentStep = 1;
    fixture.detectChanges();
  });

  it('renders correct number of step circles', () => {
    const circles = fixture.nativeElement.querySelectorAll('.step-circle');
    expect(circles.length).toBe(3);
  });

  it('marks completed steps as done', () => {
    const circles = fixture.nativeElement.querySelectorAll('.step-circle');
    expect(circles[0].classList).toContain('done');
    expect(circles[1].classList).toContain('active');
    expect(circles[2].classList).toContain('pending');
  });
});
