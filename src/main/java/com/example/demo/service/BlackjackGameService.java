package com.example.demo.service;

import com.example.demo.model.Card;
import com.example.demo.model.Deck;
import com.example.demo.model.Player;
import com.example.demo.model.Table;
import com.example.demo.repository.BlackJackRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
            for (Player jogador : mesa.getJogadores()) {
                jogador.adicionarCarta(mesa.getDeck().distribuirCarta());
                jogador.adicionarCarta(mesa.getDeck().distribuirCarta());
                jogador.calcularPontuacao();
            }
        }
    }

    @Override
    public void distribuirCartas() {

    }


    @Override
    public boolean comprarCarta(Player jogador) {
        Player jogadorNovo = mesa.encontrarJogador(jogador.getNome());
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
        List<Player> jogadoresAtivos = mesa.getJogadores().stream()
                .filter(player -> !player.getPerdeuTurno())
                .toList();

        if (jogadoresAtivos.isEmpty()) {
            return "Todos os jogadores estouraram. Ninguém venceu.";
        }

        Player vencedor = jogadoresAtivos.getFirst();
        for (Player jogador : jogadoresAtivos) {
            if (jogador.calcularPontuacao() > vencedor.calcularPontuacao()) {
                vencedor = jogador;
            }
        }

        return "O vencedor é " + vencedor.getNome() + " com " + vencedor.calcularPontuacao() + " pontos!";
    }

    public Player proximoJogador() {
        return mesa.proximoJogador();
    }

    public void eliminarJogador(String nome) {
        mesa.eliminarJogador(nome);
    }

    public List<Player> getJogadores() {
        return mesa.getJogadores();
    }

    public boolean encerrarMao(Player jogador){
        jogador = mesa.encontrarJogador(jogador.getNome());
        if (jogador != null) {
            jogador.encerrarMao();
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
}