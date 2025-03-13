import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { delay } from 'rxjs/operators';

// Helper function to toggle demo mode
export function toggleDemoMode(enabled: boolean): void {
  localStorage.setItem('demoMode', enabled ? 'true' : 'false');
  
  // Also clear the demo login attempted flag when disabling
  if (!enabled) {
    localStorage.setItem('demo_login_attempted', 'false');
  }
  
  console.log(`Demo mode ${enabled ? 'enabled' : 'disabled'}`);
}

// Sample data for mock responses
const mockFamilyMembers = {
  primaryUser: {
    id: 1,
    firstName: 'Demo',
    lastName: 'User',
    isChild: false,
    rsvpStatus: 'MAYBE'
  },
  familyMembers: [
    {
      id: 2,
      firstName: 'Demo',
      lastName: 'Spouse',
      isChild: false,
      rsvpStatus: 'MAYBE'
    },
    {
      id: 3,
      firstName: 'Demo',
      lastName: 'Child',
      isChild: true,
      rsvpStatus: 'MAYBE'
    }
  ]
};

const mockRoomStatus = {
  success: true,
  message: "Room booking found",
  roomNumber: 101,
  familyId: 1,
  status: "CONFIRMED",
  notes: "Demo booking"
};

export const mockApiInterceptor: HttpInterceptorFn = (
  request: HttpRequest<unknown>,
  next: HttpHandlerFn
) => {
  // Check if demo mode is enabled
  const demoModeEnabled = localStorage.getItem('demoMode') === 'true';
  
  if (!demoModeEnabled) {
    return next(request);
  }
  
  console.log('Mock API intercepting request to:', request.url);
  
  // Add a small delay to simulate network request
  const delayTime = 300;
  
  // Auth endpoints
  if (request.url.includes('/api/auth/login') || request.url.includes('/api/auth/login-by-names')) {
    const mockAuthResponse = {
      token: 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsInVzZXJUeXBlIjoiR1VFU1QiLCJmYW1pbHlJZCI6MSwic3ViIjoiRGVtbyBVc2VyIiwiaWF0IjoxNjE0NTU2MTAwLCJleHAiOjk5OTk5OTk5OTl9.8Jy5_dIQ3CXiHyNYqnY6o9p8kWDDMpw2D8xrM3IXs8Y',
      userType: 'GUEST',
      user: {
        firstName: 'Demo',
        lastName: 'User',
        familyId: 1
      }
    };
    return of(new HttpResponse({ status: 200, body: mockAuthResponse })).pipe(delay(delayTime));
  }

  // RSVP endpoints
  if (request.url.includes('/api/rsvp/family-members')) {
    return of(new HttpResponse({ status: 200, body: mockFamilyMembers })).pipe(delay(delayTime));
  }
  
  if (request.url.includes('/api/rsvp') && request.method === 'POST') {
    return of(new HttpResponse({ 
      status: 200, 
      body: {
        success: true,
        message: "RSVP has been successfully recorded",
        primaryUserId: 1,
        confirmedAttendees: 3
      }
    })).pipe(delay(delayTime));
  }
  
  // Accommodation endpoints
  if (request.url.includes('/api/accommodation/available-rooms')) {
    return of(new HttpResponse({ status: 200, body: 5 })).pipe(delay(delayTime));
  }
  
  if (request.url.includes('/api/accommodation/booking-status')) {
    return of(new HttpResponse({ status: 200, body: mockRoomStatus })).pipe(delay(delayTime));
  }
  
  if (request.url.includes('/api/accommodation/book') || 
      request.url.includes('/api/accommodation/request-single')) {
    return of(new HttpResponse({ 
      status: 200, 
      body: {
        success: true,
        message: "Room booked successfully",
        roomNumber: 101,
        familyId: 1
      }
    })).pipe(delay(delayTime));
  }
  
  if (request.url.includes('/api/accommodation/cancel')) {
    return of(new HttpResponse({ 
      status: 200, 
      body: {
        success: true,
        message: "Room booking cancelled successfully"
      }
    })).pipe(delay(delayTime));
  }
  
  // If no mock is defined, pass through to the next handler
  return next(request);
};