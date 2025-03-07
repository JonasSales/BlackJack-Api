package com.example.demo.blackjack.model;

import com.example.demo.auth.dto.UserDTO;
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
    private UserDTO vencedor;

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
        this.vencedor = new UserDTO();
        adicionarCrupie();
        iniciarJogo();
    }

    // Métodos relacionados aos jogadores
    public void adicionarJogador(Player jogador) {
        if (!jogoIniciado) {
            jogadores.addFirst(jogador);
        }
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


    public void iniciarJogo() {
        if (jogadores.getSize() > 1) {
            setJogoIniciado(true);
        }
    }

    public void proximoJogador() {
        if (!jogoIniciado || jogadores.isEmpty()) {
            return;
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
                return;
            }
            jogadorAnterior = jogador;
        }
    }

    public boolean todosJogadoresEncerraramMao() {
        if (jogadores.isEmpty()) {
            return false;
        }
        for (Player jogador : getJogadores()) {
            if (jogador instanceof Crupie) {
                continue;
            }
            if (!jogador.isStand()) {
                return false;
            }
        }
        return true;
    }

    public void resetarMesa() {
        this.deck = new Deck(Card.criarBaralho(2));
        this.jogoIniciado = false;
        this.jogadorAtual = null;
        this.jogadores = new ListaDuplamenteEncadeada<>();
        setTempoInicioContador();
        adicionarCrupie();
    }

    public void distribuirCartasIniciais() {
        ArrayList<Player> jogadores = getJogadores();
        Deck deck = getDeck();
        for (Player jogador : jogadores) {
            jogador.adicionarCarta(deck.distribuirCarta());
            jogador.adicionarCarta(deck.distribuirCarta());
            jogador.calcularPontuacao();
        }
    }

    public void definirPrimeiroJogador() {
        ArrayList<Player> jogadores = getJogadores();
        Player primeiroJogador = jogadores.getFirst();
        primeiroJogador.setJogadorAtual(true);
        setJogadorAtual(primeiroJogador);
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
        this.tempoInicioContador = System.currentTimeMillis();
    }

    public long getTempoDecorrido() {
        return System.currentTimeMillis() - tempoInicioContador;
    }

    public UserDTO getVencedor() {
        return vencedor;
    }

    public void setVencedor(UserDTO vencedor) {
        this.vencedor = vencedor;
    }

    public Player determinarVencedor() {
        if (jogadores.isEmpty()) {
            return null; // Retorna null se não houver jogadores
        }

        Player vencedor = null;
        int maiorPontuacao = Integer.MIN_VALUE;

        ListaDuplamenteEncadeada<Player>.Nodo atual = jogadores.getHead();

        while (atual != null) {
            Player jogador = (Player) atual.getData();

            if (!jogador.isPerdeuTurno() && jogador.getPontuacao() > maiorPontuacao) {
                maiorPontuacao = jogador.getPontuacao();
                vencedor = jogador;
            }
            atual = atual.getNext();
        }

        return vencedor; // Retorna o jogador com maior pontuação que não perdeu
    }

    private void adicionarCrupie(){
        Crupie crupie = new Crupie();
        jogadores.addLast(crupie);
    }
}