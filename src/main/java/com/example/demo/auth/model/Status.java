package com.example.demo.auth.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_status")
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "id")
    private User user;

    private double money;
    private int partidasJogadas;
    private int partidasGanhas;

    public Status() {
        this.money = 10000;
        this.partidasJogadas = 0;
        this.partidasGanhas = 0;
    }

    public Status(User user, double money, int partidasJogadas, int partidasGanhas) {
        this.user = user;
        this.money = money;
        this.partidasJogadas = partidasJogadas;
        this.partidasGanhas = partidasGanhas;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double dinheiro) {
        this.money = dinheiro;
    }

    public int getPartidasJogadas() {
        return partidasJogadas;
    }

    public void setPartidasJogadas(int partidasJogadas) {
        this.partidasJogadas = partidasJogadas;
    }

    public int getPartidasGanhas() {
        return partidasGanhas;
    }

    public void setPartidasGanhas(int partidasGanhas) {
        this.partidasGanhas = partidasGanhas;
    }

    public void perdeuPartida(){
        partidasJogadas++;
    }

    public double subtrair100Money(){
        this.money -= 100;
        return money;
    }

    public double adicionar100Money(){
        this.money += 100;
        return money;
    }
}