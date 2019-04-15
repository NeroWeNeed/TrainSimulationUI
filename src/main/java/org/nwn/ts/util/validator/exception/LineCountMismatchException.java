package org.nwn.ts.util.validator.exception;

public class LineCountMismatchException extends Exception {
    public LineCountMismatchException() {
    }

    public LineCountMismatchException(String message) {
        super(message);
    }

    public LineCountMismatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public LineCountMismatchException(Throwable cause) {
        super(cause);
    }

    public LineCountMismatchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
