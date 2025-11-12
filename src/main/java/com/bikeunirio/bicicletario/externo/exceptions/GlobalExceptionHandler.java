package com.bikeunirio.bicicletario.externo.exceptions;

import org.hibernate.ObjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    //Exceptions do Email
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> handleExceptionValidacao(MethodArgumentNotValidException exception){
        List<String> erros = exception.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> (error).getDefaultMessage())
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


}
