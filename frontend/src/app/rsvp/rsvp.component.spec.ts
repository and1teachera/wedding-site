import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RsvpComponent } from './rsvp.component';
import { RsvpService } from './services/rsvp.service';
import { of } from 'rxjs';

describe('RsvpComponent', () => {
  let component: RsvpComponent;
  let fixture: ComponentFixture<RsvpComponent>;

  beforeEach(async () => {
    // Create a mock RsvpService
    const rsvpServiceMock = jasmine.createSpyObj('RsvpService', ['getFamilyMembers']);

    // Make the getFamilyMembers method return some test data
    rsvpServiceMock.getFamilyMembers.and.returnValue(of({
      primaryUser: {
        id: 1,
        firstName: 'Test',
        lastName: 'User',
        isChild: false,
        rsvpStatus: 'MAYBE'
      },
      familyMembers: []
    }));

    await TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        RsvpComponent
      ],
      providers: [
        { provide: RsvpService, useValue: rsvpServiceMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RsvpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});