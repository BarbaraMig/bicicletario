package com.bikeunirio.bicicletario.externo.controller;

import com.bikeunirio.bicicletario.externo.dto.RespostaHttpDto;
import com.bikeunirio.bicicletario.externo.service.RestaurarDadosService;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestaurarDadosControllerTest {

    private RestaurarDadosController restaurarDadosController;
    private RestaurarDadosService restaurarDadosServiceMock;

    @BeforeEach
    void setUp() {
        // Cria o mock para a dependência Service
        restaurarDadosServiceMock = mock(RestaurarDadosService.class);
        // Instancia o Controller injetando o mock
        restaurarDadosController = new RestaurarDadosController(restaurarDadosServiceMock);
    }

    /**
     * Testa se o método do controller chama o serviço e retorna a ResponseEntity correta
     */
    @Test
    void restaurarBanco_sucesso() {
        //criações necessárias
        //retorno mockado
        RespostaHttpDto dtoSimulado = new RespostaHttpDto(HttpStatus.SC_OK, "Banco restaurado");

        //config mock
        when(restaurarDadosServiceMock.restaurarBanco()).thenReturn(dtoSimulado);

        //execução
        ResponseEntity<RespostaHttpDto> responseEntity = restaurarDadosController.restaurarBanco();

        //metodo foi chamado exatamente uma vez?
        verify(restaurarDadosServiceMock, times(1)).restaurarBanco();

        //verifica retorno != null
        assertNotNull(responseEntity);
        // verifica status == 200
        assertEquals(org.springframework.http.HttpStatus.OK, responseEntity.getStatusCode());
        //verifica se o corpo == "retornoService"
        assertEquals(dtoSimulado, responseEntity.getBody());
        assertEquals("Banco restaurado", Objects.requireNonNull(responseEntity.getBody()).getMessage());
    }
}