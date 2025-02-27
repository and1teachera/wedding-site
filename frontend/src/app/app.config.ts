import { ApplicationConfig } from '@angular/core';
import {provideRouter, withHashLocation} from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { routes } from './app.routes';
import { authInterceptor } from './auth/services/auth.interceptor';
import { loggingInterceptor } from './auth/services/logging.interceptor';
import {RsvpService} from "./rsvp/services/rsvp.service";
import {AccommodationService} from "./accommodation/services/accommodation.service";

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes, withHashLocation()),
    provideHttpClient(
      withInterceptors([
        loggingInterceptor, // Add logging interceptor first
        authInterceptor
      ])
    ),
    RsvpService,
    AccommodationService
  ]
};