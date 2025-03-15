package com.example.demo.blackjack.utils;

public  class AlgoritmosBusca<T>{

    public static int buscaBinaria(int valor,int[] vetor){
        int inicio, fim, meio;
        inicio = 0;
        fim = vetor.length;
        meio = (fim+inicio)/2;

        while (inicio !=meio){
            if (valor == vetor[meio]){
                return meio;
            }
            else if (valor < vetor[meio]){
                fim = meio;
                meio = (fim+inicio)/2;
            }
            else if (valor > vetor[meio]){
                inicio = meio;
                meio = (fim+inicio)/2;
            }

        }
        return -1;
    }


    public int buscaLinear(T valor,T[] vetor){
        for (int i = 0; i < vetor.length; i++){
            if (vetor[i].equals(valor)){
                return i;
            }
        }
        return -1;
    }

}
