# Relatório - Sistema de Estacionamento

## Visão Geral

Sistema de controle de estacionamento desenvolvido em Java com JPA (Hibernate) e banco de dados H2 embarcado, executado via terminal. O banco de dados é criado automaticamente na primeira execução do programa.

---

## Classes Java

### Pacote `com.estacionamento.model`

#### `Veiculo` (abstrata)
Classe base que representa qualquer veículo. Mapeada com `@Entity` e `@Inheritance(SINGLE_TABLE)` — todos os subtipos ficam na mesma tabela, diferenciados pela coluna `tipo`.

| Atributo | Tipo   | Descrição                     |
|----------|--------|-------------------------------|
| id       | Long   | Chave primária (auto-gerada)  |
| placa    | String | Única, obrigatória            |
| modelo   | String | Ex: "Civic", "CB300"          |
| cor      | String | Ex: "Prata", "Preta"          |

**Método abstrato:** `calcularValor(double valorBase)` — cada subclasse implementa o multiplicador de preço (polimorfismo).

---

#### `Carro extends Veiculo`
Discriminador: `CARRO`. Implementa `calcularValor` com multiplicador **1.0** (valor integral).

#### `Moto extends Veiculo`
Discriminador: `MOTO`. Implementa `calcularValor` com multiplicador **0.5** (50% do valor).

#### `Caminhonete extends Veiculo`
Discriminador: `CAMINHONETE`. Implementa `calcularValor` com multiplicador **1.5** (150% do valor).

---

#### `Vaga`
Representa uma vaga física do estacionamento.

| Atributo | Tipo    | Descrição                    |
|----------|---------|------------------------------|
| id       | Long    | Chave primária               |
| numero   | int     | Número único da vaga (1–10)  |
| ocupada  | boolean | `false` = livre, `true` = ocupada |

---

#### `Movimentacao`
Registra cada entrada e saída de veículo.

| Atributo    | Tipo          | Descrição                                |
|-------------|---------------|------------------------------------------|
| id          | Long          | Chave primária                           |
| veiculo     | Veiculo (FK)  | Veículo envolvido                        |
| vaga        | Vaga (FK)     | Vaga ocupada                             |
| dataEntrada | LocalDateTime | Data/hora de entrada                     |
| dataSaida   | LocalDateTime | Data/hora de saída (null = ainda dentro) |
| valorPago   | Double        | Calculado ao registrar saída             |

**Método:** `calcularValorPagar()` — aplica a regra de cobrança e chama `veiculo.calcularValor()`.

**Regra de cobrança:**
- 1ª hora: R$ 5,00
- Cada hora adicional: R$ 3,00
- Horas são arredondadas para cima (ex: 1h10min = 2 horas)
- O resultado é passado para o `calcularValor()` do veículo (polimorfismo)

---

### Pacote `com.estacionamento.service`

#### `Estacionamento`
Classe de serviço que gerencia todas as operações de negócio. Mantém o `EntityManagerFactory` (JPA) e expõe os métodos:

| Método                           | Descrição                                                         |
|----------------------------------|-------------------------------------------------------------------|
| `cadastrarVeiculo(Veiculo)`      | Valida placa duplicada e persiste                                 |
| `registrarEntrada(placa, vaga)`  | Valida: veiculo existe, não está dentro, vaga existe e está livre |
| `registrarSaida(placa)`          | Calcula valor, registra saída, libera vaga                        |
| `listarEstacionados()`           | Movimentações com `dataSaida = null`                             |
| `listarHistorico()`              | Movimentações concluídas                                          |
| `listarVagas()`                  | Status de todas as vagas                                          |
| `listarVeiculosCadastrados()`    | Todos os veículos no banco                                        |

---

### Pacote `com.estacionamento`

#### `Main`
Ponto de entrada. Exibe o menu interativo no terminal e chama os métodos de `Estacionamento` conforme a opção escolhida. Trata erros de validação exibindo a mensagem para o usuário sem encerrar o programa.

---

## Tabelas do Banco de Dados

O JPA cria as tabelas automaticamente via `hbm2ddl.auto = update`.

### `veiculos`
| Coluna | Tipo        | Restrição     |
|--------|-------------|---------------|
| id     | BIGINT      | PK, AUTO      |
| tipo   | VARCHAR(15) | NOT NULL      |
| placa  | VARCHAR(10) | NOT NULL, UNIQUE |
| modelo | VARCHAR(50) | NOT NULL      |
| cor    | VARCHAR(30) | NOT NULL      |

> Usa **Single Table Inheritance**: Carro, Moto e Caminhonete são diferenciados pela coluna `tipo`.

### `vagas`
| Coluna  | Tipo    | Restrição     |
|---------|---------|---------------|
| id      | BIGINT  | PK, AUTO      |
| numero  | INT     | NOT NULL, UNIQUE |
| ocupada | BOOLEAN | NOT NULL, default false |

### `movimentacoes`
| Coluna       | Tipo         | Restrição          |
|--------------|--------------|--------------------|
| id           | BIGINT       | PK, AUTO           |
| veiculo_id   | BIGINT       | FK → veiculos(id)  |
| vaga_id      | BIGINT       | FK → vagas(id)     |
| data_entrada | TIMESTAMP    | NOT NULL           |
| data_saida   | TIMESTAMP    | nullable           |
| valor_pago   | DECIMAL(10,2)| nullable           |

---

## Conceitos OOP Aplicados

| Conceito        | Onde                                                          |
|-----------------|---------------------------------------------------------------|
| **Encapsulamento** | Todos os atributos são `private` com getters/setters        |
| **Herança**     | `Carro`, `Moto`, `Caminhonete` estendem `Veiculo`            |
| **Polimorfismo** | `calcularValor()` é sobrescrito em cada subclasse            |
| **Abstração**   | `Veiculo` é abstrata, não pode ser instanciada diretamente   |

---

## Como Executar no IntelliJ

1. Abrir o IntelliJ e importar como projeto **Maven** (`File → Open → pasta java-estacionamento`)
2. Aguardar o Maven baixar as dependências (Hibernate + H2)
3. Executar a classe `com.estacionamento.Main`
4. O banco H2 é criado automaticamente como arquivo `estacionamento.mv.db` na raiz do projeto

> Não é necessário instalar nenhum banco de dados externo. O H2 é embarcado.

---

## Banco de Dados

- **Banco utilizado:** H2 (embarcado, sem instalação)
- **Arquivo gerado:** `estacionamento.mv.db` (criado automaticamente na primeira execução)
- **Configuração:** `src/main/resources/META-INF/persistence.xml`
- **Script SQL manual:** `script.sql` (para uso em MySQL/PostgreSQL se preferir)
