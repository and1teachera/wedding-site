import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NavigationComponent } from './navigation.component';
import { TokenService } from '../../../auth/services/token.service';
import { LogoutService } from '../../../auth/services/logout.service';
import { of } from 'rxjs';

describe('NavigationComponent', () => {
  let component: NavigationComponent;
  let fixture: ComponentFixture<NavigationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientTestingModule,
        NavigationComponent
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
            logout: () => of(true)
          }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(NavigationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should toggle menu', () => {
    expect(component.isMenuOpen).toBeFalse();
    component.toggleMenu();
    expect(component.isMenuOpen).toBeTrue();
  });

  it('should handle scroll events', () => {
    component.onWindowScroll();
    expect(component).toBeTruthy();
  });
});