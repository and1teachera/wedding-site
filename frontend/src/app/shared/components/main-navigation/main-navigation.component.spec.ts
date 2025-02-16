import { TestBed } from '@angular/core/testing';
import { MainNavigationComponent } from './main-navigation.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';

describe('MainNavigationComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        MainNavigationComponent,
        HttpClientTestingModule,
        RouterTestingModule,
      ],
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(MainNavigationComponent);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});
