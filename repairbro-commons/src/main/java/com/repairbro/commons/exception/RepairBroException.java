package com.repairbro.commons.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base exception for all RepairBro services.
 * Carries an HTTP status code and an error code for API responses.
 */
@Getter
public class RepairBroException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    public RepairBroException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public RepairBroException(String message, Throwable cause, HttpStatus status, String errorCode) {
        super(message, cause);
        this.status = status;
        this.errorCode = errorCode;
    }
}
