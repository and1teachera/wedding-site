import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface SingleUserRequest {
  requestId: number;
  userId: number;
  userName: string;
  requestDate: string;
  status: string;
  notes: string | null;
  processed: boolean;
  processedDate: string | null;
}

export interface SingleUserAccommodationResponses {
  requests: SingleUserRequest[];
  totalRequests: number;
  pendingRequests: number;
  processedRequests: number;
  cancelledRequests: number;
}

interface ApiResponse {
  success: boolean;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class SingleAccommodationService {
  private readonly API_URL = '/api/accommodation';

  constructor(private http: HttpClient) {}

  /**
   * Get all single user accommodation requests (admin only)
   */
  getAllSingleUserRequests(): Observable<SingleUserAccommodationResponses> {
    return this.http.get<SingleUserAccommodationResponses>(`${this.API_URL}/admin/single-requests`);
  }
  
  /**
   * Approve a single user accommodation request (admin only)
   */
  approveRequest(requestId: number): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(`${this.API_URL}/admin/single-requests/${requestId}/approve`, {});
  }
  
  /**
   * Reject a single user accommodation request (admin only)
   */
  rejectRequest(requestId: number): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(`${this.API_URL}/admin/single-requests/${requestId}/reject`, {});
  }
}