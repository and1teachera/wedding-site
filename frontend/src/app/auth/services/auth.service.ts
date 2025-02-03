import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

export interface NameLoginCredentials {
  firstName: string;
  lastName: string;
  password: string;
  rememberMe: boolean;
}

export interface EmailLoginCredentials {
  email: string;
  password: string;
  rememberMe: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  // tslint:disable-next-line
  loginWithNames(credentials: NameLoginCredentials): Observable<boolean> {
    // This is a placeholder for actual authentication logic
    // For now, it just returns a successful login
    return of(true);
  }

  // tslint:disable-next-line
  loginWithEmail(credentials: EmailLoginCredentials): Observable<boolean> {
    // This is a placeholder for actual authentication logic
    // For now, it just returns a successful login
    return of(true);
  }

  logout(): Observable<void> {
    return of(void 0);
  }
}