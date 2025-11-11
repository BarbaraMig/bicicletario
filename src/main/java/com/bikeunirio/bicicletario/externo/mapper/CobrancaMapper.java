package com.bikeunirio.bicicletario.externo.mapper;

import com.bikeunirio.bicicletario.externo.dto.CobrancaDto;
import com.bikeunirio.bicicletario.externo.entity.Cobranca;
import org.springframework.stereotype.Component;

@Component
public class CobrancaMapper {
    public CobrancaDto cobrancaParaDto(Cobranca cobrancaEntidade){
        if(cobrancaEntidade  == null)
            return null;
        return new CobrancaDto(
                cobrancaEntidade.getId(),
                cobrancaEntidade.getIdApi(),
                cobrancaEntidade.getStatus(),
                cobrancaEntidade.getHoraSolicitacao(),
                cobrancaEntidade.getHoraFinalizacao(),
                cobrancaEntidade.getValor(),
                cobrancaEntidade.getIdCiclista());
    }

    public Cobranca dtoParaCobranca(CobrancaDto dto){
        if(dto == null)
            return null;
        Cobranca cobrancaEntidade = new Cobranca();
        cobrancaEntidade.setId(dto.getId());
        cobrancaEntidade.setIdApi(dto.getIdApi());
        cobrancaEntidade.setStatus(dto.getStatus());
        cobrancaEntidade.setHoraSolicitacao(dto.getHoraSolicitacao());
        cobrancaEntidade.setHoraFinalizacao(dto.getHoraFinalizacao());
        cobrancaEntidade.setValor(dto.getValor());
        cobrancaEntidade.setIdCiclista(dto.getIdCiclista());

        return cobrancaEntidade;
    }
}
