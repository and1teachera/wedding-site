import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import {SectionContainerComponent} from "../shared/components/layout/section-container/section-container.component";

interface FamilyMember {
  id: string;
  firstName: string;
  lastName: string;
  relation: 'primary' | 'spouse' | 'child';
  isAttending: boolean;
  dietaryRequirements?: string;
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

  primaryGuest: FamilyMember = {
    id: '1',
    firstName: 'Самуил',
    lastName: 'Спасов',
    relation: 'primary',
    isAttending: false
  };

  familyMembers: FamilyMember[] = [
    {
      id: '2',
      firstName: 'Йоана',
      lastName: 'Спасова',
      relation: 'spouse',
      isAttending: false
    },
    {
      id: '3',
      firstName: 'Стефан',
      lastName: 'Спасов',
      relation: 'child',
      isAttending: false
    },
    {
      id: '4',
      firstName: 'Луиза',
      lastName: 'Спасова',
      relation: 'child',
      isAttending: false
    }
  ];

  needsAccommodation = false;
  accommodationNotes = '';
  availableRooms = 5;

  constructor(private router: Router) {}

  ngOnInit() {
    console.log('Fetching family data for:', this.primaryGuest.firstName);
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

  async submitRSVP() {
    try {
      const rsvpData = {
        primaryGuest: this.primaryGuest,
        familyMembers: this.familyMembers,
        accommodation: {
          required: this.needsAccommodation,
          notes: this.accommodationNotes
        }
      };

      console.log('Submitting family RSVP:', rsvpData);

      await this.router.navigate(['/home']);
    } catch (error) {
      console.error('Error submitting RSVP:', error);
    }
  }
}