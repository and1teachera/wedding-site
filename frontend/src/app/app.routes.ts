import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { LandingComponent } from './landing/landing.component';
import { RsvpComponent } from './rsvp/rsvp.component';
import { AuthGuard } from './auth/services/auth.guard';

export const routes: Routes = [
    {
        path: '',
        redirectTo: 'login',
        pathMatch: 'full'
    },
    {
        path: 'login',
        component: LoginComponent,
        title: 'Login - Angel & Mirena Wedding'
    },
    {
        path: 'home',
        component: LandingComponent,
        title: 'Angel & Mirena Wedding',
        canActivate: [AuthGuard]
    },
    {
        path: 'rsvp',
        component: RsvpComponent,
        title: 'RSVP - Angel & Mirena Wedding',
        canActivate: [AuthGuard]
    },
    {
        path: '**',
        redirectTo: 'login'
    }
];