package com.example.demo.blackjack.domain.service;

import com.example.demo.auth.dto.UserDTO;
import com.example.demo.auth.service.UserService;
import com.example.demo.blackjack.api.DTO.PlayerRequest;
import com.example.demo.blackjack.exceptions.BlackjackExceptions;
import com.example.demo.blackjack.model.Card;
import com.example.demo.blackjack.model.Player;
import com.example.demo.blackjack.model.Table;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PlayerService {

    private final MesaService mesaService;
    private final UserService userService;

    public PlayerService(MesaService mesaService, UserService userService) {
        this.mesaService = mesaService;
        this.userService = userService;
    }

    // Adicionar um jogador a uma mesa
    public ResponseEntity<Player> adicionarJogadorAUmaMesa(UUID mesaId, HttpServletRequest response) {
        Table mesa = mesaService.retornarMesa(mesaId);
        Player jogador = new Player(userService.getUserFromToken(response));
        if (mesa == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(jogador);
        }

        if (mesaService.jogadorEstaEmQualquerMesa(jogador)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(jogador);
        }

        mesa.adicionarJogador(jogador);
        return ResponseEntity.status(HttpStatus.OK).body(jogador);
    }

    // Encontrar um jogador em uma mesa
    public Player encontrarJogadorNaMesa(UUID mesaId, UserDTO userDTO) {
        Table mesa = mesaService.retornarMesa(mesaId);
        if (mesa == null) {
            throw new BlackjackExceptions.MesaNaoEncontradaException(mesa);
        }

        Player jogador = mesa.encontrarJogador(new Player(userDTO));
        if (jogador == null) {
            throw new BlackjackExceptions.JogadorNaoEncontradoException("Jogador não encontrado na mesa.");
        }

        return jogador;
    }

    // Listar jogadores de uma mesa
    public ResponseEntity<List<Player>> listarJogadores(UUID mesaId) {
        Table mesa = mesaService.retornarMesa(mesaId);
        if (mesa == null) {
            throw new BlackjackExceptions.MesaNaoEncontradaException(mesa);
        }
        return  new ResponseEntity<>(mesa.getJogadores(), HttpStatus.OK);
    }

    public ResponseEntity<List<Card>> obterCartasDeJogadores(UUID mesaId, HttpServletRequest request) {
        Player jogador = encontrarJogadorNaMesa(mesaId,userService.getUserFromToken(request));
        return new ResponseEntity<>(jogador.getMao(), HttpStatus.OK);
    }
}
