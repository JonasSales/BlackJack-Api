package com.example.demo.blackjack.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BlackjackExceptions.BlackjackException.class)
    public ResponseEntity<Map<String, String>> handleBlackjackException(BlackjackExceptions.BlackjackException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // Status padrão

        // Define o status HTTP com base no tipo de exceção
        switch (ex) {
            case BlackjackExceptions.MesaNaoEncontradaException mesaNaoEncontradaException ->
                    status = HttpStatus.NOT_FOUND;
            case BlackjackExceptions.TokenInvalidoException tokenInvalidoException -> status = HttpStatus.UNAUTHORIZED;
            case BlackjackExceptions.JogadoresInsuficientesException jogadoresInsuficientesException ->
                    status = HttpStatus.BAD_REQUEST;
            case BlackjackExceptions.JogadaInvalidaException jogadaInvalidaException -> status = HttpStatus.BAD_REQUEST;
            case BlackjackExceptions.MesaCheiaException mesaCheiaException -> status = HttpStatus.BAD_REQUEST;
            default -> {
            }
        }

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Ocorreu um erro inesperado: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}