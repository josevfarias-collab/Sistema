package application.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class SistemaController {

    @FXML
    private StackPane painelPrincipal;

    // 🔥 MÉTODO PARA CARREGAR TELAS DENTRO DO PAINEL
    private void carregarTela(String caminho) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(caminho));
            Parent root = loader.load();

            painelPrincipal.getChildren().clear();
            painelPrincipal.getChildren().add(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // MENU
    @FXML
    public void abrirDashboard() {
        carregarTela("/application/view/Dashboard.fxml");
    }

    @FXML
    public void abrirCliente() {
        carregarTela("/application/view/cliente.fxml");
    }

    @FXML
    public void AbrirCadastroProduto() {
        carregarTela("/application/view/Produtos.fxml");
    }

    @FXML
    public void AbrirProcessarEstoque() {
        carregarTela("/application/view/ProcessarEstoque.fxml");
    }

    @FXML
    public void abrirCadastroUsuario() {
        carregarTela("/application/view/cadastro_usuario.fxml");
        
    }
    
    @FXML
    public void abrirMovimentacao() {
        carregarTela("/application/view/movimentacao.fxml");
    }

    @FXML
    public void sair() {
        System.exit(0);
    }
}