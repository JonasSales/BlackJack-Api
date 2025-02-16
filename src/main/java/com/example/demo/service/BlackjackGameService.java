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
            }
        }
    }

    @Override
    public void distribuirCartas() {

    }

    public boolean comprarCarta(String nome) {
        for (Player jogador : mesa.getJogadores()) {
            if (jogador.getNome().equals(nome) && !jogador.isPerdeuTurno()) {
                Card carta = mesa.getDeck().distribuirCarta();
                jogador.adicionarCarta(carta);

                if (calcularPontuacao(jogador) > 21) {
                    jogador.setPerdeuTurno(true);
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public int calcularPontuacao(Player jogador) {
        int pontos = 0;
        int ases = 0;

        for (Card carta : jogador.getMao()) {
            int valor = carta.getValores()[0];
            if (valor == 1) {
                ases++;
                pontos += 11;
            } else pontos += Math.min(valor, 10);
        }

        while (pontos > 21 && ases > 0) {
            pontos -= 10;
            ases--;
        }

        return pontos;
    }

    public String finalizarJogo() {
        List<Player> jogadoresAtivos = mesa.getJogadores().stream()
                .filter(j -> !j.isPerdeuTurno())
                .toList();

        if (jogadoresAtivos.isEmpty()) {
            return "Todos os jogadores estouraram. Ninguém venceu.";
        }

        Player vencedor = jogadoresAtivos.getFirst();
        for (Player jogador : jogadoresAtivos) {
            if (calcularPontuacao(jogador) > calcularPontuacao(vencedor)) {
                vencedor = jogador;
            }
        }

        return "O vencedor é " + vencedor.getNome() + " com " + calcularPontuacao(vencedor) + " pontos!";
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

    public void resetarJogo() {
        mesa = new Table();

    }
}