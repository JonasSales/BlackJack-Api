package com.example.demo.blackjack.api.controller;

import com.example.demo.auth.dto.UserDTO;
import com.example.demo.auth.service.UserService;
import com.example.demo.blackjack.domain.service.MesaService;
import com.example.demo.blackjack.exceptions.BlackjackExceptions;
import com.example.demo.blackjack.model.Player;
import com.example.demo.blackjack.model.Table;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/blackjack/player/{mesaId}/jogadores")
public class PlayerController {
    private final MesaService mesaService;
    private final UserService userService;

    public PlayerController(MesaService mesaService, UserService userService) {
        this.mesaService = mesaService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Player>> listarJogadores(@PathVariable UUID mesaId) {
        Table mesa = mesaService.encontrarMesaPorId(mesaId);
        if (mesa == null) {
            throw new BlackjackExceptions.MesaNaoEncontradaException(mesa);
        }
        return ResponseEntity.ok(mesa.getJogadores());
    }

    @GetMapping("/jogadoratual")
    public ResponseEntity<UserDTO> obterJogadorAtual(@PathVariable UUID mesaId) {
        Table mesa = mesaService.encontrarMesaPorId(mesaId);
        if (mesa == null) {
            throw new BlackjackExceptions.MesaNaoEncontradaException(mesa);
        }
        return ResponseEntity.ok(mesa.getJogadorAtual().getUser());
    }

    @PostMapping("/adicionar")
    public ResponseEntity<Map<String, String>> adicionarJogador(@PathVariable UUID mesaId, HttpServletRequest request) {
        UserDTO userDTO = userService.getUserFromToken(request);
        Player jogador = new Player(userDTO);

        if (mesaService.jogadorEstaEmQualquerMesa(jogador)) {
            throw new BlackjackExceptions.JogadorJaNaMesaException(jogador.getUser().getName());
        }

        Table mesa = mesaService.encontrarMesaPorId(mesaId);
        if (mesa == null) {
            throw new BlackjackExceptions.MesaNaoEncontradaException(mesa);
        }

        mesaService.adicionarJogador(mesa, jogador);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Jogador " + jogador.getUser().getName() + " adicionado à mesa!");
        return ResponseEntity.ok(response);
    }
}
