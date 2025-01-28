import { TestBed } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { LandingComponent } from './landing/landing.component';

describe('AppComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppComponent, LandingComponent],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it(`should have the 'frontend' title`, () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app.title).toEqual('frontend');
  });

  it('should render title image', () => {
    const fixture = TestBed.createComponent(AppComponent);
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    const img = compiled.querySelector('img[alt="It\'s Just Forever, No Big Deal"]');
    expect(img).toBeTruthy();
  });
});