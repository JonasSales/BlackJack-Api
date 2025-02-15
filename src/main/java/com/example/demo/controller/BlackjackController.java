package com.example.demo.controller;

import com.example.demo.model.Player;
import com.example.demo.service.BlackjackGameFunctions;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/blackjack")
@CrossOrigin(origins = "*")
public class BlackjackController {

    private BlackjackGameFunctions gameFunctions;  // Autowire the service

    public BlackjackController(BlackjackGameFunctions gameFunctions) {
        this.gameFunctions = gameFunctions;
    }

    @PostMapping("/iniciar")
    public String iniciarJogo(@RequestBody List<String> nomes) {
        gameFunctions.iniciarJogo(nomes);
        gameFunctions.distribuirCartas();
        return "Jogo iniciado! Cartas distribuídas!";
    }

    @GetMapping("/jogadores")
    public List<Player> obterJogadores() {
        return gameFunctions.getJogadores();
    }


    @GetMapping("/cartas")
    public List<String> obterCartasDeTodosOsJogadores() {
        return gameFunctions.getJogadores().stream()
                .map(jogador -> {
                    // Obtemos as cartas do jogador
                    String cartas = jogador.getMao().stream()
                            .map(carta -> carta.toString()) // Representação das cartas
                            .collect(Collectors.joining(", "));

                    // Calculamos a pontuação do jogador
                    int pontos = gameFunctions.calcularPontuacao(jogador); // Chama o método para calcular a pontuação

                    // Retornamos o nome do jogador, as cartas e a pontuação
                    return jogador.getNome() + ": " + cartas + " | Pontuação: " + pontos;
                })
                .collect(Collectors.toList());
    }

    @PostMapping("/comprar/{nome}")
    public String comprarCarta(@PathVariable  String nome) {
        boolean sucesso = gameFunctions.comprarCarta(nome);
        if (sucesso) {
            return "Jogador " + nome + " comprou uma carta.";
        }
        return "Jogador " + nome + " não pode comprar mais cartas.";
    }

    @GetMapping("/finalizar")
    public String finalizarJogo() {
        String fimDeJogo = gameFunctions.finalizarJogo();
        gameFunctions = new BlackjackGameFunctions();
        return fimDeJogo;
    }

    @GetMapping("/proximoJogador")
    public Player proximoJogador() {
        return gameFunctions.proximoJogador();
    }
}
