package com.estacionamento.service;

import com.estacionamento.model.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

public class Estacionamento {

    private static final int TOTAL_VAGAS = 10;
    private final EntityManagerFactory emf;

    public Estacionamento() {
        emf = Persistence.createEntityManagerFactory("estacionamentoPU");
        inicializarVagas();
    }

    private void inicializarVagas() {
        EntityManager em = emf.createEntityManager();
        try {
            Long count = em.createQuery("SELECT COUNT(v) FROM Vaga v", Long.class).getSingleResult();
            if (count == 0) {
                em.getTransaction().begin();
                for (int i = 1; i <= TOTAL_VAGAS; i++) {
                    em.persist(new Vaga(i));
                }
                em.getTransaction().commit();
                System.out.println("✔ " + TOTAL_VAGAS + " vagas inicializadas.");
            }
        } finally {
            em.close();
        }
    }

    public void cadastrarVeiculo(Veiculo veiculo) {
        EntityManager em = emf.createEntityManager();
        try {
            Veiculo existente = buscarVeiculoPorPlaca(veiculo.getPlaca(), em);
            if (existente != null) {
                throw new IllegalArgumentException("Já existe um veículo com a placa " + veiculo.getPlaca() + " cadastrado.");
            }
            em.getTransaction().begin();
            em.persist(veiculo);
            em.getTransaction().commit();
            System.out.println("✔ Veículo cadastrado com sucesso: " + veiculo.getPlaca());
        } finally {
            em.close();
        }
    }

    public void registrarEntrada(String placa, int numeroVaga) {
        EntityManager em = emf.createEntityManager();
        try {
            Veiculo veiculo = buscarVeiculoPorPlaca(placa, em);
            if (veiculo == null) {
                throw new IllegalArgumentException("Veículo com placa " + placa + " não cadastrado.");
            }

            boolean jaEstacionado = verificarSeEstacionado(placa, em);
            if (jaEstacionado) {
                throw new IllegalStateException("Veículo " + placa + " já está estacionado.");
            }

            Vaga vaga = buscarVagaPorNumero(numeroVaga, em);
            if (vaga == null) {
                throw new IllegalArgumentException("Vaga " + numeroVaga + " não existe.");
            }
            if (vaga.isOcupada()) {
                throw new IllegalStateException("Vaga " + numeroVaga + " já está ocupada.");
            }

            em.getTransaction().begin();
            vaga.setOcupada(true);
            Movimentacao mov = new Movimentacao(veiculo, vaga, LocalDateTime.now());
            em.merge(vaga);
            em.persist(mov);
            em.getTransaction().commit();

            System.out.println("✔ Entrada registrada!");
            System.out.printf("  Veículo : %s%n", veiculo);
            System.out.printf("  Vaga    : %d%n", numeroVaga);
            System.out.printf("  Entrada : %s%n", mov.getDataEntrada().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        } finally {
            em.close();
        }
    }

    public void registrarSaida(String placa) {
        EntityManager em = emf.createEntityManager();
        try {
            List<Movimentacao> lista = em.createQuery(
                    "SELECT m FROM Movimentacao m WHERE m.veiculo.placa = :placa AND m.dataSaida IS NULL",
                    Movimentacao.class)
                    .setParameter("placa", placa.toUpperCase().trim())
                    .getResultList();

            if (lista.isEmpty()) {
                throw new IllegalStateException("Veículo " + placa + " não está estacionado no momento.");
            }

            Movimentacao mov = lista.get(0);
            if (mov.getDataEntrada() == null) {
                throw new IllegalStateException("Erro: movimentação sem data de entrada.");
            }

            em.getTransaction().begin();
            mov.setDataSaida(LocalDateTime.now());
            double valor = mov.calcularValorPagar();
            mov.setValorPago(valor);
            mov.getVaga().setOcupada(false);
            em.merge(mov);
            em.getTransaction().commit();

            System.out.println("✔ Saída registrada!");
            System.out.println(mov);
        } finally {
            em.close();
        }
    }

    public void listarEstacionados() {
        EntityManager em = emf.createEntityManager();
        try {
            List<Movimentacao> lista = em.createQuery(
                    "SELECT m FROM Movimentacao m WHERE m.dataSaida IS NULL ORDER BY m.dataEntrada",
                    Movimentacao.class).getResultList();

            if (lista.isEmpty()) {
                System.out.println("  Nenhum veículo estacionado no momento.");
                return;
            }

            System.out.printf("%-12s %-20s %-15s %-10s %-20s%n",
                    "PLACA", "MODELO", "TIPO", "VAGA", "ENTRADA");
            System.out.println("-".repeat(80));
            for (Movimentacao m : lista) {
                System.out.printf("%-12s %-20s %-15s %-10d %-20s%n",
                        m.getVeiculo().getPlaca(),
                        m.getVeiculo().getModelo(),
                        m.getVeiculo().getTipo(),
                        m.getVaga().getNumero(),
                        m.getDataEntrada().format(
                                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }
        } finally {
            em.close();
        }
    }

    public void listarHistorico() {
        EntityManager em = emf.createEntityManager();
        try {
            List<Movimentacao> lista = em.createQuery(
                    "SELECT m FROM Movimentacao m WHERE m.dataSaida IS NOT NULL ORDER BY m.dataSaida DESC",
                    Movimentacao.class).getResultList();

            if (lista.isEmpty()) {
                System.out.println("  Nenhuma movimentação concluída ainda.");
                return;
            }

            System.out.printf("%-5s %-12s %-15s %-22s %-22s %-10s%n",
                    "ID", "PLACA", "TIPO", "ENTRADA", "SAÍDA", "VALOR");
            System.out.println("-".repeat(90));
            java.time.format.DateTimeFormatter fmt =
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            for (Movimentacao m : lista) {
                System.out.printf("%-5d %-12s %-15s %-22s %-22s R$ %.2f%n",
                        m.getId(),
                        m.getVeiculo().getPlaca(),
                        m.getVeiculo().getTipo(),
                        m.getDataEntrada().format(fmt),
                        m.getDataSaida().format(fmt),
                        m.getValorPago());
            }
        } finally {
            em.close();
        }
    }

    public void listarVagas() {
        EntityManager em = emf.createEntityManager();
        try {
            List<Vaga> vagas = em.createQuery("SELECT v FROM Vaga v ORDER BY v.numero", Vaga.class)
                    .getResultList();
            System.out.println("Status das vagas:");
            for (Vaga v : vagas) {
                System.out.printf("  Vaga %2d: %s%n", v.getNumero(),
                        v.isOcupada() ? "[ OCUPADA ]" : "[ LIVRE   ]");
            }
        } finally {
            em.close();
        }
    }

    public void listarVeiculosCadastrados() {
        EntityManager em = emf.createEntityManager();
        try {
            List<Veiculo> lista = em.createQuery("SELECT v FROM Veiculo v ORDER BY v.placa", Veiculo.class)
                    .getResultList();
            if (lista.isEmpty()) {
                System.out.println("  Nenhum veículo cadastrado.");
                return;
            }
            System.out.printf("%-12s %-20s %-15s %-15s%n", "PLACA", "MODELO", "COR", "TIPO");
            System.out.println("-".repeat(65));
            for (Veiculo v : lista) {
                System.out.printf("%-12s %-20s %-15s %-15s%n",
                        v.getPlaca(), v.getModelo(), v.getCor(), v.getTipo());
            }
        } finally {
            em.close();
        }
    }

    private Veiculo buscarVeiculoPorPlaca(String placa, EntityManager em) {
        List<Veiculo> lista = em.createQuery(
                "SELECT v FROM Veiculo v WHERE v.placa = :placa", Veiculo.class)
                .setParameter("placa", placa.toUpperCase().trim())
                .getResultList();
        return lista.isEmpty() ? null : lista.get(0);
    }

    private boolean verificarSeEstacionado(String placa, EntityManager em) {
        Long count = em.createQuery(
                "SELECT COUNT(m) FROM Movimentacao m WHERE m.veiculo.placa = :placa AND m.dataSaida IS NULL",
                Long.class)
                .setParameter("placa", placa.toUpperCase().trim())
                .getSingleResult();
        return count > 0;
    }

    private Vaga buscarVagaPorNumero(int numero, EntityManager em) {
        List<Vaga> lista = em.createQuery(
                "SELECT v FROM Vaga v WHERE v.numero = :numero", Vaga.class)
                .setParameter("numero", numero)
                .getResultList();
        return lista.isEmpty() ? null : lista.get(0);
    }

    public void fechar() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
