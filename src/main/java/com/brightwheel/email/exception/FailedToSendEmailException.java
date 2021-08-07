package com.brightwheel.email.exception;

public class FailedToSendEmailException extends RuntimeException {

    public FailedToSendEmailException(String message) {
        super(message);
    }
}
