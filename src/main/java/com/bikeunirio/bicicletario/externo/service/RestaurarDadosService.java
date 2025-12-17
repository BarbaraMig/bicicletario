package com.bikeunirio.bicicletario.externo.service;

import com.bikeunirio.bicicletario.externo.dto.RespostaHttpDto;
import com.bikeunirio.bicicletario.externo.entity.Cobranca;
import com.bikeunirio.bicicletario.externo.enums.CobrancaEnum;
import com.bikeunirio.bicicletario.externo.repository.CobrancaRepository;
import jakarta.transaction.Transactional;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class RestaurarDadosService {
    private CobrancaRepository cobrancaRepository;
    CobrancaService cobrancaService;
    RestaurarDadosService(CobrancaRepository cobrancaRepository, CobrancaService cobrancaService){
        this.cobrancaRepository=cobrancaRepository;
        this.cobrancaService=cobrancaService;
    }

    @Transactional
    public RespostaHttpDto restaurarBanco() {
        //limpa o banco de dados
        cobrancaRepository.deleteAll();

        //recriar os dados originais
        Cobranca cobranca1 = new Cobranca();
        cobranca1.setId(1);
        cobranca1.setStatus(String.valueOf(CobrancaEnum.PENDENTE));
        cobranca1.setHoraSolicitacao(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
        cobranca1.setValor(10F);
        cobranca1.setIdCiclista(3L);
        cobrancaRepository.save(cobranca1);

        Cobranca cobranca2 = new Cobranca();
        cobranca2.setId(2);
        cobranca2.setStatus(String.valueOf(CobrancaEnum.FALHA));
        cobranca2.setHoraSolicitacao(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
        cobranca2.setValor(25.5F);
        cobranca2.setIdCiclista(4L);
        cobrancaRepository.save(cobranca2);

        return new RespostaHttpDto(HttpStatus.SC_OK,"Banco restaurado");
    }
}
