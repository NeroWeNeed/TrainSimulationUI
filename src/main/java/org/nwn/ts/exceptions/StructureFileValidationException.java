package org.nwn.ts.exceptions;

public class StructureFileValidationException extends FileValidationException {
    public StructureFileValidationException() {
    }

    public StructureFileValidationException(String message) {
        super(message);
    }

    public StructureFileValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public StructureFileValidationException(Throwable cause) {
        super(cause);
    }

    public StructureFileValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
