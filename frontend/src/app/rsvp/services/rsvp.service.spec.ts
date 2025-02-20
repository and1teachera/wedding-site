import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RsvpService } from './rsvp.service';

describe('RsvpService', () => {
  let service: RsvpService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [RsvpService]
    });
    service = TestBed.inject(RsvpService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});