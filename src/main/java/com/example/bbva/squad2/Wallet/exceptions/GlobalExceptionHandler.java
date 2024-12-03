package com.example.bbva.squad2.Wallet.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WalletsException.class)
    public ResponseEntity<Map<String, Object>> handleWalletsException(WalletsException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("error", ex.getStatus().getReasonPhrase()); // Descripción del código HTTP
        response.put("status", ex.getStatus().value()); // Código numérico del estado
        response.put("message", ex.getMessage()); // Mensaje personalizado de la excepción

        return ResponseEntity.status(ex.getStatus()).body(response);
    }
}
