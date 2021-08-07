package com.brightwheel.email.service.api;

import static com.brightwheel.email.service.HttpMapper.buildGetRequest;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.brightwheel.email.dto.response.EmailResponse;
import com.brightwheel.email.dto.response.EmailStatus;
import com.brightwheel.email.dto.response.Response;
import com.brightwheel.email.dto.response.SnailgunResponse;
import com.brightwheel.email.exception.SnailgunQueuedRetryException;
import com.brightwheel.email.service.HttpMapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;

@Service
public class SnailgunApiRetryableService {
    private static final Logger logger = LoggerFactory.getLogger(SnailgunApiRetryableService.class);

    @Value("${api.snailgun.url}")
    private String url;

    @Value("${api.snailgun.api_key}")
    private String apiKey;

    @Autowired
    private OkHttpClient httpClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Retryable(value = SnailgunQueuedRetryException.class, maxAttempts = 3, backoff = @Backoff(delay = 5000))
    public EmailResponse poll(String id) {
        Request httpRequest = buildGetRequest(url + "/" + id, apiKey);
        try (okhttp3.Response httpResponse = httpClient.newCall(httpRequest).execute()) {
            Response response = HttpMapper.mapResponse(httpResponse);

            SnailgunResponse snailgunResponse = objectMapper.readValue(response.getBody(), SnailgunResponse.class);
            EmailStatus status = mapStatus(snailgunResponse.getStatus());

            if (EmailStatus.QUEUED.equals(status)) {
                logger.info("retrying queued email, id: {}", id);
                throw new SnailgunQueuedRetryException("retrying queued email", id);
            }

            return EmailResponse.builder().emailStatus(status).httpResponse(response).build();
        } catch (IOException e) {
            logger.error("timed out waiting for snailgun");
            return createFailedResponse();
        }
    }

    @Recover
    public EmailResponse recover(SnailgunQueuedRetryException e) {
        logger.info("exhausted retries for snailgun email, id: {}", e.getId());
        return createFailedResponse();
    }

    private static EmailStatus mapStatus(String status) {
        EmailStatus emailStatus;
        switch (status) {
            case "queued":
                emailStatus = EmailStatus.QUEUED;
                break;
            case "sent":
                emailStatus = EmailStatus.SENT;
                break;
            case "failed":
                emailStatus = EmailStatus.FAILED;
                break;
            default:
                emailStatus = EmailStatus.UNKNOWN;
        }
        return emailStatus;
    }

    private static EmailResponse createFailedResponse() {
        return EmailResponse.builder().emailStatus(EmailStatus.FAILED).httpResponse(null).build();
    }

}
