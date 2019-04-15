package org.nwn.ts.util.validator.exception;

public class InvalidLineException extends FileValidationException {
    public InvalidLineException() {
    }

    public InvalidLineException(String message) {
        super(message);
    }

    public InvalidLineException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidLineException(Throwable cause) {
        super(cause);
    }

    public InvalidLineException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
