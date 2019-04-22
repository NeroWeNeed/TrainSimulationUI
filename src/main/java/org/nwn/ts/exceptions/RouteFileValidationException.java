package org.nwn.ts.exceptions;

public class RouteFileValidationException extends FileValidationException {
    public RouteFileValidationException() {
    }

    public RouteFileValidationException(String message) {
        super(message);
    }

    public RouteFileValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public RouteFileValidationException(Throwable cause) {
        super(cause);
    }

    public RouteFileValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
