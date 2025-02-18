package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private String nome;
    private List<Card> mao = new ArrayList<>();
    private boolean perdeuTurno = false;
    private boolean stand = false;
    private boolean jogadorAtual = false;
    private int pontuacao = 0;


    public Player() {

    }

    public Player(String nome, List<Card> mao, boolean perdeuTurno, boolean stand, boolean jogadorAtual, int pontuacao) {
        this.nome = nome;
        this.mao = mao;
        this.perdeuTurno = perdeuTurno;
        this.stand = stand;
        this.jogadorAtual = jogadorAtual;
        this.pontuacao = pontuacao;
    }

    public Player(String nome) {
        this.nome = nome;
    }

    public Player(String nome, boolean jogadorAtual) {
        this.nome = nome;
        this.jogadorAtual = jogadorAtual;
    }

    public boolean isPerdeuTurno() {
        return perdeuTurno;
    }

    public boolean isStand() {
        return stand;
    }

    public void setMao(List<Card> mao) {
        this.mao = mao;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public void setJogadorAtual(boolean jogadorAtual) {
        this.jogadorAtual = jogadorAtual;
    }


    public boolean isJogadorAtual() {
        return jogadorAtual;
    }

    public void setPontuacao(int pontuacao) {
        this.pontuacao = pontuacao;
    }

    public int calcularPontuacao() {
        int pontos = 0;
        int ases = 0;

        for (Card carta : mao) {
            int valor = carta.getValores()[0];
            if (valor == 1) {
                ases++;
                pontos += 11;  // Inicialmente considera o Ás como 11
            } else {
                pontos += Math.min(valor, 10);  // Cartas com valor maior que 10 valem 10
            }
        }
        // Ajuste dos ases
        while (pontos > 21 && ases > 0) {
            pontos -= 10;  // Cada Ás que passa de 11 para 1
            ases--;
        }
        this.pontuacao = pontos;
        return pontos;
    }



    public List<Card> getMao() {
        return mao;
    }

    public boolean getStand() {
        return !stand;
    }

    public boolean getPerdeuTurno() {
        return !perdeuTurno;
    }

    public void encerrarMao(){
        stand  = true;
    }

    public String getNome() {
        return nome;
    }

    public void adicionarCarta(Card carta) {
        mao.add(carta);
        calcularPontuacao();
    }

    public void setPerdeuTurno() {
        this.perdeuTurno = true;
        this.stand = true;
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
