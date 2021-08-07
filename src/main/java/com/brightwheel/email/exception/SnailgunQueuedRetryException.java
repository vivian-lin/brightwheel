package com.brightwheel.email.exception;

public class SnailgunQueuedRetryException extends RuntimeException {

    private final String id;

    public SnailgunQueuedRetryException(String message, String id) {
        super(message);
        this.id = id;
    }

    public String getId() {
        return id;
    }

}
