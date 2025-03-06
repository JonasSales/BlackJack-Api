package com.example.demo.blackjack.api.DTO;


public class PlayerRequest {
    private String jogada;


    public PlayerRequest(){

    }

    public PlayerRequest(String jogada) {
        this.jogada = jogada;
    }

    public String getJogada() {
        return jogada;
    }

    public void setJogada(String jogada) {
        this.jogada = jogada;
    }
}
