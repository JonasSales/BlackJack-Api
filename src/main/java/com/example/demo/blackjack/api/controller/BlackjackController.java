package com.example.demo.blackjack.api.controller;

import com.example.demo.auth.dto.UserDTO;
import com.example.demo.blackjack.api.DTO.*;
import com.example.demo.blackjack.model.Card;
import com.example.demo.blackjack.model.Player;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping("/blackjack")
public class BlackjackController {

    private final MesaController mesaController;
    private final PlayerController jogadorController;
    private final JogoController jogoController;

    public BlackjackController(MesaController mesaController, PlayerController jogadorController, JogoController jogoController) {
        this.mesaController = mesaController;
        this.jogadorController = jogadorController;
        this.jogoController = jogoController;
    }

    // Delegar para MesaController
    @GetMapping("/mesas")
    public ResponseEntity<List<Map<String, Object>>> listarMesas() {
        return mesaController.listarMesas();
    }

    @GetMapping("/mesas/{mesaId}")
    public ResponseEntity<MesaInfoResponse> acessarMesa(@PathVariable UUID mesaId) {
        return mesaController.acessarMesa(mesaId);
    }

    // Delegar para JogadorController
    @GetMapping("/mesas/{mesaId}/jogadores")
    public ResponseEntity<List<Player>> listarJogadores(@PathVariable UUID mesaId) {
        return jogadorController.listarJogadores(mesaId);
    }

    @GetMapping("/mesas/{mesaId}/jogadoratual")
    public ResponseEntity<UserDTO> obterJogadorAtual(@PathVariable UUID mesaId) {
        return jogadorController.obterJogadorAtual(mesaId);
    }

    @PostMapping("/mesas/{mesaId}/adicionar")
    public ResponseEntity<Map<String, String>> adicionarJogador(@PathVariable UUID mesaId, HttpServletRequest request) {
        return jogadorController.adicionarJogador(mesaId, request);
    }

    // Delegar para JogoController
    @PostMapping("/mesas/{mesaId}/iniciar")
    public ResponseEntity<Map<String, String>> iniciarJogo(@PathVariable UUID mesaId) {
        return jogoController.iniciarJogo(mesaId);
    }

    @GetMapping("/mesas/{mesaId}/cartas")
    public ResponseEntity<List<Card>> obterCartasDeJogadores(@PathVariable UUID mesaId, HttpServletRequest request) {
        return jogoController.obterCartasDeJogadores(mesaId, request);
    }

    @PostMapping("/mesas/{mesaId}/jogada")
    public ResponseEntity<JogadaResponse> realizarJogada(
            @PathVariable UUID mesaId,
            HttpServletRequest request,
            @RequestBody PlayerRequest jogada) {
        return jogoController.realizarJogada(mesaId, request, jogada);
    }
}