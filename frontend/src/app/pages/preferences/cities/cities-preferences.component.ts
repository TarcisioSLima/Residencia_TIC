import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormArray,
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { Observable } from 'rxjs';
import { Router, RouterModule } from '@angular/router';
import { map, startWith } from 'rxjs/operators';

import { AuthService } from '../../../services/auth.service';
import { CatalogService } from '../../../services/catalog.service';
import { Cidade } from '../../../models/catalog.models';
import { EVENT_IDS } from '../user/ids.constants';
import { ProgressStepperComponent, StepperStep } from '../../../shared/stepper/progress-stepper.component';

@Component({
  selector: 'app-preferences',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatAutocompleteModule,
    MatCheckboxModule,
    MatIconModule,
    ProgressStepperComponent,
  ],
  templateUrl: './cities-preferences.component.html',
  styleUrls: ['./cities-preferences.component.scss'],
})
export class PreferencesComponent implements OnInit {
  steps: StepperStep[] = [{ label: 'Cadastro' }, { label: 'Cidades' }, { label: 'Preferências' }];
  userName = localStorage.getItem('user_nome') ?? 'Usuário';

  citySearchCtrl = new FormControl<string | Cidade | null>('');
  preferencesForm: FormGroup;

  allCities: Cidade[] = [];
  filteredCities!: Observable<Cidade[]>;

  isLoadingData = false;
  private navStateData: any[] | null = null;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService,
    private catalogService: CatalogService
  ) {
    this.preferencesForm = this.fb.group({
      selectedCities: this.fb.array([]),
    });

    const nav = this.router.getCurrentNavigation();
    if (nav?.extras?.state?.['citiesPreferences']) {
      this.navStateData = nav.extras.state['citiesPreferences'];
    } else if (history.state.citiesPreferences) {
      this.navStateData = history.state.citiesPreferences;
    }
  }

  ngOnInit() {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/auth/login']);
      return;
    }

    this.loadCities();

    this.filteredCities = this.citySearchCtrl.valueChanges.pipe(
      startWith(''),
      map((value) => {
        const name = typeof value === 'string' ? value : value?.nome;
        return name ? this._filter(name) : this.allCities.slice();
      })
    );
  }

  loadCities() {
    this.isLoadingData = true;

    this.catalogService.getAllCidades().subscribe({
      next: (cidades) => {
        const optionTodas: Cidade = { id: 'TODAS_ID', nome: 'TODAS' };
        const cidadesOrdenadas = cidades.sort((a, b) =>
          a.nome.localeCompare(b.nome)
        );
        this.allCities = [optionTodas, ...cidadesOrdenadas];

        if (this.navStateData && this.navStateData.length > 0) {
          this.populateFormFromState(this.navStateData);
          this.isLoadingData = false;
        } else {
          this.loadUserPreferences();
        }
      },
      error: (_err) => {
        this.isLoadingData = false;
      },
    });
  }

  loadUserPreferences() {
    this.authService.getUserPreferences().subscribe({
      next: (prefs) => {
        this.populateFormWithData(prefs);
        this.isLoadingData = false;
      },
      error: (_err) => {
        this.isLoadingData = false;
      },
    });
  }

  private populateFormWithData(preferences: any[]) {
    if (!preferences || preferences.length === 0) return;

    const cityIdToNameMap = this.allCities.reduce((acc, city) => {
      acc[city.id] = city.nome;
      return acc;
    }, {} as { [key: string]: string });

    const groupedByCity: { [cityId: string]: Set<string> } = {};

    preferences.forEach((p) => {
      const cityId = p.idCidade;
      const eventId = p.idEvento;

      if (!groupedByCity[cityId]) {
        groupedByCity[cityId] = new Set();
      }
      groupedByCity[cityId].add(eventId);
    });

    this.selectedCities.clear();

    Object.keys(groupedByCity).forEach((cityId) => {
      const cityName = cityIdToNameMap[cityId];
      const eventIdsSet = groupedByCity[cityId];

      if (cityName) {
        const cityGroup = this.newCity(cityName);
        const prefsGroup = cityGroup.get('preferences');

        if (eventIdsSet.has(EVENT_IDS.HUMIDITY)) {
          prefsGroup?.get('humidity')?.setValue(true);
        }
        if (eventIdsSet.has(EVENT_IDS.HIGH_TEMP)) {
          prefsGroup?.get('temperature')?.setValue(true);
        }
        if (eventIdsSet.has(EVENT_IDS.LOW_TEMP)) {
          prefsGroup?.get('temperature')?.setValue(true);
        }
        if (eventIdsSet.has(EVENT_IDS.WIND)) {
          prefsGroup?.get('wind')?.setValue(true);
        }
        if (eventIdsSet.has(EVENT_IDS.RAIN)) {
          prefsGroup?.get('heavyRain')?.setValue(true);
        }
        if (eventIdsSet.has(EVENT_IDS.HEAT_INDEX)) {
          prefsGroup?.get('heatIndex')?.setValue(true);
        }

        this.selectedCities.push(cityGroup);
      }
    });
  }

  get selectedCities(): FormArray {
    return this.preferencesForm.get('selectedCities') as FormArray;
  }

  newCity(cityName: string): FormGroup {
    return this.fb.group({
      name: [cityName],
      preferences: this.fb.group({
        humidity: [false],
        temperature: [false],
        wind: [false],
        heavyRain: [false],
        heatIndex: [false],
      }),
    });
  }

  displayFn(city: Cidade | null): string {
    return city?.nome ?? '';
  }

  private normalizeText(text: string): string {
    return text
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .toLowerCase()
      .trim();
  }

  private _filter(name: string): Cidade[] {
    const filterValue = this.normalizeText(name);

    return this.allCities.filter((city) =>
      this.normalizeText(city.nome).includes(filterValue)
    );
  }

  addCity() {
    const value = this.citySearchCtrl.value;
    let cityName = '';

    if (typeof value === 'string') {
      const found = this.allCities.find(
        (c) => this.normalizeText(c.nome) === this.normalizeText(value)
      );
      if (found) {
        cityName = found.nome;
      } else {
        return;
      }
    } else if (value && value.nome) {
      cityName = value.nome;
    } else {
      return;
    }

    if (cityName === 'TODAS') {
      if (!this.cityAlreadyAdded('TODAS')) {
        this.selectedCities.clear();
        this.selectedCities.push(this.newCity('TODAS'));
        this.citySearchCtrl.setValue('');
      }
      return;
    }

    if (this.cityAlreadyAdded('TODAS')) {
      alert(
        'Você já selecionou "TODAS" as cidades. Remova essa opção para adicionar cidades específicas.'
      );
      this.citySearchCtrl.setValue('');
      return;
    }

    if (!this.cityAlreadyAdded(cityName)) {
      this.selectedCities.push(this.newCity(cityName));
      this.citySearchCtrl.setValue('');
    }
  }

  cityAlreadyAdded(cityName: string): boolean {
    return this.selectedCities.value.some((city: any) => city.name === cityName);
  }

  removeCity(index: number) {
    this.selectedCities.removeAt(index);
  }

  get canProceed(): boolean {
    if (this.selectedCities.length === 0) return false;

    return this.selectedCities.controls.some((cityGroup) => {
      const prefs = cityGroup.get('preferences')?.value;
      return (
        prefs.humidity ||
        prefs.temperature ||
        prefs.wind ||
        prefs.heavyRain ||
        prefs.heatIndex
      );
    });
  }

  onSubmit() {
    if (this.canProceed) {
      const citiesData = this.preferencesForm.value.selectedCities;

      const currentCityMap: { [key: string]: string } = {};
      this.allCities.forEach((c) => (currentCityMap[c.nome] = c.id));

      this.router.navigate(['/preferences/user'], {
        state: {
          citiesPreferences: citiesData,
          cityMap: currentCityMap,
        },
      });
    }
  }

  getCheckedCount(cityCtrl: any): number {
    const prefs = cityCtrl.get('preferences')?.value;
    if (!prefs) return 0;
    return Object.values(prefs).filter(Boolean).length;
  }

  logout() {
    this.authService.logout();
  }

  private populateFormFromState(citiesData: any[]) {
    this.selectedCities.clear();

    citiesData.forEach((cityData) => {
      const cityGroup = this.newCity(cityData.name);

      const prefs = cityData.preferences;
      cityGroup.get('preferences')?.patchValue({
        humidity: prefs.humidity,
        temperature: prefs.temperature,
        wind: prefs.wind,
        heavyRain: prefs.heavyRain,
        heatIndex: prefs.heatIndex,
      });

      this.selectedCities.push(cityGroup);
    });
  }
}