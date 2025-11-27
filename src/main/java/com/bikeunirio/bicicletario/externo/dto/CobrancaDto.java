package com.bikeunirio.bicicletario.externo.dto;

import java.time.LocalDateTime;

public class CobrancaDto {
    private Long idCobranca;
    private String idApi;

    public String getIdApi() {
        return idApi;
    }

    public void setIdApi(String idApi) {
        this.idApi = idApi;
    }

    private String status;
    private LocalDateTime horaSolicitacao;
    private LocalDateTime horaFinalizacao;
    private Float valorCobranca;
    private Long idCiclista;

    public Long getIdCobranca() {
        return idCobranca;
    }

    public void setIdCobranca(Long idCobranca) {
        this.idCobranca = idCobranca;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    public Float getValorCobranca() {
        return valorCobranca;
    }

    public void setValorCobranca(Float valorCobranca) {
        this.valorCobranca = valorCobranca;
    }

    public Long getIdCiclista() {
        return idCiclista;
    }

    public void setIdCiclista(Long idCiclista) {
        this.idCiclista = idCiclista;
    }
}
