package com.bikeunirio.bicicletario.externo.excecoes;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class EmailExceptions {
    //EmailException
    public ResponseEntity<?> tratamentoEmailException(/*EmailException exception*/){
        return  ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(" Erro ao enviar e-mail: " /* + exception.getMessage()*/);
    }

}
