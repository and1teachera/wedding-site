import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface FamilyMember {
  id: number;
  firstName: string;
  lastName: string;
  isChild: boolean;
  rsvpStatus: string;
  dietaryNotes?: string;
  additionalNotes?: string;
}

export interface Family {
  id: number;
  name: string;
  members: FamilyMember[];
  hasRoomBooked: boolean;
  roomNumber?: number;
  totalMembers: number;
  confirmedMembers: number;
}

export interface SingleUser {
  id: number;
  firstName: string;
  lastName: string;
  email?: string;
  rsvpStatus: string;
  hasAccommodationRequest: boolean;
  accommodationStatus?: string;
}

export interface FamilyOverviewResponse {
  families: Family[];
  singleUsers: SingleUser[];
  totalFamilies: number;
  totalSingleUsers: number;
  totalGuests: number;
  confirmedGuests: number;
}

@Injectable({
  providedIn: 'root'
})
export class AdminFamilyService {
  private readonly API_URL = '/api/admin';

  constructor(private http: HttpClient) {}

  /**
   * Get all families with their members and relevant details
   */
  getAllFamilies(): Observable<FamilyOverviewResponse> {
    return this.http.get<FamilyOverviewResponse>(`${this.API_URL}/families-overview`);
  }
}