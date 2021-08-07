package com.brightwheel.email.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@AllArgsConstructor
public class SnailgunRequest implements EmailRequest {

    String from_email;
    String from_name;
    String to_email;
    String to_name;
    String subject;
    String body;

}
