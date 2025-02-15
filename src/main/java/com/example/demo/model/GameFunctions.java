package com.example.demo.model;

import java.util.List;

public interface GameFunctions {

    // Inicia o jogo com uma lista de jogadores
    void iniciarJogo(List<String> nomes);

    // Distribui as cartas aos jogadores
    void distribuirCartas();

    // Permite que um jogador compre uma carta
    boolean comprarCarta(String nome);

    // Retorna a pontuação do jogador

    // Finaliza o jogo e determina o vencedor
    String finalizarJogo();
}

