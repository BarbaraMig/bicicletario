package com.bikeunirio.bicicletario.externo.controller;

import com.bikeunirio.bicicletario.externo.dto.RespostaHttpDto;
import com.bikeunirio.bicicletario.externo.service.RestaurarDadosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class RestaurarDadosController {

    private final RestaurarDadosService restaurarDadosService;

    public RestaurarDadosController(RestaurarDadosService restaurarDadosService) {
        this.restaurarDadosService = restaurarDadosService;
    }

    @GetMapping("/restaurarBanco")
    public ResponseEntity<RespostaHttpDto> restaurarBanco() {
        return ResponseEntity.ok().body(restaurarDadosService.restaurarBanco());
    }
}
