package com.example.demo.blackjack.api.controller;

import com.example.demo.auth.service.AuthenticationService;
import com.example.demo.blackjack.api.DTO.MesaInfoResponse;
import com.example.demo.blackjack.domain.service.MesaService;
import com.example.demo.blackjack.exceptions.BlackjackExceptions;
import com.example.demo.blackjack.model.Table;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/blackjack/mesasA")
public class MesaController {

    private final MesaService mesaService;
    private final AuthenticationService authenticationService;

    public MesaController(MesaService mesaService, AuthenticationService authenticationService) {
        this.mesaService = mesaService;
        this.authenticationService = authenticationService;
    }

    @GetMapping
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

    @GetMapping("/{mesaId}")
    public ResponseEntity<MesaInfoResponse> acessarMesa(@PathVariable UUID mesaId) {
        Table mesa = mesaService.encontrarMesaPorId(mesaId);
        if (mesa == null) {
            throw new BlackjackExceptions.MesaNaoEncontradaException(mesa);
        }

        if (authenticationService.getAuthentication(mesa.getToken()) == null) {
            throw new BlackjackExceptions.TokenInvalidoException();
        }

        MesaInfoResponse response = new MesaInfoResponse(mesa);
        return ResponseEntity.ok(response);
    }
}
