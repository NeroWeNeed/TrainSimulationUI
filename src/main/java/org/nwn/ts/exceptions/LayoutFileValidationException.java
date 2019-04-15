package org.nwn.ts.exceptions;

public class LayoutFileValidationException extends ValidationException {
    public LayoutFileValidationException() {
    }

    public LayoutFileValidationException(String message) {
        super(message);
    }

    public LayoutFileValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public LayoutFileValidationException(Throwable cause) {
        super(cause);
    }

    public LayoutFileValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
