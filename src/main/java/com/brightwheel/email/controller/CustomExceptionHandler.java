package com.brightwheel.email.controller;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.brightwheel.email.dto.ValidationError;
import com.brightwheel.email.exception.FailedToSendEmailException;
import com.brightwheel.email.exception.ValidationException;
import com.brightwheel.email.validator.ValidatorUtils;

@ControllerAdvice
@ResponseBody
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class CustomExceptionHandler {

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler
    public ResponseEntity<ValidationError> handleValidationException(ValidationException exception) {
        return ValidatorUtils.unprocessableEntity(exception.getBindingResult());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ResponseEntity<Void> handleFailedToDispatchEmailException(FailedToSendEmailException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
