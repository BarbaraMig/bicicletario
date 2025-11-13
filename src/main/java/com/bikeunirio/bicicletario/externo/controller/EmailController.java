package com.bikeunirio.bicicletario.externo.controller;

import com.bikeunirio.bicicletario.externo.dto.EmailDto;
import com.bikeunirio.bicicletario.externo.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class EmailController {

    private final EmailService emailService;

    EmailController(EmailService emailService){
        this.emailService=emailService;
    }

    @PostMapping("/enviarEmail")
    public ResponseEntity<EmailDto> enviarEmail(@RequestBody EmailDto email){
        emailService.enviarEmail(email);
        return ResponseEntity.ok(email);
    }

}