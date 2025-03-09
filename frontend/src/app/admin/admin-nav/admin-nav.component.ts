import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-admin-nav',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  template: `
    <div class="bg-gray-100 p-4 mb-6 rounded-lg shadow-sm">
      <nav class="flex space-x-4">
        <a 
          routerLink="/admin/rsvp" 
          routerLinkActive="bg-pink-600 text-white" 
          class="px-4 py-2 rounded-md hover:bg-pink-100 transition-colors"
          [routerLinkActiveOptions]="{ exact: true }">
          RSVP Responses
        </a>
        <a 
          routerLink="/admin/rooms" 
          routerLinkActive="bg-pink-600 text-white" 
          class="px-4 py-2 rounded-md hover:bg-pink-100 transition-colors"
          [routerLinkActiveOptions]="{ exact: true }">
          Room Availability
        </a>
        <a 
          routerLink="/admin/users" 
          routerLinkActive="bg-pink-600 text-white" 
          class="px-4 py-2 rounded-md hover:bg-pink-100 transition-colors"
          [routerLinkActiveOptions]="{ exact: true }">
          User Management
        </a>
      </nav>
    </div>
  `,
  styles: [`
    .router-link-active {
      font-weight: bold;
    }
  `]
})
export class AdminNavComponent {}