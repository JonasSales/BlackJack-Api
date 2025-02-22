package com.example.demo.blackjack.model;

import com.example.demo.auth.dto.UserDTO;
import com.example.demo.blackjack.utils.ListaDuplamenteEncadeada;
import lombok.Getter;
import lombok.Setter;


import java.util.List;

@Setter
@Getter
public class Table {
    private ListaDuplamenteEncadeada<Player> jogadores;
    private Deck deck;
    private boolean jogoIniciado;
    private Player jogadorAtual;

    public Table() {
        this.jogadores = new ListaDuplamenteEncadeada<>();

        UserDTO userDTO = new UserDTO("Crupîe");

        adicionarJogador(new Player(userDTO));
        this.deck = new Deck(Card.criarBaralho(2));
        this.jogoIniciado = false;
        this.jogadorAtual = null;
    }


    public Object[] getJogadores() {
        return jogadores.retornArrayData();
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
                    jogadorAnterior.setJogadorAtual(false); // Define o jogador anterior como false
                }
                // Define o novo jogador como atual
                jogador.setJogadorAtual(true);
                jogadorAtual = jogador; // Atualiza o jogador atual
                return jogadorAtual; // Retorna o próximo jogador válido
            }
            // Atualiza o jogador anterior para o próximo jogador
            jogadorAnterior = jogador;
        }
        // Caso não tenha encontrado um jogador válido
        return null;
    }



    public void eliminarJogador(Player player) {
        Player jogador = encontrarJogador(player);
        if (jogador != null) {
            jogador.setPerdeuTurno();
        }
    }

    public void encerrarMao(Player player) {
        // Encontra o jogador na lista
        Player jogadorNodo = encontrarJogador(player);
        // Verifica se o jogador foi encontrado
        if (jogadorNodo != null) {
            jogadorNodo.encerrarMao();
        }
    }


    public boolean todosJogadoresEncerraramMao() {
        for (Object obj : getJogadores()) {
            Player jogador = (Player) obj;  // Cast para Jogador
            if (!jogador.isStand()) {  // Verifique se o jogador não encerrou a mão
                return false;
            }
        }
        return true;
    }
}
