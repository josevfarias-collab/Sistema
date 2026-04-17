package application.view;

import application.dao.ProdutoDao;
import application.dao.MovimentacaoEstoqueDAO;
import application.model.ProdutoModel;
import application.model.MovimentacaoEstoqueModel;

import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class MovimentacaoController {

    @FXML private ComboBox<ProdutoModel> cbProduto;
    @FXML private TextField txtQuantidade;

    @FXML private TableView<MovimentacaoEstoqueModel> tableMovimentacao;

    @FXML private TableColumn<MovimentacaoEstoqueModel, String> colProduto;
    @FXML private TableColumn<MovimentacaoEstoqueModel, String> colTipo;
    @FXML private TableColumn<MovimentacaoEstoqueModel, Integer> colQtd;
    @FXML private TableColumn<MovimentacaoEstoqueModel, String> colUsuario;
    @FXML private TableColumn<MovimentacaoEstoqueModel, String> colData;

    private ObservableList<ProdutoModel> listaProdutos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        cbProduto.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(ProdutoModel item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNome());
            }
        });

        cbProduto.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(ProdutoModel item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNome());
            }
        });

        carregarProdutos();

        // ✅ CORRIGIDO
        colProduto.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colQtd.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));

        carregarHistorico();
    }

    private void carregarProdutos() {
        ProdutoDao dao = new ProdutoDao();
        listaProdutos.addAll(dao.listarProduto(null));
        cbProduto.setItems(listaProdutos);
    }

    @FXML
    public void entrada() {
        movimentar("ENTRADA");
    }

    @FXML
    public void saida() {
        movimentar("SAIDA");
    }

    private void movimentar(String tipo) {

        try {

            ProdutoModel produto = cbProduto.getValue();

            if (produto == null || txtQuantidade.getText().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Preencha tudo!").show();
                return;
            }

            int qtd = Integer.parseInt(txtQuantidade.getText());

            ProdutoDao dao = new ProdutoDao();

            // ✅ CORRIGIDO (usuarioId inteiro)
            dao.processarEstoque(produto.getId(), qtd, tipo, 1);

            new Alert(Alert.AlertType.INFORMATION, "Movimentação realizada!").show();

            carregarHistorico();

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    private void carregarHistorico() {

        MovimentacaoEstoqueDAO dao = new MovimentacaoEstoqueDAO();

        tableMovimentacao.setItems(
                FXCollections.observableArrayList(
                        dao.listarHistorico(
                                0,
                                LocalDate.now().minusDays(30),
                                LocalDate.now()
                        )
                )
        );
    }
}