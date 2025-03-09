import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminRsvpService, AllRsvpResponses, RsvpEntry } from '../services/admin-rsvp.service';
import { SectionContainerComponent } from "../../shared/components/layout/section-container/section-container.component";
import { AdminNavComponent } from '../admin-nav/admin-nav.component';

@Component({
  selector: 'app-admin-rsvp-list',
  standalone: true,
  imports: [CommonModule, SectionContainerComponent, AdminNavComponent],
  templateUrl: './admin-rsvp-list.component.html',
  styleUrls: ['./admin-rsvp-list.component.css']
})
export class AdminRsvpListComponent implements OnInit {
  responses: AllRsvpResponses | null = null;
  isLoading = true;
  error: string | null = null;

  constructor(private adminRsvpService: AdminRsvpService) {}

  ngOnInit(): void {
    this.loadResponses();
  }

  loadResponses(): void {
    this.isLoading = true;
    this.error = null;

    this.adminRsvpService.getAllRsvpResponses().subscribe({
      next: (data) => {
        this.responses = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading RSVP responses:', err);
        this.error = 'Failed to load RSVP responses. Please try again later.';
        this.isLoading = false;
      }
    });
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'YES':
        return 'text-green-600';
      case 'NO':
        return 'text-red-600';
      default:
        return 'text-yellow-600';
    }
  }
}