package com.bikeunirio.bicicletario.externo.service;


import com.bikeunirio.bicicletario.externo.entity.Email;
import com.bikeunirio.bicicletario.externo.repository.EmailRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final EmailRepository emailRepository;

    public EmailService(EmailRepository emailRepository){
        this.emailRepository = emailRepository;
    }


    public String enviarEmail(String receptor, String assunto, String mensagem){
        //construtor do SimpleMailMessage não recebe nenhum parâmetro
        SimpleMailMessage email = new SimpleMailMessage();

        email.setText(mensagem);
        email.setSubject(assunto);
        email.setTo(receptor);
        email.setFrom("emailexternoes2@gmail.com");
        //excecoes tratadas pelo @ControllerAdvice
        //aqui, só roda se o sistema não lançar nenhuma exceção
        // também deve salvar nos logs de envio
        Email emailEntity = new Email();
        emailEntity.setAssunto(assunto);
        emailEntity.setMensagem(mensagem);
        emailEntity.setReceptor(receptor);


        emailRepository.save(emailEntity);
        return "Email enviado com sucesso";

    }
}
