import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UserCreationRequest {
  firstName: string;
  lastName: string;
  email?: string;
  phone?: string;
  isChild?: boolean;
}

export interface FamilyCreationRequest {
  primaryUser: UserCreationRequest;
  familyName: string;
  familyMembers: UserCreationRequest[];
}

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private readonly API_URL = '/api/admin';

  constructor(private http: HttpClient) {}

  createUser(user: UserCreationRequest): Observable<void> {
    return this.http.post<void>(`${this.API_URL}/users`, user);
  }

  createFamily(family: FamilyCreationRequest): Observable<void> {
    return this.http.post<void>(`${this.API_URL}/families`, family);
  }
}