package application.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.util.ResourceBundle;

import application.dao.ProdutoDao;
import application.model.ProdutoModel;

public class CadastroDeProduto implements Initializable {

    @FXML private TextField txtNome, txtBuscar, txtDescricao;
    @FXML private TextField txtPrecoCusto, txtPrecoVenda;
    @FXML private TextField txtQuantidade, txtId, txtCodigoBarras;
    @FXML private TextField txtMargemLucro;
    @FXML private TextField txtEstoqueMinimo;

    @FXML private ComboBox<String> cbCategoria;

    @FXML private TableView<ProdutoModel> tableProdutos;

    @FXML private TableColumn<ProdutoModel, Integer> colID;
    @FXML private TableColumn<ProdutoModel, String> colNome, colDescricao, colCategoria, colCodigoBarras;
    @FXML private TableColumn<ProdutoModel, Double> colPrecoCusto, colPrecoVenda;
    @FXML private TableColumn<ProdutoModel, Integer> colQtd;

    private ObservableList<ProdutoModel> lista = FXCollections.observableArrayList();
    private ProdutoModel produto = new ProdutoModel(0, "", "", "", "", 0, 0, 0, 0);

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colCodigoBarras.setCellValueFactory(new PropertyValueFactory<>("codBarras"));
        colPrecoCusto.setCellValueFactory(new PropertyValueFactory<>("precoCusto"));
        colPrecoVenda.setCellValueFactory(new PropertyValueFactory<>("precoVenda"));
        colQtd.setCellValueFactory(new PropertyValueFactory<>("quantidade"));

        tableProdutos.setItems(lista);

        // 🔥 CATEGORIAS
        cbCategoria.getItems().addAll("Eletrônicos", "Cabos", "Iluminação", "Ferramentas");

        listar(null);

        // 🔢 Apenas números no código de barras
        txtCodigoBarras.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                txtCodigoBarras.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });

        // 💰 Cálculo automático
        txtMargemLucro.textProperty().addListener((obs, oldVal, newVal) -> calcularPrecoVenda());
        txtPrecoCusto.textProperty().addListener((obs, oldVal, newVal) -> calcularPrecoVenda());

        // 🖱️ Clique na tabela
        tableProdutos.setOnMouseClicked(e -> {
            ProdutoModel p = tableProdutos.getSelectionModel().getSelectedItem();
            if (p != null) {
                txtId.setText(String.valueOf(p.getId()));
                txtNome.setText(p.getNome());
                txtDescricao.setText(p.getDescricao());
                txtCodigoBarras.setText(p.getCodBarras());
                txtPrecoCusto.setText(String.valueOf(p.getPrecoCusto()));
                txtPrecoVenda.setText(String.valueOf(p.getPrecoVenda()));
                txtQuantidade.setText(String.valueOf(p.getQuantidade()));
                cbCategoria.setValue(p.getCategoria());
                produto = p;
            }
        });

        // 🔥 COR DO ESTOQUE (CORRIGIDO)
        colQtd.setCellFactory(column -> new TableCell<ProdutoModel, Integer>() {
            @Override
            protected void updateItem(Integer qtd, boolean empty) {
                super.updateItem(qtd, empty);

                if (empty || qtd == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(qtd.toString());

                    // Só pinta se tiver valor definido
                    if (!txtEstoqueMinimo.getText().isEmpty()) {
                        try {
                            int minimo = Integer.parseInt(txtEstoqueMinimo.getText());

                            if (qtd <= minimo) {
                                setStyle("-fx-background-color: red; -fx-text-fill: white;");
                            } else {
                                setStyle("");
                            }

                        } catch (Exception e) {
                            setStyle("");
                        }
                    } else {
                        setStyle("");
                    }
                }
            }
        });
    }

    private void calcularPrecoVenda() {
        try {
            if (!txtPrecoCusto.getText().isEmpty() && !txtMargemLucro.getText().isEmpty()) {
                double custo = Double.parseDouble(txtPrecoCusto.getText().replace(",", "."));
                double margem = Double.parseDouble(txtMargemLucro.getText().replace(",", "."));
                double venda = custo + (custo * margem / 100);
                txtPrecoVenda.setText(String.format("%.2f", venda).replace(",", "."));
            }
        } catch (Exception e) {
        }
    }

    @FXML
    public void salvar() {
        try {
            ProdutoDao dao = new ProdutoDao();

            if (txtNome.getText().isEmpty() ||
                txtPrecoCusto.getText().isEmpty() ||
                txtPrecoVenda.getText().isEmpty() ||
                txtCodigoBarras.getText().isEmpty() ||
                cbCategoria.getValue() == null) {

                new Alert(Alert.AlertType.WARNING, "Preencha todos os campos obrigatórios!").show();
                return;
            }

            if (produto.getId() == 0 && dao.codigoExiste(txtCodigoBarras.getText())) {
                new Alert(Alert.AlertType.ERROR, "Código de barras já existe!").show();
                return;
            }

            double precoCusto = Double.parseDouble(txtPrecoCusto.getText().replace(",", "."));
            double precoVenda = Double.parseDouble(txtPrecoVenda.getText().replace(",", "."));

            produto.setNome(txtNome.getText());
            produto.setDescricao(txtDescricao.getText());
            produto.setCategoria(cbCategoria.getValue());
            produto.setCodBarras(txtCodigoBarras.getText());
            produto.setPrecoCusto(precoCusto);
            produto.setPrecoVenda(precoVenda);

            if (produto.getId() == 0) produto.setQuantidade(0);

            dao.salvar(produto);

            new Alert(Alert.AlertType.INFORMATION, "Produto salvo com sucesso!").show();

            listar(null);
            limpar();

        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Use apenas números válidos!").show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erro ao salvar!").show();
            e.printStackTrace();
        }
    }

    @FXML
    public void Pesquisar() {
        listar(txtBuscar.getText());
    }

    @FXML
    public void Excluir() {
        if (produto.getId() == 0) {
            new Alert(Alert.AlertType.WARNING, "Selecione um produto!").show();
            return;
        }

        new ProdutoDao().excluir(produto.getId());

        new Alert(Alert.AlertType.INFORMATION, "Produto excluído!").show();

        listar(null);
        limpar();
    }

    private void listar(String valor) {
        ProdutoDao dao = new ProdutoDao();
        lista.clear();
        lista.addAll(dao.listarProduto(valor));
    }

    private void limpar() {
        txtNome.clear();
        txtDescricao.clear();
        txtPrecoCusto.clear();
        txtPrecoVenda.clear();
        txtMargemLucro.clear();
        txtEstoqueMinimo.clear();
        txtQuantidade.clear();
        txtBuscar.clear();
        txtId.clear();
        txtCodigoBarras.clear();
        cbCategoria.setValue(null);

        produto = new ProdutoModel(0, "", "", "", "", 0, 0, 0, 0);
    }
}