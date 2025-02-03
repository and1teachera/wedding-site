import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { LandingComponent } from './landing/landing.component';

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
        title: 'Angel & Mirena Wedding'
    },
    {
        path: '**',
        redirectTo: 'login'
    }
];