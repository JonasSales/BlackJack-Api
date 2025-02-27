package com.example.demo.blackjack.model;

import com.example.demo.auth.service.AuthenticationService;
import com.example.demo.blackjack.utils.ListaDuplamenteEncadeada;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@Builder
@Component // Adiciona a anotação @Component para que o Spring gerencie essa classe
public class Table {

    private UUID id;
    private ListaDuplamenteEncadeada<Player> jogadores;
    private Deck deck;
    private boolean jogoIniciado;
    private Player jogadorAtual;
    private String token;


    private final AuthenticationService authenticationService;



    @Autowired // Injeta o AuthenticationService
    public Table(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
        this.id = UUID.randomUUID(); // Gera um ID único para o jogo
        this.jogadores = new ListaDuplamenteEncadeada<>(); // Inicializa a lista de jogadores
        this.deck = new Deck(Card.criarBaralho(2)); // Inicializa o deck
        this.jogoIniciado = false; // O jogo não foi iniciado ainda
        this.jogadorAtual = null; // Nenhum jogador foi definido como atual
        this.token = authenticationService.generateToken(this.id.toString()); // Gera o token da mesa
    }

    public ArrayList<Player> getJogadores() {
        Object[] objetos = jogadores.retornArrayData();
        ArrayList<Player> jogadoresList = new ArrayList<>();

        // Verifica se o array é nulo
        if (objetos == null) {
            return jogadoresList; // Retorna uma lista vazia
        }
        // Itera sobre o array
        for (Object obj : objetos) {
            if (obj instanceof Player) {
                jogadoresList.add((Player) obj);  // Realiza o cast seguro
            } else {
                // Trata casos onde o objeto não é do tipo Player
                System.out.println("Objeto não é um Player: " + obj);
            }
        }
        return jogadoresList;
    }

    public boolean adicionarJogador(Player jogador) {
        if (!jogoIniciado) {
            jogadores.addLast(jogador);
            return true;
        }
        return false;
    }

    public Player encontrarJogador(Player jogador) {
        ListaDuplamenteEncadeada<Player>.Nodo jogadorAchado = jogadores.searchNodo(jogador);

        // Verifica se o jogador foi encontrado no Nodo e retorna o jogador
        if (jogadorAchado != null) {
            return (Player) jogadorAchado.getData(); // Retorna o objeto Player encontrado
        }
        return null; // Retorna null caso o jogador não seja encontrado
    }

    public void iniciarJogo() {
        if (!jogadores.isEmpty()) {
            setJogoIniciado(true);
            deck.embaralhar();
            jogadorAtual = jogadores.PeekFirst(); // Corrigido para fazer o cast corretamente
        }
    }

    public Player proximoJogador() {
        if (!jogoIniciado || jogadores.isEmpty()) {
            return null;
        }

        // Verifica se jogadorAtual é nulo, caso seja, define o primeiro jogador como atual
        if (jogadorAtual == null) {
            jogadorAtual = jogadores.PeekFirst();
        }

        // Obtém todos os nodos de uma vez
        List<ListaDuplamenteEncadeada<Player>.Nodo> nodos = jogadores.getAllNodos();
        // Variável para armazenar o jogador anterior
        Player jogadorAnterior = null;

        // Itera pelos nodos para encontrar o próximo jogador válido
        for (ListaDuplamenteEncadeada<Player>.Nodo nodo : nodos) {
            Player jogador = (Player) nodo.getData(); // Acessa o jogador no nodo atual

            // Verifica se o jogador não perdeu o turno e se não deu stand
            if (!jogador.isPerdeuTurno() && !jogador.isStand()) {

                // Atualiza o estado do jogador anterior
                if (jogadorAnterior != null) {
                    jogadorAnterior.setJogadorAtual(false);
                }

                // Define o novo jogador como atual
                jogador.setJogadorAtual(true);
                jogadorAtual = jogador;
                return jogadorAtual;
            }
            // Atualiza o jogador anterior para o próximo jogador
            jogadorAnterior = jogador;
        }

        // Caso não tenha encontrado um jogador válido
        return null;
    }

    public boolean todosJogadoresEncerraramMao() {
        if (jogadores.isEmpty()) {
            return false;
        }
        for (Player jogador : getJogadores()) {
            if (!jogador.isStand()) {  // Verifique se o jogador não encerrou a mão
                return false;
            }
        }
        return true;
    }

    public boolean getJogoIniciado() {
        return jogoIniciado;
    }
}