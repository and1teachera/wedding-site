import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { SectionContainerComponent } from "../shared/components/layout/section-container/section-container.component";
import {RsvpService, ResponseStatus} from './services/rsvp.service';
import {
  AccommodationService,
  RoomBookingRequest,
  RoomBookingResponse
} from '../accommodation/services/accommodation.service';
import { NotificationService } from '../shared/services/notification.service';

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
  hasChanges = false;

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
  roomBooking: RoomBookingResponse | null = null;
  isBookingRoom = false;

  constructor(
      private router: Router,
      private rsvpService: RsvpService,
      private accommodationService: AccommodationService,
      private notificationService: NotificationService
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

          if (this.isSingleUser && response.status === 'PENDING') {
            // Already has a pending request
            this.hasRoomBooking = true;
          }
        }
      },
      error: (error) => {
        console.error('Error loading booking status:', error);
      }
    });
  }

  setFamilyAttendance(willAttend: boolean) {
    this.isLoading = true;

    // Set attendance for primary guest and all family members
    this.primaryGuest.isAttending = willAttend;
    this.familyMembers.forEach(member => {
      member.isAttending = willAttend;
    });

    // Create responses for all family members
    const familyResponses = {
      primaryGuest: {
        userId: this.primaryGuest.id,
        status: willAttend ? 'YES' : 'NO' as ResponseStatus,
        dietaryNotes: this.primaryGuest.dietaryRequirements,
        additionalNotes: this.primaryGuest.additionalNotes
      },
      familyMembers: this.familyMembers.map(member => ({
        userId: member.id,
        status: willAttend ? 'YES' : 'NO' as ResponseStatus,
        dietaryNotes: member.dietaryRequirements,
        additionalNotes: member.additionalNotes
      }))
    };

    this.rsvpService.submitRsvp(familyResponses).subscribe({
      next: () => {
        this.attending = willAttend;
        this.isLoading = false;
        
        // Add notification
        if (willAttend) {
          this.notificationService.success("Благодарим ви! Вашият отговор беше записан успешно!");
        } else {
          this.notificationService.info("Съжаляваме, че няма да можете да присъствате! Вашият отговор беше записан успешно!");
        }
        
        this.nextStep();
      },
      error: (error) => {
        console.error('Error saving attendance:', error);
        this.errorMessage = 'Възникна грешка при записване на отговора. Моля, опитайте отново по-късно.';
        this.isLoading = false;
        
        // Add error notification
        this.notificationService.error(this.errorMessage);
      }
    });
  }

  onFamilyMemberSelectionChange() {
    this.hasChanges = true;
    this.updateAttendanceStatus();
  }

  private updateAttendanceStatus() {
    const anyoneAttending = this.primaryGuest.isAttending ||
        this.familyMembers.some(member => member.isAttending);

    if (this.attending !== anyoneAttending) {
      this.attending = anyoneAttending;
      this.hasChanges = true;
    }
  }

  get isSingleUser(): boolean {
    return this.familyMembers.length === 0;
  }

  confirmFamilySelection() {
    this.isLoading = true;
    this.errorMessage = '';

    const familyResponses = {
      primaryGuest: {
        userId: this.primaryGuest.id,
        status: this.primaryGuest.isAttending ? 'YES' : 'NO' as ResponseStatus,
        dietaryNotes: this.primaryGuest.dietaryRequirements,
        additionalNotes: this.primaryGuest.additionalNotes
      },
      familyMembers: this.familyMembers.map(member => ({
        userId: member.id,
        status: member.isAttending ? 'YES' : 'NO' as ResponseStatus,
        dietaryNotes: member.dietaryRequirements,
        additionalNotes: member.additionalNotes
      }))
    };

    this.rsvpService.submitRsvp(familyResponses).subscribe({
      next: () => {
        this.isLoading = false;
        this.hasChanges = false;
        
        // Add notification
        this.notificationService.success("Благодарим ви! Вашият отговор беше записан успешно!");
        
        this.nextStep();
      },
      error: (error) => {
        console.error('Error saving family selections:', error);
        this.errorMessage = 'Възникна грешка при записване на отговорите. Моля, опитайте отново по-късно.';
        this.isLoading = false;
        
        // Add error notification
        this.notificationService.error(this.errorMessage);
      }
    });
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
          
          // Add notification
          const roomNumber = response.roomNumber ?? '';
          this.notificationService.success(`Стая номер ${roomNumber} беше успешно запазена за вас и семейството ви!`);
        }
      },
      error: (error) => {
        console.error('Error booking room:', error);
        this.errorMessage = 'Възникна грешка при резервиране на стаята. Моля, опитайте отново по-късно.';
        this.isBookingRoom = false;
        
        // Add error notification
        this.notificationService.error(this.errorMessage);
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
          
          // Add notification
          this.notificationService.success("Вашата резервация беше отказана. Благодарим ви!");
        }
      },
      error: (error) => {
        console.error('Error cancelling booking:', error);
        this.errorMessage = 'Възникна грешка при отказване на резервацията. Моля, опитайте отново по-късно.';
        this.isBookingRoom = false;
        
        // Add error notification
        this.notificationService.error(this.errorMessage);
      }
    });
  }

  finishProcess() {
    // Show completion notification
    this.notificationService.success("Вашият отговор беше успешно записан! Благодарим ви!");
    
    // Navigate to home with success message
    this.router.navigate(['/home'], {
      queryParams: {
        rsvpSuccess: true,
        attendees: this.getAttendingCount()
      }
    });
  }

  /**
   * Handles accommodation request for both single users and families
   */
  requestAccommodation(): void {
    if (this.isBookingRoom) {
      return;
    }

    this.isBookingRoom = true;
    this.errorMessage = '';

    const request: RoomBookingRequest = {
      notes: this.accommodationNotes
    };

    const request$ = this.isSingleUser ?
        this.accommodationService.requestSingleAccommodation(request) :
        this.accommodationService.bookRoom(request);

    request$.subscribe({
      next: (response) => {
        this.hasRoomBooking = response.success;
        this.roomBooking = response.success ? response : null;
        this.isBookingRoom = false;

        if (response.success) {
          if (!this.isSingleUser) {
            this.availableRooms--;
            this.notificationService.success(`Стая номер ${response.roomNumber || ''} беше успешно запазена за вас и семейството ви!`);
          } else {
            this.notificationService.success("Заявката ви беше записана и скоро ще бъде разгледана!");
          }
        }
      },
      error: (error) => {
        console.error('Error processing accommodation request:', error);
        this.errorMessage = 'Възникна грешка при обработката на заявката. Моля, опитайте отново по-късно.';
        this.isBookingRoom = false;
        
        // Add error notification
        this.notificationService.error(this.errorMessage);
      }
    });
  }

  /**
   * Loads the current accommodation status on init
   */
  private loadAccommodationStatus(): void {
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
        console.error('Error loading accommodation status:', error);
      }
    });
  }

  /**
   * Cancels the current accommodation request/booking
   */
  cancelAccommodation(): void {
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
          if (!this.isSingleUser) {
            this.availableRooms++;
            this.notificationService.success("Вашата резервация беше отказана. Благодарим ви!");
          } else {
            this.notificationService.success("Заявката ви беше отказана. Благодарим ви!");
          }
        }
      },
      error: (error) => {
        console.error('Error cancelling accommodation:', error);
        this.errorMessage = 'Възникна грешка при отказване на заявката. Моля, опитайте отново по-късно.';
        this.isBookingRoom = false;
        
        // Add error notification
        this.notificationService.error(this.errorMessage);
      }
    });
  }
}