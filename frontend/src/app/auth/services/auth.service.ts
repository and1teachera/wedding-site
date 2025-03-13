import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders} from '@angular/common/http';
import {Observable, throwError, of} from 'rxjs';
import { catchError, tap, map } from 'rxjs/operators';
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
  // Demo mode flag - set to true to enable bypass
  private demoModeEnabled = true;

  constructor(
      private http: HttpClient,
      private tokenService: TokenService
  ) {}

  loginWithNames(credentials: NameLoginCredentials): Observable<LoginResponse> {
    if (this.demoModeEnabled) {
      console.log('Demo mode enabled, bypassing authentication');
      
      // Create a dummy successful response
      const dummyResponse: LoginResponse = {
        token: 'demo-token-' + Date.now(),
        userType: 'GUEST',
        user: {
          firstName: credentials.firstName || 'Demo',
          lastName: credentials.lastName || 'User'
        }
      };
      
      // Store the token
      this.tokenService.setToken(dummyResponse.token, true);
      
      // Return as an observable
      return of(dummyResponse);
    }
  
    console.log('Attempting login with names:', credentials.firstName, credentials.lastName);
    
    const request: NameAuthenticationRequest = {
      firstName: credentials.firstName,
      lastName: credentials.lastName,
      password: credentials.password
    };
    
    console.log('Request payload:', JSON.stringify(request));
    
    return this.http.post<LoginResponse>(`${this.API_URL}/login-by-names`, request, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      }),
      observe: 'response' // Get the full response
    }).pipe(
        tap(response => {
          console.log('Response status:', response.status);
          console.log('Response headers:', response.headers.keys());
          console.log('Content-Type:', response.headers.get('Content-Type'));
          console.log('Response body:', JSON.stringify(response.body));
          
          if (!response.body) {
            console.error('Response body is null or undefined despite 200 status');
            throw new Error('Empty response body');
          }
          
          this.tokenService.setToken(response.body.token, credentials.rememberMe);
        }),
        map(response => response.body as LoginResponse), // Extract just the body for the return type
        catchError((error: HttpErrorResponse) => {
          console.error('Login error details:', {
            status: error.status,
            statusText: error.statusText,
            url: error.url,
            headers: error.headers ? Array.from(error.headers.keys()) : 'No headers',
            error: error.error,
            message: error.message
          });
          return this.handleError(error);
        })
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
    if (this.demoModeEnabled) {
      console.log('Demo mode enabled, bypassing authentication');
      
      // Create a dummy successful response
      const dummyResponse: LoginResponse = {
        token: 'demo-token-' + Date.now(),
        userType: 'GUEST',
        user: {
          firstName: 'Demo',
          lastName: 'User'
        }
      };
      
      // Store the token
      this.tokenService.setToken(dummyResponse.token, true);
      
      // Return as an observable
      return of(dummyResponse);
    }
    
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