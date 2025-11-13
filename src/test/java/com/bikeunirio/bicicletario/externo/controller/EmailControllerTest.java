package com.bikeunirio.bicicletario.externo.controller;

import com.bikeunirio.bicicletario.externo.dto.EmailDto;
import com.bikeunirio.bicicletario.externo.service.EmailService;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EmailControllerTest {
    @InjectMocks
    EmailController emailController;

    @Mock
    EmailService emailService;

    @Test
    public void testEnviarEmail(){
        EmailDto dto = new EmailDto("emailexternoes2@gmail.com","assunto","mensagem");


    }

}
