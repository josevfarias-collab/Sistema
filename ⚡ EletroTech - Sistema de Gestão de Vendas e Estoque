⚡ EletroTech - Sistema de Gestão de Vendas e Estoque
Este projeto é uma solução robusta desenvolvida em JavaFX para a empresa EletroTech Distribuidora, visando automatizar o controle de frente de caixa (PDV) e a gestão rigorosa de armazém.

🚀 Funcionalidades Implementadas
📦 Gestão de Estoque & Produtos
Baixa Automática (RN01): Integração total entre PDV e Estoque. Vendeu, baixou.

Bloqueio de Venda sem Estoque (RN02): O sistema impede vendas negativas com alertas em tempo real.

Processamento de Entradas: Tela dedicada para reposição de mercadorias.

Estorno Automático (RN03): Ao cancelar uma venda, os produtos retornam ao estoque com registro de motivo.

💰 Frente de Caixa (PDV)
Pagamentos Mistos: Suporte para pagamento simultâneo (Dinheiro + Cartão + Pix).

Sistema de Descontos: Limite de 5% para vendedores. Descontos maiores exigem autenticação de Gerente via popup.

QR Code Pix: Geração dinâmica de QR Code para pagamentos instantâneos.

Cupom Não Fiscal: Emissão de recibo detalhado e estilizado (estilo impressora térmica).

🔐 Segurança e Acessos
Níveis de Permissão: Interface adaptativa para Gerentes, Vendedores e Estoquistas.

Controle de Sessão: Trava de segurança que impede navegação indevida durante o carregamento inicial.

🛠 Pré-requisitos e Instalação
1. Banco de Dados (MySQL)
O sistema utiliza MySQL. Siga os passos abaixo:

Crie um banco de dados chamado eletrotech_db (ou o nome definido no seu projeto).

Execute o script SQL localizado em: src/main/resources/sql/script_banco.sql.

Importante: Verifique as credenciais (usuário e senha) na sua classe de conexão:

Java
// Localize este arquivo no seu projeto: application.dao.Conexao
private static final String USER = "seu_usuario";
private static final String PASS = "sua_senha";
2. Configuração do JavaFX
SDK: Certifique-se de ter o JavaFX SDK configurado no seu Eclipse/IntelliJ.

VM Arguments: Lembre-se de adicionar os módulos do JavaFX se estiver rodando via terminal ou se o projeto não for Maven:
--module-path "caminho/para/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml

3. Bibliotecas (JARs)
Inclua no seu Build Path:

mysql-connector-j-x.x.x.jar

Bibliotecas do JavaFX (controls, fxml, graphics, base).

🖥️ Como Executar
Importe o projeto na sua IDE (Eclipse/IntelliJ).

Certifique-se de que o servidor MySQL está ativo.

Execute a classe application.Main.

👨‍🏫 Instruções para Avaliação (Professor Carlos Guilherme)
O usuário e senha inicial para teste podem ser encontrados na tabela usuarios do banco de dados (Sugestão: Crie um usuário 'admin' no seu script SQL).

O fluxo de venda pode ser testado selecionando múltiplos produtos e combinando formas de pagamento.

O bloqueio de estoque pode ser validado tentando vender uma quantidade superior à disponível após a entrada de mercadoria.
