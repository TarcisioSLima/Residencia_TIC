import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { EnvioService } from '../../../services/envio.service';
import { AuthService } from '../../../services/auth.service';
import { ApplicationLogEntry } from '../../../models/aviso.models';
import { SidebarComponent } from '../../../shared/sidebar/sidebar.component';

@Component({
  selector: 'app-logs-view',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSelectModule,
    MatTableModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    SidebarComponent
  ],
  templateUrl: './logs-view.component.html',
  styleUrl: './logs-view.component.scss'
})
export class LogsViewComponent implements OnInit {
  userName = localStorage.getItem('user_nome') || 'Administrador';
  userInitial = (localStorage.getItem('user_nome') || 'A').charAt(0).toUpperCase();

  filterForm: FormGroup;
  logsData: ApplicationLogEntry[] = [];

  readonly taskOptions = [
    { value: 'etl',           label: 'ETL' },
    { value: 'notifications', label: 'Notifications' },
  ];

  pageIndex = 0;
  pageSize = 25;

  displayedColumns: string[] = ['createdAt', 'task', 'level', 'status', 'message'];

  isLoading = false;
  lastUpdatedAt: Date | null = null;
  loadError = '';

  constructor(
    private fb: FormBuilder,
    private envioService: EnvioService,
    private authService: AuthService
  ) {
    this.filterForm = this.fb.group({
      task: ['etl', [Validators.required]],
      date: [new Date()]
    });
  }

  ngOnInit(): void {
    this.loadLogs();
  }

  // ─── Dados derivados ───────────────────────────────────────────────────────

  get dataSource(): ApplicationLogEntry[] {
    const start = this.pageIndex * this.pageSize;
    return this.logsData.slice(start, start + this.pageSize);
  }

  get totalCount(): number { return this.logsData.length; }
  get totalLogs(): number  { return this.logsData.length; }

  get infoCount(): number {
    return this.logsData.filter(log => log.level === 'info').length;
  }

  get warnCount(): number {
    return this.logsData.filter(log => log.level === 'warn').length;
  }

  get errorCount(): number {
    return this.logsData.filter(log => log.level === 'error').length;
  }

  // ─── Eventos de interação ─────────────────────────────────────────────────

  onFilter() {
    if (this.filterForm.invalid) {
      this.filterForm.markAllAsTouched();
      return;
    }
    this.loadLogs();
  }

  clearFilters() {
    this.filterForm.reset({ task: 'etl', date: new Date() });
    this.pageIndex = 0;
    this.loadLogs();
  }

  onPageChange(event: PageEvent) {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
  }

  logout() {
    this.authService.logout();
  }

  levelLabel(level: ApplicationLogEntry['level']): string {
    return level.toUpperCase();
  }

  rowClass(log: ApplicationLogEntry): string {
    return `row-${log.level}`;
  }

  // ─── Carregamento ─────────────────────────────────────────────────────────

  private loadLogs() {
    this.isLoading = true;
    this.loadError = '';
    this.pageIndex = 0;

    const task = this.filterForm.get('task')?.value ?? '';
    const date = this.filterForm.get('date')?.value;
    const formattedDate = date ? this.formatDate(date) : null;

    this.envioService.listApplicationLogs(task, formattedDate).subscribe({
      next: (logs) => {
        this.logsData = logs;
        this.lastUpdatedAt = new Date();
        this.isLoading = false;
      },
      error: (err) => {
        this.logsData = [];
        this.loadError = err?.error?.message || 'Nao foi possivel carregar os logs.';
        this.isLoading = false;
      }
    });
  }

  private formatDate(value: Date | string): string {
    const date = new Date(value);
    const year = date.getFullYear();
    const month = `${date.getMonth() + 1}`.padStart(2, '0');
    const day = `${date.getDate()}`.padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}
