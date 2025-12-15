package com.bikeunirio.bicicletario.externo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name="cobrancas")
@Data
public class Cobranca{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String idApi;

    private String status;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime horaSolicitacao;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime horaFinalizacao;

    private Float valor;

    private long idCiclista;

}
