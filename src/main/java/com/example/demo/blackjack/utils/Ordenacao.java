package com.example.demo.blackjack.utils;

import com.example.demo.auth.dto.UserDTO;

import java.util.List;

public class Ordenacao{

    public static void ordenarPorMoney(List<UserDTO> vetor, int inicio, int fim) {
        if (inicio < fim) {
            int i = inicio;
            int j = fim;
            UserDTO pivot = vetor.get((inicio + fim) / 2);

            while (i <= j) {
                // Avança i enquanto o elemento for menor que o pivot
                while (vetor.get(i).getMoney() < pivot.getMoney()) {
                    i++;
                }
                // Recua j enquanto o elemento for maior que o pivot
                while (vetor.get(j).getMoney() > pivot.getMoney()) {
                    j--;
                }
                // Troca os elementos se i <= j
                if (i <= j) {
                    UserDTO aux = vetor.get(i);
                    vetor.set(i, vetor.get(j));
                    vetor.set(j, aux);
                    i++;
                    j--;
                }
            }

            // Recursão para as sublistas
            if (inicio < j) {
                ordenarPorMoney(vetor, inicio, j);
            }
            if (i < fim) {
                ordenarPorMoney(vetor, i, fim);
            }
        }
    }


    public static void ordenarPorVitoria(List<UserDTO> vetor, int inicio, int fim) {
        if (inicio < fim) {
            int i = inicio;
            int j = fim;
            UserDTO pivot = vetor.get((inicio + fim) / 2);

            while (i <= j) {
                while (vetor.get(i).getPartidasGanhas() < pivot.getPartidasGanhas()) {
                    i++;
                }
                while (vetor.get(j).getPartidasGanhas() > pivot.getPartidasGanhas()) {
                    j--;
                }

                if (i <= j) {
                    UserDTO aux = vetor.get(i);
                    vetor.set(i, vetor.get(j));
                    vetor.set(j, aux);
                    i++;
                    j--;
                }
            }

            if (inicio < j) {
                ordenarPorVitoria(vetor, inicio, j);
            }
            if (i < fim) {
                ordenarPorVitoria(vetor, i, fim);
            }
        }
    }
}
