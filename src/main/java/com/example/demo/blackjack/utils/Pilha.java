package com.example.demo.blackjack.utils;

public class Pilha {


    private  int size;
    private  NO topo;


    public Pilha(){
        this.size = -1;
        this.topo = null;
    }



    public Object push(Object x) {
        NO nodo = new NO(x);
        nodo.next = topo;
        topo = nodo;
        size++;
        return x;
    }

    public Object pop(){
        if (isEmpty()) {
            System.out.println("Pilha vazia");
            return null;
        }
        Object valor = topo.getObject();
        topo = topo.next;
        size--;
        return valor;
    }

    public boolean isEmpty(){
        return size == -1;
    }

    public Object peek(){
        if (isEmpty()) {
            System.out.println("Pilha vazia");
            return null;
        }
        return topo.getObject();
    }

}
