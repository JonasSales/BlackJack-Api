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



    public void iniciarJogo(UUID idMesa) {
        Table mesa = encontrarMesaPorId(idMesa);
        if (mesa != null && !mesa.getJogoIniciado()) {
            mesa.iniciarJogo();
            List<Object> jogadores = List.of(mesa.getJogadores());

            // Distribuindo cartas para os jogadores
            for (Object jogador : jogadores) {
                Player jogador2 = (Player) jogador;
                jogador2.adicionarCarta(mesa.getDeck().distribuirCarta());
                jogador2.adicionarCarta(mesa.getDeck().distribuirCarta());
                jogador2.calcularPontuacao();
            }
            // Definindo o primeiro jogador como o jogador atual
            Player primeiroJogador = (Player) jogadores.getFirst();
            primeiroJogador.setJogadorAtual(true);
        }
    }

    @Override
    public boolean comprarCarta(Player jogador, UUID idMesa) {
        Table mesa = encontrarMesaPorId(idMesa);
        if (mesa != null) {
            Player jogadorNovo = mesa.encontrarJogador(jogador);
            Card carta = mesa.getDeck().distribuirCarta();
            jogadorNovo.adicionarCarta(carta);
            int pontuacaoJogador = jogadorNovo.calcularPontuacao();
            if (pontuacaoJogador > 21) {
                jogadorNovo.setPerdeuTurno();
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

    public Player proximoJogador(UUID idMesa) {
        Table mesa = encontrarMesaPorId(idMesa);
        if (mesa != null) {
            return mesa.proximoJogador();
        }
        return null;
    }

    public List<Player> getJogadores(UUID idMesa) {
        Table mesa = encontrarMesaPorId(idMesa);
        if (mesa != null) {
            return mesa.getJogadores();
        }
        return new ArrayList<>();
    }


    public boolean encerrarMao(Player jogador, UUID idMesa) {
        Table mesa = encontrarMesaPorId(idMesa);
        if (mesa != null) {
            jogador = mesa.encontrarJogador(jogador);
            if (jogador != null) {
                jogador.encerrarMao();
                jogador.setJogadorAtual(false);
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

    public boolean jogada(Player player, String jogada, UUID idMesa) {
        Table mesa = encontrarMesaPorId(idMesa);
        if (mesa != null) {
            jogada = jogada.trim();
            Player jogador = mesa.encontrarJogador(player);
            if (jogador == null) {
                return false;
            }
            if (jogada.equalsIgnoreCase("hit")) {
                comprarCarta(jogador, idMesa);
                return true;
            } else if (jogada.equalsIgnoreCase("stand")) {
                encerrarMao(jogador, idMesa);
                return true;
            }
        }
        return false;
    }

    public Table criarNovaMesa() {
        Table mesa = new Table();
        UUID mesaId = UUID.randomUUID();
        mesa.setId(mesaId);
        mesas.put(mesaId, mesa);
        return mesa;
    }


    public Table encontrarMesaPorId(UUID id) {
        return mesas.get(id);
    }
}
