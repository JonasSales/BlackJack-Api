package com.example.demo.blackjack.model;

import com.example.demo.auth.dto.UserDTO;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Player {

    private UserDTO user;
    private List<Card> mao;
    private boolean perdeuTurno;
    private boolean stand;
    private boolean jogadorAtual;
    private int pontuacao;
    private boolean jogandoAtualmente;

    // Construtor padrão
    public Player() {
        this.mao = new ArrayList<>();
        this.perdeuTurno = false;
        this.stand = false;
        this.jogadorAtual = false;
        this.pontuacao = 0;
        this.jogandoAtualmente = false;
    }

    // Construtor com UserDTO
    public Player(UserDTO userDTO) {
        this();
        this.user = userDTO;
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

    public void encerrarMao() {
        stand = true;
    }

    public void adicionarCarta(Card carta) {
        mao.add(carta);
        calcularPontuacao();
    }

    public void setPerdeuTurno() {
        this.perdeuTurno = true;
        this.stand = true;
    }

    // Método para resetar o jogador criando um novo objeto com o mesmo userDTO
    public void resetar() {
        this.mao.clear();  // Limpa as cartas do jogador
        this.pontuacao = 0;    // Zera a pontuação
        this.perdeuTurno = false;  // Reinicia o status de "perdeu o turno"
        this.stand = false;        // Reinicia o status de "stand"
        this.jogadorAtual = false; // Define o jogador como não sendo o atual
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Jogador: ").append(user.getName()).append("\n");
        sb.append("Cartas: \n");
        for (Card carta : mao) {
            sb.append(carta).append("\n");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(this.user.getId(), player.getUser().getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUser().getId());
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public List<Card> getMao() {
        return mao;
    }

    public void setMao(List<Card> mao) {
        this.mao = mao;
    }

    public boolean isPerdeuTurno() {
        return perdeuTurno;
    }

    public void setPerdeuTurno(boolean perdeuTurno) {
        this.perdeuTurno = perdeuTurno;
    }

    public boolean isStand() {
        return stand;
    }

    public void setStand(boolean stand) {
        this.stand = stand;
    }

    public boolean isJogadorAtual() {
        return jogadorAtual;
    }

    public void setJogadorAtual(boolean jogadorAtual) {
        this.jogadorAtual = jogadorAtual;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(int pontuacao) {
        this.pontuacao = pontuacao;
    }

    public boolean isJogandoAtualmente() {
        return jogandoAtualmente;
    }

    public void setJogandoAtualmente(boolean jogandoAtualmente) {
        this.jogandoAtualmente = jogandoAtualmente;
    }

}
