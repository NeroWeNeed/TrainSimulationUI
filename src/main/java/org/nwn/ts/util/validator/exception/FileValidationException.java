package org.nwn.ts.util.validator.exception;

public class FileValidationException extends Exception {
    public FileValidationException() {
    }

    public FileValidationException(String message) {
        super(message);
    }

    public FileValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileValidationException(Throwable cause) {
        super(cause);
    }

    public FileValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
