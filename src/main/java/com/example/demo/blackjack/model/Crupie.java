package com.example.demo.blackjack.model;

import com.example.demo.auth.dto.UserDTO;

import java.util.ArrayList;

public class Crupie extends Player {

    public void setUser(String name) {
        setUser(new UserDTO(name));
    }

    public Crupie() {
        setUser("Crupie");
        setMao(new ArrayList<>());
        setPontuacao(0);
        setPerdeuTurno(false);
        setStand(false);
        setJogadorAtual(false);
        setJogandoAtualmente(false);
    }

}