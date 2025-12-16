package com.bikeunirio.bicicletario.externo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CobrancaDto {
    private Long idCobranca;

    private String status;
    private LocalDateTime horaSolicitacao;
    private LocalDateTime horaFinalizacao;
    private Float valorCobranca;
    private Long idCiclista;

}
