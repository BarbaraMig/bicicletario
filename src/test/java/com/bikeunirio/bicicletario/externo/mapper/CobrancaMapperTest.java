package com.bikeunirio.bicicletario.externo.mapper;

import com.bikeunirio.bicicletario.externo.dto.CobrancaDto;
import com.bikeunirio.bicicletario.externo.entity.Cobranca;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

//Não precisa do mockito., portanto sem @ExtendWith
class CobrancaMapperTest {

    // Instanciamos o mapper diretamente pois ele não tem dependências
    private final CobrancaMapper mapper = new CobrancaMapper();

    @Test
    void deveConverterEntityParaDtoComSucesso() {
        //setup
        Cobranca entity = new Cobranca();
        entity.setId(1L);
        entity.setValor(50.0F);
        entity.setIdCiclista(10L);
        entity.setStatus("PENDENTE");
        entity.setHoraSolicitacao(LocalDateTime.now());
        entity.setHoraFinalizacao(LocalDateTime.now().plusHours(1));

        //mocks não são necessários
        //execução
        CobrancaDto resultado = mapper.toDTO(entity);

        //verificações
        assertNotNull(resultado);
        assertEquals(entity.getId(), resultado.getIdCobranca());
        assertEquals(entity.getValor(), resultado.getValorCobranca());
        assertEquals(entity.getIdCiclista(), resultado.getIdCiclista());
        assertEquals(entity.getStatus(), resultado.getStatus());
        assertEquals(entity.getHoraSolicitacao(), resultado.getHoraSolicitacao());
        assertEquals(entity.getHoraFinalizacao(), resultado.getHoraFinalizacao());
    }

    @Test
    void deveRetornarNullQuandoEntityForNull() {
        CobrancaDto resultado = mapper.toDTO(null);
        assertNull(resultado);
    }

    @Test
    void deveConverterDtoParaEntityComSucesso() {
        //setup
        CobrancaDto dto = new CobrancaDto();
        dto.setIdCobranca(2L);
        dto.setValorCobranca(75.50F);
        dto.setIdCiclista(20L);
        dto.setStatus("PAGO");
        dto.setHoraSolicitacao(LocalDateTime.now().minusHours(2));
        dto.setHoraFinalizacao(LocalDateTime.now().minusHours(1));
        //mocks não são necessários
        //execução
        Cobranca resultado = mapper.toEntity(dto);

        //verificações
        assertNotNull(resultado);
        assertEquals(dto.getIdCobranca(), resultado.getId());
        assertEquals(dto.getValorCobranca(), resultado.getValor());
        assertEquals(dto.getIdCiclista(), resultado.getIdCiclista());
        assertEquals(dto.getStatus(), resultado.getStatus());
        assertEquals(dto.getHoraSolicitacao(), resultado.getHoraSolicitacao());
        assertEquals(dto.getHoraFinalizacao(), resultado.getHoraFinalizacao());
    }

    @Test
    void deveRetornarNullQuandoDtoForNull() {
        Cobranca resultado = mapper.toEntity(null);
        assertNull(resultado);
    }
}