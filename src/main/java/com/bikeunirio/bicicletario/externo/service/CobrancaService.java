package com.bikeunirio.bicicletario.externo.service;

import com.bikeunirio.bicicletario.externo.dto.CartaoDto;
import com.bikeunirio.bicicletario.externo.dto.CobrancaDto;
import com.bikeunirio.bicicletario.externo.dto.PedidoCobrancaDto;
import com.bikeunirio.bicicletario.externo.dto.RespostaHttpDto;
import com.bikeunirio.bicicletario.externo.entity.Cobranca;
import com.bikeunirio.bicicletario.externo.enums.CobrancaEnum;
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
    private final WebClient webClient;
    private final CobrancaRepository cobrancaRepository;
    private final List<Cobranca> filaCobranca;
    private final CobrancaMapper mapper;
    String zoneId = "America/Sao_Paulo";

    public CobrancaService(PaypalAutenticacao paypalAutenticacao,
                           WebClient webClient,
                           CobrancaRepository cobrancaRepository,
                           List<Cobranca> filaCobranca,
                           CobrancaMapper mapper) {
        this.paypalAutenticacao = paypalAutenticacao;
        this.webClient = webClient;
        this.cobrancaRepository = cobrancaRepository;
        this.filaCobranca = filaCobranca;
        this.mapper = mapper;
    }

    public CobrancaDto incluirCobrancaNaFila(PedidoCobrancaDto pedido) {
        Cobranca cobranca = new Cobranca();

        cobranca.setValor(pedido.getValor());
        cobranca.setIdCiclista(pedido.getIdCiclista());
        cobranca.setStatus(String.valueOf(CobrancaEnum.PENDENTE));
        LocalDateTime horario = LocalDateTime.now(ZoneId.of(zoneId));
        cobranca.setHoraSolicitacao(horario);
        cobranca.setHoraFinalizacao(horario);

        Cobranca salva = cobrancaRepository.save(cobranca);
        filaCobranca.add(salva);


        return mapper.toDTO(salva);
    }

    public RespostaHttpDto validarCartaoCredito(CartaoDto cartao) {
        RespostaHttpDto resposta = new RespostaHttpDto();
        resposta.setStatus(200);
        resposta.setMessage("dados atualizados do cartão de "+ cartao.getNomeTitular());
        return resposta;
    }

    public CobrancaDto realizarCobranca(PedidoCobrancaDto pedido) {
        Cobranca cobranca = new Cobranca();
        cobranca.setHoraSolicitacao(LocalDateTime.now(ZoneId.of(zoneId)));
        cobranca.setIdCiclista(pedido.getIdCiclista());

        //metodo que recupera/gera um token de autenticação para a API


        Map<String, Object> respostaExterno = montarRequisicaoCobranca(pedido);


        cobranca.setValor(pedido.getValor());

        //caso a resposta não seja null e exista o atributo status -> getStatus
        //caso contrário, marca a cobraça como FALHA
        if (respostaExterno != null && respostaExterno.containsKey("status")) {
            String statusAPI = String.valueOf(respostaExterno.get("status"));
            if ("CREATED".equals(statusAPI)) {
                cobranca.setStatus(CobrancaEnum.PAGA.toString());
            } else if ("VOIDED".equals(statusAPI)) {
                cobranca.setStatus(CobrancaEnum.CANCELADA.toString());
            } else {
                cobranca.setStatus(CobrancaEnum.FALHA.toString());
            }
        } else {
            cobranca.setStatus(CobrancaEnum.FALHA.toString());
        }
        cobranca.setHoraFinalizacao(LocalDateTime.now(ZoneId.of(zoneId)));
        Cobranca entidadeSalva = cobrancaRepository.save(cobranca);
        return mapper.toDTO(entidadeSalva);
    }

    public Optional<CobrancaDto> obterCobranca(Long id) {

        return cobrancaRepository.findById(id)
                .map(cobranca -> {
                    return mapper.toDTO(cobranca);
                });
    }

    public List<CobrancaDto> processaCobrancasEmFila() {
        List<CobrancaDto> processadas = new ArrayList<>();

        Iterator<Cobranca> iterator = filaCobranca.iterator();

        while (iterator.hasNext()) {
            Cobranca cobrancaDaFila = iterator.next();

            PedidoCobrancaDto pedido = new PedidoCobrancaDto();
            pedido.setValor(cobrancaDaFila.getValor());
            pedido.setIdCiclista(cobrancaDaFila.getIdCiclista());

            //se o webClient der problema, lança exceção
            CobrancaDto resultado = realizarCobranca(pedido);

            processadas.add(resultado);

            // remove da fila somente se sucesso, se lançar exceção acima, não chega aqui
            iterator.remove();
        }

        return processadas;
    }


    private Map<String, Object> montarRequisicaoCobranca(PedidoCobrancaDto pedido) {
        String token = paypalAutenticacao.getTokenAutenticacao();

        Map<String, Object> amount = new HashMap<>();
        amount.put("currency_code", "BRL");
        amount.put("value", String.format(Locale.US, "%.2f", pedido.getValor()));


        Map<String, Object> purchaseUnit = new HashMap<>();
        purchaseUnit.put("amount", amount);
        purchaseUnit.put("description", "Cobranca ID Ciclista: " + pedido.getIdCiclista());

        Map<String, Object> applicationContext = new HashMap<>();
        applicationContext.put("return_url","https://bicicletarioexterno.onrender.com");
        applicationContext.put("cancel_url","https://bicicletarioexterno.onrender.com");


        Map<String, Object> paypalRequest = new HashMap<>();
        paypalRequest.put("intent", "CAPTURE"); // V2 usa CAPTURE ou AUTHORIZE
        paypalRequest.put("purchase_units", Collections.singletonList(purchaseUnit));
        paypalRequest.put("application_context", applicationContext); // <--- Adicionado aqui


        return webClient.post()
                .uri("/v2/checkout/orders")
                .header("Authorization", "Bearer " + token)
                .bodyValue(paypalRequest)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }
}