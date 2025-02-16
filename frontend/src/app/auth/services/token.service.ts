import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TokenService {
  private readonly TOKEN_KEY = 'wedding_auth_token';
  private readonly SESSION_CLEANUP_KEY = 'clearOnClose';

  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  isAuthenticated$: Observable<boolean> = this.isAuthenticatedSubject.asObservable();

  constructor() {
    this.isAuthenticatedSubject.next(this.isTokenValid());
  }

  setToken(token: string, rememberMe: boolean): void {
    localStorage.setItem(this.TOKEN_KEY, token);

    // If remember me is false, store the cleanup flag in sessionStorage
    if (!rememberMe) {
      sessionStorage.setItem(this.SESSION_CLEANUP_KEY, 'true');
    }

    this.isAuthenticatedSubject.next(true);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  removeToken(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    sessionStorage.removeItem(this.SESSION_CLEANUP_KEY);
    this.isAuthenticatedSubject.next(false);
  }

  isTokenValid(): boolean {
    const token = this.getToken();
    if (!token) return false;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));

      const currentTime = Math.floor(Date.now() / 1000);
      return payload.exp > currentTime + 5;
    } catch {
      return false;
    }
  }

  /**
   * Performs cleanup for non-remembered sessions
   * This should be called in component's ngOnDestroy
   */
  performCleanup(): void {
    if (sessionStorage.getItem(this.SESSION_CLEANUP_KEY)) {
      this.removeToken();
    }
  }
}