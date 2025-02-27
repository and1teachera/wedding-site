import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpEvent, HttpResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';

export const loggingInterceptor: HttpInterceptorFn = (
    req: HttpRequest<unknown>,
    next: HttpHandlerFn
): Observable<HttpEvent<unknown>> => {
  const startTime = Date.now();
  console.log(`Request started: ${req.method} ${req.url}`);
  
  return next(req).pipe(
    tap(event => {
      if (event instanceof HttpResponse) {
        const elapsed = Date.now() - startTime;
        console.log(`Response for ${req.url} received after ${elapsed}ms`);
        console.log(`Status: ${event.status}, Body type: ${typeof event.body}`);
        console.log(`Body is array: ${Array.isArray(event.body)}, Body is null: ${event.body === null}`);
        
        // Check content type and try to parse response manually
        const contentType = event.headers.get('Content-Type');
        console.log(`Content-Type: ${contentType}`);
        
        // If body is a string (might be unparsed JSON), try parsing it
        if (typeof event.body === 'string' && 
            (contentType?.includes('application/json') || (event.body && event.body.startsWith('{')))) {
          try {
            const parsedBody = JSON.parse(event.body);
            console.log('Manually parsed body:', parsedBody);
          } catch (e) {
            console.error('Failed to parse body as JSON:', e);
          }
        }
      }
    }),
    catchError(error => {
      console.error(`Request to ${req.url} failed:`, error);
      return throwError(() => error);
    })
  );
};