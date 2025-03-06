package com.example.demo.blackjack.model;



import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class Deck {

    private LinkedList<Card> cards;

    public Deck(List<Card> baralho) {
        this.cards = new LinkedList<>(baralho);
        embaralhar();
    }

    private void embaralhar() {
        Collections.shuffle(cards);
    }

    public Card distribuirCarta() {
        if (!cards.isEmpty()) {
            return cards.removeFirst();  // Remove a carta do início da lista
        }
        return null;
    }

    public int tamanho() {
        return cards.size();
    }


    public LinkedList<Card> getCards() {
        return cards;
    }

    public void setCards(LinkedList<Card> cartas) {
        this.cards = cartas;
    }

    @Override
    public String toString() {
        return "Deck contém " + tamanho() + " cartas.";
    }


}
