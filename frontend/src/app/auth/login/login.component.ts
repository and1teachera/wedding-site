import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { TitlePictureComponent } from '../../shared/components/title-picture/title-picture.component';

type LoginMethod = 'names' | 'email';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, TitlePictureComponent],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  pageTitle = "It's Just Forever, No Big Deal";
  loginMethod: LoginMethod = 'names';
  firstName = '';
  lastName = '';
  email = '';
  password = '';
  rememberMe = false;

  constructor(
      private router: Router,
      private authService: AuthService
  ) {}

  toggleLoginMethod(method: LoginMethod) {
    this.loginMethod = method;
    // Clear form fields when switching methods
    this.firstName = '';
    this.lastName = '';
    this.email = '';
    this.password = '';
  }

  onSubmit() {
    if (this.loginMethod === 'names') {
      this.authService.loginWithNames(/*{
        firstName: this.firstName,
        lastName: this.lastName,
        password: this.password,
        rememberMe: this.rememberMe
      }*/).subscribe({
        next: () => this.router.navigate(['/home']),
        error: (error) => console.error('Login failed:', error)
      });
    } else {
      this.authService.loginWithEmail(/*{
        email: this.email,
        password: this.password,
        rememberMe: this.rememberMe
      }*/).subscribe({
        next: () => this.router.navigate(['/home']),
        error: (error) => console.error('Login failed:', error)
      });
    }
  }
}