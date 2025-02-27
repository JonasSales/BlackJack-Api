package com.example.demo.blackjack.domain.service;

import com.example.demo.blackjack.model.Card;
import com.example.demo.blackjack.model.Player;
import com.example.demo.blackjack.model.Table;
import com.example.demo.blackjack.domain.repository.BlackJackRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.*;


@Getter
@Setter
@Service
public class BlackjackGameService implements BlackJackRepository {

    private Map<UUID, Table> mesas;  // Mapa de mesas com UUID como chave

    public BlackjackGameService() {
        this.mesas = new HashMap<>(); // Inicializa o mapa de mesas
    }

    public void iniciarJogo(Table mesa) {
        if (mesa.getJogoIniciado()) {
            ArrayList<Player> jogadores = mesa.getJogadores();
            // Distribuindo cartas para os jogadores
            for (Player jogador : jogadores) {
                jogador.adicionarCarta(mesa.getDeck().distribuirCarta());
                jogador.adicionarCarta(mesa.getDeck().distribuirCarta());
                jogador.calcularPontuacao();
            }
            // Definindo o primeiro jogador como o jogador atual
            Player primeiroJogador = jogadores.getFirst();
            primeiroJogador.setJogadorAtual(true);
        }
    }

    @Override
    public boolean comprarCarta(Player jogador,Table mesa) {

        if (mesa != null) {
            Player jogadorNovo = mesa.encontrarJogador(jogador);
            Card carta = mesa.getDeck().distribuirCarta();
            jogadorNovo.adicionarCarta(carta);
            int pontuacaoJogador = jogadorNovo.calcularPontuacao();
            if (pontuacaoJogador > 21) {
                jogadorNovo.setPerdeuTurno();
                mesa.proximoJogador();
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public String finalizarJogo(UUID idMesa) {
        Table mesa = encontrarMesaPorId(idMesa);
        if (mesa != null) {
            // Obtendo a lista de jogadores ativos (aqueles que não perderam o turno)
            List<Player> jogadoresAtivos = mesa.getJogadores().stream()
                    .filter(jogador -> !jogador.isPerdeuTurno())  // Filtrando jogadores que não perderam o turno
                    .toList();  // Coletando os Players em uma lista

            // Se não houver jogadores ativos, significa que todos perderam
            if (jogadoresAtivos.isEmpty()) {
                return "Todos os jogadores estouraram. Ninguém venceu.";
            }

            // Inicializando o vencedor como o primeiro jogador ativo
            Player vencedor = jogadoresAtivos.getFirst();

            // Encontrando o jogador com maior pontuação
            for (Player jogador : jogadoresAtivos) {
                if (jogador.calcularPontuacao() > vencedor.calcularPontuacao()) {
                    vencedor = jogador;
                }
            }
            return "O vencedor é " + vencedor.getUser().getName() + " com " + vencedor.calcularPontuacao() + " pontos!";
        }
        return "Mesa não encontrada.";
    }


    public boolean encerrarMao(Player jogador, Table mesa) {
        if (mesa != null) {
            jogador = mesa.encontrarJogador(jogador);
            if (jogador != null) {
                jogador.encerrarMao();
                jogador.setJogadorAtual(false);
                mesa.proximoJogador();
                return true;
            }
        }
        return false;
    }


    public boolean verificarTodosEncerraram(UUID idMesa) {
        Table mesa = encontrarMesaPorId(idMesa);
        if (mesa != null) {
            return mesa.todosJogadoresEncerraramMao();
        }
        return false;
    }

    public boolean jogada(Player player, String jogada, Table mesa) {
        System.out.println("Chegou aqui");
        if (mesa != null) {
            Player jogador = mesa.encontrarJogador(player);
            if (jogador == null) {
                return false;
            }
            if (jogada.equals("hit")) {
                comprarCarta(jogador, mesa);
                return true;
            } else if (jogada.equals("stand")) {
                encerrarMao(jogador, mesa);
                return true;
            }
        }
        return false;
    }


    public Table encontrarMesaPorId(UUID id) {
        return mesas.get(id);
    }
}
