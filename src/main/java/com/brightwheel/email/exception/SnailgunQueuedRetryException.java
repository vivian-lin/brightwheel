package com.brightwheel.email.exception;

import com.brightwheel.email.dto.response.Response;

public class SnailgunQueuedRetryException extends RuntimeException {

    private final String id;
    private final Response response;

    public SnailgunQueuedRetryException(String message, String id, Response response) {
        super(message);
        this.id = id;
        this.response = response;
    }

    public String getId() {
        return id;
    }

    public Response getResponse() {
        return response;
    }

}
