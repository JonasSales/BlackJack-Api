package com.example.demo.blackjack.api.controller;

import com.example.demo.auth.dto.UserDTO;
import com.example.demo.auth.service.AuthenticationService;
import com.example.demo.auth.service.UserService;
import com.example.demo.blackjack.api.DTO.*;
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

    // Método utilitário para validar a mesa
    private Table validarMesa(UUID mesaId) {
        Table mesa = mesaService.encontrarMesaPorId(mesaId);
        if (mesa == null) {
            throw new BlackjackExceptions.MesaNaoEncontradaException(mesa);
        }
        return mesa;
    }

    // Método utilitário para validar o jogador na mesa
    private Player validarJogadorNaMesa(Table mesa, HttpServletRequest request) {
        UserDTO userDTO = userService.getUserFromToken(request);
        Player jogador = mesa.encontrarJogador(new Player(userDTO));
        if (jogador == null) {
            throw new BlackjackExceptions.JogadorNaoEncontradoException("Jogador não encontrado na mesa.");
        }
        return jogador;
    }

    // Endpoint para listar jogadores de uma mesa
    @GetMapping("/mesas/{mesaId}/jogadores")
    public ResponseEntity<List<Player>> listarJogadores(@PathVariable UUID mesaId) {
        Table mesa = validarMesa(mesaId);
        return ResponseEntity.ok(mesa.getJogadores());
    }

    // Endpoint para obter o jogador atual
    @GetMapping("/mesas/{mesaId}/jogadoratual")
    public ResponseEntity<UserDTO> obterJogadorAtual(@PathVariable UUID mesaId) {
        Table mesa = validarMesa(mesaId);
        return ResponseEntity.ok(mesa.getJogadorAtual().getUser());
    }

    // Endpoint para adicionar um jogador à mesa
    @PostMapping("/mesas/{mesaId}/adicionar")
    public ResponseEntity<Map<String, String>> adicionarJogador(@PathVariable UUID mesaId, HttpServletRequest request) {
        UserDTO userDTO = userService.getUserFromToken(request);
        Player jogador = new Player(userDTO);

        if (mesaService.jogadorEstaEmQualquerMesa(jogador)) {
            throw new BlackjackExceptions.JogadorJaNaMesaException(jogador.getUser().getName());
        }

        Table mesa = validarMesa(mesaId);
        mesaService.adicionarJogador(mesa, jogador);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Jogador " + jogador.getUser().getName() + " adicionado à mesa!");
        return ResponseEntity.ok(response);
    }

    // Endpoint para iniciar o jogo
    @PostMapping("/mesas/{mesaId}/iniciar")
    public ResponseEntity<Map<String, String>> iniciarJogo(@PathVariable UUID mesaId) {
        Table mesa = validarMesa(mesaId);

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

    // Endpoint para obter as cartas dos jogadores
    @GetMapping("/mesas/{mesaId}/cartas")
    public ResponseEntity<List<Card>> obterCartasDeJogadores(@PathVariable UUID mesaId, HttpServletRequest request) {
        Table mesa = validarMesa(mesaId);
        Player jogador = validarJogadorNaMesa(mesa, request);
        return ResponseEntity.ok(jogador.getMao());
    }

    // Endpoint para realizar uma jogada (HIT ou STAND)
    @PostMapping("/mesas/{mesaId}/jogada")
    public ResponseEntity<JogadaResponse> realizarJogada(
            @PathVariable UUID mesaId,
            HttpServletRequest request,
            @RequestBody PlayerRequest jogada) {

        Table mesa = validarMesa(mesaId);
        Player jogador = validarJogadorNaMesa(mesa, request);

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

    // Endpoint para acessar uma mesa específica
    @GetMapping("/mesas/{mesaId}")
    public ResponseEntity<MesaInfoResponse> acessarMesa(@PathVariable UUID mesaId) {
        Table mesa = validarMesa(mesaId);

        if (authenticationService.getAuthentication(mesa.getToken()) == null) {
            throw new BlackjackExceptions.TokenInvalidoException();
        }

        MesaInfoResponse response = new MesaInfoResponse();
        response.setMesaId(mesa.getId());
        response.setJogoIniciado(mesa.isJogoIniciado());
        response.setQuantidadeDeJogadores(mesa.getJogadores().size());
        response.setMesaEncerrada(mesa.todosJogadoresEncerraramMao());
        response.setJogadores(mesa.getJogadores().stream()
                .map(Player::getUser)
                .collect(Collectors.toList()));
        response.setTempoInicio(mesa.getTempoInicioContador());
        response.setTempoDecorrido(mesa.getTempoDecorrido());
        return ResponseEntity.ok(response);
    }

    // Endpoint para listar todas as mesas
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