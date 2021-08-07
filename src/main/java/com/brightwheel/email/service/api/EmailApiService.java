package com.brightwheel.email.service.api;

import com.brightwheel.email.dto.Email;
import com.brightwheel.email.dto.request.EmailRequest;
import com.brightwheel.email.dto.response.EmailResponse;

public interface EmailApiService {

    EmailRequest buildEmailRequest(Email email);

    EmailResponse dispatch(EmailRequest emailRequest);

}
