package com.example.demo.blackjack.model;


import java.util.ArrayList;
import java.util.List;


public class Card {
    private final String naipe;
    private final String letra;
    private final int[] valores;

    private Card(String naipe, String letra) {
        // Validação de entrada para o naipe e letra
        if (!isNaipeValido(naipe)) {
            throw new IllegalArgumentException("Naipe inválido.");
        }
        if (!isLetraValida(letra)) {
            throw new IllegalArgumentException("Letra inválida.");
        }

        this.naipe = naipe;
        this.letra = letra;
        this.valores = determinarValores(letra);
    }

    private boolean isNaipeValido(String naipe) {
        return naipe.equals("Copas") || naipe.equals("Ouros") || naipe.equals("Espadas") || naipe.equals("Paus");
    }

    private boolean isLetraValida(String letra) {
        return letra.equals("A") || letra.equals("2") || letra.equals("3") || letra.equals("4") ||
                letra.equals("5") || letra.equals("6") || letra.equals("7") || letra.equals("8") ||
                letra.equals("9") || letra.equals("10") || letra.equals("J") || letra.equals("Q") ||
                letra.equals("K");
    }

    private int[] determinarValores(String letra) {
        return switch (letra) {
            case "A" -> new int[]{1, 11};
            case "2" -> new int[]{2};
            case "3" -> new int[]{3};
            case "4" -> new int[]{4};
            case "5" -> new int[]{5};
            case "6" -> new int[]{6};
            case "7" -> new int[]{7};
            case "8" -> new int[]{8};
            case "9" -> new int[]{9};
            case "10", "J", "Q", "K" -> new int[]{10};
            default -> new int[]{1, 11}; // Ás
        };
    }

    public static List<Card> criarBaralho(int quantidadeBaralhos) {
        List<Card> baralho = new ArrayList<>();
        String[] naipes = {"Copas", "Ouros", "Espadas", "Paus"};
        String[] letras = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};

        for (int i = 0; i < quantidadeBaralhos; i++) {
            for (String naipe : naipes) {
                for (String letra : letras) {
                    baralho.add(new Card(naipe, letra));
                }
            }
        }
        return baralho;
    }

    public String getNaipe() {
        return naipe;
    }

    public String getLetra() {
        return letra;
    }

    public int[] getValores() {
        return valores;
    }

    @Override
    public String toString() {
        return getLetra() + " de " + getNaipe();
    }



}
