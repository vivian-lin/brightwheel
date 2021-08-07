package com.brightwheel.email.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@AllArgsConstructor
public class EmailResponse {

    EmailStatus emailStatus;
    Response httpResponse;

}
