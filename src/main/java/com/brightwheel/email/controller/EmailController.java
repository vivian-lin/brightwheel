package com.brightwheel.email.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brightwheel.email.dto.Email;
import com.brightwheel.email.dto.response.EmailResponse;
import com.brightwheel.email.service.EmailService;

@RestController
@RequestMapping(path = "/email", consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class EmailController {

    @Autowired
    private EmailService emailManagementService;

    @PostMapping
    public ResponseEntity<EmailResponse> send(@RequestBody Email email) {
        EmailResponse response = emailManagementService.send(email);
        return ResponseEntity.ok(response);
    }

}
