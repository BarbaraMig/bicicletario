package com.bikeunirio.bicicletario.externo.dto;

import java.time.LocalDateTime;

public class EmailDto {
    private long id;
    private String receptor;
    private String assunto;
    private String mensagem;
    private LocalDateTime horaEnvio;

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
