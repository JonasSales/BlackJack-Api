package com.example.demo.auth.dto;

import com.example.demo.auth.model.User;


public class UserDTO {

    private Long id;
    private String name;
    private String email;
    private int partidasTotais;
    private int partidasGanhas;
    private double money;


    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.partidasGanhas = user.getStatus().getPartidasGanhas();
        this.partidasTotais = user.getStatus().getPartidasJogadas();
        this.money = user.getStatus().getMoney();
    }

    public UserDTO(){
    }

    public UserDTO(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPartidasTotais() {
        return partidasTotais;
    }

    public void setPartidasTotais(int partidasTotais) {
        this.partidasTotais = partidasTotais;
    }

    public int getPartidasGanhas() {
        return partidasGanhas;
    }

    public void setPartidasGanhas(int partidasGanhas) {
        this.partidasGanhas = partidasGanhas;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }
}
