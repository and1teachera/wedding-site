import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { appConfig } from './app/app.config';

window.addEventListener('unhandledrejection', event => {
    if (event.reason?.message?.includes('Receiving end does not exist')) {
        event.preventDefault(); // Prevent the error from appearing in console
        return;
    }
    console.error('Unhandled promise rejection:', event.reason);
});

bootstrapApplication(AppComponent, appConfig)
    .catch(err => console.error('Application bootstrap error:', err));