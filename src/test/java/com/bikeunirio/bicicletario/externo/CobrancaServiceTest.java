package com.bikeunirio.bicicletario.externo;

import com.bikeunirio.bicicletario.externo.dto.CobrancaDto;
import com.bikeunirio.bicicletario.externo.dto.PedidoCobrancaDto;
import com.bikeunirio.bicicletario.externo.entity.Cobranca;
import com.bikeunirio.bicicletario.externo.mapper.CobrancaMapper;
import com.bikeunirio.bicicletario.externo.repository.CobrancaRepository;
import com.bikeunirio.bicicletario.externo.service.CobrancaService;
import com.bikeunirio.bicicletario.externo.service.PaypalAutenticacao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CobrancaServiceTest {

    @Mock
    private CobrancaMapper mapper;

    @Mock
    private List<Cobranca> filaCobranca;

    @Mock
    private PaypalAutenticacao paypalAutenticacao;

    @Mock
    private WebClient webClient;

    // Mocks para a cadeia fluente do WebClient
    @Mock private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock private WebClient.RequestBodySpec requestBodySpec;
    @Mock private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock private WebClient.ResponseSpec responseSpec;

    @Mock
    private CobrancaRepository cobrancaRepository;

    @InjectMocks
    private CobrancaService cobrancaService;

    @Test
    void obterCobranca_Sucesso() {
        // Cenário
        Cobranca cobranca = new Cobranca();
        cobranca.setId(200L);
        cobranca.setValor(200F);
        cobranca.setStatus("COMPLETED");
        cobranca.setHoraSolicitacao(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
        cobranca.setIdCiclista(2);

        when(cobrancaRepository.findById(200L)).thenReturn(Optional.of(cobranca));

        // Execução
        Optional<CobrancaDto> resultado = cobrancaService.obterCobranca(200L);

        // Verificação
        assertTrue(resultado.isPresent());
        assertEquals(200L, resultado.get().getIdCobranca());
        assertEquals(200F, resultado.get().getValorCobranca());
    }

    @Test
    void realizarCobranca_Sucesso() {
        // Cenário
        PedidoCobrancaDto pedido = new PedidoCobrancaDto();
        pedido.setValor(10.50f);
        pedido.setIdCiclista(5L);

        // Mock Autenticação
        when(paypalAutenticacao.getTokenAutenticacao()).thenReturn("TOKEN_FAKE");

        // Mock WebClient (Cadeia completa)
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/v2/checkout/orders")).thenReturn(requestBodySpec);
        when(requestBodySpec.header(eq("Authorization"), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        Map<String, Object> respostaPaypal = Map.of(
                "id", "PAYPAL-ID-123",
                "status", "COMPLETED"
        );

        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(respostaPaypal));

        // --- MOCK DO MAPPER ---
        Cobranca cobrancaSimulada = new Cobranca();
        cobrancaSimulada.setStatus("COMPLETED");
        cobrancaSimulada.setIdApi("PAYPAL-ID-123");
        cobrancaSimulada.setValor(10.50f);

        // Aqui usamos any() genérico para garantir que o mock funcione independente
        // de pequenos problemas de importação do DTO.
        when(mapper.toEntity(any())).thenReturn(cobrancaSimulada);

        // Mock do Repository
        when(cobrancaRepository.save(any(Cobranca.class))).thenReturn(cobrancaSimulada);

        // Execução
        CobrancaDto dto = cobrancaService.realizarCobranca(pedido);

        // Verificação
        assertNotNull(dto);
        assertEquals("COMPLETED", dto.getStatus());
        assertEquals(10.50f, dto.getValorCobranca());

        // Verificamos se o save foi chamado com QUALQUER objeto Cobranca
        verify(cobrancaRepository, times(1)).save(any(Cobranca.class));
    }


}
