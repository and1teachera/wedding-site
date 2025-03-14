import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { TokenService } from './token.service';
import { catchError, finalize, map } from 'rxjs/operators';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LogoutService {
  private readonly API_URL = '/api/auth';

  constructor(
      private http: HttpClient,
      private tokenService: TokenService,
      private router: Router
  ) {}

  /**
   * Handles the complete logout process including:
   * 1. Server-side session termination
   * 2. Local token cleanup
   * 3. Navigation to login page
   *
   * @returns Observable<boolean> indicating logout success
   */
  logout(): Observable<boolean> {
    return this.http.post<void>(`${this.API_URL}/logout`, {})
        .pipe(
            map(() => true), // Convert successful void response to true
            catchError(error => {
              console.error('Logout request failed:', error);
              // Always return boolean (true) even on error
              return of(true);
            }),
            finalize(() => {
              this.performLocalCleanup();
            })
        );
  }

  /**
   * Performs an immediate logout without waiting for server response.
   * Useful for handling authentication errors or forced logouts.
   */
  forceLogout(): void {
    this.performLocalCleanup();
  }

  /**
   * Handles cleanup of local storage and navigation
   * @private
   */
  private performLocalCleanup(): void {
    // Clear authentication data
    this.tokenService.removeToken();
    sessionStorage.removeItem('clearOnClose');

    // Navigate to login page
    this.router.navigate(['/login'], {
      queryParams: { reason: 'logged-out' }
    });
  }
}