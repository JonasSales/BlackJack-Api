package com.example.demo.blackjack.model;

import com.example.demo.auth.dto.UserDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter

public class Player {

    private UserDTO user;
    private List<Card> mao = new ArrayList<>();
    private boolean perdeuTurno = false;
    private boolean stand = false;
    private boolean jogadorAtual = false;
    private int pontuacao = 0;


    public Player() {

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


    public String getName(){
        return user.getName();
    }

    public void encerrarMao(){
        stand  = true;
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
        sb.append("Jogador: ").append(user.getName()).append("\n");
        sb.append("Cartas: \n");
        for (Card carta : mao) {
            sb.append(carta).append("\n");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Player other = (Player) obj;
        return Objects.equals(this.user.getName().trim().toLowerCase(), other.user.getName().trim().toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(user.getName());  // Definindo um critério consistente para hashCode
    }

}
