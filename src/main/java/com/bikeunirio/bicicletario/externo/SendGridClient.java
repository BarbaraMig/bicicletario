package com.bikeunirio.bicicletario.externo;

import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SendGridClient {

    private final SendGrid sendGrid;

    public SendGridClient(SendGrid sendGrid) {
        this.sendGrid = sendGrid;
    }

    public void enviarEmail(
            String fromEmail,
            String fromName,
            String toEmail,
            String subject,
            String body
    ) {
        Email from = new Email(fromEmail, fromName);
        Email to = new Email(toEmail);
        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, to, content);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);

            if (response.getStatusCode() >= 400) {
                throw new IllegalStateException(
                        "Erro SendGrid | Status: " + response.getStatusCode()
                                + " | Body: " + response.getBody()
                );
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao chamar API do SendGrid", e);
        }
    }
}

