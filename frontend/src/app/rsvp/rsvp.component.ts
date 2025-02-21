import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { SectionContainerComponent } from "../shared/components/layout/section-container/section-container.component";
import { RsvpService, GuestResponse } from './services/rsvp.service';
import { AccommodationService } from '../accommodation/services/accommodation.service';

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

  // Room booking related properties
  hasRoomBooking = false;
  roomBooking: any = null;
  isBookingRoom = false;

  constructor(
      private router: Router,
      private rsvpService: RsvpService,
      private accommodationService: AccommodationService
  ) {}

  ngOnInit() {
    this.loadFamilyData();
    this.loadAccommodationData();
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

  private loadAccommodationData() {
    this.accommodationService.getAvailableRoomsCount().subscribe({
      next: (count) => {
        this.availableRooms = count;
      },
      error: (error) => {
        console.error('Error loading available rooms:', error);
      }
    });

    this.accommodationService.getBookingStatus().subscribe({
      next: (response) => {
        this.hasRoomBooking = response.success;
        this.roomBooking = response.success ? response : null;

        if (response.success) {
          this.needsAccommodation = true;
          this.accommodationNotes = response.notes || '';
        }
      },
      error: (error) => {
        console.error('Error loading booking status:', error);
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

  bookRoom() {
    if (this.isBookingRoom) {
      return;
    }

    this.isBookingRoom = true;
    this.errorMessage = '';

    this.accommodationService.bookRoom({ notes: this.accommodationNotes }).subscribe({
      next: (response) => {
        this.hasRoomBooking = response.success;
        this.roomBooking = response.success ? response : null;
        this.isBookingRoom = false;

        if (response.success) {
          this.availableRooms--;
        }
      },
      error: (error) => {
        console.error('Error booking room:', error);
        this.errorMessage = 'Възникна грешка при резервиране на стаята. Моля, опитайте отново по-късно.';
        this.isBookingRoom = false;
      }
    });
  }

  cancelRoomBooking() {
    if (this.isBookingRoom) {
      return;
    }

    this.isBookingRoom = true;
    this.errorMessage = '';

    this.accommodationService.cancelBooking().subscribe({
      next: (response) => {
        this.hasRoomBooking = false;
        this.roomBooking = null;
        this.isBookingRoom = false;

        if (response.success) {
          this.availableRooms++;
        }
      },
      error: (error) => {
        console.error('Error cancelling booking:', error);
        this.errorMessage = 'Възникна грешка при отказване на резервацията. Моля, опитайте отново по-късно.';
        this.isBookingRoom = false;
      }
    });
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

    // Submit the RSVP without accommodation info since it's not in the interface
    this.rsvpService.submitRsvp({
      primaryGuest: primaryGuestResponse,
      familyMembers: familyMemberResponses
    }).subscribe({
      next: (response) => {
        // If accommodation is needed and no booking exists, create one after RSVP is submitted
        if (this.needsAccommodation && !this.hasRoomBooking && this.availableRooms > 0) {
          this.bookRoom();
        }

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