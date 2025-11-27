package com.bikeunirio.bicicletario.externo.zmudancas.exceptions;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalHandler{
    private static final String STATUS = "status";
    private static final String MESSAGE = "mensagem";


    @ExceptionHandler(MailException.class)
    public ResponseEntity<Map<String,Object>> handleMailException(MailException exception){
        Map<String, Object> corpoDoErro = new LinkedHashMap<>();
        corpoDoErro.put(STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
        corpoDoErro.put(MESSAGE, exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(corpoDoErro);
    }


    @ExceptionHandler(MailParseException.class)
    public ResponseEntity<Map<String,Object>> handleMailException(MailParseException exception){
        Map<String, Object> corpoDoErro = new LinkedHashMap<>();
        corpoDoErro.put(STATUS, HttpStatus.UNPROCESSABLE_ENTITY.value());
        corpoDoErro.put(MESSAGE, exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(corpoDoErro);
    }
    // já feita no lugar certo
    //========== adições de outras exceptions ========== //
    // Possivelmente fazer:
    // NullPointerException
    // NotFound


}

