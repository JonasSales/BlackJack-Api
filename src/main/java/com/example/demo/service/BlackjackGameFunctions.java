package com.example.demo.service;

import com.example.demo.model.Card;
import com.example.demo.model.Deck;
import com.example.demo.model.GameFunctions;
import com.example.demo.model.Player;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlackjackGameFunctions implements GameFunctions {

    private List<Player> jogadores = new ArrayList<>();
    private Deck deck;
    private Map<String, Player> mapaJogadores = new HashMap<>();


    public List<Player> getJogadores() {
        return jogadores;
    }


    private Deck getDeck() {
        return deck;
    }


    @Override
    public void iniciarJogo(List<String> nomes) {
        List<Card> baralho = Card.criarBaralho(1);
        deck = new Deck(baralho);
        deck.embaralhar();
        // Adiciona jogadores ao mapa e à lista
        for (String nome : nomes) {
            Player jogador = new Player(nome);
            jogadores.add(jogador);
            mapaJogadores.put(nome, jogador);
        }

        // Adiciona o crupiê
        Player crupie = new Player("Crupiê");
        jogadores.add(crupie);
        mapaJogadores.put("Crupiê", crupie);
    }

    @Override
    public void distribuirCartas() {
        for (Player jogador : jogadores) {
            jogador.adicionarCarta(deck.distribuirCarta());
            jogador.adicionarCarta(deck.distribuirCarta());
        }
    }

    @Override
    public boolean comprarCarta(String nome) {
        Player jogador = mapaJogadores.get(nome);
        if (jogador != null && calcularPontuacao(jogador) <= 21) {
            jogador.adicionarCarta(deck.distribuirCarta());
            return true;
        }
        return false;
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
