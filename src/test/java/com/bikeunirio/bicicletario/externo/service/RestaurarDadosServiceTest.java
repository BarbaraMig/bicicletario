package com.bikeunirio.bicicletario.externo.service;

import com.bikeunirio.bicicletario.externo.dto.RespostaHttpDto;
import com.bikeunirio.bicicletario.externo.entity.Cobranca;
import com.bikeunirio.bicicletario.externo.repository.CobrancaRepository;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestaurarDadosServiceTest {

    private RestaurarDadosService restaurarDadosService;
    private CobrancaRepository cobrancaRepositoryMock;
    private CobrancaService cobrancaServiceMock;

    @BeforeEach
    void setUp() {
        cobrancaRepositoryMock = mock(CobrancaRepository.class);
        cobrancaServiceMock = mock(CobrancaService.class);
        restaurarDadosService = new RestaurarDadosService(cobrancaRepositoryMock, cobrancaServiceMock);
    }

    @Test
    void restaurarBanco_sucesso() {
        //mock
        //retorna o argumento que foi passado
        when(cobrancaRepositoryMock.save(any(Cobranca.class)))
                .then(AdditionalAnswers.returnsFirstArg());

        //execução
        RespostaHttpDto resultado = restaurarDadosService.restaurarBanco();

        //verifica se o deleteAll foi chamado 1 vez
        verify(cobrancaRepositoryMock, times(1)).deleteAll();

        //Captura os objetos salvos para verificação
        ArgumentCaptor<Cobranca> cobrancaCaptor = ArgumentCaptor.forClass(Cobranca.class);

        //verifica se save foi chamado 2 vezes
        verify(cobrancaRepositoryMock, times(2)).save(cobrancaCaptor.capture());

        List<Cobranca> cobrancasSalvas = cobrancaCaptor.getAllValues();

        //valida dos dados
        assertEquals(2, cobrancasSalvas.size());

        Cobranca c1 = cobrancasSalvas.getFirst();
        assertEquals(1, c1.getId());
        assertEquals(10F, c1.getValor());

        Cobranca c2 = cobrancasSalvas.get(1);
        assertEquals(2, c2.getId());
        assertEquals(25.5F, c2.getValor());

        // valida o retorno do metodo
        assertEquals(HttpStatus.SC_OK, resultado.getStatus());
        assertEquals("Banco restaurado", resultado.getMessage());
    }
}