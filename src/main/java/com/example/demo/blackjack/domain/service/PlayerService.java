package com.example.demo.blackjack.domain.service;

import com.example.demo.auth.dto.UserDTO;
import com.example.demo.blackjack.model.Player;
import com.example.demo.blackjack.model.Table;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerService {

    public Player criarJogador(UserDTO userDTO) {
        return new Player(userDTO);
    }

    public List<Player> listarJogadores(Table mesa) {
        return mesa.getJogadores();
    }

    public List<Player> listarJogadoresAtuais(Table mesa) {
        return mesa.getJogadores().stream()
                .filter(player -> player.isJogadorAtual() && !player.isPerdeuTurno() && !player.isStand())
                .collect(Collectors.toList());
    }
}
