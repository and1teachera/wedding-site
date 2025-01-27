import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HonorFamilyComponent } from './honor-family.component';

describe('HonorFamilyComponent', () => {
  let component: HonorFamilyComponent;
  let fixture: ComponentFixture<HonorFamilyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HonorFamilyComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(HonorFamilyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
