package com.example.demo.blackjack.api.controller;

import com.example.demo.auth.dto.UserDTO;
import com.example.demo.auth.service.AuthenticationService;
import com.example.demo.auth.service.UserService;
import com.example.demo.blackjack.api.DTO.JogadaResponse;
import com.example.demo.blackjack.api.DTO.playerRequest;
import com.example.demo.blackjack.domain.service.BlackjackGameService;
import com.example.demo.blackjack.domain.service.MesaService;
import com.example.demo.blackjack.exceptions.BlackjackExceptions;
import com.example.demo.blackjack.model.Card;
import com.example.demo.blackjack.model.Player;
import com.example.demo.blackjack.model.Table;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
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

       // Endpoint para listar jogadores de uma mesa
    @GetMapping("/mesas/{mesaId}/jogadores")
    public ResponseEntity<List<Player>> listarJogadores(@PathVariable UUID mesaId) {
        Table mesa = mesaService.encontrarMesaPorId(mesaId);
        if (mesa == null) {
            throw new BlackjackExceptions.MesaNaoEncontradaException(mesa);
        }
        return ResponseEntity.ok(mesa.getJogadores());
    }

    // Endpoint para obter o jogador atual
    @GetMapping("/mesas/{mesaId}/jogadoratual")
    public ResponseEntity<UserDTO> listarJogadoresAtuais(@PathVariable UUID mesaId) {
        Table mesa = mesaService.encontrarMesaPorId(mesaId);
        if (mesa == null) {
            throw new BlackjackExceptions.MesaNaoEncontradaException(mesa);
        }
        return ResponseEntity.ok(mesa.getJogadorAtual().getUser());
    }

    // Endpoint para adicionar um jogador à mesa
    @PostMapping("/mesas/{mesaId}/adicionar")
    public ResponseEntity<Map<String, String>> adicionarJogador(@PathVariable UUID mesaId, HttpServletRequest request) {
        UserDTO userDTO = userService.getUserFromToken(request);
        Player jogador = new Player(userDTO);

        if (mesaService.jogadorEstaEmQualquerMesa(jogador)){
            throw new BlackjackExceptions.JogadorJaNaMesaException(jogador.getUser().getName());
        }
        Table mesa = mesaService.encontrarMesaPorId(mesaId);
        boolean sucesso = mesaService.adicionarJogador(mesa, jogador);
        if (!sucesso) {
            throw new BlackjackExceptions.MesaNaoEncontradaException(mesa);
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Jogador " + jogador.getUser().getName() + " adicionado à mesa!");
        return ResponseEntity.ok(response);
    }

    // Endpoint para iniciar o jogo
    @PostMapping("/mesas/{mesaId}/iniciar")
    public ResponseEntity<Map<String, String>> iniciarJogo(@PathVariable UUID mesaId) {
        Table mesa = mesaService.encontrarMesaPorId(mesaId);
        Map<String, String> response = new HashMap<>();
        if (mesa.getJogadores().size() < 2) {
            response.put("message", "O jogo para começar precisa de ao menos 2 jogadores");
            mesa.iniciarContador(60);
            return ResponseEntity.ok(response);
        }
        if (!mesa.getJogoIniciado()) {
            blackjackGameService.iniciarJogo(mesa);
            response.put("message", "Jogo iniciado! Cartas distribuídas!");
            response.put("mesaToken", mesa.getToken());
        }else {
            response.put("message", "Jogo iniciado!");
        }
        return ResponseEntity.ok(response);
    }

    // Endpoint para obter as cartas dos jogadores
    @GetMapping("/mesas/{mesaId}/cartas")
    public ResponseEntity<List<Card>> getCartasDeJogadores(@PathVariable UUID mesaId, HttpServletRequest request) {
        Table mesa = mesaService.encontrarMesaPorId(mesaId);
        UserDTO userDTO = userService.getUserFromToken(request);

        if (mesa == null) {
            throw new BlackjackExceptions.MesaNaoEncontradaException(mesa);
        }

        Player jogador = mesa.encontrarJogador(new Player(userDTO));

        if (jogador == null){
            throw new BlackjackExceptions.JogadorJaNaMesaException("Jogador não existe");
        }

        List<Card> cartasDoJogador = jogador.getMao();

        return ResponseEntity.ok(cartasDoJogador);
    }

    // Endpoint para realizar uma jogada (HIT ou STAND)
    @PostMapping("/mesas/{mesaId}/jogada")
    public ResponseEntity<JogadaResponse> realizarJogada(
            @PathVariable UUID mesaId,
            HttpServletRequest request,
            @RequestBody playerRequest jogada) {

        // Obtém o usuário a partir do token
        UserDTO userDTO = userService.getUserFromToken(request);

        // Encontra a mesa pelo ID
        Table mesa = mesaService.encontrarMesaPorId(mesaId);
        if (mesa == null) {
            throw new BlackjackExceptions.MesaNaoEncontradaException(mesa);
        }
        // Encontra o jogador na mesa
        Player jogador = mesa.encontrarJogador(new Player(userDTO));
        if (jogador == null) {
            throw new BlackjackExceptions.JogadorNaoEncontradoException("Jogador não encontrado na mesa.");
        }

        // Realiza a jogada
        boolean jogadaRealizada = blackjackGameService.jogada(jogador, jogada.getJogada(), mesa);

        // Prepara a resposta
        JogadaResponse response = new JogadaResponse();
        if (jogadaRealizada) {
            response.setMensagem("Jogada realizada com sucesso.");
        } else {
            response.setMensagem("Jogada inválida.");
        }

        // Verifica se o jogo terminou
        Player vencedor = null;
        if (blackjackGameService.verificarTodosEncerraram(mesa)) {
            vencedor = blackjackGameService.finalizarJogo(mesa);
            if (vencedor != null) {
                response.setVencedor(vencedor.getUser().getName());
                response.setPontuacaoVencedor(vencedor.calcularPontuacao());
                response.setMensagem("O jogo terminou! O vencedor é: " + vencedor.getUser().getName() + " com "
                        + vencedor.getPontuacao() + " pontos");
            } else {
                response.setMensagem("O jogo terminou! Não houve vencedor.");
            }

        }
        if (vencedor != null){
            mesa.resetarMesa();
        }
            return ResponseEntity.ok(response);
    }


    // Endpoint para acessar uma mesa específica
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
                "mesaEncerrada?", mesa.todosJogadoresEncerraramMao(),
                "jogadores", mesa.getJogadores().stream()
                        .map(Player::getUser)
                        .collect(Collectors.toList())
        ));
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

    // Endpoint para obter o tempo restante de uma mesa
    @GetMapping("/mesas/{mesaId}/tempo-restante")
    public ResponseEntity<Map<String, Integer>> getTempoRestante(@PathVariable UUID mesaId) {
        Table mesa = mesaService.encontrarMesaPorId(mesaId);
        if (mesa == null) {
            throw new BlackjackExceptions.MesaNaoEncontradaException(mesa);
        }

        int tempoRestante = mesa.getTempoRestante();
        Map<String, Integer> response = new HashMap<>();
        response.put("tempoRestante", tempoRestante);
        return ResponseEntity.ok(response);
    }
}