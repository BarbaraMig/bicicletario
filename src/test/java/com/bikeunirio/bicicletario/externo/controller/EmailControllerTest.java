package com.bikeunirio.bicicletario.externo.controller;

import com.bikeunirio.bicicletario.externo.dto.EmailDto;
import com.bikeunirio.bicicletario.externo.exceptions.GlobalExceptionHandler;
import com.bikeunirio.bicicletario.externo.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Define o teste como um teste da camada Web, focado no EmailController
@WebMvcTest(controllers = EmailController.class)
@Import(GlobalExceptionHandler.class) // IMPORTANTE: Traz seu handler para o contexto do teste
class EmailControllerTest {

    @Autowired
    private MockMvc mockMvc; // Para simular requisições HTTP

    @Autowired
    private ObjectMapper objectMapper; // Para converter objetos em JSON

    @Mock // O Spring mocka o EmailService, já que não queremos enviar emails de verdade
    private EmailService emailService;

    // Teste 1: Caminho feliz (HTTP 200)
    @Test
    void testEnviarEmail_Sucesso() throws Exception {
        EmailDto dto = new EmailDto("emailexternoes2@gmail.com", "assunto", "mensagem");
        ResponseEntity<EmailDto> respostaMock = ResponseEntity.ok(dto);

        // Quando o serviço for chamado, retorne o mock
        when(emailService.enviarEmail(dto)).thenReturn(respostaMock);

        // Simula a chamada POST para /enviarEmail
        mockMvc.perform(post("/enviarEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))) // Converte o DTO em JSON
                .andExpect(status().isOk()) // Espera status 200
                .andExpect(jsonPath("$.receptor").value(dto.getReceptor())) // Verifica o JSON de resposta
                .andExpect(jsonPath("$.assunto").value(dto.getAssunto()));
    }

    // Teste 2: Testando o GlobalExceptionHandler para MailParseException (HTTP 422)
    @Test
    void testEnviarEmail_Falha_MailParseException() throws Exception {
        EmailDto dto = new EmailDto("email_invalido", "assunto", "mensagem");

        // Mocka o serviço para LANÇAR a exceção que o handler deve pegar
        when(emailService.enviarEmail(dto)).thenThrow(new MailParseException("Email inválido"));

        mockMvc.perform(post("/enviarEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity()) // Espera status 422
                .andExpect(jsonPath("$.status").value(422)) // Verifica o corpo do erro
                .andExpect(jsonPath("$.mensagem").value("Email inválido"));
    }

    // Teste 3: Testando o GlobalExceptionHandler para MailException (HTTP 500)
    @Test
    void testEnviarEmail_Falha_MailException() throws Exception {
        EmailDto dto = new EmailDto("email@existe.com", "assunto", "mensagem");

        // Mocka o serviço para LANÇAR a exceção
        when(emailService.enviarEmail(dto)).thenThrow(new MailException("Erro de servidor de email") {});

        mockMvc.perform(post("/enviarEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError()) // Espera status 500
                .andExpect(jsonPath("$.status").value(500)) // Verifica o corpo do erro
                .andExpect(jsonPath("$.mensagem").value("Erro de servidor de email"));
    }
}