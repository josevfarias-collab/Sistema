package application.view;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import application.dao.ProdutoDao;
import application.dao.UsuarioDAO;
import application.dao.VendaDAO;
import application.model.VendaModel;
import application.util.Sessao;
import application.model.ItemCarrinho;
import application.model.ProdutoModel;
import application.model.UsuarioModel;
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
        // --- VINCULAÇÃO DA TABELA ---
        tableCarrinho.setItems(carrinho);
        colNome.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNome()));
        colQtd.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getQuantidade()));
        colPreco.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getPreco()));

        cbPagamento.getItems().addAll("DINHEIRO", "CARTAO", "PIX");

        cbPagamento.setOnAction(e -> {
            String tipo = cbPagamento.getValue();
            if (tipo == null) return;
            
            // Alterado para não esconder os outros, permitindo pagamento misto
            switch (tipo) {
                case "DINHEIRO": boxDinheiro.setVisible(true); break;
                case "CARTAO": boxCartao.setVisible(true); break;
                case "PIX": boxPix.setVisible(true); gerarQrCodePix(); break;
            }
        });

        // Ouvintes para atualizar o troco em tempo real com qualquer campo
        txtDinheiro.textProperty().addListener((obs, oldVal, newVal) -> atualizarTroco());
        txtCartao.textProperty().addListener((obs, oldVal, newVal) -> atualizarTroco());
        txtPix.textProperty().addListener((obs, oldVal, newVal) -> atualizarTroco());
        txtDesconto.textProperty().addListener((obs, oldVal, newVal) -> atualizarTroco());

        aplicarRestricoesDeCargo();
    }

    private void aplicarRestricoesDeCargo() {
        UsuarioModel logado = Sessao.getUsuario();
        if (logado != null) {
            String cargo = logado.getTipo().toUpperCase();
            if (cargo.equals("ESTOQUISTA")) {
                cbPagamento.setDisable(true);
                txtDinheiro.setEditable(false);
                txtDesconto.setEditable(false);
                System.out.println("Acesso limitado: Estoquista detectado.");
            } 
            else if (cargo.equals("VENDEDOR")) {
                System.out.println("Acesso: Vendedor detectado.");
            } 
            else if (cargo.equals("GERENTE")) {
                System.out.println("Acesso total: Gerente detectado.");
            }
        }
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

            // Adiciona ao carrinho
            carrinho.add(new ItemCarrinho(p.getId(), p.getNome(), qtd, p.getPrecoVenda()));
            
            // 🔥 FORÇA A TABELA A MOSTRAR O PRODUTO IMEDIATAMENTE
            tableCarrinho.refresh();

            txtProduto.clear();
            txtQuantidade.clear();
            atualizarTroco();

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erro ao adicionar produto").show();
        }
    }

    @FXML
    public void finalizarVenda() {
        try {
            double totalVenda = calcularTotal();

            if (!txtDesconto.getText().trim().isEmpty()) {
                double desconto = Double.parseDouble(txtDesconto.getText().trim().replace(",", "."));
                if (!validarDesconto(desconto)) return;
                totalVenda -= totalVenda * desconto / 100;
            }

            double valorDinheiro = txtDinheiro.getText().trim().isEmpty() ? 0.0 :
                    Double.parseDouble(txtDinheiro.getText().trim().replace(",", "."));
            double valorCartao = txtCartao.getText().trim().isEmpty() ? 0.0 :
                    Double.parseDouble(txtCartao.getText().trim().replace(",", "."));
            double valorPix = txtPix.getText().trim().isEmpty() ? 0.0 :
                    Double.parseDouble(txtPix.getText().trim().replace(",", "."));

            double valorRecebido = valorDinheiro + valorCartao + valorPix;
            
            if (valorRecebido < totalVenda) {
                new Alert(Alert.AlertType.WARNING, "Valor insuficiente!").show();
                return;
            }

            double troco = valorRecebido - totalVenda;
            lblTroco.setText("Troco: R$ " + String.format("%.2f", troco));

            exibirCupom(txtCliente.getText().trim(), totalVenda, valorRecebido, troco);

            new Alert(Alert.AlertType.INFORMATION, "Venda finalizada com sucesso!").show();

            carrinho.clear();
            txtCliente.clear();
            txtDinheiro.clear();
            txtCartao.clear();
            txtPix.clear();
            txtDesconto.clear();
            lblTroco.setText("Troco: R$ 0,00");
            
            boxDinheiro.setVisible(false);
            boxCartao.setVisible(false);
            boxPix.setVisible(false);

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erro ao finalizar a venda: " + e.getMessage()).show();
        }
    }

    private void exibirCupom(String cliente, double total, double pago, double troco) {
        String nomeCliente = cliente.isEmpty() ? "CONSUMIDOR FINAL" : cliente.toUpperCase();
        String dataVenda = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                            .format(java.time.LocalDateTime.now());

        StringBuilder sb = new StringBuilder();
        sb.append("==========================================\n");
        sb.append("           NOSSA LOJA EXCELSIOR           \n");
        sb.append("       Rua do Sucesso, 777 - Centro       \n");
        sb.append("==========================================\n");
        sb.append(String.format("DATA/HORA: %s\n", dataVenda));
        sb.append(String.format("CLIENTE:   %s\n", nomeCliente));
        sb.append("------------------------------------------\n");
        sb.append(String.format("%-20s %-5s %-12s\n", "ITEM", "QTD", "VALOR"));
        
        for (ItemCarrinho item : carrinho) {
            String nomeProd = item.getNome().length() > 18 ? item.getNome().substring(0, 18) : item.getNome();
            sb.append(String.format("%-20s %-5d R$ %-10.2f\n", 
                    nomeProd, item.getQuantidade(), (item.getPreco() * item.getQuantidade())));
        }
        
        sb.append("------------------------------------------\n");
        // As cores nós vamos aplicar via CSS na Label, aqui mantemos o texto alinhado
        sb.append(String.format("TOTAL DA VENDA:             R$ %10.2f\n", total));
        sb.append(String.format("VALOR RECEBIDO:            R$ %10.2f\n", pago));
        sb.append("------------------------------------------\n");
        sb.append(String.format("TROCO:                     R$ %10.2f\n", troco < 0 ? 0 : troco));
        sb.append("==========================================\n");
        sb.append("        OBRIGADO E VOLTE SEMPRE!          \n");
        sb.append("==========================================\n");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Recibo de Venda");
        alert.setHeaderText(null);
        alert.setGraphic(null); 

        Label label = new Label(sb.toString());
        
        // CSS PARA DEIXAR BONITO:
        // 1. Background que lembra papel antigo (#feffdf)
        // 2. Borda tracejada lateral
        // 3. Fonte monoespaçada preta
        label.setStyle(
            "-fx-font-family: 'Consolas'; " +
            "-fx-font-size: 14; " +
            "-fx-text-fill: #2e2e2e; " +
            "-fx-background-color: #ffffff; " + 
            "-fx-padding: 20; " +
            "-fx-border-color: #cccccc; " +
            "-fx-border-style: dashed; " +
            "-fx-border-width: 2; " +
            "-fx-alignment: center;"
        );

        // Ajuste para centralizar o cupom na janela do Alerta
        VBox container = new VBox(label);
        container.setStyle("-fx-alignment: center; -fx-padding: 10; -fx-background-color: #eeeeee;");
        
        alert.getDialogPane().setContent(container);
        
        // Remove o texto padrão "OK" e deixa a janela mais limpa
        alert.showAndWait();
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

            double pDinheiro = txtDinheiro.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtDinheiro.getText().trim().replace(",", "."));
            double pCartao = txtCartao.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtCartao.getText().trim().replace(",", "."));
            double pPix = txtPix.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtPix.getText().trim().replace(",", "."));

            double totalPago = pDinheiro + pCartao + pPix;
            double troco = totalPago - total;

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