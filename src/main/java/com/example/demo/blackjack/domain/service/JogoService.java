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
            subtrairDinheiro(mesaId);
        }
        ResponseEntity.status(HttpStatus.OK).body(mesa);
    }

    // Realizar uma jogada (HIT ou STAND)
    public ResponseEntity<String> realizarJogada(UUID mesaId, HttpServletRequest request, String jogada) {
        if (jogada == null || jogada.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Jogada não pode ser nula ou vazia.");
        }

        jogada = jogada.trim().toLowerCase();

        // Obtém a mesa e valida sua existência
        Table mesa = mesaService.retornarMesa(mesaId);

        if (mesa == null) {
            throw new BlackjackExceptions.MesaNaoEncontradaException(mesa);
        }

        // Verifica se o jogo já começou
        if (!mesa.isJogoIniciado()) {
            throw new BlackjackExceptions.JogoJaIniciadoException(mesaId);
        }
        mesa.setTempoInicioContador();
        Player jogador = mesa.encontrarJogador(new Player(userService.getUserFromToken(request)));
        if (jogador == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Jogador não encontrado na mesa.");
        }

        try {
            if (jogada.equals("hit")) {
                realizarJogadaHit(mesa, jogador);
            } else if (jogada.equals("stand")) {
                realizarJogadaStand(mesa, jogador);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Jogada inválida: " + jogada);
            }

            // Verifica se todos os jogadores encerraram a mão
            if (mesa.todosJogadoresEncerraramMao()) {
                realizarJogadaCrupie(mesa); // Inicia a jogada do crupiê
                finalizarJogo(mesaId);
            }

            return ResponseEntity.status(HttpStatus.OK).body("Jogada realizada: " + jogada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar a jogada.");
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
        mesa.determinarVencedor();
    }

    // Finalizar o jogo e determinar o vencedor
    public void finalizarJogo(UUID mesaId) {
        Table mesa = mesaService.retornarMesa(mesaId);
        if (mesa == null) {
            throw new BlackjackExceptions.MesaNaoEncontradaException(mesa);
        }

        Player vencedor = mesa.determinarVencedor();
        mesa.setVencedor(vencedor.getUser());
        userService.adicionarMoney(vencedor);
        mesa.resetarMesa();
        ResponseEntity.status(HttpStatus.OK).body(vencedor);
    }

    private void subtrairDinheiro(UUID mesaId) {
        Table mesa = mesaService.retornarMesa(mesaId);
        if (mesa == null) {
            throw new BlackjackExceptions.MesaNaoEncontradaException(mesa);
        }
        for (Player jogador: mesa.getJogadores()) {
            if (jogador.getClass() != Crupie.class){
                userService.subtrairMoney(jogador);
            }
        }
    }

}