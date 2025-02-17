import { Component, HostListener } from '@angular/core';
import { CommonModule, ViewportScroller} from '@angular/common';
import {
  ContentContainerComponent
} from "../../../shared/components/layout/content-container/content-container.component";
import {RouterLink} from "@angular/router";
import {TokenService} from "../../../auth/services/token.service";

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

  constructor(private scroller: ViewportScroller, private tokenService: TokenService) {}

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

  get isAuthenticated(): boolean {
    return this.tokenService.isTokenValid();
  }

  get isAdmin(): boolean {
    return this.tokenService.isAdmin();
  }

}
