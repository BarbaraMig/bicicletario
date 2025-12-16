package com.bikeunirio.bicicletario.externo.service;

import com.bikeunirio.bicicletario.externo.dto.CartaoDto;
import com.bikeunirio.bicicletario.externo.dto.CobrancaDto;
import com.bikeunirio.bicicletario.externo.dto.PedidoCobrancaDto;
import com.bikeunirio.bicicletario.externo.dto.RespostaErroDto;
import com.bikeunirio.bicicletario.externo.entity.Cobranca;
import com.bikeunirio.bicicletario.externo.enums.CobrancaEnum;
import com.bikeunirio.bicicletario.externo.mapper.CobrancaMapper;
import com.bikeunirio.bicicletario.externo.repository.CobrancaRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CobrancaService {

    private final PaypalAutenticacao paypalAutenticacao;
    private final WebClient webClient;
    private final CobrancaRepository cobrancaRepository;
    private final List<Cobranca> filaCobranca;
    private final CobrancaMapper mapper;

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

        filaCobranca.add(cobranca);

        return mapper.toDTO(cobranca);
    }

    public RespostaErroDto validarCartaoCredito(CartaoDto cartao) {
        RespostaErroDto resposta = new RespostaErroDto();
        resposta.setStatus(200);
        resposta.setMessage("não programado para validar o cartão de "+ cartao.getNomeTitular());
        return resposta;
    }

    public CobrancaDto realizarCobranca(PedidoCobrancaDto pedido) {
        //metodo que recupera/gera um token de autenticação para a API
        String token = paypalAutenticacao.getTokenAutenticacao();

        //chamada ao webclient
        Map<String, Object> respostaExterno = webClient.post()
                .uri("/v1/payments/payment")
                .header("Authorization", "Bearer " + token)
                .bodyValue(pedido)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        Cobranca cobranca = new Cobranca();
        cobranca.setValor(pedido.getValor());

        //caso a resposta não seja null e exista o atributo status -> getStatus
        //caso contrário, marca a cobraça como Erro
        String status = (respostaExterno != null && respostaExterno.containsKey("status"))
                ? (String) respostaExterno.get("status")
                : String.valueOf(CobrancaEnum.ERRO);

        cobranca.setStatus(status);

        Cobranca entidadeSalva = cobrancaRepository.save(cobranca);
        return mapper.toDTO(entidadeSalva);
    }

    public Optional<CobrancaDto> obterCobranca(Long id) {
        return cobrancaRepository.findById(id)
                .map(cobranca -> {
                    CobrancaDto dto = new CobrancaDto();
                    dto.setValorCobranca(cobranca.getValor());
                    return dto;
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
}