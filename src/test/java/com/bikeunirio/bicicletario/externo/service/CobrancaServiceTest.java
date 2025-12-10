package com.bikeunirio.bicicletario.externo.service;

import com.bikeunirio.bicicletario.externo.dto.CartaoDto;
import com.bikeunirio.bicicletario.externo.dto.CobrancaDto;
import com.bikeunirio.bicicletario.externo.dto.PedidoCobrancaDto;
import com.bikeunirio.bicicletario.externo.dto.RespostaErroDto;
import com.bikeunirio.bicicletario.externo.entity.Cobranca;
import com.bikeunirio.bicicletario.externo.mapper.CobrancaMapper;
import com.bikeunirio.bicicletario.externo.repository.CobrancaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CobrancaServiceTest {

    @Mock private CobrancaMapper mapper;
    @Mock private PaypalAutenticacao paypalAutenticacao;
    @Mock private CobrancaRepository cobrancaRepository;

    // Mocks do WebClient para simular a chamada sem ir à rede
    @Mock private WebClient webClient;
    @Mock private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock private WebClient.RequestBodySpec requestBodySpec;
    @Mock private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock private WebClient.ResponseSpec responseSpec;

    private List<Cobranca> filaCobrancaReal;
    private CobrancaService cobrancaService;

    @BeforeEach
    void setup() {
        filaCobrancaReal = new ArrayList<>();

        cobrancaService = new CobrancaService(
                paypalAutenticacao,
                webClient,
                cobrancaRepository,
                filaCobrancaReal,
                mapper
        );
    }

    @Test
    void incluirCobrancaNaFila_Sucesso() {
        PedidoCobrancaDto pedido = new PedidoCobrancaDto();
        pedido.setValor(50.0f);
        pedido.setIdCiclista(3L);

        CobrancaDto dtoEsperado = new CobrancaDto();
        dtoEsperado.setStatus("NA_FILA");

        when(mapper.toDTO(any(Cobranca.class))).thenReturn(dtoEsperado);

        CobrancaDto resultado = cobrancaService.incluirCobrancaNaFila(pedido);

        assertEquals(1, filaCobrancaReal.size());
        assertEquals("NA_FILA", resultado.getStatus());
    }

    @Test
    void validarCartaoCredito_Sucesso() {
        CartaoDto cartao = new CartaoDto();
        RespostaErroDto resposta = cobrancaService.validarCartaoCredito(cartao);
        assertNotNull(resposta);
        assertEquals(200, resposta.getStatus());
    }

    @Test
    void realizarCobranca_Sucesso() {
        PedidoCobrancaDto pedido = new PedidoCobrancaDto();
        pedido.setValor(10.50f);

        // Configura o Mock do WebClient para retornar sucesso
        prepararMocksWebClientSuccess();
        when(paypalAutenticacao.getTokenAutenticacao()).thenReturn("TOKEN_FAKE");

        Cobranca cobrancaSalva = new Cobranca();
        cobrancaSalva.setStatus("COMPLETED");

        when(cobrancaRepository.save(any())).thenReturn(cobrancaSalva);
        when(mapper.toDTO(any(Cobranca.class))).thenReturn(new CobrancaDto() {{ setStatus("COMPLETED"); }});

        CobrancaDto resultado = cobrancaService.realizarCobranca(pedido);

        assertEquals("COMPLETED", resultado.getStatus());

        // Verifica se o save foi chamado
        verify(cobrancaRepository, times(1)).save(any());
        // Verifica se o WebClient foi realmente invocado (o mock captura a chamada)
        verify(webClient, times(1)).post();
    }

    @Test
    void processaCobrancasEmFila_Sucesso() {
        // 1. Setup
        Cobranca itemFila = new Cobranca();
        itemFila.setValor(100f);
        itemFila.setIdCiclista(10L);
        filaCobrancaReal.add(itemFila);

        // 2. Mocks
        // Como 'processaCobrancasEmFila' chama 'realizarCobranca' internamente,
        // precisamos garantir que o Mock do WebClient esteja pronto para responder.
        prepararMocksWebClientSuccess();
        when(paypalAutenticacao.getTokenAutenticacao()).thenReturn("TOKEN_FAKE");

        Cobranca cobrancaSalva = new Cobranca();
        cobrancaSalva.setStatus("COMPLETED");

        // Usamos lenient() apenas nos stubs auxiliares que não são o foco principal do teste de concorrência/webclient
        lenient().when(mapper.toEntity(any())).thenReturn(cobrancaSalva);
        lenient().when(cobrancaRepository.save(any())).thenReturn(cobrancaSalva);
        lenient().when(mapper.toDTO(any(Cobranca.class))).thenReturn(new CobrancaDto());

        // 3. Execução
        List<CobrancaDto> resultados = cobrancaService.processaCobrancasEmFila();

        // 4. Verificações
        assertEquals(0, filaCobrancaReal.size(), "O item deveria ter sido removido da fila real");
        assertEquals(1, resultados.size());

        // Garante que o WebClient foi chamado 1 vez através do método interno
        verify(webClient, times(1)).post();
    }

    @Test
    void obterCobranca_Sucesso() {
        Cobranca cobranca = new Cobranca();
        cobranca.setId(1L);
        cobranca.setValor(200F);

        when(cobrancaRepository.findById(1L)).thenReturn(Optional.of(cobranca));
        Optional<CobrancaDto> resultado = cobrancaService.obterCobranca(1L);
        assertTrue(resultado.isPresent());
    }

    private void prepararMocksWebClientSuccess() {
        // Encadeamento do WebClient Mockado
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        Map<String, Object> respostaPaypal = Map.of("id", "123", "status", "COMPLETED");
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(respostaPaypal));
    }
}