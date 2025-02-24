import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface RoomBookingRequest {
  notes?: string;
}

export interface RoomBookingResponse {
  success: boolean;
  message: string;
  roomNumber?: number;
  familyId?: number;
  status?: string;
  notes?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AccommodationService {
  private readonly API_URL = '/api/accommodation';

  constructor(private http: HttpClient) {}

  /**
   * Get the number of available rooms
   */
  getAvailableRoomsCount(): Observable<number> {
    return this.http.get<number>(`${this.API_URL}/available-rooms`);
  }

  /**
   * Book a room for the current user's family
   */
  bookRoom(request: RoomBookingRequest): Observable<RoomBookingResponse> {
    return this.http.post<RoomBookingResponse>(`${this.API_URL}/book`, request);
  }

  /**
   * Get the current booking or request status for the user
   */
  getBookingStatus(): Observable<RoomBookingResponse> {
    return this.http.get<RoomBookingResponse>(`${this.API_URL}/booking-status`);
  }

  /**
   * Cancel a room booking or request
   */
  cancelBooking(): Observable<RoomBookingResponse> {
    return this.http.post<RoomBookingResponse>(`${this.API_URL}/cancel`, {});
  }

  /**
   * Request accommodation for a single user
   */
  requestSingleAccommodation(request: RoomBookingRequest): Observable<RoomBookingResponse> {
    return this.http.post<RoomBookingResponse>(`${this.API_URL}/request-single`, request);
  }
}