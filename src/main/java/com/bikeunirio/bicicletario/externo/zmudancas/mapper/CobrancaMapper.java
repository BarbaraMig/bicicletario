package com.bikeunirio.bicicletario.externo.zmudancas.mapper;

import com.bikeunirio.bicicletario.externo.zmudancas.dto.CobrancaDto;
import com.bikeunirio.bicicletario.externo.zmudancas.entity.Cobranca;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CobrancaMapper {

    public CobrancaDto toDTO(Cobranca entity) {
        if (entity == null) return null;
        CobrancaDto cobrancaDto = new CobrancaDto();
        cobrancaDto.setIdCobranca(entity.getId());
        cobrancaDto.setStatus(entity.getStatus());
        cobrancaDto.setHoraSolicitacao(entity.getHoraSolicitacao());
        cobrancaDto.setHoraFinalizacao(entity.getHoraFinalizacao());
        cobrancaDto.setValorCobranca(entity.getValor());
        cobrancaDto.setIdCiclista(entity.getIdCiclista());

        return cobrancaDto;

    }

    public Cobranca toEntity(CobrancaDto dto) {
        if (dto == null) return null;

        Cobranca entity = new Cobranca();
        entity.setId(dto.getIdCobranca());
        entity.setValor(dto.getValorCobranca());
        entity.setIdCiclista(dto.getIdCiclista());
        entity.setStatus(dto.getStatus());
        entity.setHoraSolicitacao(dto.getHoraSolicitacao());
        entity.setHoraFinalizacao(dto.getHoraFinalizacao());

        return entity;
    }
}
