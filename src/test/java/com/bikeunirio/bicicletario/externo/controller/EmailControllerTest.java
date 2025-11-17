package com.bikeunirio.bicicletario.externo.controller;

import com.bikeunirio.bicicletario.externo.dto.EmailDto;
import com.bikeunirio.bicicletario.externo.exceptions.GlobalExceptionHandler;
import com.bikeunirio.bicicletario.externo.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Define o teste como um teste da camada Web, focado no EmailController
@WebMvcTest(EmailController.class)
@Import(GlobalExceptionHandler.class) // IMPORTANTE: Traz seu handler para o contexto do teste
class EmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired // 4. O Spring agora injeta o mock do nosso metodo @Bean
    private EmailService emailService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public EmailService emailService() {
            // 3. Criar e retornar o mock manualmente
            return Mockito.mock(EmailService.class);
        }
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
                        .content(objectMapper.writeValueAsString(dto))) // Converte o DTO em JSON
                .andExpect(status().isOk()) // Espera status 200
                .andExpect(jsonPath("$.receptor").value(dto.getReceptor())) // Verifica o JSON de resposta
                .andExpect(jsonPath("$.assunto").value(dto.getAssunto()));
    }

    // Teste 2: Testando o GlobalExceptionHandler para MailParseException (HTTP 422)
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

    // Teste 3: Testando o GlobalExceptionHandler para MailException (HTTP 500)
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