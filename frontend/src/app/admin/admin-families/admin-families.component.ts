import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminFamilyService, FamilyOverviewResponse } from '../services/admin-family.service';
import { SectionContainerComponent } from "../../shared/components/layout/section-container/section-container.component";
import { AdminNavComponent } from '../admin-nav/admin-nav.component';

@Component({
  selector: 'app-admin-families',
  standalone: true,
  imports: [CommonModule, SectionContainerComponent, AdminNavComponent],
  templateUrl: './admin-families.component.html',
  styleUrls: ['./admin-families.component.css']
})
export class AdminFamiliesComponent implements OnInit {
  familyData: FamilyOverviewResponse | null = null;
  isLoading = true;
  error: string | null = null;
  expandedFamilies: Record<number, boolean> = {};

  constructor(private adminFamilyService: AdminFamilyService) {}

  ngOnInit(): void {
    this.loadFamilyData();
  }

  loadFamilyData(): void {
    this.isLoading = true;
    this.error = null;

    this.adminFamilyService.getAllFamilies().subscribe({
      next: (data) => {
        this.familyData = data;
        
        // Initialize expanded state for families
        data.families.forEach(family => {
          this.expandedFamilies[family.id] = false;
        });
        
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading family data:', err);
        this.error = 'Failed to load family data. Please try again later.';
        this.isLoading = false;
      }
    });
  }

  toggleFamily(familyId: number): void {
    this.expandedFamilies[familyId] = !this.expandedFamilies[familyId];
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

  getRsvpStatusCount(status: string): number {
    if (!this.familyData) return 0;
    
    let count = 0;
    
    // Count family members
    this.familyData.families.forEach(family => {
      family.members.forEach(member => {
        if (member.rsvpStatus === status) count++;
      });
    });
    
    // Count single users
    this.familyData.singleUsers.forEach(user => {
      if (user.rsvpStatus === status) count++;
    });
    
    return count;
  }

  getAccommodationStatusClass(status: string | undefined): string {
    if (!status) return 'text-gray-600';
    
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
}