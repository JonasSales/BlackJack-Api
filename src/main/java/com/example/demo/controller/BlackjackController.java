package com.example.demo.controller;

import com.example.demo.model.Card;
import com.example.demo.model.Player;
import com.example.demo.service.BlackjackGameService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/blackjack")
@CrossOrigin(origins = "*")
public class BlackjackController {

    private BlackjackGameService gameFunctions;

    public BlackjackController(BlackjackGameService gameFunctions) {
        this.gameFunctions = gameFunctions;
    }

    @PostMapping("/iniciar")
    public String iniciarJogo(@RequestBody List<String> nomes) {
        gameFunctions.iniciarJogo(nomes);
        return "Jogo iniciado! Cartas distribuídas!";
    }


    @GetMapping("/cartas")
    public List<String> obterCartasDeTodosOsJogadores() {
        return gameFunctions.getJogadores().stream()
                .filter(jogador -> !jogador.isPerdeuTurno())
                .map(jogador -> {
                    String cartas = jogador.getMao().stream()
                            .map(Card::toString)
                            .collect(Collectors.joining(", "));
                    int pontos = gameFunctions.calcularPontuacao(jogador);
                    return jogador.getNome() + ": " + cartas + " | Pontuação: " + pontos;
                })
                .collect(Collectors.toList());
    }

    @PostMapping("/comprar/{nome}")
    public String comprarCarta(@PathVariable String nome) {
        boolean sucesso = gameFunctions.comprarCarta(nome);
        return sucesso ? "Jogador " + nome + " comprou uma carta." : "Jogador " + nome + " não pode comprar mais cartas.";
    }

    @GetMapping("/finalizar")
    public String finalizarJogo() {
        String fimDeJogo = gameFunctions.finalizarJogo();
        gameFunctions = new BlackjackGameService();
        return fimDeJogo;
    }

    @GetMapping("/proximoJogador")
    public Player proximoJogador() {
        return gameFunctions.proximoJogador();
    }

    @PostMapping("/estourou/{nome}")
    public String estourouJogo(@PathVariable String nome) {
        gameFunctions.eliminarJogador(nome);
        return "Jogador " + nome + " saiu da mesa.";
    }
}

