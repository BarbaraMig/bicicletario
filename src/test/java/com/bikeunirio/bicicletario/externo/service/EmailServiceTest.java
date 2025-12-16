package com.bikeunirio.bicicletario.externo.service;

import com.bikeunirio.bicicletario.externo.config.SendGridConfigurationProperties;
import com.bikeunirio.bicicletario.externo.dto.EmailDto;
import com.bikeunirio.bicicletario.externo.client.SendGridClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private SendGridClient sendGridClient;

    @Mock
    private SendGridConfigurationProperties props;

    private EmailService emailService;

    @BeforeEach
    void setup() {
        emailService = new EmailService(sendGridClient, props);
    }

    //CAMINHO FELIZ
    @Test
    void enviarEmail_sucesso() {
        EmailDto dto = new EmailDto(
                "destino@teste.com",
                "Assunto",
                "Mensagem"
        );

        when(props.getFromEmail()).thenReturn("noreply@teste.com");
        when(props.getFromName()).thenReturn("BikeUnirio");

        doNothing().when(sendGridClient).enviarEmail(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString()
        );

        assertDoesNotThrow(() -> emailService.enviarEmail(dto));

        verify(sendGridClient, times(1)).enviarEmail(
                eq("emailexternoes2@gmail.com"),
                eq("BikeUnirio"),
                eq(dto.getReceptor()),
                eq(dto.getAssunto()),
                eq(dto.getMensagem())
        );
    }

    //DTO inválido → não chama SendGrid
    @Test
    void enviarEmail_dtoInvalido() {
        EmailDto dto = new EmailDto("", "", "");

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> emailService.enviarEmail(dto));

        assertEquals("Receptor do email é obrigatório", ex.getMessage());

        verifyNoInteractions(sendGridClient);
    }

    //Falha no SendGrid
    @Test
    void enviarEmail_falhaSendGrid() {
        EmailDto dto = new EmailDto(
                "destino@teste.com",
                "Assunto",
                "Mensagem"
        );

        when(props.getFromEmail()).thenReturn("noreply@teste.com");
        when(props.getFromName()).thenReturn("BikeUnirio");

        doThrow(new RuntimeException("Erro SendGrid"))
                .when(sendGridClient)
                .enviarEmail(any(), any(), any(), any(), any());

        RuntimeException ex =
                assertThrows(RuntimeException.class, () -> emailService.enviarEmail(dto));

        assertEquals("Erro SendGrid", ex.getMessage());
    }
}