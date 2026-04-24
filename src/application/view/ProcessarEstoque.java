package application.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import application.model.ProdutoModel;
import application.util.Sessao;
import application.dao.ProdutoDao;

public class ProcessarEstoque implements Initializable {

    @FXML private TextField txtBuscar, txtId, txtCodigoBarras, txtProduto, txtQuantidade;

    @FXML private TableView<ProdutoModel> tableProdutos;

    @FXML private TableColumn<ProdutoModel, Integer> colID;
    @FXML private TableColumn<ProdutoModel, String> colNome, colDescricao, colCategoria, colCodigoBarras;
    @FXML private TableColumn<ProdutoModel, Integer> colQtd;

    // ✔ deixa SOMENTE isso aqui
    @FXML private ToggleGroup grupoOperacao;
    @FXML private RadioButton rbEntrada;
    @FXML private RadioButton rbSaida;

    private ObservableList<ProdutoModel> lista = FXCollections.observableArrayList();
    private ProdutoModel produto = new ProdutoModel(0,"","","","",0,0,0,0);

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colCodigoBarras.setCellValueFactory(new PropertyValueFactory<>("codBarras"));
        colQtd.setCellValueFactory(new PropertyValueFactory<>("quantidade"));

        tableProdutos.setItems(lista);

        listar(null);

        // ✔ CORREÇÃO IMPORTANTE: garantir ToggleGroup caso FXML falhe
        if (grupoOperacao == null) {
            grupoOperacao = new ToggleGroup();
        }

        rbEntrada.setToggleGroup(grupoOperacao);
        rbSaida.setToggleGroup(grupoOperacao);

        tableProdutos.setOnMouseClicked(e -> {
            ProdutoModel p = tableProdutos.getSelectionModel().getSelectedItem();

            if (p != null) {
                txtId.setText(String.valueOf(p.getId()));
                txtCodigoBarras.setText(p.getCodBarras());
                txtProduto.setText(p.getNome());
                txtQuantidade.setText("");
                produto = p;
            }
        });
    }

    @FXML
    public void buscar() {
        listar(txtBuscar.getText());
    }

    @FXML
    public void atualizar() {
        listar(null);
        limpar();
    }

    private void listar(String valor) {
        lista.clear();
        lista.addAll(new ProdutoDao().listarProduto(valor));
    }

    @FXML
    public void processar() {

        if (produto.getId() == 0) {
            new Alert(Alert.AlertType.WARNING, "Selecione um produto").show();
            return;
        }

        if (grupoOperacao == null || grupoOperacao.getSelectedToggle() == null) {
            new Alert(Alert.AlertType.WARNING, "Escolha Entrada ou Saída").show();
            return;
        }

        try {
            String txt = txtQuantidade.getText();

            if (txt == null || txt.trim().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Digite uma quantidade válida").show();
                return;
            }

            int qtd = Integer.parseInt(txt);

            if (qtd <= 0) {
                new Alert(Alert.AlertType.WARNING, "Quantidade inválida").show();
                return;
            }

            String operacao = rbEntrada.isSelected() ? "ENTRADA" : "SAIDA";

            if (operacao.equals("SAIDA") && qtd > produto.getQuantidade()) {
                new Alert(Alert.AlertType.WARNING, "Estoque insuficiente").show();
                return;
            }

            ProdutoDao dao = new ProdutoDao();

            int usuarioId = 0;
            if (Sessao.getUsuario() != null) {
                usuarioId = Sessao.getUsuario().getId();
            }

            dao.processarEstoque(produto.getId(), qtd, operacao, usuarioId);

            new Alert(Alert.AlertType.INFORMATION, "Operação realizada!").show();

            listar(null);
            limpar();

        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.WARNING, "Digite uma quantidade válida").show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    private void limpar() {
        txtId.clear();
        txtCodigoBarras.clear();
        txtProduto.clear();
        txtQuantidade.clear();

        if (grupoOperacao != null) {
            grupoOperacao.selectToggle(null);
        }

        produto = new ProdutoModel(0,"","","","",0,0,0,0);
    }

    @FXML
    public void abrirHistorico() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/application/view/HistoricoProcessamento.fxml")
            );

            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Histórico");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void abrirMovimentacao() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/application/view/movimentacao.fxml")
            );

            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Movimentação de Estoque");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}