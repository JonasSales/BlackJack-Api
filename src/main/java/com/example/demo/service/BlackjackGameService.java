package com.example.demo.service;

import com.example.demo.model.Card;
import com.example.demo.model.Deck;
import com.example.demo.model.Player;
import com.example.demo.repository.BlackJackRepository;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Service
public class BlackjackGameService implements BlackJackRepository {

    private final LinkedList<Player> jogadores;
    private final Deck deck;
    private Iterator<Player> iterador;

    public BlackjackGameService() {
        // Inicializa a lista de jogadores e o deck
        this.jogadores = new LinkedList<>();
        this.deck = new Deck(Card.criarBaralho(1));
    }


    public void iniciarJogo(List<String> nomes) {
        deck.embaralhar();
        // Adiciona o crupiê
        Player crupie = new Player("Crupiê");
        jogadores.add(crupie);

        // Adiciona os jogadores
        for (String nome : nomes) {
            Player jogador = new Player(nome);
            jogadores.add(jogador);
        }
        // Inicializa o iterador circular
        iterador = jogadores.iterator();
        distribuirCartas();
    }

    public void distribuirCartas() {
        for (Player jogador : jogadores) {
            jogador.adicionarCarta(deck.distribuirCarta());
            jogador.adicionarCarta(deck.distribuirCarta());
        }
    }

    public List<Player> getJogadores() {
        return jogadores;
    }

    public boolean comprarCarta(String nome) {
        Player jogador = encontrarJogador(nome);
        if (jogador != null && !jogador.isPerdeuTurno() && calcularPontuacao(jogador) <= 21) {
            jogador.adicionarCarta(deck.distribuirCarta());
            return true;
        }
        return false;
    }

    public String finalizarJogo() {
        int maiorPontuacao = 0;
        Player vencedor = null;
        Player crupie = jogadores.getFirst(); // O primeiro jogador é o crupiê

        // Verifica se algum jogador tem Blackjack
        for (Player jogador : jogadores) {
            if (!jogador.isPerdeuTurno() && calcularPontuacao(jogador) == 21 && jogador.getMao().size() == 2) {
                vencedor = jogador;
                return "Jogador " + vencedor.getNome() + " venceu com Blackjack!";
            }
        }

        // Verifica o vencedor baseado na maior pontuação
        for (Player jogador : jogadores) {
            if (!jogador.isPerdeuTurno()) {
                int pontuacao = calcularPontuacao(jogador);
                if (pontuacao <= 21 && pontuacao > maiorPontuacao) {
                    maiorPontuacao = pontuacao;
                    vencedor = jogador;
                } else if (pontuacao == maiorPontuacao) {
                    vencedor = null; // Empate
                }
            }
        }

        // Verifica se o crupiê é o vencedor
        int pontuacaoCrupie = calcularPontuacao(crupie);
        if (pontuacaoCrupie <= 21 && pontuacaoCrupie > maiorPontuacao) {
            return "O vencedor é o Crupiê com " + pontuacaoCrupie + " pontos!";
        }


        return vencedor != null ? "O vencedor é: " + vencedor.getNome() + " com " + maiorPontuacao + " pontos!" : "O jogo terminou em empate (push).";
    }

    public Player proximoJogador() {
        Player jogador = null;
        while (jogador == null || jogador.isPerdeuTurno()) {
            if (!iterador.hasNext()) {
                iterador = jogadores.iterator();
            }
            jogador = iterador.next();
        }
        return jogador;
    }

    public void eliminarJogador(String nome) {
        Player jogador = encontrarJogador(nome);
        assert jogador != null;
        jogador.setPerdeuTurno(true);
    }

    private Player encontrarJogador(String nome) {
        for (Player jogador : jogadores) {
            if (jogador.getNome().equals(nome)) {
                return jogador;
            }
        }
        return null;
    }



    public int calcularPontuacao(Player jogador) {
        int pontos = 0;
        int ases = 0;

        for (Card carta : jogador.getMao()) {
            int[] valores = carta.getValores();
            for (int valor : valores) {
                pontos += valor;
                if (valor == 1) {
                    ases++;
                }
            }
        }

        while (pontos <= 11 && ases > 0) {
            pontos += 10;
            ases--;
        }

        return pontos;
    }
}
