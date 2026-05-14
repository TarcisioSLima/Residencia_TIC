import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatListModule } from '@angular/material/list';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { CatalogService } from '../../../services/catalog.service';
import { Aviso, EnvioLog } from '../../../models/aviso.models';
import { Cidade, Evento } from '../../../models/catalog.models';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AvisoService } from '../../../services/aviso.service';
import { EnvioService } from '../../../services/envio.service';
import { SidebarComponent } from '../../../shared/sidebar/sidebar.component';
import { ForceResendDialogComponent, ForceResendDialogResult } from '../force-resend-dialog/force-resend-dialog.component';

@Component({
  selector: 'app-admin-panel',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatSidenavModule,
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
    MatListModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatTableModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatSnackBarModule,
    RouterModule,
    SidebarComponent,
    ForceResendDialogComponent,
  ],
  templateUrl: './admin-panel.component.html',
  styleUrls: ['./admin-panel.component.scss']
})
export class AdminPanelComponent implements OnInit {
  userName = localStorage.getItem('user_nome') || 'Administrador';
  userInitial = (localStorage.getItem('user_nome') || 'A').charAt(0).toUpperCase();

  selectedView: 'avisos' | 'envios' = 'avisos';
  filterForm: FormGroup;

  eventos: Evento[] = [];
  cidades: Cidade[] = [];

  avisosData: Aviso[] = [];
  enviosData: EnvioLog[] = [];
  totalElements = 0;
  pageSize = 25;
  pageIndex = 0;
  isLoading = false;

  displayedColumns: string[] = ['nomeEvento', 'nomeCidade', 'valor', 'dataReferencia', 'dataGeracao'];
  displayedEnvioColumns: string[] = ['data', 'evento', 'cidade', 'canal', 'status', 'destinatario'];

  searchTerm = '';

  get avisos(): Aviso[] { return this.avisosData; }

  isSending = false;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService,
    private catalogService: CatalogService,
    private avisoService: AvisoService,
    private envioService: EnvioService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
  ) {
    this.filterForm = this.fb.group({
      data: [null],
      evento: [''],
      cidade: ['']
    });
  }

  ngOnInit(): void {
    this.loadCatalogs();
    this.carregarViewAtual();
  }

  loadCatalogs() {
    this.catalogService.getAllEventos().subscribe({
      next: (data) => this.eventos = data,
      error: () => {}
    });

    this.catalogService.getAllCidades().subscribe({
      next: (data) => this.cidades = data,
      error: () => {}
    });
  }

  onViewChange(view: 'avisos' | 'envios') {
    this.selectedView = view;
    this.pageIndex = 0;
    this.carregarViewAtual();
  }

  onFilter() {
    this.pageIndex = 0;
    this.carregarViewAtual();
  }

  onPageChange(event: PageEvent) {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.carregarViewAtual();
  }

  clearFilters() {
    this.filterForm.reset({ data: null, evento: '', cidade: '' });
    this.pageIndex = 0;
    this.buscarAvisos();
  }

  openForceResendDialog() {
    const ref = this.dialog.open(ForceResendDialogComponent, { disableClose: true });

    ref.afterClosed().subscribe((result: ForceResendDialogResult | null) => {
      if (!result) return;

      const { userIds, bypassPreferences } = result;
      this.isSending = true;
      this.avisoService.forceResend(userIds, bypassPreferences).subscribe({
        next: (res) => {
          this.isSending = false;
          const destino = userIds.length > 0
            ? `${res.targetUsersCount} usuário(s)`
            : 'toda a base';
          const bypassMsg = bypassPreferences ? ' (preferências ignoradas)' : '';
          this.snackBar.open(
            `Reenvio iniciado: ${res.alertsCount} alerta(s) para ${destino}${bypassMsg}.`,
            'OK',
            { duration: 5000 }
          );
        },
        error: () => {
          this.isSending = false;
          this.snackBar.open('Erro ao iniciar reenvio. Tente novamente.', 'OK', { duration: 4000 });
        },
      });
    });
  }

  logout() {
    this.authService.logout();
  }

  private carregarViewAtual() {
    if (this.selectedView === 'envios') {
      this.buscarLogsEnvio();
      return;
    }

    this.buscarAvisos();
  }

  private getFiltrosFormatados() {
    const { data, evento, cidade } = this.filterForm.value;

    let dataFormatada = null;
    if (data) {
      const d = new Date(data);
      dataFormatada = d.toLocaleDateString('en-CA');
    }

    return {
      data: dataFormatada,
      evento: evento || null,
      cidade: cidade || null
    };
  }

  buscarAvisos() {
    this.isLoading = true;
    const filtros = this.getFiltrosFormatados();

    this.avisoService.filtrarAvisos(
      filtros.data,
      filtros.evento,
      filtros.cidade,
      this.pageIndex,
      this.pageSize
    ).subscribe({
      next: (page) => {
        this.avisosData = page.content;
        this.totalElements = page.totalElements;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  buscarLogsEnvio() {
    this.isLoading = true;
    const filtros = this.getFiltrosFormatados();

    this.envioService.filtrarLogsEnvio(
      filtros.data,
      filtros.evento,
      filtros.cidade,
      this.pageIndex,
      this.pageSize
    ).subscribe({
      next: (page) => {
        this.enviosData = page.content;
        this.totalElements = page.totalElements;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }
}
