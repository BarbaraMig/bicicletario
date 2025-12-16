package com.bikeunirio.bicicletario.externo.controller;

import com.bikeunirio.bicicletario.externo.dto.EmailDto;
import com.bikeunirio.bicicletario.externo.exceptions.GlobalExceptionHandler;
import com.bikeunirio.bicicletario.externo.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EmailControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private EmailService emailService;

    @BeforeEach
    void setup() {
        EmailController controller = new EmailController(emailService);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ===============================
    // Teste 1 — Sucesso (200 OK)
    // ===============================
    @Test
    void testEnviarEmail_Sucesso() throws Exception {
        EmailDto dto = new EmailDto(
                "emailexternoes2@gmail.com",
                "Assunto teste",
                "Mensagem teste"
        );

        // service agora é void
        doNothing().when(emailService).enviarEmail(any(EmailDto.class));

        mockMvc.perform(post("/enviarEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.receptor").value(dto.getReceptor()))
                .andExpect(jsonPath("$.assunto").value(dto.getAssunto()))
                .andExpect(jsonPath("$.mensagem").value(dto.getMensagem()));
    }

    // ======================================
    // Teste 2 — MailParseException (422)
    // ======================================
    @Test
    void testEnviarEmail_Falha_MailParseException() throws Exception {
        EmailDto dto = new EmailDto(
                "email_invalido",
                "Assunto",
                "Mensagem"
        );

        doThrow(new MailParseException("Email inválido"))
                .when(emailService).enviarEmail(any(EmailDto.class));

        mockMvc.perform(post("/enviarEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("Email inválido"));
    }

    // ======================================
    // Teste 3 — MailException (500)
    // ======================================
    @Test
    void testEnviarEmail_Falha_MailException() throws Exception {
        EmailDto dto = new EmailDto(
                "email@existe.com",
                "Assunto",
                "Mensagem"
        );

        doThrow(new MailException("Erro de servidor de email") {})
                .when(emailService).enviarEmail(any(EmailDto.class));

        mockMvc.perform(post("/enviarEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Erro de servidor de email"));
    }
}
