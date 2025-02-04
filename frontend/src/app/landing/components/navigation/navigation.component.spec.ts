import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { NavigationComponent } from './navigation.component';
import { ContentContainerComponent } from '../../../shared/components/layout/content-container/content-container.component';

describe('NavigationComponent', () => {
  let component: NavigationComponent;
  let fixture: ComponentFixture<NavigationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        NavigationComponent,
        ContentContainerComponent
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
    component.toggleMenu();
    expect(component.isMenuOpen).toBeFalse();
  });

  it('should handle scroll events', () => {
    expect(component.isScrolled).toBeFalse();
    window.scrollY = 100;
    component.onWindowScroll();
    expect(component.isScrolled).toBeTrue();
    window.scrollY = 0;
    component.onWindowScroll();
    expect(component.isScrolled).toBeFalse();
  });
});