package com.bikeunirio.bicicletario.externo.controller;

import com.bikeunirio.bicicletario.externo.dto.EmailDto;
import com.bikeunirio.bicicletario.externo.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmailController.class)
class EmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmailService emailService;

    private EmailDto emailDto;
    private final String ENDPOINT = "/enviarEmail";

    @BeforeEach
    void setUp() {
        // CORRIGIDO: Usando o construtor de 3 argumentos: (receptor, assunto, mensagem)
        emailDto = new EmailDto(
            "destinatario@teste.com", // receptor
            "Assunto Teste",          // assunto
            "Corpo da mensagem"       // mensagem
        );
        // O campo 'remetente' que estava no teste anterior foi removido.
    }

    @Test
    void enviarEmail_DeveRetornar200EChamarServico() throws Exception {
        // ARRANGE
        doNothing().when(emailService).enviarEmail(any(EmailDto.class));

        // ACT & ASSERT
        mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailDto)))
                
                // 1. Verifica o status HTTP
                .andExpect(status().isOk()) 
                
                // 2. CORRIGIDO: Usando getReceptor() em vez de getRemetente()
                .andExpect(jsonPath("$.receptor").value(emailDto.getReceptor()))
                .andExpect(jsonPath("$.assunto").value(emailDto.getAssunto()));

        // 3. Verifica se o método do serviço foi chamado
        verify(emailService, times(1)).enviarEmail(any(EmailDto.class));
    }
}
