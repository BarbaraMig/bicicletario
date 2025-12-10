package com.bikeunirio.bicicletario.externo.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class FilaCobranca {
    List<Cobranca> filaCobrancas;
    LocalDateTime ultimaChecagem;

    public List<Cobranca> getFilaCobrancas() {
        return filaCobrancas;
    }

    public void setFilaCobrancas(List<Cobranca> filaCobrancas) {
        this.filaCobrancas = filaCobrancas;
    }

    public LocalDateTime getUltimaChecagem() {
        return ultimaChecagem;
    }

    public void setUltimaChecagem(LocalDateTime ultimaChecagem) {
        this.ultimaChecagem = ultimaChecagem;
    }
}
