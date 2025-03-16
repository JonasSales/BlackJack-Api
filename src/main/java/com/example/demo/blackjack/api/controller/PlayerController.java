package com.example.demo.blackjack.api.controller;

import com.example.demo.blackjack.api.DTO.PlayerRequest;
import com.example.demo.blackjack.domain.service.JogoService;
import com.example.demo.blackjack.domain.service.PlayerService;
import com.example.demo.blackjack.model.Card;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/blackjack/mesas/{mesaId}")
public class PlayerController {

    private final PlayerService jogadorService;
    private final JogoService jogoService;


    public PlayerController(PlayerService jogadorService, JogoService jogoService) {
        this.jogadorService = jogadorService;
        this.jogoService = jogoService;
    }


    @PostMapping("/jogada")
    public ResponseEntity<Map<String, Object>> realizarJogada(
            @PathVariable UUID mesaId,
            HttpServletRequest request,
            @RequestBody PlayerRequest jogada) {
        return jogoService.realizarJogada(mesaId, request, jogada.getJogada());
    }


    @GetMapping("/cartas")
    public ResponseEntity<List<Card>> obterCartasDeJogadores(@PathVariable UUID mesaId, HttpServletRequest request) {
        return jogadorService.obterCartasDeJogadores(mesaId, request);
    }



}
