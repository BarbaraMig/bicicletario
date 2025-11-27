package com.bikeunirio.bicicletario.externo.zmudancas.service;

import com.bikeunirio.bicicletario.externo.zmudancas.dto.CartaoDto;
import com.bikeunirio.bicicletario.externo.zmudancas.dto.CobrancaDto;
import com.bikeunirio.bicicletario.externo.zmudancas.dto.PedidoCobrancaDto;
import com.bikeunirio.bicicletario.externo.zmudancas.dto.RespostaDto;
import com.bikeunirio.bicicletario.externo.zmudancas.entity.Cobranca;
import com.bikeunirio.bicicletario.externo.zmudancas.mapper.CobrancaMapper;
import com.bikeunirio.bicicletario.externo.zmudancas.repository.CobrancaRepository;
import org.springframework.cglib.core.Local;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CobrancaService {
    private final PaypalAutenticacao paypalAutenticacao;
    private final WebClient paypalWebClient;
    private final CobrancaRepository cobrancaRepository;
    private final List<Cobranca> filaCobranca;
    private final CobrancaMapper mapper;

    public CobrancaService(PaypalAutenticacao paypalAutenticacao, WebClient paypalWebClient, CobrancaRepository cobrancaRepository, List<Cobranca> Cobranca, CobrancaMapper mapper) {
        this.paypalAutenticacao = paypalAutenticacao;
        this.paypalWebClient = paypalWebClient;
        this.cobrancaRepository = cobrancaRepository;
        this.mapper = mapper;
        this.filaCobranca = new ArrayList<>();
    }


    public CobrancaDto realizarCobranca(PedidoCobrancaDto pedidoCobrancaDto){
        CobrancaDto cobrancaDto = new CobrancaDto();
        cobrancaDto.setHoraSolicitacao(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));

        //chamado pelo realizarCobranca do controller
        String tokenAutenticacao = paypalAutenticacao.getTokenAutenticacao();

        //pega o valor da compra
        float valorCompra = pedidoCobrancaDto.getValor();
        cobrancaDto.setValorCobranca(valorCompra);

        //faz a requisição para a api de pagamento
        Map<String,Object> retornoApi = paypalWebClient.post()
                .uri("/v2/checkout/orders")
                .header("Authorization", "Bearer " + tokenAutenticacao)
                .bodyValue(Map.of("intent", "CAPTURE",
                        "purchase_units", List.of(
                                Map.of(
                                        "amount", Map.of(
                                                "currency_code", "USD",
                                                "value", String.format("%.2f", valorCompra)
                                        )
                                )))).retrieve().bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {}).block();


        cobrancaDto.setStatus((String)retornoApi.get("status"));
        cobrancaRepository.save(mapper.toEntity(cobrancaDto));
        cobrancaDto.setHoraFinalizacao(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
        return cobrancaDto;
        /*
        deve fazer o pedido de um token (PaypalAutenticacao.getTokenAutenticacao)
        fazer a requisicao da cobranca em si, utilizando o token no tipo bearer auth
        receber
        */

    }

    public Optional<CobrancaDto> obterCobranca(long id){
        //Optional não permite retornar null mas pode retornar um objeto vazio
        //ele passa as inforamções do Optional para um dto
        return cobrancaRepository.findById(id)
                .map(entidade -> {
                    CobrancaDto dto = new CobrancaDto();
                    //preenche os dados manualmente
                    dto.setIdCobranca(entidade.getId());
                    dto.setValorCobranca(entidade.getValor());
                    dto.setStatus(entidade.getStatus());
                    dto.setHoraSolicitacao(entidade.getHoraSolicitacao());
                    dto.setHoraFinalizacao(entidade.getHoraFinalizacao());
                    dto.setIdCiclista(entidade.getIdCiclista());
                    return dto;
                });
    }


    public RespostaDto validarCartaoCredito(CartaoDto cartaoDto){
        //pegar quais atributos do cartão para chamar o pedido de cobrança?
        //chamar o realizarCobranca para fazer a validação (cobrança de 1 centavo)
        //realizarCobranca();

        return null;
    }

    public void incluirCobrancaNaFila(CobrancaDto cobrancaDto) {
        //salva na fila;
        filaCobranca.add(mapper.toEntity(cobrancaDto));

    }

    public void processaCobrancasEmFila(List<Cobranca> filaCobrancaAtrasada) {

        for(Cobranca cobranca : filaCobrancaAtrasada){
        //realizarCobrança
        //trocar o status para paga
        }

    }
}
