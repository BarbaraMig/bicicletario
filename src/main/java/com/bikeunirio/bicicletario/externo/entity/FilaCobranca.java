package com.bikeunirio.bicicletario.externo.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FilaCobranca {
    List<Cobranca> filaCobrancas;
    LocalDateTime ultimaChecagem;
}
