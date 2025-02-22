package com.example.demo.blackjack.domain.service;

import com.example.demo.blackjack.model.Card;
import com.example.demo.blackjack.model.Deck;
import com.example.demo.blackjack.model.Player;
import com.example.demo.blackjack.model.Table;
import com.example.demo.blackjack.domain.repository.BlackJackRepository;
import lombok.Getter;
import org.springframework.stereotype.Service;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Service
public class BlackjackGameService implements BlackJackRepository {

    private Table mesa;

    public BlackjackGameService() {
        this.mesa = new Table(); // Sempre mantém o estado da mesa
    }
    public boolean adicionarJogador(Player jogador) {
        return mesa.adicionarJogador(jogador);
    }
    public Deck getDeck(){
        return mesa.getDeck();
    }

    @Override
    public void iniciarJogo() {
        if (!mesa.isJogoIniciado()) {
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
            if (!jogadores.isEmpty()) {
                Player primeiroJogador = (Player) jogadores.getFirst();
                primeiroJogador.setJogadorAtual(true);
            }
        }
    }




    @Override
    public boolean comprarCarta(Player jogador) {
        Player jogadorNovo = mesa.encontrarJogador(jogador);
        Card carta = mesa.getDeck().distribuirCarta();
        jogadorNovo.adicionarCarta(carta);
        int pontuacaoJogador = jogadorNovo.calcularPontuacao();
        if ( pontuacaoJogador > 21) {
            jogadorNovo.setPerdeuTurno();
            return false;
        }
        return true;
    }


    @Override
    public String finalizarJogo() {
        // Filtrando jogadores que não perderam o turno
        List<Player> jogadoresAtivos = Arrays.stream(mesa.getJogadores())
                .filter(obj -> obj instanceof Player)  // Garantindo que é um Player
                .map(obj -> (Player) obj)              // Fazendo o cast para Player
                .filter(jogador -> !jogador.isPerdeuTurno()) // Filtrando jogadores que não perderam o turno
                .toList();         // Coletando os Players em uma lista

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


    public Player proximoJogador() {
        return mesa.proximoJogador();
    }

    public void eliminarJogador(Player jogador) {
        mesa.eliminarJogador(jogador);
    }

    public List<Player> getJogadores() {
        // Fazendo o cast de Object[] para List<Player>
        return Arrays.stream(mesa.getJogadores())
                .filter(obj -> obj instanceof Player)  // Garantindo que é um Player
                .map(obj -> (Player) obj)              // Cast para Player
                .collect(Collectors.toList());         // Coletando os Players em uma lista
    }


    public boolean encerrarMao(Player jogador){
        jogador = mesa.encontrarJogador(jogador);
        if (jogador != null) {
            jogador.encerrarMao();
            jogador.setJogadorAtual(false);
            return true;
        }
        return false;
    }

    public void resetarJogo() {
        mesa = new Table();

    }

    public boolean verificarTodosEncerraram() {
        return mesa.todosJogadoresEncerraramMao();
    }

    public boolean jogada(Player player, String jogada) {
        Player jogador = mesa.encontrarJogador(player);

        // Verifica se o jogador existe na mesa
        return switch (jogada.toLowerCase()) {
            case "hit" -> comprarCarta(jogador);
            case "stand" -> encerrarMao(jogador);
            default -> {
                System.out.println("Jogada inválida: " + jogada + " para " + jogador.getUser().getName());
                yield false;
            }
        };
    }

}