package com.brightwheel.email.service.api;

import static com.brightwheel.email.service.HttpMapper.buildPostRequest;
import static com.brightwheel.email.service.HttpMapper.mapResponse;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.brightwheel.email.dto.Email;
import com.brightwheel.email.dto.request.EmailRequest;
import com.brightwheel.email.dto.request.SpendgridRequest;
import com.brightwheel.email.dto.response.EmailResponse;
import com.brightwheel.email.dto.response.EmailStatus;
import com.brightwheel.email.dto.response.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;

@Service("spendgridApiImpl")
public class SpendgridApiService implements EmailApiService {

    private static final Logger logger = LoggerFactory.getLogger(SpendgridApiService.class);

    @Value("${api.spendgrid.url}")
    private String url;

    @Value("${api.spendgrid.api_key}")
    private String apiKey;

    @Autowired
    private OkHttpClient httpClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public EmailRequest buildEmailRequest(Email email) {
        return SpendgridRequest.builder().sender(String.format("%s <%s>", email.getFrom_name(), email.getFrom()))
                .recipient(String.format("%s <%s>", email.getTo_name(), email.getTo())).subject(email.getSubject())
                .body(email.getBody()).build();
    }

    @Override
    public EmailResponse dispatch(EmailRequest emailRequest) {
        Request httpRequest;

        try {
            httpRequest = buildPostRequest(url, apiKey, objectMapper.writeValueAsString(emailRequest));
        } catch (JsonProcessingException e) {
            logger.error("could not write email request to string", e);
            return null;
        }

        try (okhttp3.Response httpResponse = httpClient.newCall(httpRequest).execute()) {
            Response response = mapResponse(httpResponse);
            logger.info("snailgun response {}", response.getBody());

            return EmailResponse.builder().emailStatus(EmailStatus.SENT).httpResponse(response).build();
        } catch (IOException e) {
            logger.error("timed out waiting for spendgrid");
            return createFailedResponse();
        }
    }

    private static EmailResponse createFailedResponse() {
        return EmailResponse.builder().emailStatus(EmailStatus.FAILED).httpResponse(null).build();
    }

}
