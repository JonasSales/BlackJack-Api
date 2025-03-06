package com.example.demo.blackjack.domain.service;

import com.example.demo.blackjack.model.Card;
import com.example.demo.blackjack.model.Deck;
import com.example.demo.blackjack.model.Player;
import com.example.demo.blackjack.model.Table;
import com.example.demo.blackjack.domain.repository.BlackJackRepository;

import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class BlackjackGameService implements BlackJackRepository {

    private Map<UUID, Table> mesas;  // Mapa de mesas com UUID como chave

    public BlackjackGameService() {
        this.mesas = new HashMap<>(); // Inicializa o mapa de mesas
    }

    public void iniciarJogo(Table mesa) {
        if (!mesa.isJogoIniciado()) {
            distribuirCartasIniciais(mesa);
            definirPrimeiroJogador(mesa);
            mesa.iniciarJogo();
        }
    }

    private void distribuirCartasIniciais(Table mesa) {
        ArrayList<Player> jogadores = mesa.getJogadores();
        Deck deck = mesa.getDeck();
        for (Player jogador : jogadores) {
            jogador.adicionarCarta(deck.distribuirCarta());
            jogador.adicionarCarta(deck.distribuirCarta());
            jogador.calcularPontuacao();
        }
    }


    private void definirPrimeiroJogador(Table mesa) {
        ArrayList<Player> jogadores = mesa.getJogadores();
        Player primeiroJogador = jogadores.getFirst();
        primeiroJogador.setJogadorAtual(true);
    }

    @Override
    public boolean comprarCarta(Player jogador, Table mesa) {
        if (mesa == null) {
            return false;
        }

        Player jogadorNovo = encontrarJogadorNaMesa(jogador, mesa);
        if (jogadorNovo == null) {
            return false;
        }

        Card carta = distribuirCartaDoDeck(mesa);
        adicionarCartaAoJogador(jogadorNovo, carta);

        int pontuacaoJogador = calcularPontuacaoJogador(jogadorNovo);
        if (pontuacaoJogador > 21) {
            processarPerdaDoTurno(jogadorNovo, mesa);
            return false;
        }
        return true;
    }

    private Player encontrarJogadorNaMesa(Player jogador, Table mesa) {
        return mesa.encontrarJogador(jogador);
    }

    private Card distribuirCartaDoDeck(Table mesa) {
        return mesa.getDeck().distribuirCarta();
    }

    private void adicionarCartaAoJogador(Player jogador, Card carta) {
        jogador.adicionarCarta(carta);
    }

    private int calcularPontuacaoJogador(Player jogador) {
        return jogador.calcularPontuacao();
    }

    private void processarPerdaDoTurno(Player jogador, Table mesa) {
        jogador.setPerdeuTurno();
        mesa.proximoJogador();
    }

    @Override
    public Player finalizarJogo(Table mesa) {
        if (mesa == null) {
            return null; // Retorna null se a mesa não for válida
        }

        List<Player> jogadoresAtivos = obterJogadoresAtivos(mesa);

        if (jogadoresAtivos.isEmpty()) {
            resetarMesa(mesa);
            return null; // Retorna null se não houver jogadores ativos
        }

        return encontrarVencedor(jogadoresAtivos); // Retorna o jogador vencedor
    }

    private List<Player> obterJogadoresAtivos(Table mesa) {
        return mesa.getJogadores().stream()
                .filter(jogador -> !jogador.isPerdeuTurno()) // Filtra jogadores que não perderam o turno
                .toList(); // Coleta os jogadores ativos em uma lista
    }

    private void resetarMesa(Table mesa) {
        mesa.resetarMesa(); // Reseta a mesa e os jogadores
    }

    private Player encontrarVencedor(List<Player> jogadoresAtivos) {
        return jogadoresAtivos.stream()
                .max(Comparator.comparingInt(Player::calcularPontuacao))
                .orElse(null); // Retorna o jogador vencedor
    }

    public boolean encerrarMao(Player jogador, Table mesa) {
        if (mesa == null) {
            return false;
        }

        jogador = encontrarJogadorNaMesa(jogador, mesa);
        if (jogador == null) {
            return false;
        }
        processarEncerramentoMao(jogador, mesa);
        return true;
    }


    private void processarEncerramentoMao(Player jogador, Table mesa) {
        jogador.encerrarMao(); // Encerra a mão do jogador
        jogador.setJogadorAtual(false); // Marca o jogador como não atual
        mesa.proximoJogador(); // Avança para o próximo jogador
    }


    public boolean verificarTodosEncerraram(Table mesa) {
        if (mesa != null) {
            return mesa.todosJogadoresEncerraramMao();
        }
        return false;
    }

    public boolean jogada(Player player, String jogada, Table mesa) {
        if (!validarMesaEJogador(mesa, player)) {
            return false; // Retorna false se a mesa ou o jogador não forem válidos
        }

        Player jogador = encontrarJogadorNaMesa(player, mesa);
        return processarJogada(jogador, jogada, mesa); // Processa a jogada e retorna o resultado
    }

    private boolean validarMesaEJogador(Table mesa, Player player) {
        return mesa != null && player != null; // Valida se a mesa e o jogador são válidos
    }


    private boolean processarJogada(Player jogador, String jogada, Table mesa) {
        if (jogada.equals("hit")) {
            return comprarCarta(jogador, mesa); // Processa a jogada "hit"
        } else if (jogada.equals("stand")) {
            return encerrarMao(jogador, mesa); // Processa a jogada "stand"
        }
        return false; // Retorna false se a jogada não for reconhecida
    }

    public Map<UUID, Table> getMesas() {
        return mesas;
    }

    public void setMesas(Map<UUID, Table> mesas) {
        this.mesas = mesas;
    }
}
