package com.estacionamento.model;

import jakarta.persistence.*;

@Entity
@Table(name = "veiculos")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo", discriminatorType = DiscriminatorType.STRING)
public abstract class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "placa", unique = true, nullable = false, length = 10)
    private String placa;

    @Column(name = "modelo", nullable = false, length = 50)
    private String modelo;

    @Column(name = "cor", nullable = false, length = 30)
    private String cor;

    public Veiculo() {}

    public Veiculo(String placa, String modelo, String cor) {
        this.placa = placa.toUpperCase().trim();
        this.modelo = modelo;
        this.cor = cor;
    }

    public abstract double calcularValor(double valorBase);

    public abstract String getTipo();

    public Long getId() { return id; }
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa.toUpperCase().trim(); }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }

    @Override
    public String toString() {
        return String.format("[%s] Placa: %s | Modelo: %s | Cor: %s",
                getTipo(), placa, modelo, cor);
    }
}
