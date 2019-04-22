package org.nwn.ts.exceptions;

public class MaintenanceFileValidationException extends FileValidationException {
    public MaintenanceFileValidationException() {
    }

    public MaintenanceFileValidationException(String message) {
        super(message);
    }

    public MaintenanceFileValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public MaintenanceFileValidationException(Throwable cause) {
        super(cause);
    }

    public MaintenanceFileValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
