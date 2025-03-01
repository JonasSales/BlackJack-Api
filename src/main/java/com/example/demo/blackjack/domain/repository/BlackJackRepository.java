package com.example.demo.blackjack.domain.repository;

import com.example.demo.blackjack.model.Player;
import com.example.demo.blackjack.model.Table;

import java.util.UUID;

public interface BlackJackRepository {


    // Permite que um jogador compre uma carta
    boolean comprarCarta(Player jogador, Table idMesa);

    // Finaliza o jogo e determina o vencedor
    Player finalizarJogo(Table idMesa);
}

