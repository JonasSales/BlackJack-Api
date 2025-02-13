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
    private Player crupie;

    @Override
    public void iniciarJogo(List<String> nomes) {
        List<Card> baralho = Card.criarBaralho(1);
        deck = new Deck(baralho);
        deck.embaralhar();

        jogadores = new ArrayList<>();
        for (String nome : nomes) {
            jogadores.add(new Player(nome));
        }
        crupie = new Player("Crupiê");
    }

    @Override
    public void distribuirCartas() {
        for (Player jogador : jogadores) {
            jogador.adicionarCarta(deck.distribuirCarta());
            jogador.adicionarCarta(deck.distribuirCarta());
        }
        crupie.adicionarCarta(deck.distribuirCarta());
        crupie.adicionarCarta(deck.distribuirCarta());
    }

    @Override
    public void comprarCarta(String nome) {
        for (Player jogador : jogadores) {
            if (jogador.getNome().equals(nome)) {
                jogador.adicionarCarta(deck.distribuirCarta());
            }
        }
    }

    @Override
    public int obterPontuacao(String nome) {
        for (Player jogador : jogadores) {
            if (jogador.getNome().equals(nome)) {
                return calcularPontuacao(jogador); // Chama o método para calcular a pontuação
            }
        }
        return 0; // Retorna 0 se o jogador não for encontrado
    }

    @Override
    public String finalizarJogo() {
        int maiorPontuacao = 0;
        Player vencedor = null;
        boolean algumBlackjack = false;

        // Verifica se algum jogador tem Blackjack
        for (Player jogador : jogadores) {
            if (calcularPontuacao(jogador) == 21 && jogador.getMao().size() == 2) {
                algumBlackjack = true;
            }
        }

        // Verifica se algum jogador tem Blackjack e retorna o vencedor
        for (Player jogador : jogadores) {
            if (calcularPontuacao(jogador) == 21 && jogador.getMao().size() == 2) {
                if (!algumBlackjack) {
                    return "O vencedor é: " + jogador.getNome() + " com Blackjack!";
                }
                // Se mais de um jogador tem Blackjack, é empate (push)
                vencedor = null;
            }
        }

        // Se o crupiê não tem Blackjack, ele deve comprar cartas até atingir pelo menos 17 pontos
        while (calcularPontuacao(crupie) < 17) {
            crupie.adicionarCarta(deck.distribuirCarta());
        }

        // Determina o vencedor com base na pontuação final
        for (Player jogador : jogadores) {
            int pontuacao = calcularPontuacao(jogador);
            if (pontuacao > 21) {
                continue; // Jogador estourou, não pode ser vencedor
            }

            if (pontuacao > maiorPontuacao) {
                maiorPontuacao = pontuacao;
                vencedor = jogador;
            } else if (pontuacao == maiorPontuacao) {
                // Se ambos tiverem a mesma pontuação, é empate (push)
                vencedor = null; // Indica empate
            }
        }

        // Verifica se o crupiê venceu
        int pontuacaoCrupie = calcularPontuacao(crupie);
        if (pontuacaoCrupie <= 21 && maiorPontuacao > pontuacaoCrupie) {
            vencedor = vencedor;
        } else if (pontuacaoCrupie == maiorPontuacao) {
            return "O jogo terminou em empate (push).";
        } else {
            return "O crupiê venceu!";
        }

        if (vencedor != null) {
            return "O vencedor é: " + vencedor.getNome() + " com " + maiorPontuacao + " pontos!";
        } else {
            return "O jogo terminou em empate (push).";
        }
    }

    // Método para calcular a pontuação de um jogador no Blackjack
    private int calcularPontuacao(Player jogador) {
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
