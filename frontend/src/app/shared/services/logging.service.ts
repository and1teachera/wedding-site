import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

export enum LogLevel {
  TRACE = 0,
  DEBUG = 1,
  INFO = 2,
  WARN = 3,
  ERROR = 4,
  FATAL = 5
}

export interface LogEntry {
  timestamp: string;
  level: LogLevel;
  message: string;
  context?: string;
  userId?: string;
  requestId?: string;
  traceId?: string;
  additional?: Record<string, unknown>;
  stack?: string;
}

@Injectable({
  providedIn: 'root'
})
export class LoggingService {
  private readonly level: LogLevel = LogLevel.INFO;
  private buffer: LogEntry[] = [];
  private readonly bufferSize = 10;
  private sendingLogs = false;
  
  // Environment detection for production or staging
  private isProdOrStaging = window.location.hostname !== 'localhost';
  
  // Store correlation IDs
  private currentRequestId?: string;
  private currentTraceId?: string;

  constructor(private readonly http: HttpClient) {
    // Initialize from localStorage if available
    const storedLevel = localStorage.getItem('logLevel');
    if (storedLevel) {
      this.level = parseInt(storedLevel, 10);
    }
    
    // Set up error listeners
    this.setupErrorListeners();
    
    // Try to send any pending logs that failed to send previously
    this.sendPendingLogs();
    
    // Flush logs on window unload
    window.addEventListener('beforeunload', () => {
      this.flushLogs();
    });
    
    // Flush logs periodically (every 30 seconds)
    setInterval(() => {
      if (this.buffer.length > 0) {
        this.flushLogs();
      }
    }, 30000);
  }
  
  private sendPendingLogs(): void {
    try {
      const pendingLogsJson = localStorage.getItem('pendingLogs');
      if (pendingLogsJson) {
        const pendingLogs = JSON.parse(pendingLogsJson) as LogEntry[];
        if (pendingLogs.length > 0) {
          console.info(`Attempting to send ${pendingLogs.length} pending logs`);
          this.sendLogsToBackend(pendingLogs)
            .subscribe({
              next: () => {
                // Clear pending logs on success
                localStorage.removeItem('pendingLogs');
              },
              error: () => {
                // Keep pending logs on failure (will try again later)
              }
            });
        }
      }
    } catch (e) {
      console.error('Failed to process pending logs:', e);
      // Clear corrupted logs
      localStorage.removeItem('pendingLogs');
    }
  }
  
  setRequestId(requestId: string | undefined): void {
    this.currentRequestId = requestId;
  }
  
  setTraceId(traceId: string | undefined): void {
    this.currentTraceId = traceId;
  }
  
  trace(message: string, context?: string, additional?: Record<string, unknown>): void {
    this.log(LogLevel.TRACE, message, context, additional);
  }
  
  debug(message: string, context?: string, additional?: Record<string, unknown>): void {
    this.log(LogLevel.DEBUG, message, context, additional);
  }
  
  info(message: string, context?: string, additional?: Record<string, unknown>): void {
    this.log(LogLevel.INFO, message, context, additional);
  }
  
  warn(message: string, context?: string, additional?: Record<string, unknown>): void {
    this.log(LogLevel.WARN, message, context, additional);
  }
  
  error(message: string, context?: string, additional?: Record<string, unknown>, stack?: string): void {
    this.log(LogLevel.ERROR, message, context, additional, stack);
  }
  
  fatal(message: string, context?: string, additional?: Record<string, unknown>, stack?: string): void {
    this.log(LogLevel.FATAL, message, context, additional, stack);
  }
  
  private log(level: LogLevel, message: string, context?: string, additional?: Record<string, unknown>, stack?: string): void {
    if (level < this.level) {
      return;
    }
    
    // Log to console for development
    this.logToConsole(level, message, context, additional, stack);
    
    const entry: LogEntry = {
      timestamp: new Date().toISOString(),
      level,
      message,
      context,
      additional,
      stack,
      requestId: this.currentRequestId,
      traceId: this.currentTraceId,
    };
    
    this.buffer.push(entry);
    
    if (this.buffer.length >= this.bufferSize) {
      this.flushLogs();
    }
  }
  
  private logToConsole(level: LogLevel, message: string, context?: string, additional?: Record<string, unknown>, stack?: string): void {
    const logFn = this.getConsoleMethod(level);
    const formattedMessage = context ? `[${context}] ${message}` : message;
    
    if (additional) {
      logFn(formattedMessage, additional);
    } else {
      logFn(formattedMessage);
    }
    
    if (stack) {
      console.error(stack);
    }
  }
  
  private getConsoleMethod(level: LogLevel): (message?: unknown, ...optionalParams: unknown[]) => void {
    switch (level) {
      case LogLevel.TRACE:
      case LogLevel.DEBUG:
        return console.debug;
      case LogLevel.INFO:
        return console.info;
      case LogLevel.WARN:
        return console.warn;
      case LogLevel.ERROR:
      case LogLevel.FATAL:
        return console.error;
      default:
        return console.log;
    }
  }
  
  flushLogs(): void {
    if (this.buffer.length === 0 || this.sendingLogs) {
      return;
    }
    
    const logsToSend = [...this.buffer];
    this.buffer = [];
    this.sendingLogs = true;
    
    // Send logs to backend in staging/production
    if (this.isProdOrStaging) {
      this.sendLogsToBackend(logsToSend)
        .pipe(
          finalize(() => {
            this.sendingLogs = false;
          })
        )
        .subscribe({
          next: () => {
            // Successfully sent logs to the backend
          },
          error: (err) => {
            console.error('Failed to send logs to backend:', err);
            
            // Store in local storage as fallback with a size limit (5MB)
            try {
              const storedLogs = JSON.parse(localStorage.getItem('pendingLogs') || '[]');
              // Add failed logs to stored logs, maintaining a limit
              const allLogs = [...storedLogs, ...logsToSend].slice(-100); // Keep only last 100 entries
              localStorage.setItem('pendingLogs', JSON.stringify(allLogs));
            } catch (storageErr) {
              console.error('Failed to store logs in local storage:', storageErr);
            }
          }
        });
    } else {
      // Local development - just log to console
      console.info(`[LoggingService] ${logsToSend.length} logs would be sent to server`);
      this.sendingLogs = false;
    }
  }
  
  private sendLogsToBackend(logs: LogEntry[]): Observable<unknown> {
    return this.http.post('/api/logs', {
      logs,
      clientInfo: {
        userAgent: navigator.userAgent,
        url: window.location.href,
        timestamp: new Date().toISOString()
      }
    }, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    });
  }
  
  private setupErrorListeners(): void {
    // Global error handler for uncaught exceptions
    window.addEventListener('error', (event) => {
      this.error(
        `Uncaught error: ${event.message}`,
        'global',
        { 
          filename: event.filename,
          lineno: event.lineno,
          colno: event.colno
        },
        event.error ? event.error.stack : undefined
      );
    });
    
    // Unhandled promise rejection handler
    window.addEventListener('unhandledrejection', (event) => {
      const reason = event.reason;
      let message = 'Unhandled Promise rejection';
      let stack = undefined;
      
      if (reason instanceof Error) {
        message = `Unhandled Promise rejection: ${reason.message}`;
        stack = reason.stack;
      } else if (typeof reason === 'string') {
        message = `Unhandled Promise rejection: ${reason}`;
      } else {
        message = `Unhandled Promise rejection: ${JSON.stringify(reason)}`;
      }
      
      this.error(message, 'promise', { reason }, stack);
    });
  }
}