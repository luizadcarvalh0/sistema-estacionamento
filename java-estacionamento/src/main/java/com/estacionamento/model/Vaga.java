package com.estacionamento.model;

import jakarta.persistence.*;

@Entity
@Table(name = "vagas")
public class Vaga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero", unique = true, nullable = false)
    private int numero;

    @Column(name = "ocupada", nullable = false)
    private boolean ocupada;

    public Vaga() {}

    public Vaga(int numero) {
        this.numero = numero;
        this.ocupada = false;
    }

    public Long getId() { return id; }
    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }
    public boolean isOcupada() { return ocupada; }
    public void setOcupada(boolean ocupada) { this.ocupada = ocupada; }

    @Override
    public String toString() {
        return String.format("Vaga %d [%s]", numero, ocupada ? "OCUPADA" : "LIVRE");
    }
}
