# 🏦 Sistema Bancário Focado no Cliente (Client-Focused Banking System)

**Projeto Semestral de [Programacao Orientada a Objectos 'POO']**

Este é um sistema de simulação bancária robusto, desenvolvido para demonstrar a gestão de operações financeiras complexas, com foco na segurança e em uma experiência de usuário (UX) intuitiva. O projeto utiliza **Java Swing** para a interface gráfica e **MySQL** para o gerenciamento persistente de dados.

## ✨ Funcionalidades Principais

O sistema é estruturado em torno de **cinco perfis de usuário** distintos, cada um com um conjunto específico de permissões e responsabilidades.

| Perfil | Descrição das Funções |
| :--- | :--- |
| **Cliente** | Realiza transações diárias: **transferências** (intra-banco, inter-banco e para carteiras móveis), consulta de saldo e visualização do perfil. |
| **Caixa (Funcionário)** | Executa operações diretas de balcão: **levantamentos**, **depósitos** e consultas rápidas de contas. |
| **Atendimento (Funcionário)** | Focado na gestão de contas e clientes: **abertura de novas contas**, atualização de registos de clientes e emissão de documentos. |
| **Gestor** | Responsável pelas decisões críticas e segurança: **aprovação de empréstimos**, monitoramento de **transações suspeitas** e visualização do fluxo operacional. |
| **Administrador (Admin)** | O superusuário do sistema: **cadastro de novos funcionários e gestores**, controle total do fluxo, e geração de relatórios operacionais em tempo real. |

## 🛠️ Tecnologias Utilizadas

O projeto foi construído utilizando as seguintes ferramentas e tecnologias:

* **Linguagem:** Java (JDK 19)
* **Interface Gráfica (GUI):** Java Swing (Implementado via Drag and Drop do NetBeans)
* **Banco de Dados:** MySQL
* **IDE de Desenvolvimento:** Apache NetBeans IDE
* **Conectividade DB:** MySQL Connector

## ⚙️ Configuração e Execução

Siga os passos abaixo para configurar e executar o projeto na sua máquina local:

### 1. Pré-requisitos

Certifique-se de ter instalado:
* **Servidor MySQL:** Para hospedar o banco de dados.
* **Apache NetBeans IDE:** Para abrir e compilar o projeto.
* **Java Development Kit (JDK).**

### 2. Configuração do Banco de Dados

1.  Crie um novo banco de dados no seu servidor MySQL (ex: `bancodatabse`).
2.  Importe o script SQL que contém a estrutura das tabelas e dados iniciais (o script deve estar na pasta `db_scripts/` do projeto).
3.  Abra o arquivo de conexão Java ( `ConnectFactory.java` ) no NetBeans.
4.  Atualize as credenciais de acesso (`URL`, `USERNAME`, `PASSWORD`) para corresponderem à sua configuração MySQL local.

### 3. Execução do Projeto

1.  Abra o projeto no Apache NetBeans.
2.  Limpe e compile o projeto (`Clean and Build`).
3.  Execute a classe principal (ex: `Main.java` ou `LoginView.java`).

## 🤝 Contribuição e Autoria

Este projeto foi desenvolvido em colaboração para o trabalho semestral.

| Contribuinte | Papel Principal |
| :--- | :--- |
| **@Tilza_Calisto** | Front-End Developmentl |
| **@Delson_Mafumo** | Banck-end Development  |
| **@FredericoM28**  | Full-Stack Development |

---
**Observação:** O código deste sistema foi desenvolvido com foco em demonstrar as interações e a lógica de negócios de um ambiente bancário.
