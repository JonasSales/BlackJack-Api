package com.example.demo.DTO;

import com.example.demo.model.Player;

public class playerRequest {
    private Player player;
    private String jogada;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getJogada() {
        return jogada;
    }

    public void setJogada(String jogada) {
        this.jogada = jogada;
    }
}
