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
import org.springframework.web.reactive.function.client.WebClientResponseException;

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
        cobranca.setStatus("NA_FILA");

        filaCobranca.add(cobranca);

        return mapper.toDTO(cobranca);
    }

    public RespostaErroDto validarCartaoCredito(CartaoDto cartao) {
        RespostaErroDto resposta = new RespostaErroDto();
        resposta.setStatus(404);
        resposta.setMessage("não programado para validar o cartão de "+ cartao.getNomeTitular());
        return resposta;
    }

    /**
     * Realiza a cobrança chamando a API externa via WebClient.
     * * @param pedido Dados do pedido de cobrança.
     * @return DTO da cobrança processada.
     * @throws WebClientResponseException Se a API externa retornar erro 4xx ou 5xx.
     * @throws RuntimeException Se houver erro de conexão ou timeout.
     */
    public CobrancaDto realizarCobranca(PedidoCobrancaDto pedido) {
        String token = paypalAutenticacao.getTokenAutenticacao();

        // Chamada ao WebClient (será interceptada pelo Mock nos testes)
        // O .block() faz a chamada síncrona e pode lançar exceções.
        Map<String, Object> respostaExterno = webClient.post()
                .uri("/v1/payments/payment")
                .header("Authorization", "Bearer " + token)
                .bodyValue(pedido)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        Cobranca cobranca = new Cobranca();
        cobranca.setValor(pedido.getValor());

        // Exemplo simplificado: pega status do map ou define default
        String status = (respostaExterno != null && respostaExterno.containsKey("status"))
                ? (String) respostaExterno.get("status")
                : "COMPLETED";

        cobranca.setStatus(status);

        Cobranca entidadeSalva = cobrancaRepository.save(cobranca);
        return mapper.toDTO(entidadeSalva);
    }

    public Optional<CobrancaDto> obterCobranca(Long id) {
        return cobrancaRepository.findById(id)
                .map(c -> {
                    CobrancaDto dto = new CobrancaDto();
                    dto.setValorCobranca(c.getValor());
                    return dto;
                });
    }

    /**
     * Processa itens da fila.
     * Se ocorrer erro no WebClient, a exceção interromperá o loop (pois removemos o try-catch).
     */
    public List<CobrancaDto> processaCobrancasEmFila() {
        List<CobrancaDto> processadas = new ArrayList<>();

        Iterator<Cobranca> iterator = filaCobranca.iterator();

        while (iterator.hasNext()) {
            Cobranca cobrancaDaFila = iterator.next();

            PedidoCobrancaDto pedido = new PedidoCobrancaDto();
            pedido.setValor(cobrancaDaFila.getValor());
            pedido.setIdCiclista(cobrancaDaFila.getIdCiclista());

            // Chama o metodo que invoca o WebClient.
            // Se falhar (ex: WebClientResponseException), o erro sobe e o metodo aborta.
            CobrancaDto resultado = realizarCobranca(pedido);

            processadas.add(resultado);

            // Remove da fila somente se sucesso (pois se lançar exceção acima, não chega aqui)
            iterator.remove();
        }

        return processadas;
    }
}