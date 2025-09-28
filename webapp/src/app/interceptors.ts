import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandlerFn,
  HttpRequest,
  HttpStatusCode,
} from '@angular/common/http';
import { inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { catchError, Observable, throwError } from 'rxjs';
import { AlertDialog } from './alert-dialog/alert-dialog';

export function unauthorizedResponseInterceptor(
  req: HttpRequest<unknown>,
  next: HttpHandlerFn
): Observable<HttpEvent<unknown>> {
  let alertedUnauthorized = false;
  const dialog = inject(MatDialog);

  return next(req).pipe(
    catchError((error) => {
      if (error instanceof HttpErrorResponse && error.status === HttpStatusCode.Unauthorized) {
        console.error('Request was unauthorized', error);

        if (alertedUnauthorized) {
          console.debug('Already alerted');
          return throwError(() => error);
        }
        alertedUnauthorized = true;

        const dialogRef = dialog.open(AlertDialog, {
          disableClose: true,
          data: { title: 'Session Expired', message: 'Please login again!' },
        });

        dialogRef.afterClosed().subscribe(() => {
          // just page reload is enough, backend will redirect to login page
          window.location.reload();
        });
      }
      return throwError(() => error);
    })
  );
}
