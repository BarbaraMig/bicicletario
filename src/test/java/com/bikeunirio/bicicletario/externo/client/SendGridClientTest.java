package com.bikeunirio.bicicletario.externo.client;

import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SendGridClientTest {

    private SendGridClient sendGridClient;
    private SendGrid sendGridMock;

    private final String TO_EMAIL = "teste@email.com";
    private final String SUBJECT = "Assunto de Teste";
    private final String BODY = "Corpo do Email de Teste";

    @BeforeEach
    void setUp() {
        // Cria um mock para a dependência SendGrid
        sendGridMock = mock(SendGrid.class);
        // Instancia a classe a ser testada, injetando o mock
        sendGridClient = new SendGridClient(sendGridMock);
    }

    //200
    @Test
    void enviarEmail_sucesso() throws IOException {
        // 1. Configura o mock de SendGrid para retornar uma resposta de sucesso
        Response successResponse = new Response();
        successResponse.setStatusCode(200); // Sucesso

        // Quando sendGridMock.api(qualquer Request) for chamado, retorna a resposta de sucesso
        when(sendGridMock.api(any(Request.class))).thenReturn(successResponse);

        // 2. Chama o metodo a ser testado
        sendGridClient.enviarEmail(TO_EMAIL, SUBJECT, BODY);

        // 3. Verifica se o método api do SendGrid foi chamado exatamente uma vez
        verify(sendGridMock, times(1)).api(any(Request.class));

        // Opcional: Verifica se a Request foi construída corretamente (capturando o argumento)
        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        verify(sendGridMock).api(requestCaptor.capture());

        Request capturedRequest = requestCaptor.getValue();
        assertEquals("mail/send", capturedRequest.getEndpoint());
        assertEquals("POST", capturedRequest.getMethod().name());
        assertNotNull(capturedRequest.getBody());
    }

    //400
    @Test
    void enviarEmail_falhaHttp() throws IOException {
        // 1. Configura o mock de SendGrid para retornar uma resposta de falha
        Response errorResponse = new Response();
        errorResponse.setStatusCode(400); // Ex: Bad Request
        errorResponse.setBody("{\"errors\": [{\"message\": \"Email inválido\"}]}");

        when(sendGridMock.api(any(Request.class))).thenReturn(errorResponse);

        // 2. Verifica se a chamada do método lança a exceção esperada
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> sendGridClient.enviarEmail(TO_EMAIL, SUBJECT, BODY)
        );

        // 3. Verifica a mensagem de exceção
        String expectedMessagePart = "Erro SendGrid | Status: 400 | Body: {\"errors\": [{\"message\": \"Email inválido\"}]}";
        assertTrue(exception.getMessage().contains(expectedMessagePart));

        // Verifica se o método api do SendGrid foi chamado
        verify(sendGridMock, times(1)).api(any(Request.class));
    }

    //lança exceção
    @Test
    void enviarEmail_ioException() throws IOException {
        // 1. Configura o mock para lançar uma IOException
        when(sendGridMock.api(any(Request.class))).thenThrow(new IOException("Erro de rede simulado"));

        // 2. Verifica se lança a exceção de Runtime, que encapsula a IOException
        RuntimeException runtimeException = assertThrows(
                RuntimeException.class,
                () -> sendGridClient.enviarEmail(TO_EMAIL, SUBJECT, BODY)
        );

        // 3. Verifica a mensagem da exceção e a causa
        assertEquals("Erro ao chamar API do SendGrid", runtimeException.getMessage());
        assertTrue(runtimeException.getCause() instanceof IOException);

        // Verifica se o metodo api do SendGrid foi chamado
        verify(sendGridMock, times(1)).api(any(Request.class));
    }
}