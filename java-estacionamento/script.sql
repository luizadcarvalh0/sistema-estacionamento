-- ============================================================
-- Script SQL - Sistema de Estacionamento
-- Compatível com: MySQL, PostgreSQL, H2
-- ============================================================

-- Caso queira recriar o banco do zero, remova as tabelas antes:
-- DROP TABLE IF EXISTS movimentacoes;
-- DROP TABLE IF EXISTS vagas;
-- DROP TABLE IF EXISTS veiculos;

-- ============================================================
-- Tabela: veiculos
-- Usa Single Table Inheritance (JPA): todos os tipos ficam
-- na mesma tabela diferenciados pela coluna 'tipo'
-- ============================================================
CREATE TABLE IF NOT EXISTS veiculos (
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo    VARCHAR(15)  NOT NULL,       -- CARRO | MOTO | CAMINHONETE
    placa   VARCHAR(10)  NOT NULL UNIQUE,
    modelo  VARCHAR(50)  NOT NULL,
    cor     VARCHAR(30)  NOT NULL
);

-- ============================================================
-- Tabela: vagas
-- ============================================================
CREATE TABLE IF NOT EXISTS vagas (
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero  INT     NOT NULL UNIQUE,
    ocupada BOOLEAN NOT NULL DEFAULT FALSE
);

-- ============================================================
-- Tabela: movimentacoes
-- ============================================================
CREATE TABLE IF NOT EXISTS movimentacoes (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    veiculo_id   BIGINT         NOT NULL,
    vaga_id      BIGINT         NOT NULL,
    data_entrada TIMESTAMP      NOT NULL,
    data_saida   TIMESTAMP,
    valor_pago   DECIMAL(10,2),

    CONSTRAINT fk_mov_veiculo FOREIGN KEY (veiculo_id) REFERENCES veiculos(id),
    CONSTRAINT fk_mov_vaga    FOREIGN KEY (vaga_id)    REFERENCES vagas(id)
);

-- ============================================================
-- Dados iniciais: 10 vagas
-- ============================================================
INSERT INTO vagas (numero, ocupada) VALUES
    (1, FALSE), (2, FALSE), (3, FALSE), (4, FALSE), (5, FALSE),
    (6, FALSE), (7, FALSE), (8, FALSE), (9, FALSE), (10, FALSE);

-- ============================================================
-- Dados de exemplo (opcional)
-- ============================================================
INSERT INTO veiculos (tipo, placa, modelo, cor) VALUES
    ('CARRO',       'ABC1234', 'Civic',   'Prata'),
    ('MOTO',        'XYZ9876', 'CB300',   'Preta'),
    ('CAMINHONETE', 'DEF5678', 'Hilux',   'Branca');

-- Exemplo de movimentação concluída
INSERT INTO movimentacoes (veiculo_id, vaga_id, data_entrada, data_saida, valor_pago)
VALUES (1, 1, '2025-05-13 08:00:00', '2025-05-13 10:30:00', 11.00);
-- Carro | 2h30min -> arredondado p/ 3h -> R$5 + 2*R$3 = R$11 -> x1.0 = R$11,00

-- Moto estacionada atualmente (sem saída)
INSERT INTO movimentacoes (veiculo_id, vaga_id, data_entrada, data_saida, valor_pago)
VALUES (2, 2, '2025-05-13 09:00:00', NULL, NULL);

-- ============================================================
-- Regras de cálculo (implementadas em Java via polimorfismo)
--
-- Valor base:
--   Até 1 hora  => R$ 5,00
--   Hora extra  => R$ 3,00 por hora adicional
--
-- Multiplicadores:
--   Carro       => x 1.00  (100%)
--   Moto        => x 0.50  (50%)
--   Caminhonete => x 1.50  (150%)
--
-- Exemplos:
--   Carro 1h       => R$  5,00
--   Carro 3h       => R$ 11,00 (5 + 2*3)
--   Moto 2h        => R$  4,00 (5 + 1*3) * 0.50
--   Caminhonete 2h => R$ 12,00 (5 + 1*3) * 1.50
-- ============================================================
