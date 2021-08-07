package com.brightwheel.email.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@AllArgsConstructor
public class SpendgridRequest implements EmailRequest {

    String sender;
    String recipient;
    String subject;
    String body;

}
