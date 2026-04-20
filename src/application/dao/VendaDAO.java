package application.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import application.Conexao;
import application.model.VendaModel;
import application.model.ItemCarrinho;
import application.dao.ProdutoDao;

public class VendaDAO {

    Connection conn;

    public VendaDAO() {
        conn = Conexao.getConnection();
    }

    // 💾 SALVAR VENDA (MANTIDO)
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

    // 🔥 SALVAR E RETORNAR ID
    public int salvarRetornandoId(VendaModel venda) {

        String sql = "INSERT INTO venda (cliente_id, total) VALUES (?, ?)";

        try {
            PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

            stmt.setInt(1, venda.getClienteId());
            stmt.setDouble(2, venda.getTotal());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    // 🔥 SALVAR ITENS
    public void salvarItens(int vendaId, List<ItemCarrinho> carrinho) {

        String sql = "INSERT INTO item_venda (venda_id, produto_id, quantidade, preco) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);

            for (ItemCarrinho item : carrinho) {

                stmt.setInt(1, vendaId);
                stmt.setInt(2, item.getProdutoId());
                stmt.setInt(3, item.getQuantidade());
                stmt.setDouble(4, item.getPreco());

                stmt.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔥 BUSCAR ITENS DA VENDA
    public List<ItemCarrinho> buscarItens(int vendaId) {

        List<ItemCarrinho> lista = new ArrayList<>();

        String sql = "SELECT produto_id, quantidade, preco FROM item_venda WHERE venda_id=?";

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, vendaId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                lista.add(new ItemCarrinho(
                        rs.getInt("produto_id"),
                        "",
                        rs.getInt("quantidade"),
                        rs.getDouble("preco")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
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

    // 🔥 CANCELAR VENDA COMPLETO
    public void cancelarVenda(int vendaId, String motivo) {

        try (Connection conn = Conexao.getConnection()) {

            conn.setAutoCommit(false);

            // 1️⃣ BUSCAR ITENS
            List<ItemCarrinho> itens = buscarItens(vendaId);

            ProdutoDao produtoDao = new ProdutoDao();

            // 2️⃣ DEVOLVER ESTOQUE
            for (ItemCarrinho item : itens) {

                produtoDao.processarEstoque(
                        item.getProdutoId(),
                        item.getQuantidade(),
                        "ENTRADA",
                        1
                );
            }

            // 3️⃣ CANCELAR VENDA
            String sql = "UPDATE venda SET status='CANCELADA', motivo_cancelamento=? WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, motivo);
            ps.setInt(2, vendaId);
            ps.executeUpdate();

            conn.commit();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao cancelar venda");
        }
    }

    public void salvarPagamento(int vendaId, String tipo, double valor) {

        if (valor <= 0) return;

        String sql = "INSERT INTO pagamento (venda_id, tipo, valor) VALUES (?, ?, ?)";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, vendaId);
            stmt.setString(2, tipo);
            stmt.setDouble(3, valor);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔥 MÉTODO ADICIONADO (CORRETO AGORA)
    public String buscarStatus(int vendaId) {

        String sql = "SELECT status FROM venda WHERE id=?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, vendaId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("status");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}