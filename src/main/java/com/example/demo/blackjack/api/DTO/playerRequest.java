package com.example.demo.blackjack.api.DTO;


public class playerRequest {
    private String jogada;


    public playerRequest(){

    }

    public playerRequest( String jogada) {
        this.jogada = jogada;
    }

    public String getJogada() {
        return jogada;
    }

    public void setJogada(String jogada) {
        this.jogada = jogada;
    }
}
