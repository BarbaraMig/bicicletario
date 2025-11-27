package com.bikeunirio.bicicletario.externo.zmudancas.controller;

import com.bikeunirio.bicicletario.externo.zmudancas.dto.CartaoDto;
import com.bikeunirio.bicicletario.externo.zmudancas.dto.CobrancaDto;
import com.bikeunirio.bicicletario.externo.zmudancas.dto.PedidoCobrancaDto;
import com.bikeunirio.bicicletario.externo.zmudancas.dto.RespostaDto;
import com.bikeunirio.bicicletario.externo.zmudancas.entity.Cobranca;
import com.bikeunirio.bicicletario.externo.zmudancas.mapper.CobrancaMapper;
import com.bikeunirio.bicicletario.externo.zmudancas.service.CobrancaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

@RestController
public class CobrancaController {



    private final CobrancaService cobrancaService;
    private final CobrancaMapper cobrancaMapper;

    CobrancaController(CobrancaService cobrancaService, CobrancaMapper cobrancaMapper){
        this.cobrancaService=cobrancaService;
        this.cobrancaMapper = cobrancaMapper;
    }

    //finalizado
    @GetMapping("/cobranca/{idCobranca}")
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
        RespostaDto resposta = cobrancaService.validarCartaoCredito(cartao);

        if(resposta.getStatus().equals("200"))
            return ResponseEntity.ok().body(resposta.toString());
        //ta certo chamar o toString assim???
        return ResponseEntity.ok("validacao");
    }



}
