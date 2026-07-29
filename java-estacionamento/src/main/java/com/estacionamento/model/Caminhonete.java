package com.estacionamento.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("CAMINHONETE")
public class Caminhonete extends Veiculo {

    public Caminhonete() {}

    public Caminhonete(String placa, String modelo, String cor) {
        super(placa, modelo, cor);
    }

    @Override
    public double calcularValor(double valorBase) {
        return valorBase * 1.50;
    }

    @Override
    public String getTipo() {
        return "Caminhonete";
    }
}
