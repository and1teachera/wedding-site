import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type ResponseStatus = 'MAYBE' | 'YES' | 'NO';

export interface GuestResponse {
  userId: number;
  status: ResponseStatus;
  dietaryNotes?: string;
  additionalNotes?: string;
}

export interface RsvpRequest {
  primaryGuest: GuestResponse;
  familyMembers?: GuestResponse[];
}

export interface RsvpResponse {
  success: boolean;
  message: string;
  primaryUserId: number;
  confirmedAttendees: number;
}

export interface UserDto {
  id: number;
  firstName: string;
  lastName: string;
  isChild: boolean;
  rsvpStatus: ResponseStatus;
  dietaryNotes?: string;
  additionalNotes?: string;
}

export interface FamilyMembersResponse {
  primaryUser: UserDto;
  familyMembers: UserDto[];
}

@Injectable({
  providedIn: 'root'
})
export class RsvpService {
  private readonly API_URL = '/api/rsvp';

  constructor(private http: HttpClient) {}

  /**
   * Submit RSVP for the current user and their family
   */
  submitRsvp(request: RsvpRequest): Observable<RsvpResponse> {
    return this.http.post<RsvpResponse>(this.API_URL, request);
  }

  /**
   * Get family member details for the authenticated user
   */
  getFamilyMembers(): Observable<FamilyMembersResponse> {
    return this.http.get<FamilyMembersResponse>(`${this.API_URL}/family-members`);
  }

  /**
   * Save the primary guest's response only
   * @param response The primary guest's response
   */
  savePrimaryGuestResponse(response: GuestResponse): Observable<RsvpResponse> {
    return this.http.post<RsvpResponse>(`${this.API_URL}/primary`, response);
  }
}