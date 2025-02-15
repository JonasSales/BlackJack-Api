package com.example.demo.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Game {
    private LinkedList<Player> jogadores = new LinkedList<>();
    private Iterator<Player> iterador;

    public void iniciarJogo(List<String> nomes) {
        List<Card> baralho = Card.criarBaralho(1);
        Deck deck = new Deck(baralho);
        deck.embaralhar();

        for (String nome : nomes) {
            jogadores.add(new Player(nome));
        }

        // Adiciona o crupiê
        jogadores.add(new Player("Crupiê"));

        // Inicializa o iterador circular
        iterador = jogadores.iterator();
    }

    public Player proximoJogador() {
        if (!iterador.hasNext()) {
            iterador = jogadores.iterator(); // Reinicia quando chega ao fim
        }
        return iterador.next();
    }
}
