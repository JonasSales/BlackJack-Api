package com.example.demo.blackjack.exceptions;

import java.util.UUID;

import java.util.UUID;

public class BlackjackExceptions {

    // Exceção base para herança
    public static class BlackjackException extends RuntimeException {
        public BlackjackException(String message) {
            super(message);
        }
    }

    // Mesa não encontrada
    public static class MesaNaoEncontradaException extends BlackjackException {
        public MesaNaoEncontradaException(UUID mesaId) {
            super("Mesa com ID " + mesaId + " não encontrada.");
        }
    }

    // Token inválido ou expirado
    public static class TokenInvalidoException extends BlackjackException {
        public TokenInvalidoException() {
            super("Token inválido ou expirado.");
        }
    }

    // Jogador não encontrado
    public static class JogadorNaoEncontradoException extends BlackjackException {
        public JogadorNaoEncontradoException(String nome) {
            super("Jogador " + nome + " não encontrado.");
        }
    }

    // Jogo já iniciado
    public static class JogoJaIniciadoException extends BlackjackException {
        public JogoJaIniciadoException(UUID mesaId) {
            super("O jogo na mesa " + mesaId + " já foi iniciado.");
        }
    }

    // Número insuficiente de jogadores
    public static class JogadoresInsuficientesException extends BlackjackException {
        public JogadoresInsuficientesException() {
            super("É necessário pelo menos dois jogadores para iniciar o jogo.");
        }
    }

    // Jogada inválida
    public static class JogadaInvalidaException extends BlackjackException {
        public JogadaInvalidaException(String jogada) {
            super("Jogada inválida: " + jogada);
        }
    }

    // Mesa cheia (limite de jogadores atingido)
    public static class MesaCheiaException extends BlackjackException {
        public MesaCheiaException(UUID mesaId) {
            super("A mesa " + mesaId + " está cheia. Não é possível adicionar mais jogadores.");
        }
    }

    // Jogador já está na mesa
    public static class JogadorJaNaMesaException extends BlackjackException {
        public JogadorJaNaMesaException(String nome) {
            super("O jogador " + nome + " já está na mesa.");
        }
    }

    // Jogo não iniciado
    public static class JogoNaoIniciadoException extends BlackjackException {
        public JogoNaoIniciadoException(UUID mesaId) {
            super("O jogo na mesa " + mesaId + " ainda não foi iniciado.");
        }
    }
}