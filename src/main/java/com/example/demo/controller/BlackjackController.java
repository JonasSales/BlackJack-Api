package com.example.demo.controller;

import com.example.demo.DTO.playerRequest;
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
@CrossOrigin(origins = "*", allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})

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

    @GetMapping("/jogadoratual")
    public ResponseEntity<List<Player>> jogadoresAtuais() {
        List<Player> players = gameFunctions.getJogadores();
        // Filtrar os jogadores que são o jogador atual, não perderam turno e não estão em stand
        List<Player> jogadoresAtuais = players.stream()
                .filter(player -> player.isJogadorAtual() && !player.isPerdeuTurno() && !player.isStand()) // Verifica os atributos
                .collect(Collectors.toList()); // Coleta todos os jogadores que atendem a essas condições
        if (jogadoresAtuais.isEmpty()) {
            throw new RuntimeException("Nenhum jogador atual encontrado ou em estado inválido");
        }
        return new ResponseEntity<>(jogadoresAtuais, HttpStatus.OK);
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


    @PostMapping("/jogada")
    public ResponseEntity<Map<String, String>> jogada(@RequestBody playerRequest request) {
        Map<String, String> response = new HashMap<>();
        Player jogador = request.getPlayer();
        String jogada = request.getJogada();

        if (jogador == null || jogada == null) {
            response.put("mensagem", "Jogador ou jogada inválida.");
            return ResponseEntity.badRequest().body(response);
        }
        // Chama a função que decide se é "hit" ou "stand"
        boolean jogadaValida = gameFunctions.jogada(jogador, jogada);

        // Mensagem de resposta com ternário para decidir o sucesso ou falha da jogada
        String mensagem = jogadaValida
                ? "Jogada realizada com sucesso para " + jogador.getNome()
                : "Jogada inválida para " + jogador.getNome();

        response.put("mensagem", mensagem);
        // Caso o jogo tenha terminado, finalize
        if (!gameFunctions.verificarTodosEncerraram()) {
            gameFunctions.finalizarJogo();
            response.put("mensagem", "Todos os jogadores encerraram a mão. O jogo foi finalizado.");
        }
        return ResponseEntity.ok(response);
    }

}
