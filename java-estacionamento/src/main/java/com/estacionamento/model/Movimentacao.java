package com.estacionamento.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "movimentacoes")
public class Movimentacao {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vaga_id", nullable = false)
    private Vaga vaga;

    @Column(name = "data_entrada", nullable = false)
    private LocalDateTime dataEntrada;

    @Column(name = "data_saida")
    private LocalDateTime dataSaida;

    @Column(name = "valor_pago")
    private Double valorPago;

    public Movimentacao() {}

    public Movimentacao(Veiculo veiculo, Vaga vaga, LocalDateTime dataEntrada) {
        this.veiculo = veiculo;
        this.vaga = vaga;
        this.dataEntrada = dataEntrada;
    }

    public double calcularValorPagar() {
        if (dataSaida == null) {
            throw new IllegalStateException("Data de saída não registrada.");
        }

        long minutos = java.time.Duration.between(dataEntrada, dataSaida).toMinutes();
        long horas = (long) Math.ceil(minutos / 60.0);
        if (horas < 1) horas = 1;

        double valorBase;
        if (horas == 1) {
            valorBase = 5.00;
        } else {
            valorBase = 5.00 + (horas - 1) * 3.00;
        }

        return veiculo.calcularValor(valorBase);
    }

    public Long getId() { return id; }
    public Veiculo getVeiculo() { return veiculo; }
    public void setVeiculo(Veiculo veiculo) { this.veiculo = veiculo; }
    public Vaga getVaga() { return vaga; }
    public void setVaga(Vaga vaga) { this.vaga = vaga; }
    public LocalDateTime getDataEntrada() { return dataEntrada; }
    public void setDataEntrada(LocalDateTime dataEntrada) { this.dataEntrada = dataEntrada; }
    public LocalDateTime getDataSaida() { return dataSaida; }
    public void setDataSaida(LocalDateTime dataSaida) { this.dataSaida = dataSaida; }
    public Double getValorPago() { return valorPago; }
    public void setValorPago(Double valorPago) { this.valorPago = valorPago; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Movimentação #%d\n", id));
        sb.append(String.format("  Veículo : %s\n", veiculo));
        sb.append(String.format("  Vaga    : %d\n", vaga.getNumero()));
        sb.append(String.format("  Entrada : %s\n", dataEntrada.format(FORMATTER)));
        if (dataSaida != null) {
            sb.append(String.format("  Saída   : %s\n", dataSaida.format(FORMATTER)));
            sb.append(String.format("  Valor   : R$ %.2f", valorPago));
        } else {
            sb.append("  Saída   : (em aberto)");
        }
        return sb.toString();
    }
}
