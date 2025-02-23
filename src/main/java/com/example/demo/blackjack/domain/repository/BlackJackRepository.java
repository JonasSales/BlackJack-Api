package com.example.demo.blackjack.domain.repository;

import com.example.demo.blackjack.model.Player;

import java.util.UUID;

public interface BlackJackRepository {


    // Permite que um jogador compre uma carta
    boolean comprarCarta(Player jogador, UUID idMesa);

    // Finaliza o jogo e determina o vencedor
    String finalizarJogo(UUID idMesa);
}

