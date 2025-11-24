package com.bikeunirio.bicicletario.externo.zmudancas.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.Base64;
import java.util.Map;

@Component
public class PaypalAutenticacao {
    private final WebClient webClient;

    private Instant expiracao;
    private String tokenAuth;

    @Value("${paypal_client_id}")
    private final String clientId = System.getenv("PAYPAL_CLIENT_ID");

    @Value("${paypal_secret}")
    private final String secret = System.getenv("PAYPAL_SECRET");

    public PaypalAutenticacao(WebClient webClient) {
        this.webClient = webClient;
    }

    private String auth(){
        final String clientId = "${PAYPAL_CLIENT_ID}";
        final String secret = "${PAYPAL_SECRET}";
        String auth = clientId + ":" + secret;
        return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
    }

    public String getTokenAutenticacao(){
        //se o token não existir (=null), se a expiracao não existir (=null) ou se o token vai expirar depois de 30 segundos
        if(tokenAuth != null && expiracao != null && expiracao.isAfter(Instant.now().plusSeconds(30))){
            return tokenAuth;
        }

        //Requisicao
        Map<String, Object> respostaRequisicao = webClient.post()
                .uri("/v1/oauth2/token")
                //pega o tipo de autenticacao e
                .header(HttpHeaders.AUTHORIZATION, auth())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("grant_type=client_credentials")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                //block esperar a resposta de forma bloqueante
                .block();

        //pega o valor do access_token
        tokenAuth = (String) respostaRequisicao.get("access_token");
        //pega o valor do expires_in -> tempo em segundos que leva para o token expirar
        Integer expiracaoSegundos = (Integer) respostaRequisicao.get("expires_in");

        //calcula o momento de expiracao
        expiracao = Instant.now().plusSeconds(expiracaoSegundos);

        return tokenAuth;
    }

}
