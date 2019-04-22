package org.nwn.ts.exceptions;

public class RouteTypeParseException extends RouteFileValidationException {
    public RouteTypeParseException() {
    }

    public RouteTypeParseException(String message) {
        super(message);
    }

    public RouteTypeParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public RouteTypeParseException(Throwable cause) {
        super(cause);
    }

    public RouteTypeParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
