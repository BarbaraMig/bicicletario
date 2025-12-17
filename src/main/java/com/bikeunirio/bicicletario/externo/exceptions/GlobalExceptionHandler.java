package com.bikeunirio.bicicletario.externo.exceptions;

import com.bikeunirio.bicicletario.externo.dto.RespostaHttpDto;
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
    public ResponseEntity<RespostaHttpDto> handleMailException(MailException exception){
        RespostaHttpDto respostaDto = new RespostaHttpDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage());

        return ResponseEntity.status(respostaDto.getStatus()).body(respostaDto);
    }


    @ExceptionHandler(MailParseException.class)
    public ResponseEntity<RespostaHttpDto> handleMailException(MailParseException exception){
        RespostaHttpDto respostaDto = new RespostaHttpDto(HttpStatus.UNPROCESSABLE_ENTITY.value(),exception.getMessage());

        return ResponseEntity.status(respostaDto.getStatus()).body(respostaDto);
    }


}
