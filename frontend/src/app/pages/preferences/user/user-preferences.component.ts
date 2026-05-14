import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatRadioModule } from '@angular/material/radio';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { forkJoin, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { Router, RouterModule } from '@angular/router';

import { AuthService } from '../../../services/auth.service';
import { EVENT_IDS, CHANNEL_IDS } from './ids.constants';
import { ProgressStepperComponent, StepperStep } from '../../../shared/stepper/progress-stepper.component';

interface CityPreferenceInput {
  name: string;
  preferences: {
    humidity: boolean;
    temperature: boolean;
    wind: boolean;
    heavyRain: boolean;
    heatIndex: boolean;
  };
}

@Component({
  selector: 'app-user-preferences',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule,
    ProgressStepperComponent,
    MatCheckboxModule,
    MatRadioModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule,
    MatSnackBarModule,
  ],
  templateUrl: './user-preferences.component.html',
  styleUrls: ['./user-preferences.component.scss'],
})
export class UserPreferencesComponent implements OnInit, OnDestroy {
  preferencesForm: FormGroup;
  private destroy$ = new Subject<void>();

  citiesData: CityPreferenceInput[] = [];
  dynamicCityIds: { [key: string]: string } = {};

  showHumidity = false;
  showTemp = false;
  showWind = false;
  showHeatIndex = false;

  steps: StepperStep[] = [{ label: 'Cadastro' }, { label: 'Cidades' }, { label: 'Preferências' }];
  userName = localStorage.getItem('user_nome') ?? 'Usuário';

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {
    this.preferencesForm = this.fb.group({
      humidity: this.fb.group({
        type: ['standard'],
      }),
      highTemp: this.fb.group({
        type: ['standard'],
        value: [{ value: null, disabled: true }],
      }),
      lowTemp: this.fb.group({
        type: ['standard'],
        value: [{ value: null, disabled: true }],
      }),
      wind: this.fb.group({
        type: ['critical'],
      }),
      heatIndex: this.fb.group({
        type: ['standard'],
      }),
      channels: this.fb.group({
        email: [true],
        whatsapp: [false],
      }),
    });
  }

  ngOnInit(): void {
    const nav = this.router.getCurrentNavigation();

    if (nav?.extras?.state) {
      this.citiesData = nav.extras.state['citiesPreferences'];
      this.dynamicCityIds = nav.extras.state['cityMap'] || {};
    } else if (history.state.citiesPreferences) {
      this.citiesData = history.state.citiesPreferences;
      this.dynamicCityIds = history.state.cityMap || {};
    }

    if (!this.citiesData || this.citiesData.length === 0) {
      this.router.navigate(['/preferences/cities']);
      return;
    }

    this.analyzeSelectedPreferences();
    this.watchAndToggle('highTemp');
    this.watchAndToggle('lowTemp');
    this.loadExistingPreferences();
  }

  private loadExistingPreferences(): void {
    forkJoin({
      user: this.authService.getMe(),
      prefs: this.authService.getUserPreferences(),
    })
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: ({ user, prefs }) => {
          if (!prefs || prefs.length === 0) return;

          const channelIds = ((user.canaisPreferidos as any[]) || []).map((c) => c.id);
          const channelsGroup = this.preferencesForm.get('channels') as FormGroup;
          channelsGroup.get('email')?.setValue(channelIds.includes(CHANNEL_IDS.EMAIL));
          channelsGroup.get('whatsapp')?.setValue(channelIds.includes(CHANNEL_IDS.WHATSAPP));

          const findPref = (eventId: string) =>
            (prefs as any[]).find((p) => p.idEvento === eventId);

          const humidityPref = findPref(EVENT_IDS.HUMIDITY);
          if (humidityPref?.personalizavel) {
            this.preferencesForm.get('humidity.type')?.setValue('custom');
          }

          const highTempPref = findPref(EVENT_IDS.HIGH_TEMP);
          if (highTempPref?.personalizavel) {
            this.preferencesForm.get('highTemp.type')?.setValue('custom');
            if (highTempPref.valor != null) {
              this.preferencesForm.get('highTemp.value')?.setValue(Number(highTempPref.valor));
            }
          }

          const lowTempPref = findPref(EVENT_IDS.LOW_TEMP);
          if (lowTempPref?.personalizavel) {
            this.preferencesForm.get('lowTemp.type')?.setValue('custom');
            if (lowTempPref.valor != null) {
              this.preferencesForm.get('lowTemp.value')?.setValue(Number(lowTempPref.valor));
            }
          }

          const windPref = findPref(EVENT_IDS.WIND);
          if (windPref?.valor != null) {
            const windValor = Number(windPref.valor);
            let windType = 'critical';
            if (windValor <= 12) windType = '12-20';
            else if (windValor <= 20) windType = '20-30';
            this.preferencesForm.get('wind.type')?.setValue(windType);
          }

          const heatIndexPref = findPref(EVENT_IDS.HEAT_INDEX);
          if (heatIndexPref?.personalizavel) {
            this.preferencesForm.get('heatIndex.type')?.setValue('custom');
          }
        },
      });
  }

  private analyzeSelectedPreferences() {
    this.showHumidity = this.citiesData.some((c) => c.preferences.humidity);
    this.showTemp = this.citiesData.some((c) => c.preferences.temperature);
    this.showWind = this.citiesData.some((c) => c.preferences.wind);
    this.showHeatIndex = this.citiesData.some((c) => c.preferences.heatIndex);
  }

  private watchAndToggle(groupName: 'highTemp' | 'lowTemp'): void {
    const group = this.preferencesForm.get(groupName) as FormGroup;
    const typeCtrl = group.get('type');
    const valueCtrl = group.get('value');

    typeCtrl?.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((val) => {
      if (val === 'custom') {
        valueCtrl?.enable();
        valueCtrl?.setValidators([Validators.required, Validators.pattern('^[0-9]*$')]);
      } else {
        valueCtrl?.disable();
        valueCtrl?.clearValidators();
        valueCtrl?.reset();
      }
      valueCtrl?.updateValueAndValidity();
    });
  }

  onBack() {
    this.router.navigate(['/preferences/cities'], {
      state: {
        citiesPreferences: this.citiesData,
        cityMap: this.dynamicCityIds,
      },
    });
  }

  goBack(): void { this.router.navigate(['/preferences/cities']); }

  logout() {
    this.authService.logout();
  }

  onSubmit() {
    if (this.preferencesForm.valid) {
      const formVal = this.preferencesForm.getRawValue();
      const preferencesPayload: any[] = [];

      this.citiesData.forEach((cityInput) => {
        const targetCityIds: string[] = [];

        if (cityInput.name === 'TODAS') {
          targetCityIds.push(...Object.values(this.dynamicCityIds).filter((id) => id !== 'TODAS_ID'));
        } else {
          const id = this.dynamicCityIds[cityInput.name];
          if (id) {
            targetCityIds.push(id);
          }
        }

        targetCityIds.forEach((cityId) => {
          if (cityInput.preferences.humidity) {
            const isCustom = formVal.humidity.type === 'custom';
            preferencesPayload.push({
              idEvento: EVENT_IDS.HUMIDITY,
              idCidade: cityId,
              personalizavel: isCustom,
              valor: isCustom ? 60 : null,
            });
          }

          if (cityInput.preferences.temperature) {
            const isHighCustom = formVal.highTemp.type === 'custom';
            preferencesPayload.push({
              idEvento: EVENT_IDS.HIGH_TEMP,
              idCidade: cityId,
              personalizavel: isHighCustom,
              valor: isHighCustom ? Number(formVal.highTemp.value) : null,
            });

            const isLowCustom = formVal.lowTemp.type === 'custom';
            preferencesPayload.push({
              idEvento: EVENT_IDS.LOW_TEMP,
              idCidade: cityId,
              personalizavel: isLowCustom,
              valor: isLowCustom ? Number(formVal.lowTemp.value) : null,
            });
          }

          if (cityInput.preferences.wind) {
            const windType = formVal.wind.type;
            let windVal = null;

            if (windType === '12-20') {
              windVal = 12;
            } else if (windType === '20-30') {
              windVal = 20;
            } else if (windType === 'critical') {
              windVal = 30;
            }

            preferencesPayload.push({
              idEvento: EVENT_IDS.WIND,
              idCidade: cityId,
              personalizavel: true,
              valor: windVal,
            });
          }

          if (cityInput.preferences.heavyRain) {
            preferencesPayload.push({
              idEvento: EVENT_IDS.RAIN,
              idCidade: cityId,
              personalizavel: false,
              valor: null,
            });
          }

          if (cityInput.preferences.heatIndex) {
            const isCustom = formVal.heatIndex.type === 'custom';
            preferencesPayload.push({
              idEvento: EVENT_IDS.HEAT_INDEX,
              idCidade: cityId,
              personalizavel: isCustom,
              valor: isCustom ? 32 : null,
            });
          }
        });
      });

      const channelsPayload: string[] = [];
      if (formVal.channels.email) {
        channelsPayload.push(CHANNEL_IDS.EMAIL);
      }
      if (formVal.channels.whatsapp) {
        channelsPayload.push(CHANNEL_IDS.WHATSAPP);
      }

      const finalPayload = {
        preferencias: preferencesPayload,
        idsCanaisPreferidos: channelsPayload,
      };

      this.authService.updateFullProfile(finalPayload).subscribe({
        next: () => {
          this.router.navigate(['/minha-conta'], { queryParams: { saved: true } });
        },
        error: () => {
          this.snackBar.open('Erro ao salvar preferencias. Tente novamente.', 'Fechar', {
            duration: 5000,
            verticalPosition: 'top',
          });
        },
      });
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
