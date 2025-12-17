package com.bikeunirio.bicicletario.externo.service;

import com.bikeunirio.bicicletario.externo.dto.RespostaHttpDto;
import com.bikeunirio.bicicletario.externo.entity.Cobranca;
import com.bikeunirio.bicicletario.externo.repository.CobrancaRepository;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestaurarDadosServiceTest {

    private RestaurarDadosService restaurarDadosService;
    private CobrancaRepository cobrancaRepositoryMock;
    private CobrancaService cobrancaServiceMock; // Mockado, mesmo que não usado no método testado

    @BeforeEach
    void setUp() {
        // Cria os mocks para as dependências
        cobrancaRepositoryMock = mock(CobrancaRepository.class);
        cobrancaServiceMock = mock(CobrancaService.class);
        // Instancia a classe a ser testada
        restaurarDadosService = new RestaurarDadosService(cobrancaRepositoryMock, cobrancaServiceMock);
    }

    /**
     * Testa o método restaurarBanco, verificando se as chamadas ao repositório estão corretas
     */
    @Test
    void restaurarBanco_sucesso() {

        //mocks
        doNothing().when(cobrancaRepositoryMock).deleteAll();
        when(cobrancaRepositoryMock.save(any(Cobranca.class))).thenReturn(any(Cobranca.class)); // O save retorna a entidade persistida

        //execução
        RespostaHttpDto resultado = restaurarDadosService.restaurarBanco();


        //deleteAll foi chamado exatamente uma vez
        verify(cobrancaRepositoryMock, times(1)).deleteAll();

        //save foi chamado exatamente duas vezes (cobranca1 e cobranca2)
        verify(cobrancaRepositoryMock, times(2)).save(any(Cobranca.class));

        //verifica se dados salvos estão corretos
        ArgumentCaptor<Cobranca> cobrancaCaptor = ArgumentCaptor.forClass(Cobranca.class);
        verify(cobrancaRepositoryMock, times(2)).save(cobrancaCaptor.capture());

        List<Cobranca> cobrancasCapturadas = cobrancaCaptor.getAllValues();

        //verifica primeira cobrança
        Cobranca cobranca1 = cobrancasCapturadas.getFirst();
        assertEquals(1, cobranca1.getId());
        assertEquals(10F, cobranca1.getValor());
        assertEquals(3L, cobranca1.getIdCiclista());

        //verifica segunda cobrança
        Cobranca cobranca2 = cobrancasCapturadas.get(1);
        assertEquals(2, cobranca2.getId());
        assertEquals(25.5F, cobranca2.getValor());
        assertEquals(4L, cobranca2.getIdCiclista());

        //verifica resultado retornado
        assertNotNull(resultado);
        assertEquals(HttpStatus.SC_OK, resultado.getStatus());
        assertEquals("Banco restaurado", resultado.getMessage());
    }
}