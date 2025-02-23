package com.example.demo.blackjack.domain.service;

import com.example.demo.blackjack.model.Player;
import com.example.demo.blackjack.model.Table;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MesaService {

    private final Map<UUID, Table> mesas = new HashMap<>();

    public Table criarMesa() {
        Table mesa = new Table();
        mesas.put(mesa.getId(), mesa);
        return mesa;
    }

    public Table encontrarMesaPorId(UUID mesaId) {
        return mesas.get(mesaId);
    }

    public List<Table> listarMesas() {
        return new ArrayList<>(mesas.values());
    }

    public boolean adicionarJogador(UUID mesaId, Player jogador) {
        Table mesa = mesas.get(mesaId);
        if (mesa != null) {
            return mesa.adicionarJogador(jogador);
        }
        return false;
    }

    public void iniciarJogo(UUID mesaId) {
        Table mesa = mesas.get(mesaId);
        if (mesa != null) {
            mesa.iniciarJogo();
        }
    }
}