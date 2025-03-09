import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminRoomService, RoomAvailability, RoomInfo } from '../services/admin-room.service';
import { SectionContainerComponent } from "../../shared/components/layout/section-container/section-container.component";
import { AdminNavComponent } from '../admin-nav/admin-nav.component';

@Component({
  selector: 'app-admin-rooms',
  standalone: true,
  imports: [CommonModule, SectionContainerComponent, AdminNavComponent],
  templateUrl: './admin-rooms.component.html',
  styleUrls: ['./admin-rooms.component.css']
})
export class AdminRoomsComponent implements OnInit {
  roomData: RoomAvailability | null = null;
  isLoading = true;
  error: string | null = null;

  constructor(private adminRoomService: AdminRoomService) {}

  ngOnInit(): void {
    this.loadRoomData();
  }

  loadRoomData(): void {
    this.isLoading = true;
    this.error = null;

    this.adminRoomService.getAllRoomsWithBookings().subscribe({
      next: (data) => {
        this.roomData = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading room data:', err);
        this.error = 'Failed to load room availability data. Please try again later.';
        this.isLoading = false;
      }
    });
  }

  getStatusClass(available: boolean): string {
    return available ? 'text-green-600' : 'text-red-600';
  }

  getBookingStatusClass(status: string): string {
    switch (status) {
      case 'CONFIRMED':
        return 'text-green-600';
      case 'CANCELLED':
        return 'text-red-600';
      default:
        return 'text-yellow-600';
    }
  }

  formatDate(dateString: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleString();
  }
}