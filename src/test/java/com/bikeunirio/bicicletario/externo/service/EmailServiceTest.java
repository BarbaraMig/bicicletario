package com.bikeunirio.bicicletario.externo.service;

import com.bikeunirio.bicicletario.externo.dto.EmailDto;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {
    @InjectMocks
    EmailService emailService;

    @Mock
    JavaMailSender mailSender;

    //Tudo dá certo
    @Test
    public void testEnviarEmailCerto(){
        EmailDto dto = new EmailDto("emailexternoes2@gmail.com","assunto","mensagem");

        //mock do mailSender
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        ResponseEntity<?> respostaService = emailService.enviarEmail(dto);

        //aqui ele verifica se o código e o corpo da requisição são iguais
        assertEquals(200, respostaService.getStatusCode().value());
        assertEquals(dto,respostaService.getBody());
        //verifica se o mailSender só foi chamado uma vez
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    //O email é inválido
    @Test
    public void testEnviarEmailInvalido(){
        EmailDto dto = new EmailDto("receptor","assunto","mensagem");

        //mock do mailSender
        doThrow(new MailParseException("Erro no envio"){}).when(mailSender)
                .send(any(SimpleMailMessage.class));

        ResponseEntity<?> respostaService = emailService.enviarEmail(dto);

        //aqui ele verifica se o código e o corpo da requisição são iguais
        assertEquals(422, respostaService.getStatusCode().value());
        assertEquals(dto,respostaService.getBody());

        //verifica se o mailSender só foi chamado uma vez
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    //O email não existe
    //https://captainverify.com/pt/mail-tester.html descobre se o email existe ou não
    @Test
    public void testEnviarEmailNaoExistente(){
        EmailDto dto = new EmailDto("ahivbvlaihvu@gmail.com","assunto","mensagem");

        //simplificação: lance uma exceção MailException quando o mailSender enviar uma mensagem
        //da classe SimpleMailMessage (SMM)
        doThrow(new MailException("Erro no envio"){}).when(mailSender)
                .send(any(SimpleMailMessage.class));

        ResponseEntity<?> respostaService = emailService.enviarEmail(dto);

        //aqui ele verifica se o código e o corpo da requisição são iguais
        assertEquals(422, respostaService.getStatusCode().value());
        assertEquals(dto,respostaService.getBody());
        //verifica se o mailSender só foi chamado uma vez para enviar uma mensagem da classe SMM
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
