package com.bikeunirio.bicicletario.externo.service;


import com.bikeunirio.bicicletario.externo.dto.EmailDto;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private JavaMailSender mailSender;
    private final String emailRemetente = "emailexternoes2@gmail.com";

    public EmailService( JavaMailSender mailSender){
        this.mailSender = mailSender;
    }


    public ResponseEntity<EmailDto> enviarEmail(EmailDto emailDto){
        //construtor do SimpleMailMessage não recebe nenhum parâmetro
        SimpleMailMessage email = new SimpleMailMessage();

        email.setText(emailDto.getMensagem());
        email.setSubject(emailDto.getAssunto());
        email.setTo(emailDto.getReceptor());
        email.setFrom(emailRemetente);
        mailSender.send(email);
        //exceções são tratadas pelo Global Handler

        return ResponseEntity.ok(emailDto);
    }
}