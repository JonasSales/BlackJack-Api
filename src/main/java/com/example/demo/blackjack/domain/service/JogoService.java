package com.example.demo.blackjack.domain.service;

import com.example.demo.auth.service.UserService;
import com.example.demo.blackjack.exceptions.BlackjackExceptions;
import com.example.demo.blackjack.model.Crupie;
import com.example.demo.blackjack.model.Player;
import com.example.demo.blackjack.model.Table;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class JogoService {

    private final MesaService mesaService;
    private final UserService userService;

    public JogoService(MesaService mesaService, UserService userService) {
        this.mesaService = mesaService;
        this.userService = userService;
    }

    // Iniciar o jogo em uma mesa
    public void iniciarJogo(UUID mesaId) {
        Table mesa = mesaService.retornarMesa(mesaId);
        mesa.setTempoInicioContador();

        if (mesa.getJogadores().size() < 2) {
            mesa.setTempoInicioContador();
            return;
        }

        if (!mesa.isJogoIniciado()) {
            mesa.iniciarJogo();
            mesa.distribuirCartasIniciais();
            mesa.definirPrimeiroJogador();
            inicioPartida(mesaId);
        }
        ResponseEntity.status(HttpStatus.OK).body(mesa);
    }

    // Realizar uma jogada (HIT ou STAND)
    public ResponseEntity<Map<String, Object>> realizarJogada(
            @PathVariable UUID mesaId,
            HttpServletRequest request,
            @RequestParam String jogada) {

        Map<String, Object> response = new HashMap<>();

        if (jogada == null || jogada.trim().isEmpty()) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", "Jogada não pode ser nula ou vazia.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        jogada = jogada.trim().toLowerCase();

        // Obtém a mesa e valida sua existência
        Table mesa = mesaService.retornarMesa(mesaId);

        if (mesa == null) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", "Mesa não encontrada.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Verifica se o jogo já começou
        if (!mesa.isJogoIniciado()) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", "O jogo ainda não começou.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (request == null) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
        }

        mesa.setTempoInicioContador();
        Player jogador = mesa.encontrarJogador(new Player(userService.getUserFromToken(request).getBody()));

        if (jogador == null) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", "Jogador não encontrado na mesa.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            if (jogada.equals("hit")) {
                realizarJogadaHit(mesa, jogador);
            } else if (jogada.equals("stand")) {
                realizarJogadaStand(mesa, jogador);
            } else {
                response.put("status", HttpStatus.BAD_REQUEST.value());
                response.put("message", "Jogada inválida: " + jogada);
                response.put("jogoIniciado", mesa.isJogoIniciado());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Verifica se todos os jogadores encerraram a mão
            if (mesa.todosJogadoresEncerraramMao()) {
                realizarJogadaCrupie(mesa); // Inicia a jogada do crupiê
                finalizarJogo(mesaId);
                response.put("status", HttpStatus.OK.value());
                response.put("message", "Todos encerram as mãos");
                response.put("jogoIniciado", mesa.isJogoIniciado());
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }

            response.put("status", HttpStatus.OK.value());
            response.put("message", "Jogada realizada com sucesso.");
            response.put("jogada", jogada);
            response.put("jogoIniciado", mesa.isJogoIniciado());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", "erro ao realizar jogada.");
            response.put("jogada", jogada);
            response.put("jogoIniciado", mesa.isJogoIniciado());
            finalizarJogo(mesaId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Método para realizar a jogada "hit"
    private void realizarJogadaHit(Table mesa, Player jogador) {
        jogador.adicionarCarta(mesa.getDeck().distribuirCarta());
        if (jogador.getPontuacao() > 21) {
            jogador.setPerdeuTurno();
            mesa.proximoJogador();
        }
    }

    // Método para realizar a jogada "stand"
    private void realizarJogadaStand(Table mesa, Player jogador) {
        jogador.encerrarMao();
        mesa.proximoJogador();
    }

    private void realizarJogadaCrupie(Table mesa) {
        Player crupie = mesa.getJogadores().stream()
                .filter(jogador -> jogador instanceof Crupie)
                .findFirst()
                .orElseThrow(() -> new BlackjackExceptions.JogadorNaoEncontradoException("Crupie"));

        // O crupiê puxa cartas até atingir pelo menos 17 pontos
        while (crupie.calcularPontuacao() < 17) {
            crupie.adicionarCarta(mesa.getDeck().distribuirCarta());
        }
        crupie.setStand(true);
        if (crupie.calcularPontuacao() > 21){
            crupie.setPerdeuTurno();
        }

    }

    // Finalizar o jogo e determinar o vencedor
    public void finalizarJogo(UUID mesaId) {
        Table mesa = mesaService.retornarMesa(mesaId);
        if (mesa == null) {
            throw new BlackjackExceptions.MesaNaoEncontradaException(mesa);
        }

        Player vencedor = mesa.determinarVencedor();
        mesa.setVencedor(vencedor.getUser());

        if (vencedor.getClass() == Player.class) {
            userService.jogadorVencedor(vencedor);
        }

        mesa.resetarMesa();
        ResponseEntity.status(HttpStatus.OK).body(vencedor);
    }

    private void inicioPartida(UUID mesaId) {
        Table mesa = mesaService.retornarMesa(mesaId);
        if (mesa == null) {
            throw new BlackjackExceptions.MesaNaoEncontradaException(mesa);
        }
        for (Player jogador: mesa.getJogadores()) {
            if (jogador.getClass() != Crupie.class){
                userService.atualizarStatus(jogador);
            }
        }
    }

}