package com.bikeunirio.bicicletario.externo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//recebe e faz as requisições para a API do PayPal
@RestController
@RequestMapping("/paypal")
public class AutenticacaoController {

    private String clienteId;


    private String clienteSecret;

    //@PostMapping

}