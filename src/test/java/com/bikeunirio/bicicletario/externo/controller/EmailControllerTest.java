package com.bikeunirio.bicicletario.externo.controller;

import com.bikeunirio.bicicletario.externo.dto.EmailDto;
import com.bikeunirio.bicicletario.externo.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EmailControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    // 2. Cria um mock do serviço
    @Mock
    private EmailService emailService;
    @InjectMocks
    private EmailController emailController;

    private EmailDto emailDto;
    private final String ENDPOINT = "/enviarEmail";

    @BeforeEach
    void setUp() {
        // Inicializa o MockMvc com o controller injetado (sem a necessidade do @WebMvcTest)
        mockMvc = MockMvcBuilders.standaloneSetup(emailController).build();
        objectMapper = new ObjectMapper(); 
        
        emailDto = new EmailDto(
            "remetente@teste.com", 
            "destinatario@teste.com", 
            "Assunto Teste", 
            "Corpo da mensagem"
        );
    }

    //-------------------------------------------------------------

    @Test
    void enviarEmail_DeveRetornar200EChamarServico() throws Exception {
        // ARRANGE: Configura o mock do serviço.
        // Como o método é void, usamos doNothing().
        doNothing().when(emailService).enviarEmail(any(EmailDto.class));

        // ACT & ASSERT: Executa a requisição simulada e verifica
        mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailDto)))
                
                // 1. Verifica o status HTTP
                .andExpect(status().isOk()) // Espera 200 OK
                
                // 2. Verifica se o JSON retornado (o próprio DTO) está correto
                .andExpect(jsonPath("$.remetente").value(emailDto.getRemetente()))
                .andExpect(jsonPath("$.assunto").value(emailDto.getAssunto()));

        // 3. Verifica se o método do serviço (mock) foi realmente chamado UMA vez
        verify(emailService, times(1)).enviarEmail(any(EmailDto.class));
    }
}
