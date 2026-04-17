package application.dao;

import java.sql.*;
import java.util.*;

import application.Conexao;
import application.model.ProdutoModel;

public class ProdutoDao {

    // 🔎 LISTAR PRODUTOS
    public List<ProdutoModel> listarProduto(String valor) {

        List<ProdutoModel> lista = new ArrayList<>();

        String sql = (valor == null || valor.isEmpty())
                ? "SELECT * FROM produto"
                : "SELECT * FROM produto WHERE nome LIKE ? OR codigo_barras LIKE ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (valor != null && !valor.isEmpty()) {
                String busca = "%" + valor + "%";
                ps.setString(1, busca);
                ps.setString(2, busca);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(new ProdutoModel(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("codigo_barras"),
                        rs.getString("descricao"),
                        rs.getString("categoria"),
                        rs.getDouble("preco_custo"),
                        rs.getDouble("preco_venda"),
                        rs.getInt("quantidade"),
                        rs.getInt("estoque_minimo")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    // 🔎 VERIFICAR CÓDIGO DE BARRAS
    public boolean codigoExiste(String codigo) {

        String sql = "SELECT * FROM produto WHERE codigo_barras = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, codigo);
            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // 💾 SALVAR
    public void salvar(ProdutoModel p) {

        try (Connection conn = Conexao.getConnection()) {

            if (p.getId() > 0) {

                String sql = "UPDATE produto SET nome=?, codigo_barras=?, descricao=?, categoria=?, preco_custo=?, preco_venda=?, quantidade=? WHERE id=?";
                PreparedStatement ps = conn.prepareStatement(sql);

                ps.setString(1, p.getNome());
                ps.setString(2, p.getCodBarras());
                ps.setString(3, p.getDescricao());
                ps.setString(4, p.getCategoria());
                ps.setDouble(5, p.getPrecoCusto());
                ps.setDouble(6, p.getPrecoVenda());
                ps.setInt(7, p.getQuantidade());
                ps.setInt(8, p.getId());

                ps.executeUpdate();

            } else {

                if (codigoExiste(p.getCodBarras())) {
                    throw new RuntimeException("Código de barras já existe!");
                }

                String sql = "INSERT INTO produto (nome, codigo_barras, descricao, categoria, preco_custo, preco_venda, quantidade) VALUES (?, ?, ?, ?, ?, ?, 0)";
                PreparedStatement ps = conn.prepareStatement(sql);

                ps.setString(1, p.getNome());
                ps.setString(2, p.getCodBarras());
                ps.setString(3, p.getDescricao());
                ps.setString(4, p.getCategoria());
                ps.setDouble(5, p.getPrecoCusto());
                ps.setDouble(6, p.getPrecoVenda());

                ps.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar produto");
        }
    }

    // ❌ EXCLUIR
    public void excluir(int id) {

        try (Connection conn = Conexao.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM produto WHERE id=?")) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao excluir produto");
        }
    }

    // 📦 ✅ PROCESSAR ESTOQUE (ENTRADA + SAÍDA + LOG)
    public void processarEstoque(int produtoId, int quantidade, String tipo, int usuarioId) {

        try (Connection conn = Conexao.getConnection()) {

            conn.setAutoCommit(false); // 🔥 TRANSAÇÃO

            String sqlBusca = "SELECT quantidade FROM produto WHERE id=?";
            PreparedStatement psBusca = conn.prepareStatement(sqlBusca);
            psBusca.setInt(1, produtoId);

            ResultSet rs = psBusca.executeQuery();

            if (!rs.next()) {
                throw new RuntimeException("Produto não encontrado!");
            }

            int estoqueAtual = rs.getInt("quantidade");
            int novoEstoque;

            if (tipo.equalsIgnoreCase("SAIDA")) {

                if (estoqueAtual < quantidade) {
                    throw new RuntimeException("Estoque insuficiente!");
                }

                novoEstoque = estoqueAtual - quantidade;

            } else {
                novoEstoque = estoqueAtual + quantidade;
            }

            // 🔄 UPDATE
            String sqlUpdate = "UPDATE produto SET quantidade=? WHERE id=?";
            PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate);

            psUpdate.setInt(1, novoEstoque);
            psUpdate.setInt(2, produtoId);
            psUpdate.executeUpdate();

            // 📝 LOG
            String sqlMov = "INSERT INTO movimentacao_estoque (produto_id, usuario_id, quantidade, operacao) VALUES (?, ?, ?, ?)";
            PreparedStatement psMov = conn.prepareStatement(sqlMov);

            psMov.setInt(1, produtoId);
            psMov.setInt(2, usuarioId);
            psMov.setInt(3, quantidade);
            psMov.setString(4, tipo.toUpperCase());
            
            psMov.executeUpdate();

            conn.commit(); // 🔥 CONFIRMA

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao processar estoque: " + e.getMessage());
        }
    }
}