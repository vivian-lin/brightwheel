package com.brightwheel.email.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@AllArgsConstructor
public class Email {

    String to;
    String to_name;
    String from;
    String from_name;
    String subject;
    String body;

}
