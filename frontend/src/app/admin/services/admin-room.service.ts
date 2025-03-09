import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface BookingInfo {
  bookingId: number;
  status: string;
  bookingTime: string;
  bookedBy: string;
  familyId: number | null;
  notes: string | null;
}

export interface RoomInfo {
  roomId: number;
  roomNumber: number;
  available: boolean;
  booking: BookingInfo | null;
}

export interface RoomAvailability {
  rooms: RoomInfo[];
  totalRooms: number;
  availableRooms: number;
  bookedRooms: number;
}

@Injectable({
  providedIn: 'root'
})
export class AdminRoomService {
  private readonly API_URL = '/api/accommodation';

  constructor(private http: HttpClient) {}

  /**
   * Get all rooms with their availability and booking details (admin only)
   */
  getAllRoomsWithBookings(): Observable<RoomAvailability> {
    return this.http.get<RoomAvailability>(`${this.API_URL}/admin/rooms`);
  }
}