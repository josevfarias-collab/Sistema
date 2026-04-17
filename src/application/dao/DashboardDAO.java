package application.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import application.Conexao;

public class DashboardDAO {

    public int totalProdutos() {

        String sql = "SELECT COUNT(*) FROM produto";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int produtosBaixoEstoque() {

        String sql = "SELECT COUNT(*) FROM produto WHERE quantidade <= estoque_minimo";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int entradasHoje() {

        String sql = "SELECT COUNT(*) FROM movimentacao_estoque " +
                     "WHERE operacao='ENTRADA' AND DATE(data_hora)=CURDATE()";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int saidasHoje() {

        String sql = "SELECT COUNT(*) FROM movimentacao_estoque " +
                     "WHERE operacao='SAIDA' AND DATE(data_hora)=CURDATE()";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}