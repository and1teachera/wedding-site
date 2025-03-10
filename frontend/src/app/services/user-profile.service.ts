import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UserProfile {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  isAdmin: boolean;
}

export interface UserProfileUpdate {
  email: string;
  phone: string;
}

export interface PasswordChange {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserProfileService {
  private readonly API_URL = '/api/user';

  constructor(private http: HttpClient) {}

  /**
   * Get the current user's profile
   */
  getUserProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.API_URL}/profile`);
  }

  /**
   * Update the current user's profile
   */
  updateUserProfile(profile: UserProfileUpdate): Observable<UserProfile> {
    return this.http.put<UserProfile>(`${this.API_URL}/profile`, profile);
  }

  /**
   * Change the current user's password
   */
  changePassword(passwordChange: PasswordChange): Observable<void> {
    return this.http.put<void>(`${this.API_URL}/change-password`, passwordChange);
  }
}