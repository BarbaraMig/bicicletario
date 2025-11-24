package com.bikeunirio.bicicletario.externo.zmudancas.service;

import com.bikeunirio.bicicletario.externo.zmudancas.dto.CobrancaDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class CobrancaService {
    private final PaypalAutenticacao paypalAutenticacao;
    private final WebClient paypalWebClient;

    public CobrancaService(PaypalAutenticacao paypalAutenticacao, WebClient paypalWebClient) {
        this.paypalAutenticacao = paypalAutenticacao;
        this.paypalWebClient = paypalWebClient;
    }


    public CobrancaDto realizarCobranca(){
        //chamado pelo realizarCobranca do controller
        String tokenAutenticacao = paypalAutenticacao.getTokenAutenticacao();

        /*
        return paypalWebClient.post()
                .uri("/v2/checkout/orders")
                .header("Authorization", "Bearer " + tokenAutenticacao)
                .bodyValue(Map.of("intent", "CAPTURE",
                                "purchase_units", List.of(
                                        Map.of(
                                                "amount", Map.of(
                                                        "currency_code", "USD",
                                                        "value", "10.00"
                                                )
                                        ))).retrieve().bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {}).block();

        deve fazer o pedido de um token (PaypalAutenticacao.getTokenAutenticacao)
        fazer a requisicao da cobranca em si, utilizando o token no tipo bearer auth
        receber
        */
        return null;
    }

}
