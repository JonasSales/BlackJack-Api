package com.example.demo.auth.exceptions;

public class AuthExceptions {

    // Exceção para quando o e-mail já está cadastrado
    public static class UserAlreadyExistsException extends RuntimeException {
        public UserAlreadyExistsException(String message) {
            super(message);
        }
    }

    // Exceção para credenciais inválidas (e-mail ou senha incorretos)
    public static class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException(String message) {
            super(message);
        }
    }

    // Exceção para quando o token é inválido ou expirado
    public static class InvalidTokenException extends RuntimeException {
        public InvalidTokenException(String message) {
            super(message);
        }
    }

    // Exceção para quando o usuário não é encontrado
    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    public static class StatusNotFoundException extends RuntimeException {
        public StatusNotFoundException(String message) {
            super(message);
        }
    }
}