package com.brightwheel.email.service.api;

import static com.brightwheel.email.service.HttpMapper.buildPostRequest;

import java.io.IOException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.brightwheel.email.dto.Email;
import com.brightwheel.email.dto.request.EmailRequest;
import com.brightwheel.email.dto.request.SnailgunRequest;
import com.brightwheel.email.dto.response.EmailResponse;
import com.brightwheel.email.dto.response.EmailStatus;
import com.brightwheel.email.dto.response.SnailgunResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;

@Service("snailgunApiImpl")
public class SnailgunApiService implements EmailApiService {
    private static final Logger logger = LoggerFactory.getLogger(SnailgunApiService.class);

    @Value("${api.snailgun.url}")
    private String url;

    @Value("${api.snailgun.api_key}")
    private String apiKey;

    @Autowired
    private OkHttpClient httpClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SnailgunApiRetryableService snailgunApiRetryableService;

    @Override
    public EmailRequest buildEmailRequest(Email email) {
        return SnailgunRequest.builder().from_email(email.getFrom()).from_name(email.getFrom_name())
                .to_email(email.getTo()).to_name(email.getTo_name()).subject(email.getSubject()).body(email.getBody())
                .build();
    }

    @Override
    public EmailResponse dispatch(EmailRequest emailRequest) {
        Request httpRequest;

        try {
            String requestBody = objectMapper.writeValueAsString(emailRequest);
            httpRequest = buildPostRequest(url, apiKey, requestBody);
        } catch (JsonProcessingException e) {
            logger.error("could not write email request to string", e);
            return null;
        }

        try (okhttp3.Response httpResponse = httpClient.newCall(httpRequest).execute()) {
            SnailgunResponse snailgunResponse =
                    objectMapper.readValue(httpResponse.body().string(), SnailgunResponse.class);

            if (Objects.isNull(snailgunResponse.getId())) {
                logger.error("cannot fetch snailgun email with null id");
                return null;
            }

            return snailgunApiRetryableService.poll(snailgunResponse.getId());
        } catch (IOException e) {
            logger.error("timed out waiting for snailgun");
            return createFailedResponse();
        }
    }

    private static EmailResponse createFailedResponse() {
        return EmailResponse.builder().emailStatus(EmailStatus.FAILED).httpResponse(null).build();
    }

}
