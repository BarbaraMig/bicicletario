package com.bikeunirio.bicicletario.externo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class EmailConfiguration {
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("emailexternoes2@gmail.com");
        mailSender.setPort(587);

        return mailSender;
    }

}
