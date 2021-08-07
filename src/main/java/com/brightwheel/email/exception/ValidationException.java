package com.brightwheel.email.exception;

import org.springframework.validation.BindingResult;

public class ValidationException extends RuntimeException {

    private final BindingResult errors;

    public ValidationException(BindingResult errors) {
        super("Validation Error");
        this.errors = errors;
    }

    public BindingResult getBindingResult() {
        return errors;
    }

}
