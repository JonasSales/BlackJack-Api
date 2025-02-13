package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private String nome;
    private List<Card> mao = new ArrayList<>();

    public Player() {

    }

    public Player(String nome) {
        this.nome = nome;
    }

    public List<Card> getMao() {
        return mao;
    }

    public void setMao(List<Card> mao) {
        this.mao = mao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }


    public void adicionarCarta(Card carta) {
        mao.add(carta);
    }

    public void removerCarta(Card carta) {
        mao.remove(carta);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Jogador: ").append(nome).append("\n");
        sb.append("Cartas: \n");
        for (Card carta : mao) {
            sb.append(carta).append("\n");
        }
        return sb.toString();
    }
}
