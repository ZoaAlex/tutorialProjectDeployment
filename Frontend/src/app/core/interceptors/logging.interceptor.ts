import { HttpInterceptorFn, HttpResponse } from '@angular/common/http';
import { tap } from 'rxjs/operators';

export const loggingInterceptor: HttpInterceptorFn = (req, next) => {
    const started = Date.now();
    console.log(`[HTTP Request] ${req.method} "${req.urlWithParams}"`);

    return next(req).pipe(
        tap({
            next: (event) => {
                if (event instanceof HttpResponse) {
                    const elapsed = Date.now() - started;
                    console.log(`[HTTP Response] ${req.method} "${req.urlWithParams}" took ${elapsed} ms`);
                    console.log('[Response Body]', event.body);
                }
            },
            error: (error) => {
                const elapsed = Date.now() - started;
                console.error(`[HTTP Error] ${req.method} "${req.urlWithParams}" took ${elapsed} ms`, error);
            }
        })
    );
};
