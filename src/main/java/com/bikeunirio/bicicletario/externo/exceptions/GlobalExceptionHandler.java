package com.bikeunirio.bicicletario.externo.exceptions;

import org.hibernate.ObjectNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler{

    //Exceptions do Email
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> handleValidationException(MethodArgumentNotValidException exception){
        List<String> erros = exception.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(erros);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleObjetoNaoEncontrado(ObjectNotFoundException exception){

        Map<String, Object> corpoDoErro = new LinkedHashMap<>();
        corpoDoErro.put("status", HttpStatus.NOT_FOUND.value());
        corpoDoErro.put("message", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(corpoDoErro);
    }

    @ExceptionHandler(MailException.class)
    public ResponseEntity<Map<String,Object>> handleMailException(MailException exception){
        Map<String, Object> corpoDoErro = new LinkedHashMap<>();
        corpoDoErro.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        corpoDoErro.put("message", exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(corpoDoErro);
    }


    @ExceptionHandler(MailParseException.class)
    public ResponseEntity<Map<String,Object>> handleMailException(MailParseException exception){
        Map<String, Object> corpoDoErro = new LinkedHashMap<>();
        corpoDoErro.put("status", HttpStatus.UNPROCESSABLE_ENTITY.value());
        corpoDoErro.put("message", exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(corpoDoErro);
    }

}
