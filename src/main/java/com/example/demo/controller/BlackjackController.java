package com.example.demo.controller;

import com.example.demo.model.Card;
import com.example.demo.model.Player;
import com.example.demo.service.BlackjackGameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/blackjack")
@CrossOrigin(origins = "*")
public class BlackjackController {

    private final BlackjackGameService gameFunctions;

    public BlackjackController(BlackjackGameService gameFunctions) {
        this.gameFunctions = gameFunctions;
    }

    @GetMapping("/jogadores")
    public ResponseEntity<List<Player>> jogadores() {
        List<Player> jogadores = gameFunctions.getJogadores();
        return new ResponseEntity<>(jogadores, HttpStatus.OK);
    }

    @PostMapping("/adicionar")
    public ResponseEntity<Map<String, String>> adicionarJogador(@RequestBody Player jogador) {
        Map<String, String> response = new HashMap<>();
        boolean sucesso = gameFunctions.adicionarJogador(jogador);
        response.put("message", sucesso ? "Jogador " + jogador.getNome() + " adicionado à mesa!" : "Não foi possível adicionar " + jogador.getNome() + ".");
        return new ResponseEntity<>(response, sucesso ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/iniciar")
    public ResponseEntity<Map<String, String>> iniciarJogo() {
        Map<String, String> response = new HashMap<>();
        if (gameFunctions.getJogadores().size() < 2) {
            response.put("message", "É necessário pelo menos dois jogadores para iniciar o jogo!");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        gameFunctions.iniciarJogo();
        response.put("message", "Jogo iniciado! Cartas distribuídas!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/cartas")
    public ResponseEntity<Map<String, List<String>>> getCartasDeJogadores() {
        // Supondo que a função gameFunctions.getJogadores() retorne uma lista de jogadores
        List<Player> players = gameFunctions.getJogadores();

        if (players.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        // Criando o HashMap diretamente utilizando o toString() da carta
        Map<String, List<String>> cartasPorJogador = players.stream()
                .collect(Collectors.toMap(
                        Player::getNome,  // Chave: Nome do jogador
                        player -> player.getMao().stream()
                                .map(Card::toString) // Usando diretamente o toString() da carta
                                .collect(Collectors.toList()) // Lista de strings
                ));
        return new ResponseEntity<>(cartasPorJogador, HttpStatus.OK);
    }


    @PostMapping("/comprar/{nome}")
    public ResponseEntity<Map<String, String>> comprarCarta(@PathVariable String nome) {
        Map<String, String> response = new HashMap<>();
        boolean sucesso = gameFunctions.comprarCarta(nome);
        response.put("message", sucesso ? "Jogador " + nome + " comprou uma carta." : "Jogador " + nome + " não pode comprar mais cartas.");
        return new ResponseEntity<>(response, sucesso ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/finalizar")
    public ResponseEntity<Map<String, String>> finalizarJogo() {
        Map<String, String> response = new HashMap<>();
        String fimDeJogo = gameFunctions.finalizarJogo();
        gameFunctions.resetarJogo();
        response.put("message", fimDeJogo);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/proximoJogador")
    public Player proximoJogador() {
        return gameFunctions.proximoJogador();
    }

    @PostMapping("/estourou/{nome}")
    public ResponseEntity<Map<String, String>> estourouJogo(@PathVariable String nome) {
        Map<String, String> response = new HashMap<>();
        gameFunctions.eliminarJogador(nome);
        response.put("message", "Jogador " + nome + " saiu da mesa.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
