package com.bikeunirio.bicicletario.externo.entity;

import com.bikeunirio.bicicletario.externo.enums.StatusCobranca;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="cobrancas")
public class Cobranca{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String idApi;

    private StatusCobranca status;


    @Column(columnDefinition = "DATETIME")
    private LocalDateTime horaSolicitacao;


    @Column(columnDefinition = "DATETIME")
    private LocalDateTime horaFinalizacao;

    private BigDecimal valor;

    private long idCiclista;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
