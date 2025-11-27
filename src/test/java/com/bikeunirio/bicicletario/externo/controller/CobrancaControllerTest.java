package com.bikeunirio.bicicletario.externo.controller;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
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
    void testRealizarCobranca_Sucesso() throws Exception {
        PedidoCobrancaDto pedido = new PedidoCobrancaDto();
        pedido.setValor(50.0F);
        pedido.setIdCiclista(2L);

        CobrancaDto retorno = new CobrancaDto();
        retorno.setIdCobranca(10L);
        retorno.setStatus("PENDENTE");

        when(cobrancaService.realizarCobranca(any(PedidoCobrancaDto.class))).thenReturn(retorno);
        //"faz" a requisição
        mockMvc.perform(post("/cobranca")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedido)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCobranca").value(10))
                .andExpect(jsonPath("$.status").value("PENDENTE"));
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
    @Test
    void validarCartaoCredito_Sucesso() throws Exception {
        // Cenário
        CartaoDto cartao = new CartaoDto();
        RespostaErroDto resposta = new RespostaErroDto();
        resposta.setStatus(200);
        // Assumindo que o toString retorna algo, ou o RespostaErroDto tem um toString padrão

        when(cobrancaService.validarCartaoCredito(any(CartaoDto.class))).thenReturn(resposta);

        // Execução e Validação
        mockMvc.perform(post("/validarCartaoCredito")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartao)))
                .andExpect(status().isOk())
                // Verifica se o corpo da resposta contem a string do objeto (conforme lógica do controller)
                .andExpect(content().string(resposta.toString()));
    }

    @Test
    void validarCartaoCredito_Falha() throws Exception {
        // Cenário
        CartaoDto cartao = new CartaoDto();
        RespostaErroDto resposta = new RespostaErroDto();
        resposta.setStatus(422); // Status de erro

        when(cobrancaService.validarCartaoCredito(any(CartaoDto.class))).thenReturn(resposta);

        // Execução e Validação
        mockMvc.perform(post("/validarCartaoCredito")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartao)))
                .andExpect(status().isOk()) // O controller retorna 200 mesmo no "else"
                .andExpect(content().string("validacao"));
    }

    // --- TESTES DE FILA DE COBRANÇA ---

    @Test
    void incluirCobrancaNaFila_Sucesso() throws Exception {
        // Cenário
        PedidoCobrancaDto pedido = new PedidoCobrancaDto();

        // Simula o retorno null que está atualmente na sua implementação do Service
        when(cobrancaService.incluirCobrancaNaFila(any(PedidoCobrancaDto.class))).thenReturn(null);

        // Execução e Validação
        mockMvc.perform(post("/filaCobranca")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedido)))
                .andExpect(status().isOk());
        // Não validamos o body JSON pois retorna null/vazio neste caso
    }

    @Test
    void processaCobrancasEmFila_Sucesso() throws Exception {
        // Cenário
        CobrancaDto c1 = new CobrancaDto();
        c1.setIdCobranca(1L);
        CobrancaDto c2 = new CobrancaDto();
        c2.setIdCobranca(2L);

        List<CobrancaDto> lista = Arrays.asList(c1, c2);

        when(cobrancaService.processaCobrancasEmFila()).thenReturn(lista);

        // Execução e Validação
        mockMvc.perform(post("/processaCobrancasEmFila")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2))) // Verifica se retornou 2 itens
                .andExpect(jsonPath("$[0].idCobranca").value(1))
                .andExpect(jsonPath("$[1].idCobranca").value(2));
    }

    @Test
    void processaCobrancasEmFila_Vazia() throws Exception {
        // Cenário
        when(cobrancaService.processaCobrancasEmFila()).thenReturn(Collections.emptyList());

        // Execução e Validação
        mockMvc.perform(post("/processaCobrancasEmFila")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}