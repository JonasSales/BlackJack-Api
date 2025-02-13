package com.example.demo.controller;

import com.example.demo.service.BlackjackGame;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/blackjack")
@CrossOrigin(origins = "*")
public class BlackjackController {

    private final BlackjackGame jogo;

    public BlackjackController() {
        jogo = new BlackjackGame(); // Instância do jogo
    }

    // Iniciar o jogo
    @PostMapping("/iniciar")
    public String iniciarJogo(@RequestBody List<String> nomes) {
        jogo.iniciarJogo(nomes);
        jogo.distribuirCartas();
        return "Jogo iniciado com os jogadores: " + String.join(", ", nomes);
    }



    // Jogador compra uma carta
    @PostMapping("/comprar/{nome}")
    public String comprarCarta(@PathVariable String nome) {
        jogo.comprarCarta(nome);
        return nome + " comprou uma carta!";
    }


    // Finalizar o jogo e determinar o vencedor
    @GetMapping("/finalizar")
    public String finalizarJogo() {
        return jogo.finalizarJogo();
    }

    @GetMapping("/cartas")
    public List<String> obterCartasDeTodosOsJogadores() {
        return jogo.getJogadores().stream()
                .map(jogador -> {
                    // Obtemos as cartas do jogador
                    String cartas = jogador.getMao().stream()
                            .map(carta -> carta.toString()) // Representação das cartas
                            .collect(Collectors.joining(", "));

                    // Calculamos a pontuação do jogador
                    int pontos = jogo.calcularPontuacao(jogador); // Chama o método para calcular a pontuação

                    // Retornamos o nome do jogador, as cartas e a pontuação
                    return jogador.getNome() + ": " + cartas + " | Pontuação: " + pontos;
                })
                .collect(Collectors.toList());
    }
}
