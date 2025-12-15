package com.bikeunirio.bicicletario.externo.controller;

import com.bikeunirio.bicicletario.externo.dto.CartaoDto;
import com.bikeunirio.bicicletario.externo.dto.CobrancaDto;
import com.bikeunirio.bicicletario.externo.dto.PedidoCobrancaDto;
import com.bikeunirio.bicicletario.externo.dto.RespostaErroDto;
import com.bikeunirio.bicicletario.externo.service.CobrancaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CobrancaController {

    private final CobrancaService cobrancaService;

    CobrancaController(CobrancaService cobrancaService){
        this.cobrancaService=cobrancaService;
    }

    //finalizado
    @GetMapping(" ")
    public ResponseEntity<CobrancaDto> recuperarCobranca(@PathVariable long idCobranca){
        // Se o Optional retornado em service.obterCobranca existir, converte para DTO e
        // retorna 200 OK. Se não, 404 Not Found.
        return cobrancaService.obterCobranca(idCobranca)
                .map(cobrancaDto -> ResponseEntity.ok(cobrancaDto))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    //realiza cobranca
    @PostMapping("/cobranca")
    public ResponseEntity<CobrancaDto> realizarCobranca(@RequestBody PedidoCobrancaDto pedidoCobranca){
        return ResponseEntity.ok(cobrancaService.realizarCobranca(pedidoCobranca));
    }

    @PostMapping("/validarCartaoCredito")
    public ResponseEntity<String> validarCartaoCredito(@RequestBody CartaoDto cartao){
        RespostaErroDto resposta = cobrancaService.validarCartaoCredito(cartao);

        if(resposta.getStatus() == 200)
            return ResponseEntity.ok().body(resposta.toString());
        //ta certo chamar o toString assim???
        return ResponseEntity.ok("validacao");
    }

    @PostMapping("/filaCobranca")
    public ResponseEntity<CobrancaDto> incluirCobrancaNaFila(@RequestBody PedidoCobrancaDto pedidoCobrancaDto){
        return ResponseEntity.ok(cobrancaService.incluirCobrancaNaFila(pedidoCobrancaDto));
    }

    @PostMapping("/processaCobrancasEmFila")
    public ResponseEntity<List<CobrancaDto>> processaCobrancasEmFila(){
        return ResponseEntity.ok(cobrancaService.processaCobrancasEmFila());
    }



}
