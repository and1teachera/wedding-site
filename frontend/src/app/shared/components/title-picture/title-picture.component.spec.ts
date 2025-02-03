import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TitlePictureComponent } from './title-picture.component';

describe('TitlePictureComponent', () => {
  let component: TitlePictureComponent;
  let fixture: ComponentFixture<TitlePictureComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TitlePictureComponent]
    })
        .compileComponents();

    fixture = TestBed.createComponent(TitlePictureComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have default title', () => {
    expect(component.title).toBe("It's Just Forever, No Big Deal");
  });

  it('should accept custom title', () => {
    component.title = 'Custom Title';
    fixture.detectChanges();
    expect(component.title).toBe('Custom Title');
  });
});