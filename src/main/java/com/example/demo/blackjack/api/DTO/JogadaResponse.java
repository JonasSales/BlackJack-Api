package com.example.demo.blackjack.api.DTO;

public class JogadaResponse {
    private String mensagem;
    private String vencedor;
    private int pontuacaoVencedor;

    // Getters e Setters
    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getVencedor() {
        return vencedor;
    }

    public void setVencedor(String vencedor) {
        this.vencedor = vencedor;
    }

    public int getPontuacaoVencedor() {
        return pontuacaoVencedor;
    }

    public void setPontuacaoVencedor(int pontuacaoVencedor) {
        this.pontuacaoVencedor = pontuacaoVencedor;
    }
}
