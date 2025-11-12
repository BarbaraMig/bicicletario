package com.bikeunirio.bicicletario.externo.dto;

import com.bikeunirio.bicicletario.externo.entity.Email;

public class EmailDto {
    private long id;
    private String receptor;
    private String assunto;
    private String mensagem;

    public EmailDto(){
    }

    public EmailDto(String receptor, String assunto, String mensagem) {
        this.receptor = receptor;
        this.assunto = assunto;
        this.mensagem = mensagem;
    }
    /*
    * git branch -m email dev
git fetch origin
git branch -u origin/dev dev
git remote set-head origin -a*/

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
