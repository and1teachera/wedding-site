import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DressCodeComponent } from './dress-code.component';

describe('DressCodeComponent', () => {
  let component: DressCodeComponent;
  let fixture: ComponentFixture<DressCodeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DressCodeComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(DressCodeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
