package com.example.demo.blackjack.api.controller;

import com.example.demo.auth.dto.UserDTO;
import com.example.demo.blackjack.api.DTO.MesaInfoResponse;
import com.example.demo.blackjack.domain.service.MesaService;
import com.example.demo.blackjack.domain.service.PlayerService;
import com.example.demo.blackjack.model.Card;
import com.example.demo.blackjack.model.Player;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/blackjack/mesas")
public class MesaController {

    private final MesaService mesaService;
    private final PlayerService playerService;

    public MesaController(MesaService mesaService, PlayerService playerService) {
        this.mesaService = mesaService;
        this.playerService = playerService;
    }

    @GetMapping
    public ResponseEntity<List<MesaInfoResponse>> listarMesas() {
        return mesaService.listarMesas();
    }

    @GetMapping("/{mesaId}")
    public ResponseEntity<MesaInfoResponse> acessarMesa(@PathVariable UUID mesaId) {
        return mesaService.encontrarMesaPorId(mesaId);
    }

    @GetMapping("/{mesaId}/crupieCard")
    public ResponseEntity<Card> cartaCrupie(@PathVariable UUID mesaId){
        return mesaService.cartaCrupie(mesaId);
    }

    @GetMapping("/{mesaId}/jogadoratual")
    public ResponseEntity<UserDTO> obterJogadorAtual(@PathVariable UUID mesaId) {
        return mesaService.jogadorAtual(mesaId);
    }

    @PostMapping("/{mesaId}/adicionar")
    public ResponseEntity<Player> adicionarJogador(@PathVariable UUID mesaId, HttpServletRequest request) {
        return playerService.adicionarJogadorAUmaMesa(mesaId, request);
    }
}