package org.nwn.ts.exceptions;

public class DailyRoutesFileValidationException extends FileValidationException {
    public DailyRoutesFileValidationException() {
    }

    public DailyRoutesFileValidationException(String message) {
        super(message);
    }

    public DailyRoutesFileValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DailyRoutesFileValidationException(Throwable cause) {
        super(cause);
    }

    public DailyRoutesFileValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
