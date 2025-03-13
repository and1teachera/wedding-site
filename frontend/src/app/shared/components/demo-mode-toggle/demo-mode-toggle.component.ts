import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { toggleDemoMode } from '../../interceptors/mock-api.interceptor';

@Component({
  selector: 'app-demo-mode-toggle',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="fixed bottom-4 right-4 z-50">
      <button 
        (click)="toggleDemoMode()" 
        class="px-3 py-2 rounded-full shadow-lg"
        [class.bg-green-500]="isDemoMode"
        [class.bg-red-500]="!isDemoMode"
        [class.text-white]="true">
        Demo Mode: {{ isDemoMode ? 'ON' : 'OFF' }}
      </button>
    </div>
  `,
  styles: []
})
export class DemoModeToggleComponent implements OnInit {
  isDemoMode = false;

  ngOnInit(): void {
    this.isDemoMode = localStorage.getItem('demoMode') === 'true';
  }

  toggleDemoMode(): void {
    this.isDemoMode = !this.isDemoMode;
    toggleDemoMode(this.isDemoMode);
    // Reload page to apply changes
    window.location.reload();
  }
}