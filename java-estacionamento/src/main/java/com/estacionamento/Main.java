package com.estacionamento;

import com.estacionamento.model.*;
import com.estacionamento.service.Estacionamento;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static Estacionamento estacionamento;

    public static void main(String[] args) {
        System.out.println("============================================");
        System.out.println("     SISTEMA DE ESTACIONAMENTO v1.0         ");
        System.out.println("============================================");

        try {
            estacionamento = new Estacionamento();
        } catch (Exception e) {
            System.err.println("Erro ao iniciar o sistema: " + e.getMessage());
            return;
        }

        boolean rodando = true;
        while (rodando) {
            exibirMenu();
            int opcao = lerInteiro("Opção: ");
            System.out.println();

            try {
                switch (opcao) {
                    case 1 -> menuCadastrarVeiculo();
                    case 2 -> menuRegistrarEntrada();
                    case 3 -> menuRegistrarSaida();
                    case 4 -> {
                        System.out.println("=== VEÍCULOS ESTACIONADOS ===");
                        estacionamento.listarEstacionados();
                    }
                    case 5 -> {
                        System.out.println("=== HISTÓRICO DE MOVIMENTAÇÕES ===");
                        estacionamento.listarHistorico();
                    }
                    case 6 -> {
                        System.out.println("=== VAGAS DO ESTACIONAMENTO ===");
                        estacionamento.listarVagas();
                    }
                    case 7 -> {
                        System.out.println("=== VEÍCULOS CADASTRADOS ===");
                        estacionamento.listarVeiculosCadastrados();
                    }
                    case 0 -> {
                        System.out.println("Encerrando o sistema. Até logo!");
                        rodando = false;
                    }
                    default -> System.out.println("Opção inválida. Tente novamente.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("⚠  " + e.getMessage());
            } catch (Exception e) {
                System.out.println("✘ Erro inesperado: " + e.getMessage());
            }

            if (rodando) {
                System.out.println();
                System.out.println("Pressione ENTER para continuar...");
                scanner.nextLine();
            }
        }

        estacionamento.fechar();
    }

    private static void exibirMenu() {
        System.out.println();
        System.out.println("============================================");
        System.out.println("                   MENU                    ");
        System.out.println("============================================");
        System.out.println(" 1. Cadastrar veículo");
        System.out.println(" 2. Registrar entrada");
        System.out.println(" 3. Registrar saída");
        System.out.println(" 4. Listar veículos estacionados");
        System.out.println(" 5. Histórico de movimentações");
        System.out.println(" 6. Status das vagas");
        System.out.println(" 7. Listar veículos cadastrados");
        System.out.println(" 0. Sair");
        System.out.println("--------------------------------------------");
    }

    private static void menuCadastrarVeiculo() {
        System.out.println("=== CADASTRAR VEÍCULO ===");
        System.out.println("Tipo de veículo:");
        System.out.println("  1 - Carro");
        System.out.println("  2 - Moto");
        System.out.println("  3 - Caminhonete");
        int tipo = lerInteiro("Tipo: ");

        if (tipo < 1 || tipo > 3) {
            System.out.println("Tipo inválido.");
            return;
        }

        System.out.print("Placa: ");
        String placa = scanner.nextLine().trim().toUpperCase();

        System.out.print("Modelo: ");
        String modelo = scanner.nextLine().trim();

        System.out.print("Cor: ");
        String cor = scanner.nextLine().trim();

        if (placa.isEmpty() || modelo.isEmpty() || cor.isEmpty()) {
            System.out.println("Todos os campos são obrigatórios.");
            return;
        }

        Veiculo veiculo = switch (tipo) {
            case 1 -> new Carro(placa, modelo, cor);
            case 2 -> new Moto(placa, modelo, cor);
            case 3 -> new Caminhonete(placa, modelo, cor);
            default -> throw new IllegalArgumentException("Tipo inválido");
        };

        estacionamento.cadastrarVeiculo(veiculo);
    }

    private static void menuRegistrarEntrada() {
        System.out.println("=== REGISTRAR ENTRADA ===");
        System.out.print("Placa do veículo: ");
        String placa = scanner.nextLine().trim().toUpperCase();

        System.out.println();
        estacionamento.listarVagas();
        System.out.println();

        int numeroVaga = lerInteiro("Número da vaga desejada: ");
        estacionamento.registrarEntrada(placa, numeroVaga);
    }

    private static void menuRegistrarSaida() {
        System.out.println("=== REGISTRAR SAÍDA ===");
        System.out.println("Veículos atualmente estacionados:");
        estacionamento.listarEstacionados();
        System.out.println();

        System.out.print("Placa do veículo: ");
        String placa = scanner.nextLine().trim().toUpperCase();

        estacionamento.registrarSaida(placa);
    }

    private static int lerInteiro(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int valor = scanner.nextInt();
                scanner.nextLine();
                return valor;
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.println("Por favor, insira um número inteiro válido.");
            }
        }
    }
}
