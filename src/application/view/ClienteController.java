package application.view;

import application.dao.ClienteDAO;
import application.dao.VendaDAO;
import application.model.ClienteModel;
import application.model.VendaModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ClienteController {

    @FXML private TextField txtNome;
    @FXML private TextField txtDocumento;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefone;
    @FXML private ComboBox<String> cbStatus;
    @FXML private TextField txtBuscar;

    @FXML private TableView<ClienteModel> tableClientes;
    @FXML private TableColumn<ClienteModel, Integer> colId;
    @FXML private TableColumn<ClienteModel, String> colNome;
    @FXML private TableColumn<ClienteModel, String> colCpf;
    @FXML private TableColumn<ClienteModel, String> colEmail;
    @FXML private TableColumn<ClienteModel, String> colTelefone;
    @FXML private TableColumn<ClienteModel, String> colStatus;

    @FXML private TableView<VendaModel> tableHistorico;
    @FXML private TableColumn<VendaModel, Integer> colVendaId;
    @FXML private TableColumn<VendaModel, String> colData;
    @FXML private TableColumn<VendaModel, Double> colTotal;

    private ObservableList<ClienteModel> lista;
    private ClienteModel clienteSelecionado;

    @FXML
    public void initialize() {

        cbStatus.getItems().addAll("ATIVO", "INATIVO");
        cbStatus.getSelectionModel().selectFirst();

        colId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("nome"));
        colCpf.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("documento"));
        colEmail.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("email"));
        colTelefone.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("telefone"));
        colStatus.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("status"));

        colVendaId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        colData.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getData().toString()
                )
        );
        colTotal.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("total"));

        carregarClientes();

        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> buscar());

        tableClientes.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, cliente) -> {

            if (cliente != null) {

                txtNome.setText(cliente.getNome());
                txtDocumento.setText(cliente.getDocumento());
                txtEmail.setText(cliente.getEmail());
                txtTelefone.setText(cliente.getTelefone());
                cbStatus.setValue(cliente.getStatus());

                clienteSelecionado = cliente;

                VendaDAO vendaDAO = new VendaDAO();

                tableHistorico.setItems(
                        FXCollections.observableArrayList(
                                vendaDAO.buscarPorCliente(cliente.getId())
                        )
                );
            }
        });
    }

    public void carregarClientes() {

        ClienteDAO dao = new ClienteDAO();
        lista = FXCollections.observableArrayList(dao.listar());
        tableClientes.setItems(lista);
    }

    private boolean documentoValido(String doc) {

        doc = doc.replaceAll("[^0-9]", "");

        return doc.length() == 11 || doc.length() == 14;
    }

    @FXML
    public void salvar() {

        if (txtNome.getText().isEmpty() ||
            txtDocumento.getText().isEmpty() ||
            txtEmail.getText().isEmpty()) {

            new Alert(Alert.AlertType.WARNING, "Preencha todos os campos!").show();
            return;
        }

        String documento = txtDocumento.getText();

        if (!documentoValido(documento)) {
            new Alert(Alert.AlertType.ERROR, "CPF ou CNPJ inválido!").show();
            return;
        }

        ClienteDAO dao = new ClienteDAO();
        ClienteModel cliente;

        if (clienteSelecionado != null) {
            cliente = clienteSelecionado;
        } else {

            if (dao.documentoExiste(documento)) {
                new Alert(Alert.AlertType.ERROR, "Documento já cadastrado!").show();
                return;
            }

            cliente = new ClienteModel();
        }

        cliente.setNome(txtNome.getText());
        cliente.setDocumento(documento);
        cliente.setEmail(txtEmail.getText());
        cliente.setTelefone(txtTelefone.getText());
        cliente.setStatus(cbStatus.getValue());

        dao.salvar(cliente);

        new Alert(Alert.AlertType.INFORMATION, "Cliente salvo!").show();

        carregarClientes();
        limparCampos();
    }

    @FXML
    public void buscar() {

        ClienteDAO dao = new ClienteDAO();

        lista = FXCollections.observableArrayList(
                dao.buscar(txtBuscar.getText())
        );

        tableClientes.setItems(lista);
    }

    @FXML
    public void excluir() {

        if (clienteSelecionado == null) {
            new Alert(Alert.AlertType.WARNING, "Selecione um cliente!").show();
            return;
        }

        ClienteDAO dao = new ClienteDAO();
        dao.excluir(clienteSelecionado.getId());

        new Alert(Alert.AlertType.INFORMATION, "Cliente excluído!").show();

        carregarClientes();
        limparCampos();
    }

    private void limparCampos() {

        clienteSelecionado = null;

        txtNome.clear();
        txtDocumento.clear();
        txtEmail.clear();
        txtTelefone.clear();
        cbStatus.getSelectionModel().selectFirst();

        tableHistorico.getItems().clear();
    }
}