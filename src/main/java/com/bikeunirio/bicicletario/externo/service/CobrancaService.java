package com.bikeunirio.bicicletario.externo.service;

import com.bikeunirio.bicicletario.externo.dto.CartaoDto;
import com.bikeunirio.bicicletario.externo.dto.CobrancaDto;
import com.bikeunirio.bicicletario.externo.dto.PedidoCobrancaDto;
import com.bikeunirio.bicicletario.externo.dto.RespostaErroDto;
import com.bikeunirio.bicicletario.externo.entity.Cobranca;
import com.bikeunirio.bicicletario.externo.mapper.CobrancaMapper;
import com.bikeunirio.bicicletario.externo.repository.CobrancaRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class CobrancaService {
    private final PaypalAutenticacao paypalAutenticacao;
    private final WebClient paypalWebClient;
    private final CobrancaRepository cobrancaRepository;
    private final List<Cobranca> filaCobrancaAtrasada;
    private final CobrancaMapper mapper;

    public CobrancaService(PaypalAutenticacao paypalAutenticacao, WebClient paypalWebClient, CobrancaRepository cobrancaRepository, List<Cobranca> filaCobrancaAtrasada, CobrancaMapper mapper) {
        this.paypalAutenticacao = paypalAutenticacao;
        this.paypalWebClient = paypalWebClient;
        this.cobrancaRepository = cobrancaRepository;
        this.mapper = mapper;
        this.filaCobrancaAtrasada = new ArrayList<>();
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
                                )))).retrieve().bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                //requisição bloqueante
                .block();

        //podia dar nullPointerEx então "se for null ele define como null, caso não seja pega o status"
        cobrancaDto.setStatus((String) (retornoApi != null ? retornoApi.get("status") : null));
        cobrancaDto.setIdApi(retornoApi != null ? retornoApi.get("id").toString() : null);
        cobrancaRepository.save(mapper.toEntity(cobrancaDto));
        cobrancaDto.setHoraFinalizacao(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
        return cobrancaDto;

    }

    public Optional<CobrancaDto> obterCobranca(long id){
        //Optional não permite retornar null, mas pode retornar um objeto vazio
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


    public RespostaErroDto validarCartaoCredito(CartaoDto cartaoDto){
        //pegar quais atributos do cartão para chamar o pedido de cobrança?
        //chamar o realizarCobranca para fazer a validação (cobrança de 1 centavo)
        //realizar Cobranca

        return null;
    }

    public CobrancaDto incluirCobrancaNaFila(PedidoCobrancaDto pedidoCobrancaDto) {
        // Converte o pedido em uma entidade Cobranca para salvar na fila
        Cobranca cobranca = new Cobranca();
        cobranca.setValor(pedidoCobrancaDto.getValor());
        cobranca.setIdCiclista(pedidoCobrancaDto.getIdCiclista());
        cobranca.setHoraSolicitacao(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
        cobranca.setStatus("NA_FILA"); // Define um status provisório

        // Adiciona na lista em memória
        filaCobrancaAtrasada.add(cobranca);
        // Retorna um DTO representando que foi agendado
        return mapper.toDTO(cobranca);
    }

    public List<CobrancaDto> processaCobrancasEmFila() {
        PedidoCobrancaDto pedidoCobrancaDto = new PedidoCobrancaDto();
        List<CobrancaDto> listaPedidosCobrados = new ArrayList<>();
        for(Cobranca cobranca : filaCobrancaAtrasada){
            pedidoCobrancaDto.setValor(cobranca.getValor());
            pedidoCobrancaDto.setIdCiclista(cobranca.getIdCiclista());
            CobrancaDto cobrancaDto = realizarCobranca(pedidoCobrancaDto);
            //assim que faz a cobrança, ele retira da fila de atrasados
            filaCobrancaAtrasada.remove(cobranca);
            //adiciona o retorno de cobranca em uma lista que vai ser retornada ao controller
            listaPedidosCobrados.add(cobrancaDto);
        //trocar o status para paga
        }
        return listaPedidosCobrados;
    }
}
