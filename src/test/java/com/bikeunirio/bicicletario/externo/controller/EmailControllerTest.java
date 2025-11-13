package com.bikeunirio.bicicletario.externo.controller;

import com.bikeunirio.bicicletario.externo.dto.EmailDto;
import com.bikeunirio.bicicletario.externo.service.EmailService;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmailControllerTest {
    @InjectMocks
    EmailController emailController;

    @Mock
    EmailService emailService;

    @Test
    public void testEnviarEmail(){
        EmailDto dto = new EmailDto("emailexternoes2@gmail.com","assunto","mensagem");

        ResponseEntity<EmailDto> respostaMock = ResponseEntity.ok(dto);
        when(emailService.enviarEmail(dto)).thenReturn(respostaMock);

        ResponseEntity<?> resposta = emailController.enviarEmail(dto);

        assertEquals(200, resposta.getStatusCode().value());
        assertEquals(dto,resposta.getBody());

        verify(emailService).enviarEmail(dto);

    }

}
