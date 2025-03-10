import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { 
  UserProfileService, 
  UserProfile, 
  UserProfileUpdate, 
  PasswordChange 
} from '../services/user-profile.service';
import { SectionContainerComponent } from '../shared/components/layout/section-container/section-container.component';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, SectionContainerComponent],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  profileForm: FormGroup;
  passwordForm: FormGroup;
  userProfile: UserProfile | null = null;
  isLoading = true;
  isSaving = false;
  profileError: string | null = null;
  passwordError: string | null = null;
  profileSuccess: string | null = null;
  passwordSuccess: string | null = null;

  constructor(
    private fb: FormBuilder,
    private userProfileService: UserProfileService
  ) {
    this.profileForm = this.fb.group({
      email: ['', [Validators.email]],
      phone: ['']
    });

    this.passwordForm = this.fb.group({
      currentPassword: ['', [Validators.required]],
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required]]
    });
  }

  ngOnInit(): void {
    this.loadUserProfile();
  }

  loadUserProfile(): void {
    this.isLoading = true;
    this.profileError = null;

    this.userProfileService.getUserProfile().subscribe({
      next: (profile) => {
        this.userProfile = profile;
        this.profileForm.patchValue({
          email: profile.email,
          phone: profile.phone
        });
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading user profile:', error);
        this.profileError = 'Failed to load user profile. Please try again later.';
        this.isLoading = false;
      }
    });
  }

  updateProfile(): void {
    if (this.profileForm.valid && !this.isSaving) {
      this.isSaving = true;
      this.profileError = null;
      this.profileSuccess = null;

      const profileUpdate: UserProfileUpdate = this.profileForm.value;

      this.userProfileService.updateUserProfile(profileUpdate).subscribe({
        next: (updatedProfile) => {
          this.userProfile = updatedProfile;
          this.profileSuccess = 'Profile updated successfully';
          this.isSaving = false;
        },
        error: (error) => {
          console.error('Error updating profile:', error);
          this.profileError = 'Failed to update profile. Please try again later.';
          this.isSaving = false;
        }
      });
    }
  }

  changePassword(): void {
    if (this.passwordForm.valid && !this.isSaving) {
      // Check if passwords match
      const values = this.passwordForm.value;
      if (values.newPassword !== values.confirmPassword) {
        this.passwordError = 'New password and confirm password do not match';
        return;
      }

      this.isSaving = true;
      this.passwordError = null;
      this.passwordSuccess = null;

      const passwordChange: PasswordChange = this.passwordForm.value;

      this.userProfileService.changePassword(passwordChange).subscribe({
        next: () => {
          this.passwordSuccess = 'Password changed successfully';
          this.passwordForm.reset();
          this.isSaving = false;
        },
        error: (error) => {
          console.error('Error changing password:', error);
          
          // Check for specific error types
          if (error.status === 401) {
            this.passwordError = 'Current password is incorrect';
          } else {
            this.passwordError = 'Failed to change password. Please try again later.';
          }
          
          this.isSaving = false;
        }
      });
    }
  }
}