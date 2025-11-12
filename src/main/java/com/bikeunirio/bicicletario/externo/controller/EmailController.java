package com.bikeunirio.bicicletario.externo.controller;

import com.bikeunirio.bicicletario.externo.dto.EmailDto;
import com.bikeunirio.bicicletario.externo.mapper.EmailMapper;
import com.bikeunirio.bicicletario.externo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/*
 service com tratamento de exceções(recomendado com erros internos que o cliente não precisa conhecer):
 VANTAGEM: lida com problemas internos que não serão informados ao cliente
 DESVANTANGEM: o service tem que "saber" como lidar com respostas http

 controller com tratamento de exceções(recomendado com erros que serão mandados para o cliente):
 VANTAGEM: separa lógica
 DESVANTAGEM: vazamento de informações do erro que deu no service
*/

@RestController
public class EmailController {

    private final EmailService emailService;

    EmailController(EmailService emailService){
        this.emailService=emailService;
    }

    @PostMapping("/enviarEmail")
    public ResponseEntity<?> enviarEmail(@RequestBody EmailDto email){
        return ResponseEntity.ok().body(emailService.enviarEmail(email));
    }

}