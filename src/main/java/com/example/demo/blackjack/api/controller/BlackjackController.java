package com.example.demo.blackjack.api.controller;

import com.example.demo.auth.dto.UserDTO;
import com.example.demo.auth.service.AuthenticationService;
import com.example.demo.auth.service.UserService;
import com.example.demo.blackjack.api.DTO.playerRequest;
import com.example.demo.blackjack.model.Card;
import com.example.demo.blackjack.model.Player;
import com.example.demo.blackjack.domain.service.BlackjackGameService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    private final AuthenticationService authenticationService;
    private final UserService userService;

    public BlackjackController(BlackjackGameService gameFunctions, AuthenticationService authenticationService, UserService userService) {
        this.gameFunctions = gameFunctions;
        this.authenticationService = authenticationService;
        this.userService = userService;
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
    public ResponseEntity<Map<String, String>> adicionarJogador(HttpServletRequest request) {

        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        // Remove o prefixo "Bearer " do token
        token = token.substring(7);

        // Verifica a autenticação
        Authentication authentication = authenticationService.getAuthentication(token);

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String username = authentication.getName();


        UserDTO userDTO = userService.getUser(username);


        Player jogador = new Player(userDTO);

        Map<String, String> response = new HashMap<>();
        boolean sucesso = gameFunctions.adicionarJogador(jogador);
        response.put("message", sucesso ? "Jogador " + jogador.getUser().getName() +
                " adicionado à mesa!" : "Não foi possível adicionar " + jogador.getUser().getName() + ".");
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
                        player -> player.getUser().getName(), // Obtendo o nome do usuário dentro do Player
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

        if (jogador == null || jogada == null) {
            response.put("mensagem", "Jogador ou jogada inválida.");
            return ResponseEntity.badRequest().body(response);
        }

        System.out.println("Jogador: " + jogador.getUser().getName() + " fez a jogada: " + jogada);

        // Realiza a jogada
        boolean jogadaValida = gameFunctions.jogada(jogador, jogada);
        if (jogadaValida) {
            response.put("mensagem", "Jogada realizada com sucesso para " + jogador.getUser().getName() );
        } else {
            response.put("mensagem", "Jogada inválida para " + jogador.getUser().getName());
        }

        // Verifica se todos os jogadores encerraram a mão
        if (gameFunctions.verificarTodosEncerraram()) {
            response.put("mensagem", "Todos os jogadores encerraram a mão. O jogo foi finalizado.");
            gameFunctions.finalizarJogo();
            return ResponseEntity.ok(response);
        }
        // Verifica se há jogadores válidos para a próxima jogada
        Player proximo = gameFunctions.proximoJogador();
        if (proximo != null) {
            response.put("mensagem", "Próximo jogador: " + jogador.getUser().getName() );
        } else {
            // Caso não haja mais jogadores válidos, finaliza o jogo
            response.put("mensagem", "Não há jogadores válidos restantes. O jogo foi finalizado.");
            gameFunctions.finalizarJogo();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("partidaIniciada")
    public ResponseEntity<Map<String, String>> partidaIniciada() {
        Map<String, String> response = new HashMap<>();
        boolean jogoIniciado = gameFunctions.getMesa().isJogoIniciado();
        // Se o jogo foi iniciado, responda com "true", senão "false"
        response.put("jogoIniciado", String.valueOf(jogoIniciado));
        return ResponseEntity.ok(response);
    }


}

