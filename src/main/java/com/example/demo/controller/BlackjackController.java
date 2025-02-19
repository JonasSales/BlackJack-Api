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
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class BlackjackController {

    private final BlackjackGameService gameFunctions;

    public BlackjackController(BlackjackGameService gameFunctions) {
        this.gameFunctions = gameFunctions;
    }

    // Listar jogadores
    @GetMapping("/jogadores")
    public ResponseEntity<List<Player>> jogadores() {
        List<Player> jogadores = gameFunctions.getJogadores();
        return new ResponseEntity<>(jogadores, HttpStatus.OK);
    }

    // Jogador Atual
    @GetMapping("/jogadoratual")
    public ResponseEntity<List<Player>> jogadoresAtuais() {
        List<Player> players = gameFunctions.getJogadores();
        List<Player> jogadoresAtuais = players.stream()
                .filter(player -> player.isJogadorAtual() && !player.isPerdeuTurno() && !player.isStand())
                .collect(Collectors.toList());
        return new ResponseEntity<>(jogadoresAtuais, HttpStatus.OK);
    }

    // Adicionar jogador
    @PostMapping("/adicionar")
    public ResponseEntity<Map<String, String>> adicionarJogador(@RequestBody Player jogador) {
        Map<String, String> response = new HashMap<>();
        boolean sucesso = gameFunctions.adicionarJogador(jogador);
        response.put("message", sucesso ? "Jogador " + jogador.getNome() + " adicionado à mesa!" : "Não foi possível adicionar " + jogador.getNome() + ".");
        return new ResponseEntity<>(response, sucesso ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST);
    }

    // Iniciar jogo
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

    // Cartas
    @GetMapping("/cartas")
    public ResponseEntity<Map<String, List<String>>> getCartasDeJogadores() {
        List<Player> players = gameFunctions.getJogadores();
        if (players.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        Map<String, List<String>> cartasPorJogador = players.stream()
                .collect(Collectors.toMap(
                        Player::getNome,
                        player -> player.getMao().stream()
                                .map(Card::toString)
                                .collect(Collectors.toList())
                ));
        return new ResponseEntity<>(cartasPorJogador, HttpStatus.OK);
    }

    // Finalizar jogo
    @GetMapping("/finalizar")
    public ResponseEntity<Map<String, String>> finalizarJogo() {
        Map<String, String> response = new HashMap<>();
        String fimDeJogo = gameFunctions.finalizarJogo();
        gameFunctions.resetarJogo();
        response.put("message", fimDeJogo);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Proximo Jogador
    @GetMapping("/proximoJogador")
    public Player proximoJogador() {
        return gameFunctions.proximoJogador();
    }

    // Jogada
    @PostMapping("/jogada")
    public ResponseEntity<Map<String, String>> jogada(@RequestBody playerRequest request) {
        Map<String, String> response = new HashMap<>();
        Player jogador = request.getPlayer();
        String jogada = request.getJogada();

        // Validação de parâmetros nulos
        if (jogador == null || jogada == null) {
            response.put("mensagem", "Jogador ou jogada inválida.");
            return ResponseEntity.badRequest().body(response);
        }

        // Verifica se a jogada é válida
        boolean jogadaValida = gameFunctions.jogada(jogador, jogada);
        if (jogadaValida) {
            response.put("mensagem", "Jogada realizada com sucesso para " + jogador.getNome());
        } else {
            response.put("mensagem", "Jogada inválida para " + jogador.getNome());
        }

        // Verifica se todos os jogadores encerraram a mão
        if (gameFunctions.verificarTodosEncerraram()) {
            response.put("mensagem", "Todos os jogadores encerraram a mão. O jogo foi finalizado.");
            gameFunctions.finalizarJogo();  // Finaliza o jogo, se necessário
        }
        return ResponseEntity.ok(response);
    }

}

