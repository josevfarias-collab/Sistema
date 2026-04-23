package application.view;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import application.dao.ProdutoDao;
import application.dao.UsuarioDAO;
import application.dao.VendaDAO;
import application.model.VendaModel;
import application.model.ItemCarrinho;
import application.model.ProdutoModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class VendaController {

    @FXML private TextField txtDesconto, txtDinheiro, txtCartao, txtPix, txtVendaId;
    @FXML private TextField txtProduto, txtQuantidade, txtCliente;
    @FXML private Label lblTroco;
    @FXML private TableView<ItemCarrinho> tableCarrinho;
    @FXML private TableColumn<ItemCarrinho, String> colNome;
    @FXML private TableColumn<ItemCarrinho, Integer> colQtd;
    @FXML private TableColumn<ItemCarrinho, Double> colPreco;
    @FXML private ComboBox<String> cbPagamento;
    @FXML private VBox boxCartao, boxPix, boxDinheiro;
    @FXML private TextField txtNumeroCartao, txtNomeCartao, txtValidade, txtCVV;
    @FXML private ImageView imgPix;

    private boolean pixPago = false;
    private ObservableList<ItemCarrinho> carrinho = FXCollections.observableArrayList();
    private int usuarioId = 1;
    private VendaDAO vendaDAO = new VendaDAO();

    @FXML
    public void initialize() {
        tableCarrinho.setItems(carrinho);

        colNome.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNome()));
        colQtd.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getQuantidade()));
        colPreco.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getPreco()));

        cbPagamento.getItems().addAll("DINHEIRO", "CARTAO", "PIX");

        cbPagamento.setOnAction(e -> {
            String tipo = cbPagamento.getValue();
            boxDinheiro.setVisible(false);
            boxCartao.setVisible(false);
            boxPix.setVisible(false);

            if (tipo == null) return;

            switch (tipo) {
                case "DINHEIRO":
                    boxDinheiro.setVisible(true);
                    break;
                case "CARTAO":
                    boxCartao.setVisible(true);
                    break;
                case "PIX":
                    boxPix.setVisible(true);
                    gerarQrCodePix();
                    break;
            }
        });

        // Atualizar troco em tempo real
        txtDinheiro.textProperty().addListener((obs, oldVal, newVal) -> atualizarTroco());
        txtDesconto.textProperty().addListener((obs, oldVal, newVal) -> atualizarTroco());
    }

    private void gerarQrCodePix() {
        double total = calcularTotal();
        if (total <= 0) return;

        String valor = String.format("%.2f", total).replace(",", ".").trim();
        txtPix.setText(valor);

        String chavePix = "SUA_CHAVE_AQUI".trim();

        try {
            String url = "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=" +
                    URLEncoder.encode(chavePix + " | Valor: R$ " + valor, StandardCharsets.UTF_8);
            imgPix.setImage(new Image(url));
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erro ao gerar QR Code").show();
        }
        pixPago = false;
    }

    @FXML
    public void confirmarPix() {
        pixPago = true;
        new Alert(Alert.AlertType.INFORMATION, "Pagamento PIX confirmado!").show();
    }

    @FXML
    public void adicionarProduto() {
        try {
            String nome = txtProduto.getText().trim();
            int qtd = Integer.parseInt(txtQuantidade.getText().trim());

            if (nome.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Informe o nome do produto").show();
                return;
            }

            ProdutoDao dao = new ProdutoDao();
            List<ProdutoModel> produtos = dao.listarProduto(nome);

            if (produtos.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Produto não encontrado").show();
                return;
            }

            ProdutoModel p = produtos.get(0);

            if (qtd > p.getQuantidade()) {
                new Alert(Alert.AlertType.ERROR, "Estoque insuficiente").show();
                return;
            }

            carrinho.add(new ItemCarrinho(p.getId(), p.getNome(), qtd, p.getPrecoVenda()));

            txtProduto.clear();
            txtQuantidade.clear();

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erro ao adicionar produto").show();
        }
    }

    @FXML
    public void finalizarVenda() {
        try {
            double totalVenda = calcularTotal();

            // aplicar desconto
            if (!txtDesconto.getText().trim().isEmpty()) {
                double desconto = Double.parseDouble(txtDesconto.getText().trim().replace(",", "."));
                if (!validarDesconto(desconto)) return;
                totalVenda -= totalVenda * desconto / 100;
            }

            // pegar valores de cada forma de pagamento
            double valorDinheiro = txtDinheiro.getText().trim().isEmpty() ? 0.0 :
                    Double.parseDouble(txtDinheiro.getText().trim().replace(",", "."));
            double valorCartao = txtCartao.getText().trim().isEmpty() ? 0.0 :
                    Double.parseDouble(txtCartao.getText().trim().replace(",", "."));
            double valorPix = txtPix.getText().trim().isEmpty() ? 0.0 :
                    Double.parseDouble(txtPix.getText().trim().replace(",", "."));

            // somar tudo
            double valorRecebido = valorDinheiro + valorCartao + valorPix;
            double troco = valorRecebido - totalVenda;

            lblTroco.setText("Troco: R$ " + String.format("%.2f", troco < 0 ? 0 : troco));

            // exibir cupom com valores corretos
            exibirCupom(txtCliente.getText().trim(), totalVenda, valorRecebido, troco);

            new Alert(Alert.AlertType.INFORMATION, "Venda finalizada com sucesso!").show();

            // limpar campos
            carrinho.clear();
            txtCliente.clear();
            txtDinheiro.clear();
            txtCartao.clear();
            txtPix.clear();
            txtDesconto.clear();
            lblTroco.setText("Troco: R$ 0,00");

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erro ao finalizar a venda: " + e.getMessage()).show();
        }
    }

    

    

    // Método do cupom (adicionado)
    private void exibirCupom(String cliente, double total, double pago, double troco) {
        String nomeCliente = cliente.isEmpty() ? "Consumidor Final" : cliente;

        String resumo = "🧾 CUPOM FISCAL\n\n" +
                "Cliente: " + nomeCliente + "\n" +
                "Data: " + java.time.LocalDate.now() + "\n\n" +
                "TOTAL: R$ " + String.format("%.2f", total) + "\n" +
                "PAGO:  R$ " + String.format("%.2f", pago) + "\n" +
                "TROCO: R$ " + String.format("%.2f", troco < 0 ? 0 : troco) + "\n\n" +
                "Obrigado pela preferência!";

        new Alert(Alert.AlertType.INFORMATION, resumo).show();
    }

    private double calcularTotal() {
        double total = 0;
        for (ItemCarrinho item : carrinho) {
            total += item.getPreco() * item.getQuantidade();
        }
        return total;
    }

    private void atualizarTroco() {
        try {
            double total = calcularTotal();

            if (!txtDesconto.getText().trim().isEmpty()) {
                double desconto = Double.parseDouble(txtDesconto.getText().trim().replace(",", "."));
                total -= total * desconto / 100;
            }

            double pago = txtDinheiro.getText().trim().isEmpty() ? 0 :
                    Double.parseDouble(txtDinheiro.getText().trim().replace(",", "."));

            double troco = pago - total;

            lblTroco.setText("Troco: R$ " + (troco < 0 ? "0.00" : String.format("%.2f", troco)));

        } catch (Exception e) {
            lblTroco.setText("Troco: R$ 0.00");
        }
    }

    private boolean validarDesconto(double desconto) {
        if (desconto <= 5) return true;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Desconto acima de 5%");
        dialog.setContentText("Senha do gerente:");
        String senha = dialog.showAndWait().orElse("");

        return new UsuarioDAO().validarGerente(senha);
    }

    @FXML
    public void cancelarVenda() {
        try {
            if (txtVendaId.getText().trim().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Informe o ID da venda").show();
                return;
            }

            int vendaId = Integer.parseInt(txtVendaId.getText().trim());
            String status = vendaDAO.buscarStatus(vendaId);

            if (status == null) {
                new Alert(Alert.AlertType.ERROR, "Venda não encontrada").show();
                return;
            }

            if ("CANCELADA".equals(status)) {
                new Alert(Alert.AlertType.WARNING, "Venda já cancelada").show();
                return;
            }

            TextInputDialog dialog = new TextInputDialog();
            dialog.setHeaderText("Motivo do cancelamento:");
            String motivo = dialog.showAndWait().orElse("");

            if (motivo.trim().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Informe o motivo").show();
                return;
            }

            vendaDAO.cancelarVenda(vendaId, motivo);
            new Alert(Alert.AlertType.INFORMATION, "Venda cancelada com sucesso!").show();
            txtVendaId.clear();

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }
}