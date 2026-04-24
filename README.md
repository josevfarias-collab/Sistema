⚡ EletroTech: Ecossistema de Gestão Comercial (PDV & Estoque)
Este projeto consiste em uma solução empresarial completa desenvolvida em JavaFX, projetada sob medida para a EletroTech Distribuidora. O sistema foca na automação do fluxo entre a frente de caixa e o controle de armazém, eliminando falhas humanas e garantindo a integridade dos dados.

🚀 Arquitetura e Regras de Negócio
📦 Inteligência de Inventário
Sincronização em Tempo Real (RN01): Implementação de baixa automática de estoque. Cada transação no PDV atualiza o banco de dados instantaneamente.

Proteção de Estoque Negativo (RN02): Validação rigorosa que impede vendas caso a quantidade solicitada exceda o saldo físico disponível.

Logística Reversa (RN03): Módulo de cancelamento que realiza o estorno automático dos itens para o estoque com registro de auditoria.

💰 PDV Dinâmico (Ponto de Venda)
Transações Híbridas: Suporte para pagamentos mistos (Dinheiro + Cartão + Pix) em uma mesma venda.

Hierarquia de Descontos: Trava lógica para descontos superiores a 5%, exigindo autenticação de nível Gerencial.

Recibos Estilizados: Motor de renderização de Cupom Não Fiscal com formatação para alta legibilidade.

🔐 Segurança e Governança
Acesso Baseado em Perfis (RBAC): Interface adaptativa que oculta funções conforme o cargo (Vendedor, Estoquista ou Gerente).

Controle de Sessão: Trava de segurança que impede acessos indevidos durante o carregamento do sistema.

🛠 Configuração Técnica e Instalação
1. Requisitos de Software
Java: JDK 17 ou superior.

Banco de Dados: MySQL 8.0+.

Bibliotecas: JavaFX SDK e MySQL Connector/J.

2. Configuração de Conexão
Ajuste as credenciais no arquivo application.dao.Conexao:

Java
private static final String USER = "seu_usuario";
private static final String PASS = "sua_senha";
🗄️ Script de Inicialização do Banco de Dados (MySQL)
Abaixo está o script completo para criação da estrutura necessária. Execute este código no seu MySQL Workbench ou terminal:

SQL
-- 1. Criação e Uso do Schema
CREATE DATABASE IF NOT EXISTS sistema;
USE sistema;

-- 2. Tabela de Produtos
CREATE TABLE produto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    codigo_barras VARCHAR(100) UNIQUE,
    descricao VARCHAR(255),
    categoria VARCHAR(100),
    preco_custo DOUBLE,
    preco_venda DOUBLE,
    quantidade INT DEFAULT 0,
    estoque_minimo INT DEFAULT 0,
    ativo BOOLEAN DEFAULT true
);

-- 3. Tabela de Usuários (Funcionários)
CREATE TABLE usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    login VARCHAR(50) UNIQUE NOT NULL,
    senha VARCHAR(100) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    telefone VARCHAR(20),
    status VARCHAR(20) DEFAULT 'ATIVO'
);

-- 4. Tabela de Clientes
CREATE TABLE cliente (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    documento VARCHAR(18) UNIQUE,
    email VARCHAR(100) UNIQUE,
    telefone VARCHAR(25) NOT NULL,
    status ENUM('ATIVO', 'INATIVO') NOT NULL
);

-- 5. Tabela de Vendas (Cabeçalho)
CREATE TABLE venda (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT,
    data DATETIME DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10,2),
    status VARCHAR(20) DEFAULT 'FINALIZADA',
    motivo_cancelamento VARCHAR(255),
    FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);

-- 6. Tabela de Itens da Venda
CREATE TABLE item_venda (
    id INT AUTO_INCREMENT PRIMARY KEY,
    venda_id INT,
    produto_id INT,
    quantidade INT,
    preco DOUBLE,
    FOREIGN KEY (venda_id) REFERENCES venda(id),
    FOREIGN KEY (produto_id) REFERENCES produto(id)
);

-- 7. Tabela de Pagamentos (Multi-forma)
CREATE TABLE pagamento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    venda_id INT,
    tipo VARCHAR(20),
    valor DOUBLE,
    FOREIGN KEY (venda_id) REFERENCES venda(id)
);

-- 8. Histórico de Movimentação de Estoque
CREATE TABLE movimentacao_estoque (
    id INT AUTO_INCREMENT PRIMARY KEY,
    produto_id INT NOT NULL,
    usuario_id INT NOT NULL,
    quantidade INT NOT NULL,
    operacao VARCHAR(20) NOT NULL,
    data_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (produto_id) REFERENCES produto(id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

-- 9. Usuário Administrador Padrão
-- Login: admin | Senha: 123
INSERT INTO usuario (nome, login, senha, tipo)
VALUES ('Administrador Geral', 'admin', '123', 'GERENTE');
👨‍🏫 Nota para Avaliação: O sistema foi desenvolvido focando na experiência do usuário (UX) e na robustez das regras de negócio solicitadas para o projeto prático.
