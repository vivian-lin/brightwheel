package com.brightwheel.email.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.brightwheel.email.dto.Email;

@Component
public class EmailValidator implements Validator {

    public static final String TO = "to";
    public static final String TO_NAME = "to_name";
    public static final String FROM = "from";
    public static final String FROM_NAME = "from_name";
    public static final String SUBJECT = "subject";
    public static final String BODY = "body";

    public static final String TO_REQUIRED = "EMAIL_REQUEST.TO.FIELD_REQUIRED";
    public static final String TO_NAME_REQUIRED = "EMAIL_REQUEST.TO_NAME.FIELD_REQUIRED";
    public static final String FROM_REQUIRED = "EMAIL_REQUEST.FROM.FIELD_REQUIRED";
    public static final String FROM_NAME_REQUIRED = "EMAIL_REQUEST.FROM_NAME.FIELD_REQUIRED";
    public static final String SUBJECT_REQUIRED = "EMAIL_REQUEST.SUBJECT.FIELD_REQUIRED";
    public static final String BODY_REQUIRED = "EMAIL_REQUEST.BODY.FIELD_REQUIRED";
    public static final String EMAIL_IS_INVALID = "EMAIL_REQUEST.EMAIL_IS_INVALID";

    @Override
    public boolean supports(Class<?> clazz) {
        return Email.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, TO, TO_REQUIRED);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, TO_NAME, TO_NAME_REQUIRED);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, FROM, FROM_REQUIRED);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, FROM_NAME, FROM_NAME_REQUIRED);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, SUBJECT, SUBJECT_REQUIRED);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, BODY, BODY_REQUIRED);

        if (errors.hasErrors())
            return;

        Email email = (Email) target;
        if (!org.apache.commons.validator.routines.EmailValidator.getInstance().isValid(email.getTo())) {
            errors.rejectValue(TO, EMAIL_IS_INVALID);
        }
        if (!org.apache.commons.validator.routines.EmailValidator.getInstance().isValid(email.getFrom())) {
            errors.rejectValue(FROM, EMAIL_IS_INVALID);
        }
    }

}
