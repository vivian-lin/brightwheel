package com.brightwheel.email.validator;

import static com.brightwheel.email.validator.EmailValidator.BODY;
import static com.brightwheel.email.validator.EmailValidator.BODY_REQUIRED;
import static com.brightwheel.email.validator.EmailValidator.EMAIL_IS_INVALID;
import static com.brightwheel.email.validator.EmailValidator.FROM;
import static com.brightwheel.email.validator.EmailValidator.FROM_NAME;
import static com.brightwheel.email.validator.EmailValidator.FROM_NAME_REQUIRED;
import static com.brightwheel.email.validator.EmailValidator.FROM_REQUIRED;
import static com.brightwheel.email.validator.EmailValidator.SUBJECT;
import static com.brightwheel.email.validator.EmailValidator.SUBJECT_REQUIRED;
import static com.brightwheel.email.validator.EmailValidator.TO;
import static com.brightwheel.email.validator.EmailValidator.TO_NAME;
import static com.brightwheel.email.validator.EmailValidator.TO_NAME_REQUIRED;
import static com.brightwheel.email.validator.EmailValidator.TO_REQUIRED;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import com.brightwheel.email.dto.Email;

public class EmailValidatorTest {

    private static final String BEAN_PROPERTY_STRING = "emailRequest";
    private static EmailValidator validator;

    @BeforeAll
    public static void before() {
        validator = new EmailValidator();
    }

    @Test
    public void shouldRejectIfRequiredFieldsNull() {
        Email email = new Email(null, null, null, null, null, null);

        Errors errors = new BeanPropertyBindingResult(email, BEAN_PROPERTY_STRING);
        validator.validate(email, errors);

        assertThat(errors.hasErrors()).isTrue();

        assertErrorCode(errors, TO, TO_REQUIRED);
        assertErrorCode(errors, TO_NAME, TO_NAME_REQUIRED);
        assertErrorCode(errors, FROM, FROM_REQUIRED);
        assertErrorCode(errors, FROM_NAME, FROM_NAME_REQUIRED);
        assertErrorCode(errors, SUBJECT, SUBJECT_REQUIRED);
        assertErrorCode(errors, BODY, BODY_REQUIRED);
    }

    @Test
    public void shouldRejectIfRequiredFieldsEmpty() {
        Email email = new Email(" ", " ", " ", " ", " ", " ");

        Errors errors = new BeanPropertyBindingResult(email, BEAN_PROPERTY_STRING);
        validator.validate(email, errors);

        assertThat(errors.hasErrors()).isTrue();

        assertErrorCode(errors, TO, TO_REQUIRED);
        assertErrorCode(errors, TO_NAME, TO_NAME_REQUIRED);
        assertErrorCode(errors, FROM, FROM_REQUIRED);
        assertErrorCode(errors, FROM_NAME, FROM_NAME_REQUIRED);
        assertErrorCode(errors, SUBJECT, SUBJECT_REQUIRED);
        assertErrorCode(errors, BODY, BODY_REQUIRED);
    }

    @Test
    public void shouldRejectInvalidEmailAddresses() {
        Email email = Email.builder().to("foo@bar").to_name("Foo Person").from("email.com").from_name("From Person")
                .subject("Subject").body("Email body").build();

        Errors errors = new BeanPropertyBindingResult(email, BEAN_PROPERTY_STRING);
        validator.validate(email, errors);

        assertThat(errors.hasErrors()).isTrue();
        assertErrorCode(errors, TO, EMAIL_IS_INVALID);
        assertErrorCode(errors, FROM, EMAIL_IS_INVALID);
    }

    @Test
    public void shouldAcceptValidEmailRequest() {
        Email email = Email.builder().to("foo@email.com").to_name("Foo Person").from("from@email.com")
                .from_name("From Person").subject("Subject").body("Email body").build();

        Errors errors = new BeanPropertyBindingResult(email, BEAN_PROPERTY_STRING);
        validator.validate(email, errors);

        assertThat(errors.hasErrors()).isFalse();
    }

    private static void assertErrorCode(Errors errors, String field, String expectedCode) {
        assertThat(errors.getFieldError(field)).isNotNull();
        assertThat(errors.getFieldError(field).getCode()).isEqualTo(expectedCode);
    }
}
