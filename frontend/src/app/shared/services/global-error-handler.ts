import { ErrorHandler, Injectable, Injector } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { LoggingService } from './logging.service';

@Injectable()
export class GlobalErrorHandler implements ErrorHandler {
  constructor(private injector: Injector) {}

  handleError(error: any): void {
    const loggingService = this.injector.get(LoggingService);
    
    let message = '';
    let stack = '';
    let context = 'unknown';
    let additional = {};
    
    if (error instanceof HttpErrorResponse) {
      message = `HTTP Error: ${error.status} ${error.statusText}`;
      context = 'http';
      additional = {
        status: error.status,
        statusText: error.statusText,
        url: error.url || 'unknown',
        error: error.error
      };
    } else if (error instanceof Error) {
      message = error.message;
      stack = error.stack || '';
      
      // Try to determine context from stack
      if (stack.includes('auth.service')) {
        context = 'auth';
      } else if (stack.includes('http-interceptor')) {
        context = 'http';
      }
    } else {
      message = String(error);
    }
    
    // Log the error
    loggingService.error(message, context, additional, stack);
  }
}