package application.view;

import java.net.URL;
import java.util.ResourceBundle;

import application.dao.DashboardDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class DashboardController implements Initializable {

    @FXML private Label lblTotalProdutos;
    @FXML private Label lblBaixoEstoque;
    @FXML private Label lblEntradas;
    @FXML private Label lblSaidas;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        carregarDados();
    }

    public void carregarDados() {

        DashboardDAO dao = new DashboardDAO();

        try {
            lblTotalProdutos.setText(String.valueOf(dao.totalProdutos()));
            lblBaixoEstoque.setText(String.valueOf(dao.produtosBaixoEstoque()));
            lblEntradas.setText(String.valueOf(dao.entradasHoje()));
            lblSaidas.setText(String.valueOf(dao.saidasHoje()));

        } catch (Exception e) {
            e.printStackTrace();

            lblTotalProdutos.setText("0");
            lblBaixoEstoque.setText("0");
            lblEntradas.setText("0");
            lblSaidas.setText("0");
        }
    }
}