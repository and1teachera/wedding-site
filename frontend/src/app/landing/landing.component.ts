import { Component } from '@angular/core';
import { NavigationComponent } from './components/navigation/navigation.component';
import { IntroSectionComponent } from './components/intro-section/intro-section.component';
import { HonorFamilyComponent } from "./components/honor-family/honor-family.component";
import { VenueComponent } from "./components/venue/venue.component";
import { AccommodationComponent } from "./components/accommodation/accommodation.component";
import { ScheduleComponent } from "./components/schedule/schedule.component";
import { InfoSectionComponent } from "./components/info-section/info-section.component";
import { ContactSectionComponent } from "./components/contact-section/contact-section.component";

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [
    NavigationComponent, 
    IntroSectionComponent, 
    HonorFamilyComponent,
    VenueComponent, 
    AccommodationComponent,
    ScheduleComponent, 
    InfoSectionComponent, 
    ContactSectionComponent
  ],
  templateUrl: './landing.component.html',
  styleUrls: ['./landing.component.css'],
})
export class LandingComponent {
  pageTitle = "It's Just Forever, No Big Deal";
  weddingDateString = "9ти Август 2025";
  coupleNames = 'Ангел & Мирена';
  weddingDate = new Date('2025-08-09');

  getDaysUntilWedding(): number {
    const today = new Date();
    const timeDiff = this.weddingDate.getTime() - today.getTime();
    return Math.ceil(timeDiff / (1000 * 3600 * 24));
  }
}
