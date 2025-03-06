package com.example.demo.blackjack.domain.service;

import com.example.demo.auth.service.AuthenticationService;
import com.example.demo.blackjack.exceptions.BlackjackExceptions;
import com.example.demo.blackjack.model.Table;
import com.example.demo.blackjack.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MesaService {

    private final Map<UUID, Table> mesas = new HashMap<>();
    private final AuthenticationService authenticationService;

    @Autowired
    public MesaService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
        criarNMesasIniciais(10); // Cria 10 mesas iniciais
    }

    // Cria uma nova mesa e a adiciona ao mapa de mesas
    public void criarMesa() {
        Table mesa = new Table(authenticationService);
        mesas.put(mesa.getId(), mesa);
    }

    // Encontra uma mesa pelo ID
    public Table encontrarMesaPorId(UUID mesaId) {
        Table mesa = mesas.get(mesaId);
        if (mesa == null) {
            throw new BlackjackExceptions.MesaNaoEncontradaException(mesa);
        }
        return mesa;
    }

    // Lista todas as mesas disponíveis
    public List<Table> listarMesas() {
        return new ArrayList<>(mesas.values());
    }

    // Adiciona um jogador a uma mesa específica
    public void adicionarJogador(Table mesa, Player jogador) {
        if (mesa == null) {
            throw new IllegalArgumentException("Mesa não pode ser nula.");
        }
        if (jogador == null) {
            throw new IllegalArgumentException("Jogador não pode ser nulo.");
        }

        if (jogadorEstaEmQualquerMesa(jogador)) {
            throw new BlackjackExceptions.JogadorJaNaMesaException(jogador.getUser().getName());
        }

        jogador.setJogandoAtualmente(true);
        mesa.adicionarJogador(jogador);
    }

    // Verifica se um jogador está em qualquer mesa
    public boolean jogadorEstaEmQualquerMesa(Player jogador) {
        if (jogador == null) {
            throw new IllegalArgumentException("Jogador não pode ser nulo.");
        }

        return mesas.values().stream()
                .flatMap(mesa -> mesa.getJogadores().stream())
                .anyMatch(p -> p.equals(jogador));
    }

    // Cria um número inicial de mesas
    private void criarNMesasIniciais(int quantidade) {
        for (int i = 0; i < quantidade; i++) {
            criarMesa();
        }
    }

}
