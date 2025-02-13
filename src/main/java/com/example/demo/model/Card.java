package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

public class Card {

    public enum Naipe {
        OUROS, PAUS, COPAS, ESPADAS
    }

    public enum Letra {
        AS(11), DOIS(2), TRES(3),
        QUATRO(4), CINCO(5), SEIS(6),
        SETE(7), OITO(8), NOVE(9),
        DEZ(10), VALETE(10), DAMA(10), REI(10);

        private final int[] valores;

        Letra(int... valores) {
            this.valores = valores; // Usando varargs para permitir múltiplos valores
        }

        public int[] getValores() {
            return valores;
        }
    }

    private Naipe naipe;
    private Letra letra;

    // Construtor
    public Card(Naipe naipe, Letra letra) {
        this.naipe = naipe;
        this.letra = letra;
    }

    public Naipe getNaipe() {
        return naipe;
    }

    public void setNaipe(Naipe naipe) {
        this.naipe = naipe;
    }

    public Letra getLetra() {
        return letra;
    }

    public void setLetra(Letra letra) {
        this.letra = letra;
    }

    // Método para obter o valor da carta (caso o ÁS, pode retornar tanto 1 quanto 11)
    public int[] getValores() {
        return letra.getValores();
    }

    @Override
    public String toString() {
        return getLetra() + " de " + getNaipe();
    }

    public int getValor(){
        return letra.getValores()[0];
    }

    // Método para criar o baralho único
    public static List<Card> criarBaralho(int quantidadeDeBaralhos) {
        List<Card> baralho = new ArrayList<>();
        for (int i = 0; i < quantidadeDeBaralhos; i++) {
            for (Card.Naipe naipe : Card.Naipe.values()) {
                adicionarCartasPorNaipe(baralho, naipe);
            }
        }
        return baralho;
    }

    // Método auxiliar para adicionar as cartas por naipe
    private static void adicionarCartasPorNaipe(List<Card> baralho, Card.Naipe naipe) {
        for (Card.Letra letra : Card.Letra.values()) {
            baralho.add(new Card(naipe, letra));
        }
    }
}