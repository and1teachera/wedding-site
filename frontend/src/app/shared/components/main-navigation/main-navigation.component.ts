import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { LogoutService } from '../../../auth/services/logout.service';
import { TokenService } from '../../../auth/services/token.service';

@Component({
  selector: 'app-main-navigation',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './main-navigation.component.html',
  styleUrls: ['./main-navigation.component.css']
})
export class MainNavigationComponent {
  isMenuOpen = false;
  isLoggingOut = false;

  constructor(
      private logoutService: LogoutService,
      private tokenService: TokenService
  ) {}

  // Check if user is authenticated
  get isAuthenticated(): boolean {
    return this.tokenService.isTokenValid();
  }

  toggleMenu(): void {
    this.isMenuOpen = !this.isMenuOpen;
  }

  closeMenu(): void {
    this.isMenuOpen = false;
  }

  onLogout(): void {
    if (this.isLoggingOut) return;

    this.isLoggingOut = true;
    this.logoutService.logout().subscribe({
      error: (error) => {
        console.error('Logout error:', error);
        this.isLoggingOut = false;
      },
      complete: () => {
        this.isLoggingOut = false;
        this.closeMenu();
      }
    });
  }

  get isAdmin(): boolean {
    return this.tokenService.isAdmin();
  }
}