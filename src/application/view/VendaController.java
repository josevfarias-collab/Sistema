package application.view;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import application.dao.ProdutoDao;
import application.dao.VendaDAO;
import application.dao.ClienteDAO;
import application.model.VendaModel;
import application.model.ItemCarrinho;
import application.model.ProdutoModel;
import application.model.ClienteModel;

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
    }

    // =========================
    // GERAR QR CODE PIX
    // =========================
    private void gerarQrCodePix() {
        double total = calcularTotal();
        if (total <= 0) return;

        // .replace(",", ".") é vital, mas o trim() remove espaços fantasmas
        String valor = String.format("%.2f", total).replace(",", ".").trim();
        txtPix.setText(valor);

        // TESTE: Coloque sua chave sem nenhum espaço, ex: "63999998888" ou "seu@email.com"
        String chavePix = "SUA_CHAVE_AQUI".trim(); 

        String payload = gerarPayloadPix(chavePix, valor);

        try {
            // A codificação UTF-8 é necessária para que símbolos não quebrem a URL
            String url = "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data="
                    + URLEncoder.encode(payload, StandardCharsets.UTF_8);
            imgPix.setImage(new Image(url));
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erro ao gerar imagem do QR Code").show();
        }

        pixPago = false;
    }

    // =========================
    // CONFIRMAR PIX
    // =========================
    @FXML
    public void confirmarPix() {
        pixPago = true;
        new Alert(Alert.AlertType.INFORMATION, "Pagamento PIX confirmado!").show();
    }

    // =========================
    // ADICIONAR PRODUTO
    // =========================
    @FXML
    public void adicionarProduto() {
        try {
            String nome = txtProduto.getText();
            int qtd = Integer.parseInt(txtQuantidade.getText());

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

            for (ItemCarrinho item : carrinho) {
                if (item.getProdutoId() == p.getId()) {

                    int novaQtd = item.getQuantidade() + qtd;

                    if (novaQtd > p.getQuantidade()) {
                        new Alert(Alert.AlertType.ERROR, "Estoque insuficiente").show();
                        return;
                    }

                    item.setQuantidade(novaQtd);
                    tableCarrinho.refresh();
                    gerarQrCodePix();
                    return;
                }
            }

            carrinho.add(new ItemCarrinho(
                    p.getId(),
                    p.getNome(),
                    qtd,
                    p.getPrecoVenda()
            ));

            txtProduto.clear();
            txtQuantidade.clear();

            gerarQrCodePix();

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erro ao adicionar produto").show();
        }
    }

    // =========================
    // FINALIZAR VENDA
    // =========================
    @FXML
    public void finalizarVenda() {

        try {

            if (txtCliente.getText().trim().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Informe o cliente").show();
                return;
            }

            if (carrinho.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Carrinho vazio").show();
                return;
            }

            if ("PIX".equals(cbPagamento.getValue()) && !pixPago) {
                new Alert(Alert.AlertType.WARNING, "Confirme o pagamento PIX!");
                return;
            }

            ClienteDAO clienteDAO = new ClienteDAO();
            ClienteModel cliente = clienteDAO.buscarPorNome(txtCliente.getText());

            if (cliente == null) {
                new Alert(Alert.AlertType.ERROR, "Cliente não encontrado").show();
                return;
            }

            double totalVenda = calcularTotal();
            double pix = Double.parseDouble(txtPix.getText());

            double troco = pix - totalVenda;
            lblTroco.setText("Troco: R$ " + String.format("%.2f", troco));

            ProdutoDao produtoDao = new ProdutoDao();

            for (ItemCarrinho item : carrinho) {
                produtoDao.processarEstoque(
                        item.getProdutoId(),
                        item.getQuantidade(),
                        "SAIDA",
                        usuarioId
                );
            }

            VendaModel venda = new VendaModel();
            venda.setClienteId(cliente.getId());
            venda.setTotal(totalVenda);

            int vendaId = vendaDAO.salvarRetornandoId(venda);

            vendaDAO.salvarItens(vendaId, carrinho);
            vendaDAO.salvarPagamento(vendaId, "PIX", pix);

            exibirCupom(cliente.getNome(), totalVenda, pix, troco);

            limparTelaVenda();

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    // =========================
    // CANCELAR VENDA (ADICIONADO)
    // =========================
    @FXML
    public void cancelarVenda() {

        try {

            if (txtVendaId.getText().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Informe o ID da venda").show();
                return;
            }

            int vendaId = Integer.parseInt(txtVendaId.getText());

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

        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "ID inválido").show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    // =========================
    // AUXILIARES
    // =========================
 // =========================
 // GERAR PAYLOAD PIX CORRIGIDO
 // =========================
    private String gerarPayloadPix(String chave, String valor) {
        // 1. Configurações Iniciais
        String nome = "ELETROTECH"; // Sem espaços ou acentos
        String cidade = "ARAGUAINA"; 

        // 2. Montagem do Campo 26 (Merchant Account Information)
        // O campo 26 é: 00(GUI) + 01(Chave)
        String gui = "0014BR.GOV.BCB.PIX";
        String campoChave = "01" + String.format("%02d", chave.length()) + chave;
        String campo26Conteudo = gui + campoChave;
        String campo26 = "26" + String.format("%02d", campo26Conteudo.length()) + campo26Conteudo;

        // 3. Montagem do Campo 54 (Valor) - CUIDADO AQUI
        // O valor não pode ter espaços
        String campo54 = "54" + String.format("%02d", valor.length()) + valor;

        // 4. Montagem do Campo 62 (Additional Data Field)
        // Muitos bancos falham se o campo 62 não tiver o subcampo 05 (Reference Label)
        String subCampo62 = "0503***"; 
        String campo62 = "62" + String.format("%02d", subCampo62.length()) + subCampo62;

        // 5. Montagem Final (A ordem das tags importa!)
        StringBuilder payload = new StringBuilder();
        payload.append("000201");                                      // Payload Format Indicator
        payload.append(campo26);                                         // Merchant Account Information
        payload.append("52040000");                                     // Merchant Category Code
        payload.append("5303986");                                      // Transaction Currency (986 = Real)
        payload.append(campo54);                                         // Transaction Amount
        payload.append("5802BR");                                       // Country Code
        payload.append("59").append(String.format("%02d", nome.length())).append(nome);     // Nome
        payload.append("60").append(String.format("%02d", cidade.length())).append(cidade); // Cidade
        payload.append(campo62);                                         // Transaction ID
        payload.append("6304");                                         // CRC Placeholder

        String resultado = payload.toString();
        return resultado + calcularCRC16(resultado);
    }
    
    

    private String calcularCRC16(String payload) {

        int crc = 0xFFFF;

        for (int i = 0; i < payload.length(); i++) {
            crc ^= payload.charAt(i) << 8;

            for (int j = 0; j < 8; j++) {
                if ((crc & 0x8000) != 0) {
                    crc = (crc << 1) ^ 0x1021;
                } else {
                    crc <<= 1;
                }
            }
        }

        crc &= 0xFFFF;

        return String.format("%04X", crc);
    }

    private void exibirCupom(String cliente, double total, double pago, double troco) {

        String resumo = "🧾 CUPOM\n\nCliente: " + cliente +
                "\nTOTAL: R$ " + String.format("%.2f", total) +
                "\nPAGO: R$ " + String.format("%.2f", pago) +
                "\nTROCO: R$ " + String.format("%.2f", troco);

        new Alert(Alert.AlertType.INFORMATION, resumo).show();
    }

    private void limparTelaVenda() {
        carrinho.clear();
        txtCliente.clear();
        txtPix.clear();
        lblTroco.setText("Troco: R$ 0.00");
        imgPix.setImage(null);
        pixPago = false;
    }

    private double calcularTotal() {
        double total = 0;
        for (ItemCarrinho item : carrinho) {
            total += item.getPreco() * item.getQuantidade();
        }
        return total;
    }
}