import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { SectionContainerComponent } from "../shared/components/layout/section-container/section-container.component";
import { RsvpService, GuestResponse } from './services/rsvp.service';

interface FamilyMember {
  id: number;
  firstName: string;
  lastName: string;
  relation: 'primary' | 'spouse' | 'child';
  isAttending: boolean;
  dietaryRequirements?: string;
  additionalNotes?: string;
}

@Component({
  selector: 'app-rsvp',
  standalone: true,
  imports: [CommonModule, FormsModule, SectionContainerComponent],
  templateUrl: './rsvp.component.html'
})
export class RsvpComponent implements OnInit {
  currentStep = 1;
  attending: boolean | null = null;
  stepLabels = ['RSVP', 'Гости', 'Настаняване'];
  isLoading = true;
  errorMessage = '';

  primaryGuest: FamilyMember = {
    id: 0,
    firstName: '',
    lastName: '',
    relation: 'primary',
    isAttending: false
  };


  needsAccommodation = false;
  accommodationNotes = '';
  availableRooms = 5;
  familyMembers: FamilyMember[] = [];


  constructor(
      private router: Router,
      private rsvpService: RsvpService
  ) {}

ngOnInit() {
  this.loadFamilyData();
}

private loadFamilyData() {
  this.isLoading = true;
  this.errorMessage = '';

  this.rsvpService.getFamilyMembers().subscribe({
    next: (response) => {
      // Set primary guest data and determine attendance status
      const primaryStatus = response.primaryUser.rsvpStatus;
      const isAttending = primaryStatus === 'YES';
      const hasResponded = primaryStatus !== 'MAYBE';

      this.primaryGuest = {
        id: response.primaryUser.id,
        firstName: response.primaryUser.firstName,
        lastName: response.primaryUser.lastName,
        relation: 'primary',
        isAttending: isAttending,
        dietaryRequirements: response.primaryUser.dietaryNotes,
        additionalNotes: response.primaryUser.additionalNotes
      };

      // Set family members data
      this.familyMembers = response.familyMembers.map(member => ({
        id: member.id,
        firstName: member.firstName,
        lastName: member.lastName,
        relation: member.isChild ? 'child' : 'spouse',
        isAttending: member.rsvpStatus === 'YES',
        dietaryRequirements: member.dietaryNotes,
        additionalNotes: member.additionalNotes
      }));

      // If user has already responded, set the appropriate state
      if (hasResponded) {
        this.attending = isAttending;

        // If attending, auto-advance to step 2
        if (isAttending && this.currentStep === 1) {
          this.currentStep = 2;
        }
      }

      this.isLoading = false;
    },
    error: (error) => {
      console.error('Error loading family data:', error);
      this.errorMessage = 'Възникна грешка при зареждане на данните. Моля, опитайте отново по-късно.';
      this.isLoading = false;
    }
  });
  }

  setAttendance(willAttend: boolean) {
    this.attending = willAttend;
    this.primaryGuest.isAttending = willAttend;

    this.nextStep();
  }

  getAttendingCount(): number {
    return [this.primaryGuest, ...this.familyMembers]
        .filter(member => member.isAttending).length;
  }

  nextStep() {
    if (this.currentStep < 3) {
      this.currentStep++;
    }
  }

  previousStep() {
    if (this.currentStep > 1) {
      this.currentStep--;
    }
  }

  submitRSVP() {
    if (this.isLoading) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    // Prepare the guest responses
    const primaryGuestResponse: GuestResponse = {
      userId: this.primaryGuest.id,
      status: this.primaryGuest.isAttending ? 'YES' : 'NO',
      dietaryNotes: this.primaryGuest.dietaryRequirements,
      additionalNotes: this.primaryGuest.additionalNotes
    };

    const familyMemberResponses: GuestResponse[] = this.familyMembers.map(member => ({
      userId: member.id,
      status: member.isAttending ? 'YES' : 'NO',
      dietaryNotes: member.dietaryRequirements,
      additionalNotes: member.additionalNotes
    }));

    // Submit the RSVP
    this.rsvpService.submitRsvp({
      primaryGuest: primaryGuestResponse,
      familyMembers: familyMemberResponses
    }).subscribe({
      next: (response) => {
        this.isLoading = false;
        // Navigate to home with success message
        this.router.navigate(['/home'], {
          queryParams: {
            rsvpSuccess: true,
            attendees: response.confirmedAttendees
          }
        });
      },
      error: (error) => {
        console.error('Error submitting RSVP:', error);
        this.errorMessage = 'Възникна грешка при записване на отговора. Моля, опитайте отново по-късно.';
        this.isLoading = false;
      }
    });
  }
}