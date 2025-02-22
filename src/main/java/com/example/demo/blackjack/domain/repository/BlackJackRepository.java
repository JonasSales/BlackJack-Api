package com.example.demo.blackjack.domain.repository;

import com.example.demo.blackjack.model.Player;

public interface BlackJackRepository {

    // Inicia o jogo com uma lista de jogadores
    void iniciarJogo();

    // Permite que um jogador compre uma carta
    boolean comprarCarta(Player jogador);

    // Finaliza o jogo e determina o vencedor
    String finalizarJogo();
}

