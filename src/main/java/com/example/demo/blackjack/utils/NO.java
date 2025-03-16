package com.example.demo.blackjack.utils;

public class NO <T>{
    T data;
    NO<T> next;
    NO<T> prev;


    public NO(T data){
        this.data = data;
        this.next = null;
        this.prev = null;
    }

    public T getData() {
        return data;
    }

    public NO<T> getNext() {
        return next;
    }

    public NO<T> getPrev() {
        return prev;
    }
}
