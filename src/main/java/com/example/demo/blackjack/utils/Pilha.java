package com.example.demo.blackjack.utils;


import java.util.List;

public class Pilha<T> {


    private  int size;
    private  NO<T> topo;


    public Pilha(){
        this.size = -1;
        this.topo = null;
    }

    public Pilha(List<T> cards){
        for (T card : cards) {
            push(card);
        }
    }



    public void push(T x) {
        NO<T> nodo = new NO<>(x);
        nodo.next = topo;
        topo = nodo;
        size++;
    }

    public T pop(){
        if (isEmpty()) {
            return null;
        }
        T valor = topo.getData();
        topo = topo.next;
        size--;
        return valor;
    }

    public boolean isEmpty(){
        return size == -1;
    }

    public Object peek(){
        if (isEmpty()) {
            return null;
        }
        return topo.getData();
    }


    public int getSize() {
        return size;
    }
}
