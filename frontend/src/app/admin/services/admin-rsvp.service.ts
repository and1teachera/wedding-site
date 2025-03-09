import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface RsvpEntry {
  userId: number;
  firstName: string;
  lastName: string;
  email: string;
  status: string;
  isChild: boolean;
  dietaryNotes?: string;
  additionalNotes?: string;
  familyName: string;
}

export interface AllRsvpResponses {
  responses: RsvpEntry[];
  totalGuests: number;
  confirmedGuests: number;
  pendingGuests: number;
  declinedGuests: number;
}

@Injectable({
  providedIn: 'root'
})
export class AdminRsvpService {
  private readonly API_URL = '/api/rsvp';

  constructor(private http: HttpClient) {}

  /**
   * Get all RSVP responses (admin only)
   */
  getAllRsvpResponses(): Observable<AllRsvpResponses> {
    return this.http.get<AllRsvpResponses>(`${this.API_URL}/admin/all-responses`);
  }
}