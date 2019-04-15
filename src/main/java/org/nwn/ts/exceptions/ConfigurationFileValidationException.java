package org.nwn.ts.exceptions;

public class ConfigurationFileValidationException extends ValidationException {
    public ConfigurationFileValidationException() {
    }

    public ConfigurationFileValidationException(String message) {
        super(message);
    }

    public ConfigurationFileValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationFileValidationException(Throwable cause) {
        super(cause);
    }

    public ConfigurationFileValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
