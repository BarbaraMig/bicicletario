package com.bikeunirio.bicicletario.externo.controller;

import com.bikeunirio.bicicletario.externo.dto.EmailDto;
import com.bikeunirio.bicicletario.externo.exceptions.GlobalExceptionHandler;
import com.bikeunirio.bicicletario.externo.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import com.bikeunirio.bicicletario.externo.controller.EmailController;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@Import(GlobalExceptionHandler.class)
class EmailControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;
    private EmailController emailController;

    @Mock
    private EmailService emailService;
    @BeforeEach
    void setup(){
        mockMvc = MockMvcBuilders.standaloneSetup(emailController)
                // registra o tratador de exceções manualmente
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // Teste 1: Caminho feliz (HTTP 200)
    @Test
    void testEnviarEmail_Sucesso() throws Exception {
        EmailDto dto = new EmailDto("emailexternoes2@gmail.com", "assunto", "mensagem");

        // Quando o serviço for chamado, não faça nada
        doNothing().when(emailService).enviarEmail(dto);

        // Simula a chamada POST para /enviarEmail
        mockMvc.perform(post("/enviarEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))) // converte dto em json
                .andExpect(status().isOk()) // Espera status 200
                .andExpect(jsonPath("$.receptor").value(dto.getReceptor()))
                .andExpect(jsonPath("$.assunto").value(dto.getAssunto()));
    }

    // MailParseException (http 422)
    @Test
    void testEnviarEmail_Falha_MailParseException() throws Exception {
        EmailDto dto = new EmailDto("email_invalido", "assunto", "mensagem");

        // o service lança a exceção que o handler vai capturar
        doThrow(new MailParseException("Email inválido")).when(emailService).enviarEmail(
                ArgumentMatchers.argThat(email -> email.getReceptor().equals("email_invalido"))
        );

        mockMvc.perform(post("/enviarEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity()) // Espera status 422
                .andExpect(jsonPath("$.status").value(422)) // Verifica o corpo do erro
                .andExpect(jsonPath("$.mensagem").value("Email inválido"));
    }

    // MailException (http 500)
    @Test
    void testEnviarEmail_Falha_MailException() throws Exception {
        EmailDto dto = new EmailDto("email@existe.com", "assunto", "mensagem");

        // Mocka o serviço para LANÇAR a exceção
        doThrow(new MailException("Erro de servidor de email") {}).when(emailService).enviarEmail(
                ArgumentMatchers.argThat(email -> email.getReceptor().equals("email@existe.com"))
        );

        mockMvc.perform(post("/enviarEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError()) // Espera status 500
                .andExpect(jsonPath("$.status").value(500)) // Verifica o corpo do erro
                .andExpect(jsonPath("$.mensagem").value("Erro de servidor de email"));
    }
}