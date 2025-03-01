package com.example.demo.blackjack.domain.service;

import com.example.demo.auth.service.AuthenticationService;
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
        criarNMesas();
    }

    public Table criarMesa() {
        Table mesa = new Table(authenticationService); // Passa o AuthenticationService para o construtor
        mesas.put(mesa.getId(), mesa);
        return mesa;
    }

    public Table encontrarMesaPorId(UUID mesaId) {
        return mesas.get(mesaId);
    }

    public List<Table> listarMesas() {
        return new ArrayList<>(mesas.values());
    }

    public boolean adicionarJogador(Table mesa, Player jogador) {
        if (mesa != null) {
            jogador.setJogandoAtualmente(true);
            return mesa.adicionarJogador(jogador);
        }
        return false;
    }

    private void criarNMesas(){
        for (int i = 0; i < 10; i++) {
            criarMesa();
        }
    }

    public boolean jogadorEstaEmQualquerMesa(Player jogador) {
        if (jogador == null) {
            throw new IllegalArgumentException("Jogador não pode ser nulo.");
        }

        for (Table mesa : mesas.values()) {
            for (Player p : mesa.getJogadores()) {
                if (p.equals(jogador)) {
                    return true;
                }
            }
        }
        return false;
    }
}