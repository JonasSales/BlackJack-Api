package com.example.demo.blackjack.utils;

import com.example.demo.auth.dto.UserDTO;

import java.util.List;

public class Ordenacao{



    public static List<UserDTO> ordenarPorMoney(List<UserDTO> vetor, int inicio, int fim){
        int i = inicio;
        int j = fim;
        UserDTO pivot = vetor.get((inicio + fim) / 2);

        while(i < j){
            while (vetor.get(i).getMoney() < pivot.getMoney()){
                i += 1;
            }
            while (vetor.get(j).getMoney() > pivot.getMoney()){
                j -= 1;
            }
            if (i < j){
                UserDTO aux = vetor.get(i);
                vetor.set(i, vetor.get(j));
                vetor.set(j, aux);
                i +=1;
                j -=1;
            }
        }
        if (j > inicio){
            ordenarPorMoney(vetor, inicio, j);
        }
        else if (i < fim){
            ordenarPorMoney(vetor, i, fim);
        }
        return vetor;
    }


    public static List<UserDTO> ordenarPorVitoria(List<UserDTO> vetor, int inicio, int fim){
        int i = inicio;
        int j = fim;
        UserDTO pivot = vetor.get((inicio + fim) / 2);

        while(i < j){
            while (vetor.get(i).getPartidasGanhas() < pivot.getPartidasGanhas()){
                i += 1;
            }
            while (vetor.get(j).getPartidasGanhas() > pivot.getPartidasGanhas()){
                j -= 1;
            }
            if (i < j){
                UserDTO aux = vetor.get(i);
                vetor.set(i, vetor.get(j));
                vetor.set(j, aux);
                i +=1;
                j -=1;
            }
        }
        if (j > inicio){
            ordenarPorVitoria(vetor, inicio, j);
        }
        else if (i < fim){
            ordenarPorVitoria(vetor, i, fim);
        }
        return vetor;
    }
}
