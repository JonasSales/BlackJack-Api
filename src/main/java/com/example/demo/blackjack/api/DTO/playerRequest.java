package com.example.demo.blackjack.api.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class playerRequest {
    private String jogada;


    public playerRequest(){

    }

    public playerRequest( String jogada) {
        this.jogada = jogada;
    }

}
