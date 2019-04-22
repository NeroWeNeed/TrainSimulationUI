package org.nwn.ts.exceptions;

public class RepeatableRouteFileValidationException extends RouteFileValidationException {
    public RepeatableRouteFileValidationException() {
    }

    public RepeatableRouteFileValidationException(String message) {
        super(message);
    }

    public RepeatableRouteFileValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepeatableRouteFileValidationException(Throwable cause) {
        super(cause);
    }

    public RepeatableRouteFileValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
