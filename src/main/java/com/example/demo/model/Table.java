package com.example.demo.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Table {
    private  LinkedList<Player> jogadores;
    private  Deck deck;
    private boolean jogoIniciado;
    private Iterator<Player> iterador;

    public Table() {
        this.jogadores = new LinkedList<>();
        adicionarJogador(new Player("Crupîe"));
        this.deck = new Deck(Card.criarBaralho(2));
        this.jogoIniciado = false;
    }


    public void setJogadores(LinkedList<Player> jogadores) {
        this.jogadores = jogadores;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public void setJogoIniciado(boolean jogoIniciado) {
        this.jogoIniciado = jogoIniciado;
    }

    public Iterator<Player> getIterador() {
        return iterador;
    }

    public void setIterador(Iterator<Player> iterador) {
        this.iterador = iterador;
    }

    public List<Player> getJogadores() {
        return jogadores;
    }

    public boolean adicionarJogador(Player jogador) {
        if (!jogoIniciado) {
            jogadores.add(jogador);
            return true;
        }
        return false;
    }

    public Player encontrarJogador(String name){
        for (Player jogador : jogadores) {
            if (jogador.getNome().equals(name)) {
                return jogador;
            }
        }
        return null;
    }

    public Player encontrarJogadorAtual(){
        for (Player jogador : jogadores) {
            if (jogador.isJogadorAtual()){
                return jogador;
            }
        }
        return null;
    }

    public boolean isJogoIniciado() {
        return jogoIniciado;
    }

    public void iniciarJogo() {
        if (!jogadores.isEmpty()) {
            setJogoIniciado(true);
            deck.embaralhar();
            iterador = jogadores.iterator();
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
            Player jogadorAtual = iterador.next();
            if (jogadorAtual.getPerdeuTurno() && jogadorAtual.getStand()) {
                jogadorAtual.setJogadorAtual(true);
                return jogadorAtual;
            }
        }
        return null;
    }

    public void eliminarJogador(String nome) {
        Player jogador = encontrarJogador(nome);
        if (jogador != null) {
            jogador.setPerdeuTurno();
        }
    }

    public void encerrarMao(String name){
        Player jogador = encontrarJogador(name);
        if (jogador != null) {
            jogador.encerrarMao();
        }
    }

    public boolean todosJogadoresEncerraramMao() {
        for (Player jogador : jogadores) {
            if (jogador.getStand()) {  // Verifique se o jogador não encerrou a mão
                return true;
            }
        }
        return false;
    }


}
