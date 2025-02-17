import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminUserCreationComponent } from './admin-user-creation.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { SectionContainerComponent } from '../../shared/components/layout/section-container/section-container.component';

describe('AdminUserCreationComponent', () => {
  let component: AdminUserCreationComponent;
  let fixture: ComponentFixture<AdminUserCreationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        ReactiveFormsModule,
        MatFormFieldModule,
        MatInputModule,
        NoopAnimationsModule,
        AdminUserCreationComponent,
        SectionContainerComponent
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AdminUserCreationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with an empty form', () => {
    expect(component.userForm.get('firstName')?.value).toBe('');
    expect(component.userForm.get('lastName')?.value).toBe('');
    expect(component.userForm.get('email')?.value).toBe('');
    expect(component.userForm.get('phone')?.value).toBe('');
    expect(component.familyMembers.length).toBe(0);
  });

  it('should add family member when addFamilyMember is called', () => {
    component.addFamilyMember();
    expect(component.familyMembers.length).toBe(1);
  });

  it('should remove family member when removeFamilyMember is called', () => {
    component.addFamilyMember();
    component.addFamilyMember();
    expect(component.familyMembers.length).toBe(2);

    component.removeFamilyMember(0);
    expect(component.familyMembers.length).toBe(1);
  });

  it('should toggle family members section', () => {
    expect(component.hasFamilyMembers).toBeFalse();

    component.toggleFamilyMembers();
    expect(component.hasFamilyMembers).toBeTrue();
    expect(component.familyMembers.length).toBe(1);

    component.toggleFamilyMembers();
    expect(component.hasFamilyMembers).toBeFalse();
    expect(component.familyMembers.length).toBe(0);
  });

  it('should validate required fields', () => {
    const form = component.userForm;
    expect(form.valid).toBeFalse();

    form.patchValue({
      firstName: 'John',
      lastName: 'Doe'
    });

    expect(form.valid).toBeTrue();
  });
});