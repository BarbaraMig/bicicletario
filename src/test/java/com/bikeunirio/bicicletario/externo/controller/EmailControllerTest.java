package com.bikeunirio.bicicletario.externo.controller;

import com.bikeunirio.bicicletario.externo.dto.EmailDto;
import com.bikeunirio.bicicletario.externo.service.EmailService;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;


public class EmailControllerTest {
    @InjectMocks
    EmailController emailController;

    @Mock
    EmailService emailService;

    //Tudo dá certo
    @Test
    public void testEnviarEmailCerto(){
        EmailDto dto = new EmailDto();


    }
    //O email é inválido
    @Test
    public void testEnviarEmailErrado1(){

    }
    //O email não existe
    @Test
    public void testEnviarEmailErrado2(){

    }
}
