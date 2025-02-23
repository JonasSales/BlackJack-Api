package com.example.demo.blackjack.api.controller;

import com.example.demo.auth.dto.UserDTO;
import com.example.demo.auth.service.AuthenticationService;
import com.example.demo.auth.service.UserService;
import com.example.demo.blackjack.api.DTO.playerRequest;
import com.example.demo.blackjack.domain.service.MesaService;
import com.example.demo.blackjack.domain.service.PlayerService;
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
    private final PlayerService jogadorService;
    private final AuthenticationService authenticationService;
    private final UserService userService;

    public BlackjackController(MesaService mesaService, PlayerService jogadorService, AuthenticationService authenticationService, UserService userService) {
        this.mesaService = mesaService;
        this.jogadorService = jogadorService;
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    @GetMapping("/mesas/{mesaId}/jogadores")
    public ResponseEntity<List<Player>> listarJogadores(@PathVariable UUID mesaId) {
        Table mesa = mesaService.encontrarMesaPorId(mesaId);
        if (mesa == null) {
            throw new BlackjackExceptions.MesaNaoEncontradaException(mesaId);
        }
        return ResponseEntity.ok(jogadorService.listarJogadores(mesa));
    }

    @GetMapping("/mesas/{mesaId}/jogadoratual")
    public ResponseEntity<List<Player>> listarJogadoresAtuais(@PathVariable UUID mesaId) {
        Table mesa = mesaService.encontrarMesaPorId(mesaId);
        if (mesa == null) {
            throw new BlackjackExceptions.MesaNaoEncontradaException(mesaId);
        }
        return ResponseEntity.ok(jogadorService.listarJogadoresAtuais(mesa));
    }

    @PostMapping("/mesas/{mesaId}/adicionar")
    public ResponseEntity<Map<String, String>> adicionarJogador(@PathVariable UUID mesaId, HttpServletRequest request) {
        UserDTO userDTO = userService.getUserFromToken(request);
        Player jogador = jogadorService.criarJogador(userDTO);

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
        if (mesa == null) {
            throw new BlackjackExceptions.MesaNaoEncontradaException(mesaId);
        }

        if (mesa.getJogadores().size() < 2) {
            throw new BlackjackExceptions.JogadoresInsuficientesException();
        }

        mesaService.iniciarJogo(mesaId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Jogo iniciado! Cartas distribuídas!");
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
    public ResponseEntity<String> realizarJogada(@PathVariable UUID mesaId, HttpServletRequest request, @RequestBody playerRequest jogada) {
        UserDTO userDTO = userService.getUserFromToken(request);
        Player jogador = jogadorService.criarJogador(userDTO);

        // Lógica de jogada (implementar no serviço apropriado)
        return ResponseEntity.ok("Jogada realizada com sucesso.");
    }

    @GetMapping("/criarMesa")
    public ResponseEntity<Map<String, Object>> criarMesa(HttpServletResponse response) {
        Table mesa = mesaService.criarMesa();
        String mesaToken = authenticationService.generateToken(mesa.getId().toString(), response);

        Map<String, Object> mesaInfo = new HashMap<>();
        mesaInfo.put("mesaId", mesa.getId());
        mesaInfo.put("token", mesaToken);

        return ResponseEntity.ok(mesaInfo);
    }

    @GetMapping("/mesas/{mesaId}")
    public ResponseEntity<?> acessarMesa(@PathVariable UUID mesaId, @RequestHeader("Authorization") String token) {
        UUID mesaIdFromToken = authenticationService.validateToken(token);
        if (mesaIdFromToken == null || !mesaIdFromToken.equals(mesaId)) {
            throw new BlackjackExceptions.TokenInvalidoException();
        }

        Table mesa = mesaService.encontrarMesaPorId(mesaId);
        if (mesa == null) {
            throw new BlackjackExceptions.MesaNaoEncontradaException(mesaId);
        }

        return ResponseEntity.ok(Map.of(
                "mesaId", mesa.getId(),
                "jogoIniciado", mesa.getJogoIniciado(),
                "quantidadeDeJogadores", mesa.getJogadores().size()
        ));
    }

    @GetMapping("/mesas")
    public ResponseEntity<List<Map<String, Object>>> listarMesas() {
        List<Map<String, Object>> mesasInfo = mesaService.listarMesas().stream()
                .map(mesa -> {
                    Map<String, Object> info = new HashMap<>();
                    info.put("mesaId", mesa.getId());
                    return info;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(mesasInfo);
    }
}