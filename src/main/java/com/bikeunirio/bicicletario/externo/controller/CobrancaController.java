package com.bikeunirio.bicicletario.externo.controller;


import com.bikeunirio.bicicletario.externo.entity.Cobranca;
import com.bikeunirio.bicicletario.externo.service.CobrancaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

//recebe as requisições http relacionadas a cobrança
@RestController
public class CobrancaController {

    private final CobrancaService cobrancaService;
    //Explicitando dependências
    public CobrancaController(CobrancaService cobrancaService){
        this.cobrancaService = cobrancaService;
    }

    //é realmente cobrança se também tem validaçao no meio??

    @PostMapping("/cobranca")
    public ResponseEntity<?> realizarCobranca(){
        return ResponseEntity.ok(cobrancaService.realizarCobranca());
    }

    @GetMapping("/cobranca/{idcobranca}")
    public ResponseEntity<?> obterCobranca(@PathVariable long idCobranca){
        Optional<Cobranca> cobrancaDesejada = cobrancaService.obterCobranca(idCobranca);

        if(cobrancaDesejada.isEmpty())
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(cobrancaDesejada);
    }

    //Adiciona a cobrança em uma fila que será processada a cada 12h
    @PostMapping("/filaCobranca")
    public void adicionarFilaCobranca(){
        cobrancaService.adicionarFilaCobranca();
    }

    @PostMapping("/processaFilaCobranca")
    public void processaCobrancasEmFila(){
        cobrancaService.processaCobrancasEmFila();
    }

    @PostMapping("/validaCartaoDeCredito")
    public void validarCartaoCredito(){
        cobrancaService.validarCartaoCredito();
    }

}
