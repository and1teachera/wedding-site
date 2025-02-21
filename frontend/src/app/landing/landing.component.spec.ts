import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { LandingComponent } from './landing.component';
import { HeaderComponent } from './components/header/header.component';
import { NavigationComponent } from './components/navigation/navigation.component';
import { HeroComponent } from './components/hero/hero.component';
import { HonorFamilyComponent } from './components/honor-family/honor-family.component';
import { FooterComponent } from '../shared/components/footer/footer.component';
import { VenueComponent } from './components/venue/venue.component';
import { AccommodationComponent } from './components/accommodation/accommodation.component';
import { ScheduleComponent } from './components/schedule/schedule.component';
import { InfoSectionComponent } from './components/info-section/info-section.component';
import { ContactSectionComponent } from './components/contact-section/contact-section.component';
import {LogoutService} from "../auth/services/logout.service";
import {of} from "rxjs";
import {TokenService} from "../auth/services/token.service";
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('LandingComponent', () => {
  let component: LandingComponent;
  let fixture: ComponentFixture<LandingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        LandingComponent,
        HeaderComponent,
        NavigationComponent,
        HeroComponent,
        HonorFamilyComponent,
        FooterComponent,
        VenueComponent,
        AccommodationComponent,
        ScheduleComponent,
        InfoSectionComponent,
        ContactSectionComponent,
        HttpClientTestingModule
      ],
      providers: [
        {
          provide: TokenService,
          useValue: {
            isTokenValid: () => true,
            isAdmin: () => false
          }
        },
        {
          provide: LogoutService,
          useValue: {
            logout: () => of(true),
            forceLogout: () => {}
          }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LandingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should calculate days until wedding correctly', () => {
    const weddingDate = new Date('2025-08-09');
    const today = new Date();
    const expectedDays = Math.ceil((weddingDate.getTime() - today.getTime()) / (1000 * 3600 * 24));
    expect(component.getDaysUntilWedding()).toBe(expectedDays);
  });
});