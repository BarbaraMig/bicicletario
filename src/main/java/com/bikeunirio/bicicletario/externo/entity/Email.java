package com.bikeunirio.bicicletario.externo.entity;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name="logs_email")
public class Email{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String receptor;

    private String assunto;

    private String mensagem;

    @GeneratedValue
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime horaEnvio;

    public Email(){

    }

    public Email(long id, String receptor, String assunto, String mensagem, LocalDateTime horaEnvio) {
        this.id=id;
        this.receptor=receptor;
        this.assunto=assunto;
        this.mensagem=mensagem;
        this.horaEnvio=horaEnvio;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getReceptor() {
        return receptor;
    }

    public void setReceptor(String receptor) {
        this.receptor = receptor;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public LocalDateTime getHoraEnvio() {
        return horaEnvio;
    }

    public void setHoraEnvio(LocalDateTime horaEnvio) {
        this.horaEnvio = horaEnvio;
    }
}
