package com.brightwheel.email.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import com.brightwheel.email.dto.ValidationError;

public class ValidatorUtils {

    public static ResponseEntity<ValidationError> unprocessableEntity(BindingResult result) {
        Map<String, List<String>> fieldErrors = new HashMap<>();
        result.getFieldErrors().forEach(error -> {
            if (fieldErrors.containsKey(error.getField())) {
                fieldErrors.get(error.getField()).add(error.getCode());
            } else {
                List<String> errorCodes = new ArrayList<>();
                errorCodes.add(error.getCode());
                fieldErrors.put(error.getField(), errorCodes);
            }
        });

        return ResponseEntity.unprocessableEntity().body(new ValidationError(fieldErrors));
    }

}
