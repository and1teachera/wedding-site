import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {MainNavigationComponent} from "./shared/components/main-navigation/main-navigation.component";
import {TokenService} from "./auth/services/token.service";
import {CommonModule} from "@angular/common";
import {DemoModeToggleComponent} from "./shared/components/demo-mode-toggle/demo-mode-toggle.component";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, MainNavigationComponent, DemoModeToggleComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'frontend';
  constructor(public tokenService: TokenService) {}
}
