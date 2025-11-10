package com.bikeunirio.bicicletario.externo.service;

import org.springframework.web.bind.annotation.PostMapping;

public class AutenticacaoService {
    //Se o tipo de autenticação mudar é só mexer aqui e não no controller de cobrança
    //Utilizado para gerar e guardar o token necessário para todas as requisições na API

    private String clienteId;

    private String clienteSecret;

    private final String urlBase ="https://api-m.sandbox.paypal.com";

    @PostMapping("/v1/oauth2/token")
    public void gerarTokenCredencial(){

    }

}
