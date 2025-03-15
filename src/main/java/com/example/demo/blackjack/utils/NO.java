package com.example.demo.blackjack.utils;

public class NO{
    Object object;
    NO next;
    NO prev;


    public NO(Object object){
        this.object = object;
        this.next = null;
        this.prev = null;
    }

    public Object getObject() {
        return object;
    }


}
