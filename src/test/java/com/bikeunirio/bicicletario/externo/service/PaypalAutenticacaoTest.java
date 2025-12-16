package com.bikeunirio.bicicletario.externo.service;

import com.bikeunirio.bicicletario.externo.dto.PedidoCobrancaDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaypalAutenticacaoTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec; // Removido o <?> para facilitar o Mock

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private PaypalAutenticacao paypalAutenticacao;

    @Test
    @DisplayName("Deve gerar um novo token quando não houver cache")
    void getTokenAutenticacao_GerarNovo_Sucesso() {
        // ------- CENÁRIO -------
        Map<String, Object> respostaPaypal = Map.of(
                "access_token", "TOKEN_NOVO_123",
                "expires_in", 3600
        );

        // Configuração da cadeia de mocks do WebClient
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/v1/oauth2/token")).thenReturn(requestBodySpec);
        when(requestBodySpec.header(eq(HttpHeaders.AUTHORIZATION), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_FORM_URLENCODED)).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue("grant_type=client_credentials")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(respostaPaypal));

        // ------- EXECUÇÃO -------
        String tokenObtido = paypalAutenticacao.getTokenAutenticacao();

        // ------- VERIFICAÇÕES -------
        assertEquals("TOKEN_NOVO_123", tokenObtido);

        // Verifica se a API foi chamada 1 vez
        verify(webClient, times(1)).post();
    }

    @Test
    void getTokenAutenticacao_ExistenteValido_Sucesso() {
        // ------- CENÁRIO -------
        // Simulamos que já existe um token válido setando os campos privados via Reflection
        ReflectionTestUtils.setField(paypalAutenticacao, "tokenAuth", "TOKEN_CACHEADO_ABC");
        // Expira daqui a 1 hora (está válido)
        ReflectionTestUtils.setField(paypalAutenticacao, "expiracao", Instant.now().plusSeconds(3600));

        // ------- EXECUÇÃO -------
        String tokenObtido = paypalAutenticacao.getTokenAutenticacao();

        // ------- VERIFICAÇÕES -------
        assertEquals("TOKEN_CACHEADO_ABC", tokenObtido);

        // O MAIS IMPORTANTE: Verifica se o webClient NUNCA foi chamado (pois usou cache)
        verify(webClient, times(0)).post();
    }

    @Test
    void getTokenAutenticacao_Gerar_Sucesso() {
        // ------- CENÁRIO -------
        // Token existe, mas expirou há 1 hora
        ReflectionTestUtils.setField(paypalAutenticacao, "tokenAuth", "TOKEN_ANTIGO");
        ReflectionTestUtils.setField(paypalAutenticacao, "expiracao", Instant.now().minusSeconds(3600));

        // Configura mocks para nova chamada
        Map<String, Object> respostaPaypal = Map.of(
                "access_token", "TOKEN_RENOVADO_999",
                "expires_in", 3600
        );

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(any(), any())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(respostaPaypal));

        // ------- EXECUÇÃO -------
        String tokenObtido = paypalAutenticacao.getTokenAutenticacao();

        // ------- VERIFICAÇÕES -------
        assertEquals("TOKEN_RENOVADO_999", tokenObtido);
        verify(webClient, times(1)).post();
    }

    @Test
    void getTokenAutenticacao_FalhaApi() {
        //config do webclient
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(any(), any())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        // erro
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenThrow(new RuntimeException("Erro de conexão com PayPal"));

        //executa e valida
        assertThrows(RuntimeException.class, () -> {
            paypalAutenticacao.getTokenAutenticacao();
        });
    }
}