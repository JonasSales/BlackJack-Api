package com.example.demo.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Table {
    private LinkedList<Player> jogadores;
    private Deck deck;
    private boolean jogoIniciado;
    private Iterator<Player> iterador;
    private Player jogadorAtual;

    public Table() {
        this.jogadores = new LinkedList<>();
        this.deck = new Deck(Card.criarBaralho(2));
        this.jogoIniciado = false;
    }



    public List<Player> getJogadores() {
        return jogadores;
    }

    public boolean adicionarJogador(Player jogador) {
        if (!jogoIniciado) {
            //System.out.println(jogador.getNome());
            jogadores.add(jogador);
            return true;
        }
        return false;
    }

    public boolean isJogoIniciado() {
        return jogoIniciado;
    }

    public void iniciarJogo() {
        if (!jogadores.isEmpty()) {
            jogoIniciado = true;
            deck.embaralhar();
            iterador = jogadores.iterator();
            jogadorAtual = proximoJogador(); // Define o primeiro jogador
        }
    }

    public Deck getDeck() {
        return deck;
    }

    public Player proximoJogador() {
        if (!jogoIniciado) {
            return null;
        }

        if (!iterador.hasNext()) {
            iterador = jogadores.iterator(); // Reinicia a iteração
        }

        while (iterador.hasNext()) {
            jogadorAtual = iterador.next();
            if (!jogadorAtual.isPerdeuTurno()) {
                return jogadorAtual;
            }
        }
        return null;
    }

    public void eliminarJogador(String nome) {
        for (Player jogador : jogadores) {
            if (jogador.getNome().equals(nome)) {
                jogador.setPerdeuTurno(true);
                break;
            }
        }
    }

    public Player getJogadorAtual() {
        return jogadorAtual;
    }
}
