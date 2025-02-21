import { Component, HostListener } from '@angular/core';
import { CommonModule, ViewportScroller} from '@angular/common';
import {
  ContentContainerComponent
} from "../../../shared/components/layout/content-container/content-container.component";
import {RouterLink} from "@angular/router";
import {TokenService} from "../../../auth/services/token.service";
import {LogoutService} from "../../../auth/services/logout.service";

@Component({
  selector: 'app-navigation',
  standalone: true,
  imports: [CommonModule, ContentContainerComponent, RouterLink],
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.css'],
})
export class NavigationComponent {
  isMenuOpen = false;
  isScrolled = false;
  isLoggingOut = false;

  constructor(
      private scroller: ViewportScroller,
      private tokenService: TokenService,
      private logoutService: LogoutService
  ) {}

  @HostListener('window:scroll', [])
  onWindowScroll() {
    this.isScrolled = window.scrollY > 0;
  }

  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;
  }

  closeMenu() {
    this.isMenuOpen = false;
  }

  scrollToSection(event: Event, sectionId: string): void {
    event.preventDefault();
    const element = document.getElementById(sectionId);
    if (element) {
      element.scrollIntoView({ behavior: 'smooth' });
    }
    this.isMenuOpen = false;
  }

  onLogout(): void {
    if (this.isLoggingOut) return;

    this.isLoggingOut = true;
    this.logoutService.logout().subscribe({
      next: () => {
        // Successful logout is handled by the service
        this.isLoggingOut = false;
        this.closeMenu();
      },
      error: (error) => {
        console.error('Logout error:', error);
        this.isLoggingOut = false;

        // Force logout even if there was an error with the server request
        this.logoutService.forceLogout();
        this.closeMenu();
      }
    });
  }

  get isAuthenticated(): boolean {
    return this.tokenService.isTokenValid();
  }

  get isAdmin(): boolean {
    return this.tokenService.isAdmin();
  }

}