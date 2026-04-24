package application.view;

import application.model.UsuarioModel;
import application.util.Sessao;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label; 
import javafx.scene.control.Button;

public class SistemaController {

    @FXML private StackPane painelPrincipal;

    @FXML private Button btnDashboard, btnClientes, btnProdutos, btnEstoque, btnUsuarios, btnMovimentacao, btnVendas;

    // 🚩 Variável de controle para impedir que o timer mude a tela se você já tiver saído
    private boolean telaJaMudou = false;

    @FXML
    public void initialize() {
        UsuarioModel logado = Sessao.getUsuario();

        if (logado != null) {
            String cargo = logado.getTipo().trim().toUpperCase();

            // Lógica de bloqueio (Funcionando para os 3 perfis)
            if (cargo.equals("VENDEDOR")) {
                bloquearBotao(btnProdutos);
                bloquearBotao(btnEstoque);
                bloquearBotao(btnUsuarios);
                bloquearBotao(btnMovimentacao);
            } 
            else if (cargo.equals("ESTOQUISTA")) {
                bloquearBotao(btnVendas);
                bloquearBotao(btnClientes);
                bloquearBotao(btnUsuarios);
                bloquearBotao(btnDashboard);
            }
        }
    }

    private void bloquearBotao(Button btn) {
        if (btn != null) {
            btn.setVisible(false);
            btn.setManaged(false);
        }
    }

    // 🔥 MENSAGEM DE BOAS-VINDAS ARRUMADA
    public void mostrarMensagemBoasVindas() {
        UsuarioModel logado = Sessao.getUsuario();
        
        String nome = (logado != null) ? logado.getNome() : "Usuário";
        String cargo = (logado != null) ? logado.getTipo().trim().toUpperCase() : "DESCONHECIDO";
        
        Label mensagem = new Label("Seja bem-vindo, " + nome + "!\n(Nível de Acesso: " + cargo + ")");
        mensagem.setStyle("-fx-text-fill: white; -fx-font-size: 24; -fx-font-weight: bold; -fx-text-alignment: center;");
        
        painelPrincipal.getChildren().clear();
        painelPrincipal.getChildren().add(mensagem);

        telaJaMudou = false; // Reseta a trava ao mostrar as boas-vindas

        // Timer para abrir o Dashboard
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(() -> {
                    // SÓ abre o dashboard se o usuário não tiver clicado em nada nesse tempo
                    if (!telaJaMudou) {
                        carregarTela("/application/view/Dashboard.fxml");
                    }
                });
            } catch (InterruptedException e) { e.printStackTrace(); }
        }).start();
    }

    private void carregarTela(String caminho) {
        try {
            telaJaMudou = true; // ⬅️ AVISAMOS QUE A TELA MUDOU (Isso cancela o timer das boas-vindas)
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(caminho));
            Parent root = loader.load();
            painelPrincipal.getChildren().clear();
            painelPrincipal.getChildren().add(root);
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML public void abrirDashboard() { carregarTela("/application/view/Dashboard.fxml"); }
    @FXML public void abrirCliente() { carregarTela("/application/view/cliente.fxml"); }
    @FXML public void AbrirCadastroProduto() { carregarTela("/application/view/Produtos.fxml"); }
    @FXML public void AbrirProcessarEstoque() { carregarTela("/application/view/ProcessarEstoque.fxml"); }
    @FXML public void abrirCadastroUsuario() { carregarTela("/application/view/cadastro_usuario.fxml"); }
    @FXML public void abrirMovimentacao() { carregarTela("/application/view/movimentacao.fxml"); }
    @FXML public void abrirVendas() { carregarTela("/application/view/Venda.fxml"); }
    @FXML public void sair() { System.exit(0); }
}