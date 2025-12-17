package com.bikeunirio.bicicletario.externo.service;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.BodyInserter;
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
    private WebClient.RequestHeadersSpec requestHeadersSpec;

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

        mockWebClient(respostaPaypal, null);

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

        mockWebClient(respostaPaypal, null);

        // ------- EXECUÇÃO -------
        String tokenObtido = paypalAutenticacao.getTokenAutenticacao();

        // ------- VERIFICAÇÕES -------
        assertEquals("TOKEN_RENOVADO_999", tokenObtido);
        verify(webClient, times(1)).post();
    }

    @Test
    void getTokenAutenticacao_FalhaApi() {
        mockWebClient(null, new RuntimeException("Erro Paypal"));
        assertThrows(RuntimeException.class,
                () -> paypalAutenticacao.getTokenAutenticacao());
    }

    private void mockWebClient(
            Map<String, Object> respostaSucesso,
            RuntimeException excecao
    ) {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);

        when(requestBodySpec.header(anyString(), anyString()))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any()))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.body(any(BodyInserter.class)))
                .thenReturn(requestHeadersSpec);

        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);

        if (excecao != null) {
            when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                    .thenThrow(excecao);
        } else {
            when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                    .thenReturn(Mono.just(respostaSucesso));
        }
    }


}