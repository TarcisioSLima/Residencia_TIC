import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, of, throwError, tap, map, catchError } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';
import { VerifyCodeResponseDto } from '../models/recovery.models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient, private router: Router) { }

  login(credentials: { email: string; senha: string }): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/usuarios/login`, credentials)
      .pipe(
        tap(response => {
          this.doLoginUser(response);
        })
      );
  }

  private doLoginUser(response: any) {
    localStorage.setItem('access_token', response.token);
    localStorage.setItem('user_id', response.id);
    localStorage.setItem('user_role', response.nivelAcesso);
    if (response.nome)     localStorage.setItem('user_nome', response.nome);
    if (response.email)    localStorage.setItem('user_email', response.email);
    if (response.whatsapp) localStorage.setItem('user_whatsapp', response.whatsapp);
  }

  getToken(): string | null {
    return localStorage.getItem('access_token');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

logout() {
    localStorage.removeItem('access_token');
    localStorage.removeItem('user_id');
    localStorage.removeItem('user_role');
    this.router.navigate(['/auth/login']);
  }
  
  register(userData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/usuarios`, userData);
  }

  savePreferences(preferencesPayload: any[]): Observable<any> {
    return this.http.post(`${this.apiUrl}/preferencias/lote`, preferencesPayload);
  }

  updateFullProfile(payload: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/usuarios/meu-perfil`, payload, { 
      responseType: 'text' 
    });
  }

  getUserRole(): string | null {
    return localStorage.getItem('user_role');
  }

  isAdmin(): boolean {
    return this.getUserRole()?.toLowerCase() === 'admin';
  }

  checkTokenValidity(): Observable<boolean> {
    const token = this.getToken();
    if (!token) {
      return of(false);
    }

    return this.http.get(`${this.apiUrl}/preferencias`).pipe(
      map(() => true),
      catchError((error) => {
        this.logout();
        return of(false);
      })
    );
  }


  getMe(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/usuarios/me`);
  }

  getUserPreferences(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/preferencias`);
  }

  // --- Password Recovery ---

  requestRecovery(email: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/auth/recovery-password`, { email });
  }

  verifyRecoveryCode(email: string, code: string): Observable<VerifyCodeResponseDto> {
    return this.http.post<VerifyCodeResponseDto>(
      `${this.apiUrl}/auth/recovery-password/verify-code`,
      { email, code }
    );
  }

  resetPassword(resetToken: string, newPassword: string): Observable<HttpResponse<any>> {
    return this.http.post(
      `${this.apiUrl}/auth/recovery-password/reset`,
      { resetToken, newPassword },
      { observe: 'response' }
    );
  }
}