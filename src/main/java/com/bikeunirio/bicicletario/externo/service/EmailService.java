package com.bikeunirio.bicicletario.externo.service;


import com.bikeunirio.bicicletario.externo.dto.EmailDto;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService( JavaMailSender mailSender){
        this.mailSender = mailSender;
    }


    public boolean enviarEmail(EmailDto emailDto){
        //construtor do SimpleMailMessage não recebe nenhum parâmetro
        SimpleMailMessage email = new SimpleMailMessage();
        String receptor = emailDto.getReceptor();
        if(validarFormatoEmail(receptor)){
            email.setText(emailDto.getMensagem());
            email.setSubject(emailDto.getAssunto());
            email.setTo(receptor);
            email.setFrom("emailexternoes2@gmail.com");
            mailSender.send(email);
            return true;
        }
        return false;
        //exceções são tratadas pelo Global Handler
    }
    //validação do email
    private boolean validarFormatoEmail(String emailAddress) {
        return Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
                .matcher(emailAddress)
                .matches();
    }
}