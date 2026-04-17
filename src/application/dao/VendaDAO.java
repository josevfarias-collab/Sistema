package application.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import application.Conexao;
import application.model.VendaModel;

public class VendaDAO {

    Connection conn;

    public VendaDAO() {
        conn = Conexao.getConnection();
    }

    // 💾 SALVAR VENDA
    public void salvar(VendaModel venda) {

        String sql = "INSERT INTO venda (cliente_id, total) VALUES (?, ?)";

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, venda.getClienteId());
            stmt.setDouble(2, venda.getTotal());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 📋 BUSCAR ÚLTIMAS 5 VENDAS
    public List<VendaModel> buscarPorCliente(int clienteId) {

        List<VendaModel> lista = new ArrayList<>();

        String sql = "SELECT id, cliente_id, IFNULL(data, NOW()) as data, total FROM venda WHERE cliente_id = ? ORDER BY data DESC LIMIT 5";
        try {

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, clienteId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                VendaModel venda = new VendaModel();

                venda.setId(rs.getInt("id"));
                venda.setClienteId(rs.getInt("cliente_id"));
                java.sql.Timestamp timestamp = rs.getTimestamp("data");

                if (timestamp != null) {
                    venda.setData(timestamp.toLocalDateTime());
                }
                venda.setTotal(rs.getDouble("total"));

                lista.add(venda);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
}