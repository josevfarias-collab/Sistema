package application.view;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import application.dao.MovimentacaoEstoqueDAO;
import application.model.MovimentacaoEstoqueModel;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class HistoricoController implements Initializable {

    @FXML private DatePicker dataInicio;
    @FXML private DatePicker dataFinal;

    @FXML private TableView<MovimentacaoEstoqueModel> tableHistorico;

    @FXML private TableColumn<MovimentacaoEstoqueModel, Integer> colId;
    @FXML private TableColumn<MovimentacaoEstoqueModel, Integer> colIdProd;
    @FXML private TableColumn<MovimentacaoEstoqueModel, String> colNome;
    @FXML private TableColumn<MovimentacaoEstoqueModel, String> colTipo;
    @FXML private TableColumn<MovimentacaoEstoqueModel, Integer> colQuantidade;
    @FXML private TableColumn<MovimentacaoEstoqueModel, String> colData;
    @FXML private TableColumn<MovimentacaoEstoqueModel, String> colUsuario; 

    private ObservableList<MovimentacaoEstoqueModel> lista = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colIdProd.setCellValueFactory(new PropertyValueFactory<>("idProd"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));

        tableHistorico.setItems(lista);

        dataInicio.setValue(LocalDate.now().withDayOfMonth(1));
        dataFinal.setValue(LocalDate.now());

        buscar();
    }

    @FXML
    public void buscar() {

        LocalDate inicio = dataInicio.getValue();
        LocalDate fim = dataFinal.getValue();

        if (inicio == null || fim == null) {
            new Alert(Alert.AlertType.WARNING, "Selecione as datas").show();
            return;
        }

        MovimentacaoEstoqueDAO dao = new MovimentacaoEstoqueDAO();

        List<MovimentacaoEstoqueModel> dados =
                dao.listarHistorico(0, inicio, fim);

        lista.setAll(dados);
    }
}