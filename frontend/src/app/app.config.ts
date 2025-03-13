import { ApplicationConfig, ErrorHandler } from '@angular/core';
import {provideRouter, withHashLocation} from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { routes } from './app.routes';
import { authInterceptor } from './auth/services/auth.interceptor';
import { loggingInterceptor } from './shared/services/logging.interceptor';
import { mockApiInterceptor } from './shared/interceptors/mock-api.interceptor';
import { RsvpService } from "./rsvp/services/rsvp.service";
import { AccommodationService } from "./accommodation/services/accommodation.service";
import { GlobalErrorHandler } from './shared/services/global-error-handler';

// Enable demo mode by default
localStorage.setItem('demoMode', 'true');

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes, withHashLocation()),
    provideHttpClient(
      withInterceptors([
        // Add mock interceptor first to intercept before other interceptors
        mockApiInterceptor,
        // Original interceptors
        loggingInterceptor,
        authInterceptor
      ])
    ),
    { provide: ErrorHandler, useClass: GlobalErrorHandler },
    RsvpService,
    AccommodationService
  ]
};