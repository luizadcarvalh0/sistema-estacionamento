package com.estacionamento.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("MOTO")
public class Moto extends Veiculo {

    public Moto() {}

    public Moto(String placa, String modelo, String cor) {
        super(placa, modelo, cor);
    }

    @Override
    public double calcularValor(double valorBase) {
        return valorBase * 0.50;
    }

    @Override
    public String getTipo() {
        return "Moto";
    }
}
