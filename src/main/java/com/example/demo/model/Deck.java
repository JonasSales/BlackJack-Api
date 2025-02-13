package com.example.demo.model;

import java.util.Collections;
import java.util.List;

public record Deck(List<Card> cartas) {

    // Método para embaralhar as cartas
    public void embaralhar() {
        Collections.shuffle(cartas);
    }

    // Método para distribuir uma carta (remover a carta do deck)
    public Card distribuirCarta() {
        if (!cartas.isEmpty()) {
            return cartas.removeFirst();  // Retira a primeira carta do deck
        }
        return null;  // Retorna null se o deck estiver vazio
    }

    // Método para ver quantas cartas restam no deck
    public int tamanho() {
        return cartas.size();
    }

    // Método para reverter as cartas de volta para o deck
    public void retornarCarta(Card carta) {
        cartas.add(carta);
    }

    // Método toString para exibir o deck
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Card carta : cartas) {
            sb.append(carta.toString()).append("\n");
        }
        return sb.toString();
    }
}
