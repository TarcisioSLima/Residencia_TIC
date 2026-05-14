import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ApplicationLogEntry, EnvioLog, Page } from '../models/aviso.models';

@Injectable({
  providedIn: 'root'
})
export class EnvioService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  filtrarLogsEnvio(
    data: string | null,
    idEvento: string | null,
    idCidade: string | null,
    page: number,
    size: number
  ): Observable<Page<EnvioLog>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (data) {
      params = params.set('data', data);
    }
    if (idEvento) {
      params = params.set('idEvento', idEvento);
    }
    if (idCidade) {
      params = params.set('idCidade', idCidade);
    }

    return this.http.get<Page<EnvioLog>>(`${this.apiUrl}/envios/logs`, { params });
  }

  listApplicationLogs(task: string, date: string | null): Observable<ApplicationLogEntry[]> {
    let params = new HttpParams().set('task', task);

    if (date) {
      params = params.set('date', date);
    }

    return this.http.get<ApplicationLogEntry[]>(`${this.apiUrl}/list-logs`, { params });
  }
}
