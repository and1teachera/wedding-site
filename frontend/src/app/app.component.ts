import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {MainNavigationComponent} from "./shared/components/main-navigation/main-navigation.component";
import {TokenService} from "./auth/services/token.service";
import {CommonModule} from "@angular/common";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, MainNavigationComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'frontend';
  constructor(public tokenService: TokenService) {}
}
