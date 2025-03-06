package com.example.demo.blackjack.api.controller;

import com.example.demo.blackjack.domain.service.JogoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/blackjack/mesas/{mesaId}")
public class JogoController {

    private final JogoService jogoService;

    public JogoController(JogoService jogoService) {
        this.jogoService = jogoService;
    }

    @PostMapping("/iniciar")
    public ResponseEntity<Map<String, String>> iniciarJogo(@PathVariable UUID mesaId) {
        jogoService.iniciarJogo(mesaId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Jogo iniciado! Cartas distribuídas!");
        return ResponseEntity.ok(response);
    }
}