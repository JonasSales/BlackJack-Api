package com.example.demo.service;

import com.example.demo.model.Card;
import com.example.demo.model.Deck;
import com.example.demo.model.Game;
import com.example.demo.model.Player;

import java.util.ArrayList;
import java.util.List;

public class BlackjackGame implements Game {

    private List<Player> jogadores;
    private Deck deck;

    public List<Player> getJogadores() {
        return jogadores;
    }

    public void setJogadores(List<Player> jogadores) {
        this.jogadores = jogadores;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    @Override
    public void iniciarJogo(List<String> nomes) {
        List<Card> baralho = Card.criarBaralho(1);
        deck = new Deck(baralho);
        deck.embaralhar();

        jogadores = new ArrayList<>();
        // Adiciona jogadores à lista
        for (String nome : nomes) {
            jogadores.add(new Player(nome));
        }
        // Adiciona o crupiê à lista de jogadores
        jogadores.add(new Player("Crupiê"));
    }

    @Override
    public void distribuirCartas() {
        for (Player jogador : jogadores) {
            jogador.adicionarCarta(deck.distribuirCarta());
            jogador.adicionarCarta(deck.distribuirCarta());
        }
    }

    @Override
    public void comprarCarta(String nome) {
        for (Player jogador : jogadores) {
            if (jogador.getNome().equals(nome)) {
                jogador.adicionarCarta(deck.distribuirCarta());
            }
        }
    }


    public List<String> obterPontuacoes(List<Player> jogadores) {
        List<String> pontuacoes = new ArrayList<>();
        for (Player jogador : jogadores) {
            int pontos = calcularPontuacao(jogador); // Calcula a pontuação do jogador
            pontuacoes.add(jogador.getNome() + ": " + pontos); // Adiciona a pontuação à lista de respostas
        }
        return pontuacoes; // Retorna a lista de pontuações de todos os jogadores
    }

    @Override
    public String finalizarJogo() {
        int maiorPontuacao = 0;
        Player vencedor = null; // Inicializa com null para o caso de empate
        boolean algumBlackjack = false;
        Player crupie = jogadores.getLast();

        // Verifica se algum jogador tem Blackjack
        for (Player jogador : jogadores) {
            if (calcularPontuacao(jogador) == 21 && jogador.getMao().size() == 2) {
                if (algumBlackjack) {
                    vencedor = null; // Empate se houver mais de um Blackjack
                } else {
                    vencedor = jogador; // Primeiro jogador com Blackjack é o vencedor
                    return "Jogador " + vencedor.getNome() + " venceu com Blackjack!";
                }
                algumBlackjack = true;
            }
        }

        // O crupiê compra cartas até ter pelo menos 17 pontos
        while (calcularPontuacao(crupie) < 17) {
            crupie.adicionarCarta(deck.distribuirCarta());
        }

        // Verifica o vencedor baseado na maior pontuação
        for (Player jogador : jogadores) {
            int pontuacao = calcularPontuacao(jogador);
            if (pontuacao <= 21 && pontuacao > maiorPontuacao) {
                maiorPontuacao = pontuacao;
                vencedor = jogador;
            } else if (pontuacao == maiorPontuacao) {
                vencedor = null; // Empate se pontuação for igual
            }
        }

        // Verifica se o crupiê é o vencedor
        int pontuacaoCrupie = calcularPontuacao(crupie);
        if (pontuacaoCrupie <= 21 && pontuacaoCrupie > maiorPontuacao) {
            return "O vencedor é o Crupiê com " + pontuacaoCrupie + " pontos!";
        }

        // Retorna o resultado
        return vencedor != null ? "O vencedor é: " + vencedor.getNome() + " com " + maiorPontuacao + " pontos!" : "O jogo terminou em empate (push).";
    }


    // Método para calcular a pontuação de um jogador no Blackjack
    public int calcularPontuacao(Player jogador) {
        int pontos = 0;
        int ases = 0;

        // Soma os pontos das cartas
        for (Card carta : jogador.getMao()) {
            int[] valores = carta.getValores();
            for (int valor : valores) {
                pontos += valor;
                if (valor == 1) {
                    ases++;
                }
            }
        }

        // Ajusta a pontuação se houver ases (um ÁS pode valer 1 ou 11)
        while (pontos <= 11 && ases > 0) {
            pontos += 10; // Conta um ÁS como 11, se não ultrapassar 21
            ases--;
        }

        return pontos;
    }
}
