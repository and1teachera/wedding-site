import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AdminService, UserCreationRequest, FamilyCreationRequest } from './admin.service';

describe('AdminService', () => {
  let service: AdminService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AdminService]
    });

    service = TestBed.inject(AdminService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should create a user', () => {
    const mockUser: UserCreationRequest = {
      firstName: 'John',
      lastName: 'Doe',
      email: 'john@example.com'
    };

    service.createUser(mockUser).subscribe();

    const req = httpMock.expectOne('/api/admin/user');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockUser);
  });

  it('should create a family', () => {
    const mockFamily: FamilyCreationRequest = {
      primaryUser: {
        firstName: 'John',
        lastName: 'Doe',
        email: 'john@example.com'
      },
      familyName: 'Doe Family',
      familyMembers: [{
        firstName: 'Jane',
        lastName: 'Doe',
        email: 'jane@example.com'
      }]
    };

    service.createFamily(mockFamily).subscribe();

    const req = httpMock.expectOne('/api/admin/family');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockFamily);
  });
});