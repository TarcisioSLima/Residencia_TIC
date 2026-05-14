import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { EnvioService } from '../../../services/envio.service';
import { CatalogService } from '../../../services/catalog.service';
import { EnvioLog } from '../../../models/aviso.models';
import { Cidade, Evento } from '../../../models/catalog.models';
import { SidebarComponent } from '../../../shared/sidebar/sidebar.component';

@Component({
  selector: 'app-envios',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatTableModule,
    MatPaginatorModule,
    SidebarComponent
  ],
  templateUrl: './envios.component.html',
  styleUrl: './envios.component.scss'
})
export class EnviosComponent implements OnInit {
  displayedColumns: string[] = ['dataGeracaoAviso', 'destinatario', 'nomeEvento', 'nomeCidade', 'nomeCanal', 'nomeStatus'];

  filterForm: FormGroup;
  eventos: Evento[] = [];
  cidades: Cidade[] = [];

  enviosData: EnvioLog[] = [];
  totalElements = 0;
  pageSize = 25;
  pageIndex = 0;
  isLoading = false;

  get dataSource(): EnvioLog[] { return this.enviosData; }

  constructor(
    private fb: FormBuilder,
    private envioService: EnvioService,
    private catalogService: CatalogService
  ) {
    this.filterForm = this.fb.group({
      data: [null],
      evento: [''],
      cidade: ['']
    });
  }

  ngOnInit(): void {
    this.loadCatalogs();
    this.loadEnvios();
  }

  onFilter(): void {
    this.pageIndex = 0;
    this.loadEnvios();
  }

  clearFilters(): void {
    this.filterForm.reset({ data: null, evento: '', cidade: '' });
    this.pageIndex = 0;
    this.loadEnvios();
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadEnvios();
  }

  private loadCatalogs(): void {
    this.catalogService.getAllEventos().subscribe({ next: (data) => this.eventos = data, error: () => {} });
    this.catalogService.getAllCidades().subscribe({ next: (data) => this.cidades = data, error: () => {} });
  }

  private loadEnvios(): void {
    this.isLoading = true;
    const { data, evento, cidade } = this.filterForm.value;

    let dataFormatada: string | null = null;
    if (data) {
      const d = new Date(data);
      dataFormatada = d.toLocaleDateString('en-CA');
    }

    this.envioService.filtrarLogsEnvio(
      dataFormatada,
      evento || null,
      cidade || null,
      this.pageIndex,
      this.pageSize
    ).subscribe({
      next: (page) => {
        this.enviosData = page.content;
        this.totalElements = page.totalElements;
        this.isLoading = false;
      },
      error: () => {
        this.enviosData = [];
        this.isLoading = false;
      }
    });
  }
}
