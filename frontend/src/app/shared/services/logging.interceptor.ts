import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpErrorResponse, HttpEvent, HttpResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, tap, finalize } from 'rxjs/operators';
import { LoggingService } from './logging.service';

export const loggingInterceptor: HttpInterceptorFn = (
  request: HttpRequest<unknown>,
  next: HttpHandlerFn
): Observable<HttpEvent<unknown>> => {
  const loggingService = inject(LoggingService);
  const startTime = Date.now();
  const requestId = generateRequestId();
  
  // Add request ID to headers
  const modifiedRequest = request.clone({
    setHeaders: {
      'X-Request-ID': requestId
    }
  });
  
  // Set the current request ID in logging service
  loggingService.setRequestId(requestId);
  
  // Log the outgoing request
  loggingService.debug(
    `HTTP Request: ${request.method} ${request.url}`,
    'http',
    {
      method: request.method,
      url: request.url,
      headers: sanitizeHeaders(request.headers),
      body: sanitizeRequestBody(request)
    }
  );

  return next(modifiedRequest).pipe(
    tap((event) => {
      if (event instanceof HttpResponse) {
        const duration = Date.now() - startTime;
        
        // Extract trace ID from response headers
        const traceId = event.headers.get('X-Trace-ID');
        if (traceId) {
          loggingService.setTraceId(traceId);
        }
        
        // Log the response
        loggingService.debug(
          `HTTP Response: ${request.method} ${request.url} ${event.status}`,
          'http',
          {
            status: event.status,
            statusText: event.statusText,
            duration: `${duration}ms`,
            traceId: traceId
          }
        );
      }
    }),
    catchError((error: HttpErrorResponse) => {
      const duration = Date.now() - startTime;
      
      loggingService.error(
        `HTTP Error: ${request.method} ${request.url} ${error.status}`,
        'http',
        {
          status: error.status,
          statusText: error.statusText,
          duration: `${duration}ms`,
          url: request.url,
          message: error.message,
          error: error.error
        }
      );
      
      return throwError(() => error);
    }),
    finalize(() => {
      // Reset correlation IDs
      loggingService.setRequestId(undefined);
      loggingService.setTraceId(undefined);
    })
  );
};

function generateRequestId(): string {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
    const r = (Math.random() * 16) | 0;
    const v = c === 'x' ? r : (r & 0x3) | 0x8;
    return v.toString(16);
  });
}

function sanitizeHeaders(headers: any): Record<string, string> {
  const headersMap: Record<string, string> = {};
  
  if (headers && typeof headers.keys === 'function') {
    headers.keys().forEach((key: string) => {
      // Don't log sensitive headers
      if (key.toLowerCase() !== 'authorization') {
        headersMap[key] = headers.get(key) || '';
      } else {
        headersMap[key] = '[REDACTED]';
      }
    });
  }
  
  return headersMap;
}

function sanitizeRequestBody(request: HttpRequest<unknown>): unknown {
  // Don't log sensitive data
  if (request.url.includes('/auth/login')) {
    return '[REDACTED]';
  }
  return request.body;
}