package com.bikeunirio.bicicletario.externo.exceptions;

import com.bikeunirio.bicicletario.externo.dto.RespostaErroDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;



@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<RespostaErroDto> build(
            HttpStatus status, String message) {

        return ResponseEntity
                .status(status)
                .body(new RespostaErroDto(status.value(), message));
    }

    //email
    @ExceptionHandler(MailParseException.class)
    public ResponseEntity<RespostaErroDto> handleMailParse(MailParseException ex) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, "Email inválido");
    }

    @ExceptionHandler(MailException.class)
    public ResponseEntity<RespostaErroDto> handleMail(MailException ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Falha ao enviar email");
    }

    //http
    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    public ResponseEntity<RespostaErroDto> handleNotFound(HttpClientErrorException.NotFound ex) {
        return build(HttpStatus.NOT_FOUND, "Recurso não encontrado");
    }

    @ExceptionHandler(HttpClientErrorException.UnprocessableEntity.class)
    public ResponseEntity<RespostaErroDto> handleUnprocessable(HttpClientErrorException.UnprocessableEntity ex) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, "Entidade inválida");
    }

    //exceção genérica
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<RespostaErroDto> handleRuntime(RuntimeException ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno");
    }
}
