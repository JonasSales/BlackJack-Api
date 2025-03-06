package com.example.demo.blackjack.api.controller;

import com.example.demo.auth.dto.UserDTO;
import com.example.demo.auth.service.UserService;
import com.example.demo.blackjack.api.DTO.JogadaResponse;
import com.example.demo.blackjack.api.DTO.PlayerRequest;
import com.example.demo.blackjack.domain.service.BlackjackGameService;
import com.example.demo.blackjack.domain.service.MesaService;
import com.example.demo.blackjack.exceptions.BlackjackExceptions;
import com.example.demo.blackjack.model.Card;
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
@RequestMapping("/blackjack/jogo/{mesaId}")
public class JogoController {

    private final BlackjackGameService blackjackGameService;
    private final MesaService mesaService;
    private final UserService userService;

    public JogoController(BlackjackGameService blackjackGameService, MesaService mesaService, UserService userService) {
        this.blackjackGameService = blackjackGameService;
        this.mesaService = mesaService;
        this.userService = userService;
    }

    @PostMapping("/iniciar")
    public ResponseEntity<Map<String, String>> iniciarJogo(@PathVariable UUID mesaId) {
        Table mesa = mesaService.encontrarMesaPorId(mesaId);
        if (mesa == null) {
            throw new BlackjackExceptions.MesaNaoEncontradaException(mesa);
        }

        Map<String, String> response = new HashMap<>();
        if (mesa.getJogadores().size() < 2) {
            response.put("message", "O jogo para começar precisa de ao menos 2 jogadores");
            mesa.setTempoInicioContador();
            return ResponseEntity.ok(response);
        }

        if (!mesa.isJogoIniciado()) {
            blackjackGameService.iniciarJogo(mesa);
            response.put("message", "Jogo iniciado! Cartas distribuídas!");
            response.put("mesaToken", mesa.getToken());
            mesa.setTempoInicioContador();
        } else {
            response.put("message", "Jogo já iniciado!");
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/cartas")
    public ResponseEntity<List<Card>> obterCartasDeJogadores(@PathVariable UUID mesaId, HttpServletRequest request) {
        Table mesa = mesaService.encontrarMesaPorId(mesaId);
        if (mesa == null) {
            throw new BlackjackExceptions.MesaNaoEncontradaException(mesa);
        }

        UserDTO userDTO = userService.getUserFromToken(request);
        Player jogador = mesa.encontrarJogador(new Player(userDTO));
        if (jogador == null) {
            throw new BlackjackExceptions.JogadorNaoEncontradoException("Jogador não encontrado na mesa.");
        }

        return ResponseEntity.ok(jogador.getMao());
    }

    @PostMapping("/jogada")
    public ResponseEntity<JogadaResponse> realizarJogada(
            @PathVariable UUID mesaId,
            HttpServletRequest request,
            @RequestBody PlayerRequest jogada) {

        Table mesa = mesaService.encontrarMesaPorId(mesaId);
        if (mesa == null) {
            throw new BlackjackExceptions.MesaNaoEncontradaException(mesa);
        }

        UserDTO userDTO = userService.getUserFromToken(request);
        Player jogador = mesa.encontrarJogador(new Player(userDTO));
        if (jogador == null) {
            throw new BlackjackExceptions.JogadorNaoEncontradoException("Jogador não encontrado na mesa.");
        }

        boolean jogadaRealizada = blackjackGameService.jogada(jogador, jogada.getJogada(), mesa);
        JogadaResponse response = new JogadaResponse();
        response.setMensagem(jogadaRealizada ? "Jogada realizada com sucesso." : "Jogada inválida.");

        if (blackjackGameService.verificarTodosEncerraram(mesa)) {
            Player vencedor = blackjackGameService.finalizarJogo(mesa);
            if (vencedor != null) {
                response.setVencedor(vencedor.getUser().getName());
                response.setPontuacaoVencedor(vencedor.calcularPontuacao());
                response.setMensagem("O jogo terminou! O vencedor é: " + vencedor.getUser().getName() + " com "
                        + vencedor.getPontuacao() + " pontos");
            } else {
                response.setMensagem("O jogo terminou! Não houve vencedor.");
            }
            mesa.resetarMesa();
        }

        return ResponseEntity.ok(response);
    }
}