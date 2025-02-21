import { TestBed } from '@angular/core/testing';

import { AccommodationService } from './accommodation.service';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {RsvpService} from "../../rsvp/services/rsvp.service";

describe('AccommodationService', () => {
  let service: AccommodationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AccommodationService]
    });
    service = TestBed.inject(AccommodationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
