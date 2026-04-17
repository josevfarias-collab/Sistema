package application.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import application.dao.UsuarioDAO;
import application.model.UsuarioModel;

public class CadastroUsuarioController {

    @FXML private TextField txtNome;
    @FXML private TextField txtLogin;
    @FXML private PasswordField txtSenha;

    @FXML private RadioButton rbVendedor;
    @FXML private RadioButton rbGerente;
    @FXML private RadioButton rbEstoquista;

    @FXML
    public void salvar() {

        String nome = txtNome.getText();
        String login = txtLogin.getText();
        String senha = txtSenha.getText();

        String tipo = "";

        if (rbVendedor.isSelected()) {
            tipo = "Vendedor";
        } else if (rbGerente.isSelected()) {
            tipo = "Gerente";
        } else if (rbEstoquista.isSelected()) {
            tipo = "Estoquista";
        }

        // 🔥 VALIDAÇÃO
        if (nome.isEmpty() || login.isEmpty() || senha.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Preencha todos os campos!").show();
            return;
        }

        if (tipo.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Selecione o tipo de usuário!").show();
            return;
        }

        UsuarioDAO dao = new UsuarioDAO();

        // 🔥 VERIFICA LOGIN DUPLICADO
        if (dao.existeLogin(login)) {
            new Alert(Alert.AlertType.ERROR, "Login já existe!").show();
            return;
        }

        // 🔥 CRIA OBJETO
        UsuarioModel usuario = new UsuarioModel();
        usuario.setNome(nome);
        usuario.setLogin(login);
        usuario.setSenha(senha);
        usuario.setTipo(tipo);

        // 🔥 SALVA NO BANCO
        dao.salvar(usuario);

        new Alert(Alert.AlertType.INFORMATION, "Usuário cadastrado com sucesso!").show();

        limpar();
    }

    private void limpar() {
        txtNome.clear();
        txtLogin.clear();
        txtSenha.clear();

        rbVendedor.setSelected(false);
        rbGerente.setSelected(false);
        rbEstoquista.setSelected(false);
    }
}