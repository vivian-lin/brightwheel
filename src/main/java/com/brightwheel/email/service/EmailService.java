package com.brightwheel.email.service;

import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

import com.brightwheel.email.dto.Email;
import com.brightwheel.email.dto.EmailApi;
import com.brightwheel.email.dto.request.EmailRequest;
import com.brightwheel.email.dto.response.EmailResponse;
import com.brightwheel.email.exception.FailedToSendEmailException;
import com.brightwheel.email.exception.ValidationException;
import com.brightwheel.email.service.api.EmailApiService;
import com.brightwheel.email.validator.EmailValidator;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private EmailValidator validator;
    private Map<EmailApi, EmailApiService> emailDispatchServices;
    private DefaultApiResolver defaultApiResolver;

    @Autowired
    public EmailService(EmailValidator validator, @Qualifier("spendgridApiImpl") EmailApiService spendgridImpl,
            @Qualifier("snailgunApiImpl") EmailApiService snailgunImpl, DefaultApiResolver defaultApiResolver) {
        this.validator = validator;
        this.emailDispatchServices = Map.of(EmailApi.SPENDGRID, spendgridImpl, EmailApi.SNAILGUN, snailgunImpl);
        this.defaultApiResolver = defaultApiResolver;
    }

    public EmailResponse send(Email email) {
        validate(email);

        EmailApi defaultApi = defaultApiResolver.getDefault();
        logger.info("defaulting to email API {}", defaultApi);

        EmailApiService apiService = emailDispatchServices.get(defaultApi);
        EmailRequest emailRequest = apiService.buildEmailRequest(email);

        EmailResponse emailResponse = apiService.dispatch(emailRequest);
        if (Objects.isNull(emailResponse)) {
            throw new FailedToSendEmailException("did not make request to API");
        }

        return emailResponse;
    }

    private void validate(Email email) {
        DataBinder binder = new DataBinder(email);
        binder.setValidator(validator);
        binder.validate();

        BindingResult results = binder.getBindingResult();
        if (results.hasErrors()) {
            throw new ValidationException(results);
        }
    }

}
