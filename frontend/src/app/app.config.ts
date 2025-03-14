import { ApplicationConfig, ErrorHandler } from '@angular/core';
import {provideRouter, withHashLocation} from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { routes } from './app.routes';
import { authInterceptor } from './auth/services/auth.interceptor';
import { loggingInterceptor } from './shared/services/logging.interceptor';
import { RsvpService } from "./rsvp/services/rsvp.service";
import { AccommodationService } from "./accommodation/services/accommodation.service";
import { GlobalErrorHandler } from './shared/services/global-error-handler';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes, withHashLocation()),
    provideHttpClient(
      withInterceptors([
        // Order matters: logging should run first to capture everything
        loggingInterceptor,
        authInterceptor
      ])
    ),
    { provide: ErrorHandler, useClass: GlobalErrorHandler },
    RsvpService,
    AccommodationService
  ]
};