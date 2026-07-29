package com.estacionamento.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("CARRO")
public class Carro extends Veiculo {

    public Carro() {}

    public Carro(String placa, String modelo, String cor) {
        super(placa, modelo, cor);
    }

    @Override
    public double calcularValor(double valorBase) {
        return valorBase;
    }

    @Override
    public String getTipo() {
        return "Carro";
    }
}
