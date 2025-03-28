package com.example.demo.blackjack.api.DTO;

import com.example.demo.auth.dto.UserDTO;
import com.example.demo.blackjack.model.Player;
import com.example.demo.blackjack.model.Table;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MesaInfoResponse {
    private UUID mesaId;
    private boolean jogoIniciado;
    private int quantidadeDeJogadores;
    private List<UserDTO> jogadores;
    private long tempoInicio;
    private long tempoDecorrido;
    private UserDTO vencedor;
    private UserDTO jogadorAtual;

    public MesaInfoResponse(Table mesa){
        this.mesaId = mesa.getId();
        this.jogoIniciado = mesa.isJogoIniciado();
        this.quantidadeDeJogadores = mesa.getJogadores().size();
        this.tempoInicio = mesa.getTempoInicioContador();
        this.tempoDecorrido = mesa.getTempoDecorrido();
        this.jogadores = mesa.getJogadores().stream()
                .map(Player::getUser)
                .collect(Collectors.toList());
        this.vencedor = mesa.getVencedor();
        this.jogadorAtual = mesa.getJogadorAtual().getUser();
    }

    // Getters e Setters
    public UUID getMesaId() {
        return mesaId;
    }

    public void setMesaId(UUID mesaId) {
        this.mesaId = mesaId;
    }

    public boolean isJogoIniciado() {
        return jogoIniciado;
    }

    public void setJogoIniciado(boolean jogoIniciado) {
        this.jogoIniciado = jogoIniciado;
    }

    public int getQuantidadeDeJogadores() {
        return quantidadeDeJogadores;
    }

    public void setQuantidadeDeJogadores(int quantidadeDeJogadores) {
        this.quantidadeDeJogadores = quantidadeDeJogadores;
    }

    public List<UserDTO> getJogadores() {
        return jogadores;
    }

    public void setJogadores(List<UserDTO> jogadores) {
        this.jogadores = jogadores;
    }


    public long getTempoInicio() {
        return tempoInicio;
    }


    public void setTempoInicio(long tempoInicio) {
        this.tempoInicio = tempoInicio;
    }

    public long getTempoDecorrido() {
        return tempoDecorrido;
    }

    public void setTempoDecorrido(long tempoDecorrido) {
        this.tempoDecorrido = tempoDecorrido;
    }

    public UserDTO getVencedor() {
        return vencedor;
    }

    public void setVencedor(UserDTO vencedor) {
        this.vencedor = vencedor;
    }

    public UserDTO getJogadorAtual() {
        return jogadorAtual;
    }

    public void setJogadorAtual(UserDTO jogadorAtual) {
        this.jogadorAtual = jogadorAtual;
    }
}