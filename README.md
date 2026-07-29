# 🚗 Sistema de Estacionamento

Projeto desenvolvido em **Java** com o objetivo de simular o gerenciamento de um estacionamento, aplicando conceitos de Programação Orientada a Objetos (POO), como herança, encapsulamento e polimorfismo.

## 📋 Sobre o projeto

O sistema permite realizar o cadastro de veículos, controlar entradas e saídas, calcular o valor da permanência e manter um histórico das movimentações, garantindo também regras de negócio para evitar inconsistências.

## ✨ Funcionalidades

- 🚘 Cadastro de veículos
  - Placa
  - Modelo
  - Cor
  - Tipo (Carro, Moto ou Caminhonete)

- 🅿️ Controle de estacionamento
  - Registro de entrada
  - Registro de saída
  - Controle de vagas ocupadas

- 💰 Cálculo automático de tarifas
  - Até 1 hora: R$ 5,00
  - Hora adicional: R$ 3,00
  - Moto paga 50% do valor
  - Caminhonete paga 150% do valor

- 📄 Histórico de movimentações

## ✅ Regras de Negócio

O sistema impede:

- Cadastro de placas duplicadas
- Entrada de veículo já estacionado
- Saída de veículo que não esteja estacionado
- Utilização de vaga ocupada
- Registro de saída sem entrada

## 🛠️ Tecnologias utilizadas

- Java
- Spring Boot

## 📂 Estrutura do projeto

```
src
├── model
├── service
├── repository
├── controller
└── ...
```

## ▶️ Como executar

1. Clone o repositório

```bash
git clone https://github.com/luizadcarvalho/sistema-estacionamento.git
```

2. Abra o projeto na IDE (IntelliJ IDEA)

3. Execute a aplicação.

## 📸 Demonstração

Adicione aqui imagens da aplicação.

Exemplo:

```
/images
```

ou

```markdown
![Tela Inicial](images/tela-inicial.png)
```

## 🚀 Aprendizados

Durante o desenvolvimento deste projeto, pude aprimorar conhecimentos em:

- Programação Orientada a Objetos
- Organização da arquitetura da aplicação
- Estruturação de classes e responsabilidades
- Regras de negócio
- Lógica de programação
- Java

## 📌 Melhorias futuras

- Interface gráfica
- API REST
- Banco de dados
- Testes automatizados
- Docker
- Autenticação de usuários

## 👩‍💻 Desenvolvedora

**Maria Luiza Carvalho**

📧 LinkedIn: https://www.linkedin.com/in/SEU-LINK/

GitHub: https://github.com/luizadcarvalho
