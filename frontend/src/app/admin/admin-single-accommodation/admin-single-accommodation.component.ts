import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SingleAccommodationService, SingleUserAccommodationResponses } from '../services/single-accommodation.service';
import { SectionContainerComponent } from "../../shared/components/layout/section-container/section-container.component";
import { AdminNavComponent } from '../admin-nav/admin-nav.component';

@Component({
  selector: 'app-admin-single-accommodation',
  standalone: true,
  imports: [CommonModule, SectionContainerComponent, AdminNavComponent],
  templateUrl: './admin-single-accommodation.component.html',
  styleUrls: ['./admin-single-accommodation.component.css']
})
export class AdminSingleAccommodationComponent implements OnInit {
  requests: SingleUserAccommodationResponses | null = null;
  isLoading = true;
  error: string | null = null;
  processingRequest: { [key: number]: boolean } = {};

  constructor(private singleAccommodationService: SingleAccommodationService) {}

  ngOnInit(): void {
    this.loadRequests();
  }

  loadRequests(): void {
    this.isLoading = true;
    this.error = null;

    this.singleAccommodationService.getAllSingleUserRequests().subscribe({
      next: (data) => {
        this.requests = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading single user accommodation requests:', err);
        this.error = 'Failed to load accommodation requests. Please try again later.';
        this.isLoading = false;
      }
    });
  }

  approveRequest(requestId: number): void {
    this.processingRequest[requestId] = true;
    
    this.singleAccommodationService.approveRequest(requestId).subscribe({
      next: () => {
        this.loadRequests();
      },
      error: (err) => {
        console.error('Error approving request:', err);
        this.error = 'Failed to approve request. Please try again later.';
        this.processingRequest[requestId] = false;
      }
    });
  }
  
  rejectRequest(requestId: number): void {
    this.processingRequest[requestId] = true;
    
    this.singleAccommodationService.rejectRequest(requestId).subscribe({
      next: () => {
        this.loadRequests();
      },
      error: (err) => {
        console.error('Error rejecting request:', err);
        this.error = 'Failed to reject request. Please try again later.';
        this.processingRequest[requestId] = false;
      }
    });
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'PENDING':
        return 'text-yellow-600';
      case 'APPROVED':
        return 'text-green-600';
      case 'REJECTED':
      case 'CANCELLED':
        return 'text-red-600';
      default:
        return 'text-gray-600';
    }
  }

  formatDate(dateString: string): string {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleString();
  }
}