package com.example.demo.blackjack.api.controller;

import com.example.demo.auth.dto.UserDTO;
import com.example.demo.auth.service.AuthenticationService;
import com.example.demo.auth.service.UserService;
import com.example.demo.blackjack.api.DTO.playerRequest;
import com.example.demo.blackjack.domain.service.BlackjackGameService;
import com.example.demo.blackjack.domain.service.MesaService;
import com.example.demo.blackjack.exceptions.BlackjackExceptions;
import com.example.demo.blackjack.model.Card;
import com.example.demo.blackjack.model.Player;
import com.example.demo.blackjack.model.Table;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping("/blackjack")
public class BlackjackController {

    private final MesaService mesaService;
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final BlackjackGameService blackjackGameService;

    public BlackjackController(MesaService mesaService, AuthenticationService authenticationService, UserService userService, BlackjackGameService blackjackGameService) {
        this.mesaService = mesaService;
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.blackjackGameService = blackjackGameService;
    }

    @GetMapping("/mesas/{mesaId}/jogadores")
    public ResponseEntity<List<Player>> listarJogadores(@PathVariable UUID mesaId) {
        Table mesa = mesaService.encontrarMesaPorId(mesaId);

        if (mesa == null) {
            throw new BlackjackExceptions.MesaNaoEncontradaException(mesaId);
        }


        return ResponseEntity.ok(mesa.getJogadores());
    }

    @GetMapping("/mesas/{mesaId}/jogadoratual")
    public ResponseEntity<Player> listarJogadoresAtuais(@PathVariable UUID mesaId) {
        Table mesa = mesaService.encontrarMesaPorId(mesaId);
        if (mesa == null) {
            throw new BlackjackExceptions.MesaNaoEncontradaException(mesaId);
        }
        return ResponseEntity.ok(mesa.getJogadorAtual());
    }

    @PostMapping("/mesas/{mesaId}/adicionar")
    public ResponseEntity<Map<String, String>> adicionarJogador(@PathVariable UUID mesaId, HttpServletRequest request) {
        UserDTO userDTO = userService.getUserFromToken(request);
        Player jogador = new Player(userDTO);

        boolean sucesso = mesaService.adicionarJogador(mesaId, jogador);
        if (!sucesso) {
            throw new BlackjackExceptions.MesaNaoEncontradaException(mesaId);
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Jogador " + jogador.getUser().getName() + " adicionado à mesa!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/mesas/{mesaId}/iniciar")
    public ResponseEntity<Map<String, String>> iniciarJogo(@PathVariable UUID mesaId) {
        Table mesa = mesaService.encontrarMesaPorId(mesaId);
        Map<String, String> response = new HashMap<>();

        if (mesa.getJogadores().size() < 2) {
            response.put("message", "O jogo para começar precisa de ao menos 2 jogadores");
        }
        mesa.iniciarJogo();
        blackjackGameService.iniciarJogo(mesa);
        response.put("message", "Jogo iniciado! Cartas distribuídas!");
        response.put("mesaToken", mesa.getToken());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mesas/{mesaId}/cartas")
    public ResponseEntity<Map<String, List<String>>> getCartasDeJogadores(@PathVariable UUID mesaId) {
        Table mesa = mesaService.encontrarMesaPorId(mesaId);
        if (mesa == null) {
            throw new BlackjackExceptions.MesaNaoEncontradaException(mesaId);
        }

        Map<String, List<String>> cartasPorJogador = mesa.getJogadores().stream()
                .collect(Collectors.toMap(
                        player -> player.getUser().getName(),
                        player -> player.getMao().stream()
                                .map(Card::toString)
                                .collect(Collectors.toList())
                ));
        return ResponseEntity.ok(cartasPorJogador);
    }

    @PostMapping("/mesas/{mesaId}/jogada")
    public ResponseEntity<Map<String, String>> realizarJogada(@PathVariable UUID mesaId, HttpServletRequest request, @RequestBody playerRequest jogada) {
        UserDTO userDTO = userService.getUserFromToken(request);
        Table mesa = mesaService.encontrarMesaPorId(mesaId);
        if (mesa == null) {
            throw new BlackjackExceptions.MesaNaoEncontradaException(mesaId);
        }
        Player jogador = mesa.encontrarJogador(new Player(userDTO));

        Map<String, String> response = new HashMap<>();

        if (blackjackGameService.jogada(jogador, jogada.getJogada(), mesa)) {
            response.put("mensagem", "Jogada realizada com sucesso.");
            return ResponseEntity.ok(response);
        }

        if (blackjackGameService.verificarTodosEncerraram(mesaId)) {
            response.put("mensagem", blackjackGameService.finalizarJogo(mesaId));
            return ResponseEntity.ok(response);
        }

        response.put("mensagem", "Jogada invalida.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/criarMesa")
    public ResponseEntity<Map<String, Object>> criarMesa(HttpServletResponse response) {
        Table mesa = mesaService.criarMesa();
        String mesaToken = authenticationService.generateToken(mesa.getId().toString(), response);
        mesa.setToken(mesaToken);
        System.out.println(mesaToken);
        Map<String, Object> mesaInfo = new HashMap<>();
        mesaInfo.put("mesaId", mesa.getId());
        mesaInfo.put("token", mesaToken);

        return ResponseEntity.ok(mesaInfo);
    }

    @GetMapping("/mesas/{mesaId}")
    public ResponseEntity<?> acessarMesa(@PathVariable UUID mesaId) {
        Table mesa = mesaService.encontrarMesaPorId(mesaId);
        if (authenticationService.getAuthentication(mesa.getToken()) == null) {
            throw new BlackjackExceptions.TokenInvalidoException();
        }

        return ResponseEntity.ok(Map.of(
                "mesaId", mesa.getId(),
                "jogoIniciado", mesa.getJogoIniciado(),
                "quantidadeDeJogadores", mesa.getJogadores().size(),
                "mesaEncerrada?", mesa.todosJogadoresEncerraramMao()
        ));
    }

    @GetMapping("/mesas")
    public ResponseEntity<List<Map<String, Object>>> listarMesas() {
        List<Map<String, Object>> mesasInfo = mesaService.listarMesas().stream()
                .map(mesa -> {
                    Map<String, Object> info = new HashMap<>();
                    info.put("mesaId", mesa.getId());
                    info.put("quantidadeDeJogadores", mesa.getJogadores().size());
                    return info;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(mesasInfo);
    }
}