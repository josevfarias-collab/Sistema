package application.view;

import application.dao.UsuarioDAO;
import application.model.UsuarioModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
//Deve estar assim em todos:
import application.util.Sessao;

public class LoginController {

    @FXML private TextField txtLogin;
    @FXML private PasswordField txtSenha;

    @FXML
    public void entrar() {

        String login = txtLogin.getText();
        String senha = txtSenha.getText();

        // ❌ VALIDAÇÃO
        if (login.isEmpty() || senha.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Preencha login e senha!").show();
            return;
        }

        UsuarioDAO dao = new UsuarioDAO();
        UsuarioModel usuario = dao.validarLogin(login, senha);

        // ❌ LOGIN INVÁLIDO
        if (usuario == null) {
            new Alert(Alert.AlertType.ERROR, "Login ou senha inválidos!").show();
            return;
        }

        try {
            // 🔥 SALVA USUÁRIO (Identifica se é Gerente, Vendedor ou Estoquista)
            Sessao.setUsuario(usuario);

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/application/view/sistema.fxml"));

            Parent root = loader.load();

            // 🔥 PEGA O CONTROLLER E MOSTRA MENSAGEM (Sua lógica original de boas-vindas)
            SistemaController controller = loader.getController();
            controller.mostrarMensagemBoasVindas();

            // 🔥 NOVA JANELA (Mantendo suas dimensões 1200x700)
            Stage stage = new Stage();

            Scene scene = new Scene(root);

            stage.setWidth(1200);
            stage.setHeight(700);

            stage.setScene(scene);
            stage.setTitle("Sistema");
            stage.show();

            // 🔥 FECHA LOGIN
            ((Stage) txtLogin.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erro ao abrir sistema").show();
        }
    }
}