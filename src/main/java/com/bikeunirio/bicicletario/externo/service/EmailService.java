package com.bikeunirio.bicicletario.externo.service;

import com.bikeunirio.bicicletario.externo.client.SendGridClient;
import com.bikeunirio.bicicletario.externo.dto.EmailDto;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EmailService {

    private final SendGridClient sendGridClient;

    public EmailService(SendGridClient sendGridClient) {
        this.sendGridClient = sendGridClient;
    }

    public void enviarEmail(EmailDto dto) {
        validar(dto);

        sendGridClient.enviarEmail(
                dto.getReceptor(),
                dto.getAssunto(),
                dto.getMensagem()
        );
    }

    private void validar(EmailDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("EmailDto não pode ser nulo");
        }

        if (!StringUtils.hasText(dto.getReceptor())) {
            throw new IllegalArgumentException("Receptor do email é obrigatório");
        }

        if (!StringUtils.hasText(dto.getAssunto())) {
            throw new IllegalArgumentException("Assunto do email é obrigatório");
        }

        if (!StringUtils.hasText(dto.getMensagem())) {
            throw new IllegalArgumentException("Mensagem do email é obrigatória");
        }
    }
}
