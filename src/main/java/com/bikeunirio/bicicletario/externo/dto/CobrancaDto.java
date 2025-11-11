package com.bikeunirio.bicicletario.externo.dto;

import com.bikeunirio.bicicletario.externo.enums.StatusCobranca;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CobrancaDto {
    private Long id;
    private String idApi;
    private StatusCobranca status;
    private LocalDateTime horaSolicitacao;
    private LocalDateTime horaFinalizacao;
    private BigDecimal valor;
    private long idCiclista;

    public CobrancaDto(){

    }

    public CobrancaDto(long id,
                       String idApi,
                       StatusCobranca status,
                       LocalDateTime horaSolicitacao,
                       LocalDateTime horaFinalizacao,
                       BigDecimal valor,
                       long idCiclista) {
        this.id = id;
        this.idApi = idApi;
        this.status = status;
        this.horaSolicitacao = horaSolicitacao;
        this.horaFinalizacao = horaFinalizacao;
        this.valor = valor;
        this.idCiclista = idCiclista;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdApi() {
        return idApi;
    }

    public void setIdApi(String idApi) {
        this.idApi = idApi;
    }

    public StatusCobranca getStatus() {
        return status;
    }

    public void setStatus(StatusCobranca status) {
        this.status = status;
    }

    public LocalDateTime getHoraSolicitacao() {
        return horaSolicitacao;
    }

    public void setHoraSolicitacao(LocalDateTime horaSolicitacao) {
        this.horaSolicitacao = horaSolicitacao;
    }

    public LocalDateTime getHoraFinalizacao() {
        return horaFinalizacao;
    }

    public void setHoraFinalizacao(LocalDateTime horaFinalizacao) {
        this.horaFinalizacao = horaFinalizacao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public long getIdCiclista() {
        return idCiclista;
    }

    public void setIdCiclista(long idCiclista) {
        this.idCiclista = idCiclista;
    }
}
