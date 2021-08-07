package com.brightwheel.email.controller;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.brightwheel.email.dto.Email;
import com.brightwheel.email.dto.EmailApiType;
import com.brightwheel.email.dto.ValidationError;
import com.brightwheel.email.dto.response.EmailResponse;
import com.brightwheel.email.dto.response.EmailStatus;
import com.brightwheel.email.service.DefaultApiResolver;
import com.brightwheel.email.service.api.SnailgunApiRetryableService;
import com.brightwheel.email.service.api.SnailgunApiService;
import com.brightwheel.email.service.api.SpendgridApiService;
import com.brightwheel.email.validator.EmailValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmailControllerTest {

    private static final String ENDPOINT = "/email";

    @Autowired
    private ObjectMapper objectMapper;
    @LocalServerPort
    private int port;
    @MockBean
    private DefaultApiResolver defaultApiResolver;
    @Value("${default_api}")
    private String defaultApi;
    @SpyBean
    private SpendgridApiService spendgridApiService;
    @SpyBean
    private SnailgunApiService snailgunApiService;
    @SpyBean
    private SnailgunApiRetryableService snailgunApiRetryableService;

    private ObjectNode body;

    @BeforeAll
    public static void beforeAll() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @BeforeEach
    public void before() {
        body = objectMapper.createObjectNode();
        when(defaultApiResolver.getDefault()).thenReturn(EmailApiType.valueOf(defaultApi));
    }

    @Test
    public void shouldRespondUnprocessableEntityInvalidEmailRequestProvided() {
        Email email = createValidEmail().toBuilder().body(null).build();
        body = objectMapper.valueToTree(email);

        Response response = given().port(port).when().body(body).contentType(ContentType.JSON).post(ENDPOINT).then()
                .extract().response();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_UNPROCESSABLE_ENTITY);

        ValidationError actual = response.as(ValidationError.class);
        ValidationError expected = ValidationError.builder()
                .fieldError(EmailValidator.BODY, List.of(EmailValidator.BODY_REQUIRED)).build();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldDefaultToSnailgun() {
        when(defaultApiResolver.getDefault()).thenReturn(EmailApiType.SNAILGUN);

        Email email = createValidEmail();
        body = objectMapper.valueToTree(email);

        Response response = given().port(port).when().body(body).contentType(ContentType.JSON).post(ENDPOINT).then()
                .extract().response();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);

        EmailResponse actual = response.as(EmailResponse.class);
        assertThat(actual.getEmailStatus()).isEqualTo(EmailStatus.SENT);

        verify(snailgunApiService).dispatch(any());
        verify(snailgunApiRetryableService, atLeastOnce()).poll(any());
        verify(spendgridApiService, never()).dispatch(any());
    }

    @Test
    public void shouldDefaultToSpendgrid() {
        when(defaultApiResolver.getDefault()).thenReturn(EmailApiType.SPENDGRID);

        Email email = createValidEmail();
        body = objectMapper.valueToTree(email);

        Response response = given().port(port).when().body(body).contentType(ContentType.JSON).post(ENDPOINT).then()
                .extract().response();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);

        EmailResponse actual = response.as(EmailResponse.class);
        assertThat(actual.getEmailStatus()).isEqualTo(EmailStatus.SENT);

        verify(spendgridApiService).dispatch(any());
        verify(snailgunApiService, never()).dispatch(any());
    }

    private static Email createValidEmail() {
        return Email.builder().to("susan@preschool.com").to_name("Ms. Susan").from("no-reply@brightwheel.com")
                .from_name("brightwheel").subject("Your Weekly Report")
                .body("<h1>Weekly Report</h1><p>You saved 10 hours this week!</p>").build();
    }
}
