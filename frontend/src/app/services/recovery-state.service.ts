import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class RecoveryStateService {
  private emailSubject = new BehaviorSubject<string>('');
  private resetTokenSubject = new BehaviorSubject<string>('');

  readonly email$ = this.emailSubject.asObservable();
  readonly resetToken$ = this.resetTokenSubject.asObservable();

  setEmail(email: string): void {
    this.emailSubject.next(email);
  }

  getEmail(): string {
    return this.emailSubject.getValue();
  }

  setResetToken(token: string): void {
    this.resetTokenSubject.next(token);
  }

  getResetToken(): string {
    return this.resetTokenSubject.getValue();
  }

  clear(): void {
    this.emailSubject.next('');
    this.resetTokenSubject.next('');
  }
}
