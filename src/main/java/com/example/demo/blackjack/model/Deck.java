package com.example.demo.blackjack.model;



import com.example.demo.blackjack.utils.Pilha;


import java.util.List;


public class Deck {

    private Pilha<Card> cards;

    public Deck(List<Card> baralho) {
        this.cards = new Pilha<>(baralho);
    }

    public Card distribuirCarta() {
        if (!cards.isEmpty()) {
            return (Card) cards.pop();
        }
        return null;
    }

    public int tamanho() {
        return cards.getSize();
    }


    public Pilha<Card> getCards() {
        return cards;
    }

    public void setCards(Pilha<Card> cartas) {
        this.cards = cartas;
    }

    @Override
    public String toString() {
        return "Deck contém " + tamanho() + " cartas.";
    }


}
