import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { LandingComponent } from './landing/landing.component';
import { RsvpComponent } from './rsvp/rsvp.component';
import { AuthGuard } from './auth/services/auth.guard';
import { AdminUserCreationComponent } from './admin/admin-user-creation/admin-user-creation.component';
import { AdminRsvpListComponent } from './admin/admin-rsvp-list/admin-rsvp-list.component';
import { AdminRoomsComponent } from './admin/admin-rooms/admin-rooms.component';
import { AdminSingleAccommodationComponent } from './admin/admin-single-accommodation/admin-single-accommodation.component';
import { AdminGuard } from "./admin/guards/admin.guard";

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
        path: 'admin',
        canActivate: [AuthGuard, AdminGuard],
        children: [
            {
                path: '',
                redirectTo: 'rsvp',
                pathMatch: 'full'
            },
            {
                path: 'rsvp',
                component: AdminRsvpListComponent,
                title: 'RSVP Responses - Admin'
            },
            {
                path: 'rooms',
                component: AdminRoomsComponent,
                title: 'Room Availability - Admin'
            },
            {
                path: 'single-accommodation',
                component: AdminSingleAccommodationComponent,
                title: 'Single User Accommodation - Admin'
            },
            {
                path: 'users',
                component: AdminUserCreationComponent,
                title: 'User Management - Admin'
            }
            // Add more admin routes here as needed
        ]
    },
    {
        path: '**',
        redirectTo: 'login'
    }
];