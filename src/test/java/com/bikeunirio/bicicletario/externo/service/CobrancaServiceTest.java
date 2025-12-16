package com.bikeunirio.bicicletario.externo.service;

import com.bikeunirio.bicicletario.externo.dto.CartaoDto;
import com.bikeunirio.bicicletario.externo.dto.CobrancaDto;
import com.bikeunirio.bicicletario.externo.dto.PedidoCobrancaDto;
import com.bikeunirio.bicicletario.externo.dto.RespostaHttpDto;
import com.bikeunirio.bicicletario.externo.entity.Cobranca;
import com.bikeunirio.bicicletario.externo.enums.CobrancaEnum;
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

    // mocks do WebClient
    @Mock private WebClient webClient;
    @Mock private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock private WebClient.RequestBodySpec requestBodySpec;
    @Mock private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock private WebClient.ResponseSpec responseSpec;

    private List<Cobranca> filaCobranca;
    private CobrancaService cobrancaService;

    @BeforeEach
    void setup() {
        filaCobranca = new ArrayList<>();

        cobrancaService = new CobrancaService(
                paypalAutenticacao,
                webClient,
                cobrancaRepository,
                filaCobranca,
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

        assertEquals(1, filaCobranca.size());
        //pendente significa que deve estar na fila
        assertEquals(String.valueOf(CobrancaEnum.PENDENTE), resultado.getStatus());
    }

    @Test
    void validarCartaoCredito_Sucesso() {
        CartaoDto cartao = new CartaoDto();
        RespostaHttpDto resposta = cobrancaService.validarCartaoCredito(cartao);
        assertNotNull(resposta);
        assertEquals(200, resposta.getStatus());
    }

    @Test
    void realizarCobranca_Sucesso() {
        PedidoCobrancaDto pedido = new PedidoCobrancaDto();
        pedido.setValor(10.50f);

        // configura o Mock do WebClient para retornar sucesso
        prepararMocksWebClientSuccess();
        when(paypalAutenticacao.getTokenAutenticacao(pedido)).thenReturn("TOKEN_FAKE");

        Cobranca cobrancaSalva = new Cobranca();
        cobrancaSalva.setStatus(String.valueOf(CobrancaEnum.PAGA));

        when(cobrancaRepository.save(any())).thenReturn(cobrancaSalva);
        when(mapper.toDTO(any(Cobranca.class))).thenReturn(new CobrancaDto() {{ setStatus("COMPLETED"); }});

        CobrancaDto resultado = cobrancaService.realizarCobranca(pedido);

        assertEquals(String.valueOf(CobrancaEnum.PAGA), resultado.getStatus());

        // verifica se o save foi chamado
        verify(cobrancaRepository, times(1)).save(any());
        // verifica se o WebClient foi realmente invocado
        verify(webClient, times(1)).post();
    }

    @Test
    void processaCobrancasEmFila_Sucesso() {
        //criações necesssárias
        Cobranca itemFila = new Cobranca();
        itemFila.setValor(100f);
        itemFila.setIdCiclista(10L);
        filaCobranca.add(itemFila);

        CobrancaDto cobrancaDto = new CobrancaDto();
        cobrancaDto.setValorCobranca(15F);
        cobrancaDto.setIdCiclista(10L);

        prepararMocksWebClientSuccess();
        when(paypalAutenticacao.getTokenAutenticacao(any(PedidoCobrancaDto.class))).thenReturn("TOKEN_FAKE");

        Cobranca cobrancaSalva = new Cobranca();
        cobrancaSalva.setStatus(String.valueOf(CobrancaEnum.PAGA));

        when(cobrancaService.realizarCobranca(any(PedidoCobrancaDto.class))).thenReturn(cobrancaDto);

        //execução
        List<CobrancaDto> resultados = cobrancaService.processaCobrancasEmFila();

        //verificação
        assertEquals(0, filaCobranca.size(), "O item deveria ter sido removido da fila real");
        assertEquals(1, resultados.size());

        // Garante que o WebClient foi chamado 1 vez através do metodo interno
        verify(webClient, times(1)).post();
    }

    @Test
    void obterCobranca_Sucesso() {
        //cria e inicia cobrança
        Cobranca cobranca = new Cobranca();
        cobranca.setId(1L);
        cobranca.setValor(200F);

        //quando cobrança de 1L for procurado no repository, retorne um Optional da cobrança
        when(cobrancaRepository.findById(1L)).thenReturn(Optional.of(cobranca));
        //chamada da função em si
        Optional<CobrancaDto> resultado = cobrancaService.obterCobranca(1L);
        //conferir se há algum retorno no resultado
        assertTrue(resultado.isPresent());
    }

    private void prepararMocksWebClientSuccess() {
        // encadeamento do WebClient Mockado
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