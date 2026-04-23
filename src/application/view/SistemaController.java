package application.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label; 

public class SistemaController {

    @FXML
    private StackPane painelPrincipal;

    // 🔥 MÉTODO PARA CARREGAR TELAS
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

    // 🔥 MENSAGEM DE BOAS-VINDAS
    public void mostrarMensagemBoasVindas() {
        Label mensagem = new Label("Seja bem-vindo");
        mensagem.setStyle("-fx-text-fill: white; -fx-font-size: 24; -fx-font-weight: bold;");

        painelPrincipal.getChildren().clear();
        painelPrincipal.getChildren().add(mensagem);

        // some depois de 3 segundos e abre o Dashboard
        new Thread(() -> {
            try {
                Thread.sleep(3000);

                javafx.application.Platform.runLater(() -> {
                    carregarTela("/application/view/Dashboard.fxml"); 
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
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
    public void abrirVendas() {
        carregarTela("/application/view/Venda.fxml");
    }

    @FXML
    public void sair() {
        System.exit(0);
    }
}