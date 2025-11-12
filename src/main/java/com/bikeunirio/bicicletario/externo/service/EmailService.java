package com.bikeunirio.bicicletario.externo.service;


import com.bikeunirio.bicicletario.externo.dto.EmailDto;
import com.bikeunirio.bicicletario.externo.mapper.EmailMapper;
import com.bikeunirio.bicicletario.externo.repository.EmailRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final EmailRepository emailRepository;
    private JavaMailSender mailSender;

    public EmailService(EmailRepository emailRepository, JavaMailSender mailSender){
        this.emailRepository = emailRepository;
        this.mailSender = mailSender;
    }

    @Async
    public void enviarEmail(EmailDto emailEntity){
        //construtor do SimpleMailMessage não recebe nenhum parâmetro
        SimpleMailMessage email = new SimpleMailMessage();

        email.setText(emailEntity.getMensagem());
        email.setSubject(emailEntity.getAssunto());
        email.setTo(emailEntity.getReceptor());
        email.setFrom("emailexternoes2@gmail.com");
        mailSender.send(email);

        //excecoes tratadas pelo @ControllerAdvice
        //aqui, só roda se o sistema não lançar nenhuma exceção
        // também deve salvar nos logs de envio

        EmailMapper emailMapper = new EmailMapper();
        emailRepository.save(emailMapper.dtoParaEntidade(emailEntity));
        return;
    }
}