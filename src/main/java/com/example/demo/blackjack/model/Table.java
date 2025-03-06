package com.example.demo.blackjack.model;

import com.example.demo.auth.service.AuthenticationService;
import com.example.demo.blackjack.utils.ListaDuplamenteEncadeada;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class Table {

    private UUID id;
    private ListaDuplamenteEncadeada<Player> jogadores;
    private Deck deck;
    private boolean jogoIniciado;
    private Player jogadorAtual;
    private String token;
    private long tempoInicioContador;

    private final AuthenticationService authenticationService;

    @Autowired
    public Table(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
        this.id = UUID.randomUUID();
        this.jogadores = new ListaDuplamenteEncadeada<>();
        this.deck = new Deck(Card.criarBaralho(2));
        this.jogoIniciado = false;
        this.jogadorAtual = null;
        this.token = authenticationService.generateToken(this.id.toString());
        tempoInicioContador = System.currentTimeMillis();
        iniciarJogo();
    }

    // Métodos relacionados aos jogadores
    public boolean adicionarJogador(Player jogador) {
        if (!jogoIniciado) {
            jogadores.addLast(jogador);
            return true;
        }
        return false;
    }

    public Player encontrarJogador(Player jogador) {
        ListaDuplamenteEncadeada<Player>.Nodo jogadorAchado = jogadores.searchNodo(jogador);
        return jogadorAchado != null ? (Player) jogadorAchado.getData() : null;
    }

    public ArrayList<Player> getJogadores() {
        Object[] objetos = jogadores.retornArrayData();
        ArrayList<Player> jogadoresList = new ArrayList<>();

        if (objetos != null) {
            for (Object obj : objetos) {
                if (obj instanceof Player) {
                    jogadoresList.add((Player) obj);
                } else {
                    System.out.println("Objeto não é um Player: " + obj);
                }
            }
        }
        return jogadoresList;
    }

    // Métodos relacionados ao jogo
    public void iniciarJogo() {
        if (!jogadores.isEmpty()) {
            setJogoIniciado(true);
            deck.embaralhar();

            jogadorAtual = jogadores.PeekFirst();
        }
    }

    public Player proximoJogador() {
        if (!jogoIniciado || jogadores.isEmpty()) {
            return null;
        }

        if (jogadorAtual == null) {
            jogadorAtual = jogadores.PeekFirst();
        }

        List<ListaDuplamenteEncadeada<Player>.Nodo> nodos = jogadores.getAllNodos();
        Player jogadorAnterior = null;

        for (ListaDuplamenteEncadeada<Player>.Nodo nodo : nodos) {
            Player jogador = (Player) nodo.getData();

            if (!jogador.isPerdeuTurno() && !jogador.isStand()) {
                if (jogadorAnterior != null) {
                    jogadorAnterior.setJogadorAtual(false);
                }

                jogador.setJogadorAtual(true);
                jogadorAtual = jogador;
                return jogadorAtual;
            }
            jogadorAnterior = jogador;
        }

        return null;
    }

    public boolean todosJogadoresEncerraramMao() {
        if (jogadores.isEmpty()) {
            return false;
        }
        for (Player jogador : getJogadores()) {
            if (!jogador.isStand()) {
                return false;
            }
        }
        return true;
    }

    public void resetarMesa() {
        this.deck = new Deck(Card.criarBaralho(2));
        this.deck.embaralhar();
        this.jogoIniciado = false;
        this.jogadorAtual = null;
        this.jogadores = new ListaDuplamenteEncadeada<>();
    }

    // Getters e Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ListaDuplamenteEncadeada<Player> getJogadoresLista() {
        return jogadores;
    }

    public void setJogadores(ListaDuplamenteEncadeada<Player> jogadores) {
        this.jogadores = jogadores;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public boolean isJogoIniciado() {
        return jogoIniciado;
    }

    public void setJogoIniciado(boolean jogoIniciado) {
        this.jogoIniciado = jogoIniciado;
    }

    public Player getJogadorAtual() {
        return jogadorAtual;
    }

    public void setJogadorAtual(Player jogadorAtual) {
        this.jogadorAtual = jogadorAtual;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    public long getTempoInicioContador() {
        return tempoInicioContador;
    }

    public void setTempoInicioContador() {
        this.tempoInicioContador = System.currentTimeMillis();;
    }

    public long getTempoDecorrido() {
        return System.currentTimeMillis() - tempoInicioContador;
    }
}