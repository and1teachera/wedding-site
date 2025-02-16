import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService, AuthError, NetworkError } from '../services/auth.service';
import { TitlePictureComponent } from '../../shared/components/title-picture/title-picture.component';
import { TokenService } from '../services/token.service';
import { Subscription } from 'rxjs';
import {HttpClientModule} from "@angular/common/http";

type LoginMethod = 'names' | 'email';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, HttpClientModule, FormsModule, TitlePictureComponent],
  providers: [AuthService],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {
  pageTitle = "It's Just Forever, No Big Deal";
  loginMethod: LoginMethod = 'names';
  firstName = '';
  lastName = '';
  email = '';
  password = '';
  rememberMe = false;
  isLoading = false;
  errorMessage = '';
  private returnUrl = '/home';
  private queryParamsSub?: Subscription;

  constructor(
      private router: Router,
      private route: ActivatedRoute,
      private authService: AuthService,
      private tokenService: TokenService
  ) {}

  ngOnInit() {
    if (this.tokenService.isTokenValid()) {
      this.navigateToReturnUrl();
      return;
    }
    this.tokenService.removeToken();
    this.queryParamsSub = this.route.queryParams.subscribe(params => {
      this.returnUrl = params['returnUrl'] || '/home';
      if (!this.isValidReturnUrl(this.returnUrl)) {
        this.returnUrl = '/home';
      }
    });
  }

  ngOnDestroy() {
    if (this.queryParamsSub) {
      this.queryParamsSub.unsubscribe();
    }

  }

  toggleLoginMethod(method: LoginMethod) {
    this.loginMethod = method;
    this.resetForm();
  }

  onSubmit(event: Event): void {
    event.preventDefault();
    if (this.loginMethod === 'names') {
      if (!this.validateNameLoginInputs()) {
        return;
      }

      this.isLoading = true;
      this.errorMessage = '';

      this.authService.loginWithNames({
        firstName: this.firstName.trim(),
        lastName: this.lastName.trim(),
        password: this.password,
        rememberMe: this.rememberMe
      }).subscribe({
        next: () => {
          this.navigateToReturnUrl();
        },
        error: (error) => {
          this.isLoading = false;
          this.handleLoginError(error);
          this.password = ''; // Clear password on error
        }
      });
    } else {
      if (!this.validateEmailLoginInputs()) {
        this.isLoading = false;
        return;
      }
      this.authService.loginWithEmail({
        email: this.email.trim(),
        password: this.password,
        rememberMe: this.rememberMe
      }).subscribe({
        next: () => {
          this.navigateToReturnUrl();
        },
        error: (error) => {
          this.isLoading = false;
          this.handleLoginError(error);
          this.password = '';
        }
      });
    }
  }

  private validateNameLoginInputs(): boolean {
    if (!this.firstName || !this.lastName || !this.password) {
      this.errorMessage = 'Моля, попълнете всички полета';
      return false;
    }

    if (this.firstName.trim().length < 2 || this.lastName.trim().length < 2) {
      this.errorMessage = 'Имената трябва да са поне 2 символа';
      return false;
    }

    return true;
  }

  private handleLoginError(error: Error): void {
    if (error instanceof AuthError || error instanceof NetworkError) {
      this.errorMessage = error.message;
    } else {
      this.errorMessage = 'Възникна неочаквана грешка. Моля, опитайте отново по-късно.';
      console.error('Unexpected login error:', error);
    }
  }

  private navigateToReturnUrl(): void {
    this.router.navigateByUrl(this.returnUrl);
  }

  private resetForm(): void {
    this.firstName = '';
    this.lastName = '';
    this.email = '';
    this.password = '';
    this.errorMessage = '';
  }

  private isValidReturnUrl(url: string): boolean {
    // Only allow relative URLs that start with '/'
    if (!url.startsWith('/')) {
      return false;
    }

    const allowedPaths = ['/home', '/rsvp'];

    return allowedPaths.some(path => url.startsWith(path));
  }

  private validateEmailLoginInputs(): boolean {
    if (!this.email || !this.password) {
      this.errorMessage = 'Моля, попълнете всички полета';
      return false;
    }
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(this.email.trim())) {
      this.errorMessage = 'Моля, въведете валиден имейл адрес';
      return false;
    }
    return true;
  }
}