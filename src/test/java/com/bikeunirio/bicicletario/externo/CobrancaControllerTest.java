package com.bikeunirio.bicicletario.externo;

import com.bikeunirio.bicicletario.externo.controller.CobrancaController;
import com.bikeunirio.bicicletario.externo.dto.CartaoDto;
import com.bikeunirio.bicicletario.externo.dto.CobrancaDto;
import com.bikeunirio.bicicletario.externo.dto.PedidoCobrancaDto;
import com.bikeunirio.bicicletario.externo.dto.RespostaErroDto;
import com.bikeunirio.bicicletario.externo.mapper.CobrancaMapper;
import com.bikeunirio.bicicletario.externo.service.CobrancaService;
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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CobrancaControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CobrancaService cobrancaService;

    @Mock
    private CobrancaMapper cobrancaMapper;

    @InjectMocks
    private CobrancaController cobrancaController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(cobrancaController)
                .build();
    }

    @Test
    void testRecuperarCobranca_Sucesso() throws Exception {
        //inicialização do dto que seria exibido
        CobrancaDto dto = new CobrancaDto();
        dto.setIdCobranca(1L);
        dto.setStatus("PAGA");
        dto.setHoraSolicitacao(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
        dto.setHoraFinalizacao(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
        dto.setValorCobranca(100.1F);
        dto.setIdCiclista(10L);

        when(cobrancaService.obterCobranca(1L)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/cobranca/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCobranca").value(1))
                .andExpect(jsonPath("$.status").value("PAGA"));
    }

    @Test
    void testRecuperarCobranca_NaoEncontrado() throws Exception {
        when(cobrancaService.obterCobranca(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/cobranca/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRealizarCobranca() throws Exception {
        PedidoCobrancaDto pedido = new PedidoCobrancaDto(); // Preencha campos se necessário
        CobrancaDto retorno = new CobrancaDto();
        retorno.setStatus("PENDENTE");

        // Assumindo que você corrigiu o Controller para retornar o resultado do service
        when(cobrancaService.realizarCobranca(any(PedidoCobrancaDto.class))).thenReturn(retorno);

        mockMvc.perform(post("/cobranca")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedido)))
                // Nota: Seu código atual retorna null, então isso daria erro.
                // Ajustei o teste esperando que o controller retorne OK.
                .andExpect(status().isOk());
    }

    @Test
    void testValidarCartaoCredito_Sucesso() throws Exception {
        CartaoDto cartao = new CartaoDto();
        RespostaErroDto resposta = new RespostaErroDto();
        resposta.setStatus(200);

        when(cobrancaService.validarCartaoCredito(any(CartaoDto.class))).thenReturn(resposta);

        mockMvc.perform(post("/validarCartaoCredito")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartao)))
                .andExpect(status().isOk());
    }
}