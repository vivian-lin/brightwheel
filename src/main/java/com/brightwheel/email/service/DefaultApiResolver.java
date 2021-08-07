package com.brightwheel.email.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.brightwheel.email.dto.EmailApi;

@Component
public class DefaultApiResolver {

    @Value("${default_api}")
    private String defaultApi;

    public EmailApi getDefault() {
        return EmailApi.valueOf(defaultApi);
    }

}
