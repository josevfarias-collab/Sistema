package application.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import application.Conexao;
import application.model.VendaItemModel;

public class VendaItemDAO {

    Connection conn;

    public VendaItemDAO() {
        conn = Conexao.getConnection();
    }

    public void salvar(VendaItemModel item) {

        String sql = "INSERT INTO venda_item (venda_id, produto_id, quantidade, preco) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, item.getVendaId());
            stmt.setInt(2, item.getProdutoId());
            stmt.setInt(3, item.getQuantidade());
            stmt.setDouble(4, item.getPreco());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}