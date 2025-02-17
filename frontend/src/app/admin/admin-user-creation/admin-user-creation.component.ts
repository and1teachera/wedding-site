import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import {SectionContainerComponent} from "../../shared/components/layout/section-container/section-container.component";
import {AdminService, FamilyCreationRequest, UserCreationRequest} from "../services/admin.service";

@Component({
  selector: 'app-admin-user-creation',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    SectionContainerComponent
  ],
  templateUrl: './admin-user-creation.component.html',
  styleUrls: ['./admin-user-creation.component.css']
})
export class AdminUserCreationComponent {
  userForm: FormGroup;
  isSubmitting = false;

  constructor(private fb: FormBuilder, private adminService: AdminService) {
    this.userForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.email]],
      phone: [''],
      familyName: [{value: '', disabled: true}],
      familyMembers: this.fb.array([])
    });
  }

  get familyMembers() {
    return this.userForm.get('familyMembers') as FormArray;
  }

  get hasFamilyMembers(): boolean {
    return this.familyMembers.length > 0;
  }

  createFamilyMember(): FormGroup {
    return this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.email]],
      phone: [''],
      isChild: [false]
    });
  }

  addFamilyMember(): void {
    this.familyMembers.push(this.createFamilyMember());
  }

  removeFamilyMember(index: number): void {
    this.familyMembers.removeAt(index);
    if (this.familyMembers.length === 0) {
      this.userForm.get('familyName')?.disable();
    }
  }

  toggleFamilyMembers(): void {
    if (this.hasFamilyMembers) {
      while (this.familyMembers.length) {
        this.familyMembers.removeAt(0);
      }
      this.userForm.get('familyName')?.disable();
    } else {
      this.addFamilyMember();
      this.userForm.get('familyName')?.enable();
    }
  }

  onSubmit(): void {
    if (this.userForm.valid && !this.isSubmitting) {
      this.isSubmitting = true;
      const formData = this.userForm.getRawValue();
      
      if (this.hasFamilyMembers) {
        const familyRequest: FamilyCreationRequest = {
          primaryUser: {
            firstName: formData.firstName,
            lastName: formData.lastName,
            email: formData.email,
            phone: formData.phone
          },
          familyName: formData.familyName,
          familyMembers: formData.familyMembers
        };
        
        this.adminService.createFamily(familyRequest).subscribe({
          next: () => {
            this.resetForm();
            // TODO: Show success message
          },
          error: (error) => {
            console.error('Error creating family:', error);
            // TODO: Show error message
            this.isSubmitting = false;
          }
        });
      } else {
        const userRequest: UserCreationRequest = {
          firstName: formData.firstName,
          lastName: formData.lastName,
          email: formData.email,
          phone: formData.phone
        };
        
        this.adminService.createUser(userRequest).subscribe({
          next: () => {
            this.resetForm();
            // TODO: Show success message
          },
          error: (error) => {
            console.error('Error creating user:', error);
            // TODO: Show error message
            this.isSubmitting = false;
          }
        });
      }
    }
  }

  private resetForm(): void {
    this.isSubmitting = false;
    this.userForm.reset();
    while (this.familyMembers.length) {
      this.familyMembers.removeAt(0);
    }
    this.userForm.get('familyName')?.disable();
  }
}

