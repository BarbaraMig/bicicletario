package com.bikeunirio.bicicletario.externo.zmudancas.controller;

import com.bikeunirio.bicicletario.externo.zmudancas.dto.CartaoDto;
import com.bikeunirio.bicicletario.externo.zmudancas.dto.CobrancaDto;
import com.bikeunirio.bicicletario.externo.zmudancas.service.CobrancaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CobrancaController {



    private final CobrancaService cobrancaService;

    CobrancaController(CobrancaService cobrancaService){
        this.cobrancaService=cobrancaService;
    }

    @GetMapping("/cobranca/{idCobranca}")
    public ResponseEntity<CobrancaDto> recuperarCobranca(@PathVariable String idCobranca){
        //cobrancaService

        return null;
    }

    //realiza cobranca
    @PostMapping("/cobranca")
    public ResponseEntity<CobrancaDto> realizarCobranca(@RequestBody String cobranca){

        //cobrancaService

        return null;
    }
    //realiza cobranca
    @PostMapping("/processaCobrancasEmFila")
    public ResponseEntity<CobrancaDto> processaCobrancasEmFila(){
        //cobrancaService

        return null;
    }
    @PostMapping("/filaCobranca")
    public ResponseEntity<CobrancaDto> incluirCobrancaNaFila(@RequestBody String cobrancaIncluir){
        //cobrancaService

        return null;
    }
    @PostMapping("/validarCartaoCredito")
    public ResponseEntity<CartaoDto> validarCartaoCredito(@RequestBody String cartao){
        //cobrancaService

        return null;
    }



}
