package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private String nome;
    private List<Card> mao = new ArrayList<>();
    private boolean perdeuTurno = false;
    private boolean stand = false;
    private int pontuacao = 0;

    public Player(String nome, List<Card> mao, boolean perdeuTurno, boolean stand, int pontuacao) {
        this.nome = nome;
        this.mao = mao;
        this.perdeuTurno = perdeuTurno;
        this.stand = stand;
        this.pontuacao = pontuacao;
    }

    public Player(String nome) {
        this.nome = nome;
    }

    public Player() {

    }


    public int calcularPontuacao() {
        int pontos = 0;
        int ases = 0;

        // Contagem dos valores das cartas e identificação dos ases
        for (Card carta : mao) {
            int valor = carta.getValores()[0];
            if (valor == 1) {
                ases++;
                pontos += 11;  // Inicialmente consideramos o Ás como 11
            } else {
                pontos += Math.min(valor, 10);  // Cartas de valor maior que 10 valem 10
            }
        }
        // Ajuste para múltiplos ases: se a pontuação ultrapassar 21, transformamos ases de 11 para 1
        while (pontos > 21 && ases > 0) {
            pontos -= 10;  // Cada Ás que passa de 11 para 1 reduz 10 pontos
            ases--;
        }
        // Atribuição da pontuação final
        this.pontuacao = pontos;
        return pontos;
    }



    public List<Card> getMao() {
        return mao;
    }

    public boolean getStand() {
        return stand;
    }

    public boolean getPerdeuTurno() {
        return perdeuTurno;
    }



    public void setPerdeuTurno(boolean perdeuTurno) {
        this.perdeuTurno = perdeuTurno;
    }


    public void setStand(boolean stand) {
        this.stand = stand;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(int pontuacao) {
        this.pontuacao = pontuacao;
    }

    public void encerrarMao(){
        stand  = true;
    }

    public String getNome() {
        return nome;
    }


    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setMao(List<Card> mao) {
        this.mao = mao;
    }

    public void adicionarCarta(Card carta) {
        mao.add(carta);
    }

    public void removerCarta(Card carta) {
        mao.remove(carta);
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
