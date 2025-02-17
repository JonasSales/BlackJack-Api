package com.example.demo.repository;

import com.example.demo.model.Player;

public interface BlackJackRepository {

    // Inicia o jogo com uma lista de jogadores
    void iniciarJogo();

    // Distribui as cartas aos jogadores
    void distribuirCartas();

    // Permite que um jogador compre uma carta
    boolean comprarCarta(Player jogador);

    // Retorna a pontuação do jogador

    // Finaliza o jogo e determina o vencedor
    String finalizarJogo();
}

