package com.bikeunirio.bicicletario.externo.dto;

public class PedidoCobrancaDto {
    //Informações necessárias para o pedido, passadas pelo microsserviço Aluguel
    private float valor;
    private long idCiclista;

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public long getIdCiclista() {
        return idCiclista;
    }

    public void setIdCiclista(long idCiclista) {
        this.idCiclista = idCiclista;
    }
}
