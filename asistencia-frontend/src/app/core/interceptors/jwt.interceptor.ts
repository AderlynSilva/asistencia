import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent
} from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    // No meter token al login
    if (req.url.includes('/api/auth/login')) {
      return next.handle(req);
    }

    // 1) intenta leer token normal
    let token = localStorage.getItem('token');

    // 2) por si guardaste un objeto JSON en "auth" (en tu foto sale "auth")
    if (!token) {
      const authRaw = localStorage.getItem('auth');
      if (authRaw) {
        try {
          const authObj = JSON.parse(authRaw);
          token = authObj?.token ?? null;
        } catch {
          // ignore
        }
      }
    }

    if (token) {
      const authReq = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
      return next.handle(authReq);
    }

    return next.handle(req);
  }
}
