import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { TokenService } from './token.service';

export class AuthError extends Error {
  constructor(message: string) {
    super(message);
    this.name = 'AuthError';
  }
}

export class NetworkError extends Error {
  constructor(message: string) {
    super(message);
    this.name = 'NetworkError';
  }
}

export interface NameAuthenticationRequest {
  firstName: string;
  lastName: string;
  password: string;
}

export interface EmailAuthenticationRequest {
  email: string;
  password: string;
}

export interface NameLoginCredentials extends NameAuthenticationRequest {
  rememberMe: boolean;
}

export interface EmailLoginCredentials extends EmailAuthenticationRequest {
  rememberMe: boolean;
}

export interface LoginResponse {
  token: string;
  userType: string;
  user: {
    firstName: string;
    lastName: string;
  };
}


@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = '/api/auth';

  constructor(
      private http: HttpClient,
      private tokenService: TokenService
  ) {}

  loginWithNames(credentials: NameLoginCredentials): Observable<LoginResponse> {
    const request: NameAuthenticationRequest = {
      firstName: credentials.firstName,
      lastName: credentials.lastName,
      password: credentials.password
    };
    
    return this.http.post<LoginResponse>(`${this.API_URL}/login-by-names`, request, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    }).pipe(
        tap(response => {
          console.log('Full auth response:', JSON.stringify(response)); // Log the full response
          this.tokenService.setToken(response.token, credentials.rememberMe);
          console.log('Token set in service');
        }),
        catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse) {
    console.error('Login error:', error);
    if (!navigator.onLine) {
      return throwError(() => new NetworkError('Няма връзка с интернет. Моля, проверете вашата връзка.'));
    }

    if (error.status === 0) {
      return throwError(() => new NetworkError('Не можем да се свържем със сървъра. Моля, опитайте отново по-късно.'));
    }

    if (error.status === 401) {
      return throwError(() => new AuthError('Невалидна парола'));
    }

    if (error.status === 404) {
      return throwError(() => new AuthError('Не намерихме гост с тези имена'));
    }

    if (error.status === 429) {
      return throwError(() => new AuthError('Твърде много опити. Моля, изчакайте малко и опитайте отново.'));
    }

    return throwError(() => new Error('Възникна грешка. Моля, опитайте отново по-късно.'));
  }

  loginWithEmail(credentials: EmailLoginCredentials): Observable<LoginResponse> {
    const request: EmailAuthenticationRequest = {
      email: credentials.email,
      password: credentials.password
    };
    
    return this.http.post<LoginResponse>(`${this.API_URL}/login`, request, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    }).pipe(
        tap(response => {
          this.tokenService.setToken(response.token, credentials.rememberMe);
        }),
        catchError(this.handleError)
    );
  }
}