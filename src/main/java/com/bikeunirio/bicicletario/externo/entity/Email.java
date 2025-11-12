package com.bikeunirio.bicicletario.externo.entity;

import jakarta.persistence.*;

@Entity
@Table(name="logs_email")
public class Email{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String receptor;

    private String assunto;

    private String mensagem;

    public Email(){
    }

    public Email(long id, String receptor, String assunto, String mensagem) {
        this.id=id;
        this.receptor=receptor;
        this.assunto=assunto;
        this.mensagem=mensagem;
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

}