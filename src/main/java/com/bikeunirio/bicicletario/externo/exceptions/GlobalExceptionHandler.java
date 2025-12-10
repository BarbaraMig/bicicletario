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
public class GlobalExceptionHandler{

    @ExceptionHandler(MailException.class)
    public ResponseEntity<RespostaErroDto> handleMailException(MailException exception){
        RespostaErroDto respostaDto = new RespostaErroDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage());

        return ResponseEntity.status(respostaDto.getStatus()).body(respostaDto);
    }


    @ExceptionHandler(MailParseException.class)
    public ResponseEntity<RespostaErroDto> handleMailException(MailParseException exception){
        RespostaErroDto respostaDto = new RespostaErroDto(HttpStatus.UNPROCESSABLE_ENTITY.value(),exception.getMessage());

        return ResponseEntity.status(respostaDto.getStatus()).body(respostaDto);
    }

    @ExceptionHandler(HttpClientErrorException.UnprocessableEntity.class)
    public ResponseEntity<RespostaErroDto> handleUnprocessableEntity(HttpClientErrorException.UnprocessableEntity exception){
        RespostaErroDto respostaDto = new RespostaErroDto(HttpStatus.UNPROCESSABLE_ENTITY.value(),exception.getMessage());

        return ResponseEntity.status(respostaDto.getStatus()).body(respostaDto);
    }

    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    public ResponseEntity<RespostaErroDto> handleNotFound(HttpClientErrorException.NotFound exception){
        RespostaErroDto respostaDto = new RespostaErroDto(HttpStatus.NOT_FOUND.value(),exception.getMessage());

        return ResponseEntity.status(respostaDto.getStatus()).body(respostaDto);
    }
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<RespostaErroDto> handleNullPointerException(NullPointerException exception){
        RespostaErroDto respostaDto = new RespostaErroDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage());

        return ResponseEntity.status(respostaDto.getStatus()).body(respostaDto);
    }

}
