package org.nwn.ts.exceptions;

public class InvalidHeaderException extends FileValidationException {
    public InvalidHeaderException() {
    }

    public InvalidHeaderException(String message) {
        super(message);
    }

    public InvalidHeaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidHeaderException(Throwable cause) {
        super(cause);
    }

    public InvalidHeaderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
