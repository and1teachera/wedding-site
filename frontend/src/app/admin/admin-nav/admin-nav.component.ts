import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-admin-nav',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  template: `
    <div class="bg-gray-100 p-4 mb-6 rounded-lg shadow-sm">
      <nav class="flex flex-wrap space-x-4">
        <a 
          routerLink="/admin/families" 
          routerLinkActive="bg-pink-600 text-white" 
          class="px-4 py-2 rounded-md hover:bg-pink-100 transition-colors mb-2"
          [routerLinkActiveOptions]="{ exact: true }">
          Преглед на семейства
        </a>
        <a 
          routerLink="/admin/rsvp" 
          routerLinkActive="bg-pink-600 text-white" 
          class="px-4 py-2 rounded-md hover:bg-pink-100 transition-colors mb-2"
          [routerLinkActiveOptions]="{ exact: true }">
          Отговори за присъствие
        </a>
        <a 
          routerLink="/admin/rooms" 
          routerLinkActive="bg-pink-600 text-white" 
          class="px-4 py-2 rounded-md hover:bg-pink-100 transition-colors mb-2"
          [routerLinkActiveOptions]="{ exact: true }">
          Наличност на стаи
        </a>
        <a 
          routerLink="/admin/single-accommodation" 
          routerLinkActive="bg-pink-600 text-white" 
          class="px-4 py-2 rounded-md hover:bg-pink-100 transition-colors mb-2"
          [routerLinkActiveOptions]="{ exact: true }">
          Единично настаняване
        </a>
        <a 
          routerLink="/admin/users" 
          routerLinkActive="bg-pink-600 text-white" 
          class="px-4 py-2 rounded-md hover:bg-pink-100 transition-colors mb-2"
          [routerLinkActiveOptions]="{ exact: true }">
          Управление на потребители
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