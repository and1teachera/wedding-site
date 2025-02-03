import { Component } from '@angular/core';
import { HeaderComponent } from './components/header/header.component';
import { NavigationComponent } from './components/navigation/navigation.component';
import {HeroComponent} from "./components/hero/hero.component";
import {HonorFamilyComponent} from "./components/honor-family/honor-family.component";
import {FooterComponent} from "../shared/components/footer/footer.component";
import {VenueComponent} from "./components/venue/venue.component";
import {AccommodationComponent} from "./components/accommodation/accommodation.component";
import {ScheduleComponent} from "./components/schedule/schedule.component";
import {InfoSectionComponent} from "./components/info-section/info-section.component";
import {ContactSectionComponent} from "./components/contact-section/contact-section.component";

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [HeaderComponent, NavigationComponent, HeroComponent, HonorFamilyComponent,
    VenueComponent, FooterComponent, AccommodationComponent, ScheduleComponent, InfoSectionComponent, ContactSectionComponent],
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
