import { Component, OnInit, OnDestroy, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { Notification, NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-notification',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="notifications-container" [class]="position">
      <div *ngFor="let notification of notifications" 
           class="notification"
           [class]="notification.type"
           role="alert"
           tabindex="0"
           (click)="dismiss(notification.id)"
           (keydown.enter)="dismiss(notification.id)"
           (keydown.space)="dismiss(notification.id)">
        <div class="message">{{ notification.message }}</div>
        <button class="close-btn" 
                (click)="dismiss(notification.id); $event.stopPropagation()"
                (keydown.enter)="dismiss(notification.id); $event.stopPropagation()"
                aria-label="Close notification">×</button>
      </div>
    </div>
  `,
  styles: [`
    .notifications-container {
      position: fixed;
      z-index: 1000;
      display: flex;
      flex-direction: column;
      gap: 12px;
      max-width: 500px;
      pointer-events: none;
    }
    
    .top-right {
      top: 20px;
      right: 20px;
    }
    
    .top-center {
      top: 20px;
      left: 50%;
      transform: translateX(-50%);
    }
    
    .bottom-right {
      bottom: 20px;
      right: 20px;
    }
    
    .notification {
      padding: 18px;
      border-radius: 6px;
      box-shadow: 0 3px 15px rgba(0,0,0,0.15);
      display: flex;
      justify-content: space-between;
      align-items: center;
      pointer-events: auto;
      cursor: pointer;
      animation: slideIn 0.3s ease-out forwards;
      background-color: white;
      font-size: 1.1rem;
      width: 100%;
    }
    
    @keyframes slideIn {
      from { opacity: 0; transform: translateY(-30px); }
      to { opacity: 1; transform: translateY(0); }
    }
    
    .success {
      border-left: 5px solid #4CAF50;
    }
    
    .error {
      border-left: 5px solid #F44336;
    }
    
    .info {
      border-left: 5px solid #2196F3;
    }
    
    .warning {
      border-left: 5px solid #FF9800;
    }
    
    .message {
      flex-grow: 1;
      margin-right: 15px;
    }
    
    .close-btn {
      background: none;
      border: none;
      font-size: 24px;
      cursor: pointer;
      color: #666;
    }
  `]
})
export class NotificationComponent implements OnInit, OnDestroy {
  @Input() position: 'top-right' | 'top-center' | 'bottom-right' = 'top-center';
  notifications: Notification[] = [];
  private subscription: Subscription | undefined;
  
  constructor(private readonly notificationService: NotificationService) {}
  
  ngOnInit(): void {
    this.subscription = this.notificationService.getNotifications().subscribe(
      notifications => this.notifications = notifications
    );
  }
  
  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }
  
  dismiss(id: string): void {
    this.notificationService.dismiss(id);
  }
}