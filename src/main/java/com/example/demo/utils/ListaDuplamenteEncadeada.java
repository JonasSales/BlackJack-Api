package com.example.demo.utils;

import java.util.ArrayList;
import java.util.List;

public class ListaDuplamenteEncadeada<T>{

    public class Nodo{

        private T data;
        private Nodo next;
        private Nodo prev;

        public Nodo(T data){
            this.data = data;
            this.prev = null;
            this.next = null;
        }

        public Nodo(){
        }

        public Object getData(){
            return data;
        }


        public Nodo getNext() {
            return next;
        }


        public Nodo getPrev() {
            return prev;
        }




        @Override
        public String toString() {
            return "Nodo{" + "data=" + data + ", next= " + next + ", prev= " + prev + '}';
        }
    }

    private Nodo head;
    private Nodo tail;
    int size;


    public Nodo getHead() {
        return head;
    }

    public void setHead(Nodo head) {
        this.head = head;
    }

    public Nodo getTail() {
        return tail;
    }

    public void setTail(Nodo tail) {
        this.tail = tail;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public ListaDuplamenteEncadeada(){
        head = null;
        tail = null;
        size = 0;
    }

    public void showList(){
        Nodo p =  head;
        if (isEmpty()){
            return;
        }
        do {
            System.out.println(p.data);
            p = p.next;
        }
        while (p != null);
    }

    public void showReverse(){
        Nodo p =  tail;
        if (isEmpty()){
            System.out.println("Lista está vazia");
            return;
        }
        do {
            System.out.println(p.data);
            p = p.prev;
        }
        while (p.prev != null);
        System.out.println(p.data);
        System.out.println("Atualmente a lista tem " + size + " itens");
    }

    public void addFirst(T dado){
        Nodo n = new Nodo(dado);
        if (isEmpty()){
            head = n;
            tail = n;
        }
        else{
            n.next = head;
            head.prev = n;
            head = n;
        }
        size++;
    }

    public void addLast(T dado){
        Nodo n = new Nodo(dado);
        if (isEmpty()){
            head = n;
            tail = n;
        }
        else{
            n.prev = tail;
            tail.next = n;
            tail = n;
        }
        size++;
    }

    public Nodo searchNodo(Object p) {
        if (p == null) {
            return null;
        }
        if (head != null) {
            Nodo temp = head;
            while (temp != null) {
                if (temp.getData().equals(p)) {
                    return temp;  // Retorna o nó quando o jogador é encontrado
                }
                temp = temp.getNext();
            }
        }
        return null;  // Retorna null se o jogador não for encontrado
    }


    public boolean addAfter(T dado, T criterio)
    {
        // Antecessor
        Nodo p = searchNodo(criterio);
        if( p == null )
        {
            System.out.println("Criterio invalido \n");
            return false;
        }
        else
        {
            // Novo elemento
            Nodo novo = new Nodo(dado);

            // Atualiza tail quando o elemento criterio eh o ultimo
            if(p.next == null) {
                tail = novo;
            }

            // Anexa (dicas: comece atribuindo os campos null)
            novo.next = p.next;
            novo.prev = p;		// novidade
            p.next = novo;

            // novidade
            Nodo frente = novo.next;	// var auxiliar
            if(frente != null) {		// previne nullpoint quando add no tail
                frente.prev = novo;
            }
            size++;
            return true;
        }
    }

    public void removeFirst(){
        if (isEmpty()){
            return;
        }
        else if (head == tail){
            head = null;
            tail = null;
        }
        else {
            head = head.next;
            head.prev = null;
        }
        size--;
    }

    public void removeLast(){
        if (isEmpty()){
            return;
        }
        else if (tail == head){
            tail = null;
            head = null;
        }
        else {
            tail = tail.prev;
            tail.next = null;
        }
        size--;
    }

    public void remove(T criterio)
    {
        Nodo n = searchNodo(criterio);
        Nodo anterior = null;

        if(isEmpty()) {
            return;
        }

        if (n == head){
            removeFirst();
        }
        else if (n == tail){
            removeLast();
        }

        else {

            Nodo frente = n.next;	// var auxiliar
            anterior = n.prev;
            // se desliga do elemento removido
            anterior.next = frente;
            frente.prev = anterior; // novidade

            // isola elemento removido
            n.next = null;
            n.prev = null;  // novidade

            size--;

        }
    }

    public T PeekLast() {
        if(tail == null){
            return null;
        }
        return tail.data;
    }

    public T PeekFirst(){
        if(head == null){
            return null;
        }
        return head.data;
    }

    public boolean isEmpty() {
        return (tail == null) || (head == null);
    }

    public int getSize() {
        return size;
    }

    public Object[] retornArrayData(){
        Nodo p =  head;
        Object[] array = new Object[size];
        if (isEmpty()){
            return null;
        }
        for (int i = 0; i < array.length; i++) {
            array[i] = p.getData();
            p = p.next;
        }
        return array;
    }

    public List<Nodo> getAllNodos() {
        List<Nodo> nodos = new ArrayList<>();
        Nodo p = head;

        while (p != null) {
            nodos.add(p);
            p = p.next;
        }
        return nodos;
    }


}
