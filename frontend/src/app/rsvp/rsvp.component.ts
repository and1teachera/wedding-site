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
  stepLabels = ['RSVP', 'Потвърждение за семейството', 'Настаняване'];
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
        // Set primary guest data
        this.primaryGuest = {
          id: response.primaryUser.id,
          firstName: response.primaryUser.firstName,
          lastName: response.primaryUser.lastName,
          relation: 'primary',
          isAttending: response.primaryUser.rsvpStatus === 'YES',
          dietaryRequirements: response.primaryUser.dietaryNotes
        };

        // Set family members data
        this.familyMembers = response.familyMembers.map(member => ({
          id: member.id,
          firstName: member.firstName,
          lastName: member.lastName,
          relation: member.isChild ? 'child' : 'spouse',
          isAttending: member.rsvpStatus === 'YES',
          dietaryRequirements: member.dietaryNotes
        }));

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

    if (willAttend) {
      this.nextStep();
    } else {
      this.submitRSVP();
    }
  }

  getAttendingCount(): number {
    return [this.primaryGuest, ...this.familyMembers]
        .filter(member => member.isAttending).length;
  }

  getRelationLabel(relation: string): string {
    switch (relation) {
      case 'spouse': return 'Съпруга';
      case 'child': return 'Дете';
      default: return '';
    }
  }

  nextStep() {
    if (this.currentStep < 3 && this.validateCurrentStep()) {
      this.currentStep++;
    }
  }

  previousStep() {
    if (this.currentStep > 1) {
      this.currentStep--;
    }
  }

  validateCurrentStep(): boolean {
    switch (this.currentStep) {
      case 1:
        return this.attending !== null;
      case 2:
        return true;
      default:
        return true;
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