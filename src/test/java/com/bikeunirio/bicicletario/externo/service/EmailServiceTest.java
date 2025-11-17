package com.bikeunirio.bicicletario.externo.service;

import com.bikeunirio.bicicletario.externo.dto.EmailDto;
import org.junit.jupiter.api.Test; // Import do JUnit 5
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*; // Import do JUnit 5
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @InjectMocks
    EmailService emailService;

    @Mock
    JavaMailSender mailSender;

    // Teste 1: Tudo dá certo
    @Test
    void testEnviarEmailCerto() {
        EmailDto dto = new EmailDto("emailexternoes2@gmail.com", "assunto", "mensagem");

        // Configura o mock: mailSender não faz nada quando send() for chamado
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // chama o metodo e garante que ele só vai indicar "pass" se não lançar exceção
        assertDoesNotThrow(() -> emailService.enviarEmail(dto));

        // Verifica se mailSender.send() foi chamado uma vez
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    // Teste 2: O email é inválido (lança MailParseException)
    @Test
    void testEnviarEmailInvalido() {
        EmailDto dto = new EmailDto("receptor", "assunto", "mensagem");

        // Mock do mailSender para LANÇAR a exceção
        doThrow(new MailParseException("Erro de parse")).when(mailSender).send(any(SimpleMailMessage.class));

        // O teste correto é verificar se a exceção é lançada pelo serviço
        MailParseException exception = assertThrows(MailParseException.class, () -> {
            emailService.enviarEmail(dto);
        });

        // Verificações adicionais (opcional)
        assertEquals("Erro de parse", exception.getMessage());
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    // Teste 3: Outro erro de email (lança MailException)
    @Test
    void testEnviarEmailNaoExistente() {
        EmailDto dto = new EmailDto("ahivbvlaihvu@gmail.com", "assunto", "mensagem");

        // Mock do mailSender para LANÇAR a exceção
        doThrow(new MailException("Erro no envio") {})
                .when(mailSender).send(any(SimpleMailMessage.class));

        // O teste correto é verificar se a exceção é lançada pelo serviço
        MailException exception = assertThrows(MailException.class, () -> {
            emailService.enviarEmail(dto);
        });

        // Verificações adicionais (opcional)
        assertEquals("Erro no envio", exception.getMessage());
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}