import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {TokenService} from "./auth/services/token.service";
import {CommonModule} from "@angular/common";
import {NavigationComponent} from "./landing/components/navigation/navigation.component";
import {NotificationComponent} from "./shared/components/notification/notification.component";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, NavigationComponent, NotificationComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'Wedding Website';
  constructor(public tokenService: TokenService) {}
}
