package com.brightwheel.email.service;

import java.io.IOException;

import com.brightwheel.email.dto.response.Response;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpMapper {

    private static final String X_API_KEY = "X-Api-Key";

    public static Request buildPostRequest(String url, String apiKey, String body) {
        return new Request.Builder()
                .url(url).addHeader(X_API_KEY, apiKey).post(RequestBody
                        .create(MediaType.get(org.springframework.http.MediaType.APPLICATION_JSON_VALUE), body))
                .build();
    }

    public static Request buildGetRequest(String url, String apiKey) {
        return new Request.Builder().url(url).addHeader(X_API_KEY, apiKey).build();
    }

    public static Response mapResponse(okhttp3.Response response) throws IOException {
        return Response.builder().body(response.body().string()).statusCode(response.code()).build();
    }

}
