import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface Notification {
  id: string;
  message: string;
  type: 'success' | 'error' | 'info' | 'warning';
  duration?: number; // in milliseconds, default could be 3000
  position?: 'top-right' | 'top-center' | 'bottom-right';
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private notifications$ = new BehaviorSubject<Notification[]>([]);
  
  // Method to show a notification
  show(message: string, type: 'success' | 'error' | 'info' | 'warning' = 'info', duration: number = 3000): string {
    const id = this.generateId();
    const notification: Notification = { id, message, type, duration };
    
    const current = this.notifications$.value;
    this.notifications$.next([...current, notification]);
    
    // Auto-dismiss after duration
    setTimeout(() => this.dismiss(id), duration);
    
    return id;
  }
  
  // Convenience methods
  success(message: string, duration?: number): string {
    return this.show(message, 'success', duration);
  }
  
  error(message: string, duration?: number): string {
    return this.show(message, 'error', duration);
  }

  info(message: string, duration?: number): string {
    return this.show(message, 'info', duration);
  }

  warning(message: string, duration?: number): string {
    return this.show(message, 'warning', duration);
  }
  
  // Get the observable for components to subscribe to
  getNotifications(): Observable<Notification[]> {
    return this.notifications$.asObservable();
  }
  
  // Dismiss a notification by ID
  dismiss(id: string): void {
    const current = this.notifications$.value;
    this.notifications$.next(current.filter(notification => notification.id !== id));
  }
  
  // Clear all notifications
  clear(): void {
    this.notifications$.next([]);
  }
  
  private generateId(): string {
    return Math.random().toString(36).substring(2, 9);
  }
}