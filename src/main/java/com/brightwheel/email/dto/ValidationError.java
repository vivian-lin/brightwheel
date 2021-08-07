package com.brightwheel.email.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@AllArgsConstructor
public class ValidationError {

    @Singular(ignoreNullCollections = true)
    Map<String, List<String>> fieldErrors;

}
