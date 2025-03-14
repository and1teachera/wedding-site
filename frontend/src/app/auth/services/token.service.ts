import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

interface JwtPayload {
  exp: number;
  userType: string;
  familyId?: number | null;
  userId: number;
  sub: string;
  iat: number;
}

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

  /**
   * Performs cleanup for non-remembered sessions
   * This should be called in component's ngOnDestroy
   */
  performCleanup(): void {
    if (sessionStorage.getItem(this.SESSION_CLEANUP_KEY)) {
      this.removeToken();
    }
  }

  isTokenValid(): boolean {
    const token = this.getToken();
    if (!token) return false;

    try {
      const payload = this.decodeToken(token);
      if (!payload) return false;

      const currentTime = Math.floor(Date.now() / 1000);
      return payload.exp > currentTime + 5;
    } catch {
      return false;
    }
  }

  isAdmin(): boolean {
    const token = this.getToken();
    if (!token) return false;

    try {
      const payload = this.decodeToken(token);
      return payload?.userType === 'ADMIN';
    } catch {
      return false;
    }
  }

  getFamilyId(): number | null {
    try {
      const token = this.getToken();
      if (!token) return null;

      const payload = this.decodeToken(token);
      return payload?.familyId || null;
    } catch {
      return null;
    }
  }

  private decodeToken(token: string): JwtPayload | null {
    try {
      const parts = token.split('.');
      if (parts.length !== 3) {
        throw new Error('Invalid token format');
      }

      const payload = parts[1];
      const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
      const padding = '='.repeat((4 - base64.length % 4) % 4);

      const decoded = decodeURIComponent(atob(base64 + padding)
          .split('')
          .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join(''));

      return JSON.parse(decoded);
    } catch (error) {
      console.error('Error decoding token:', error);
      return null;
    }
  }
}