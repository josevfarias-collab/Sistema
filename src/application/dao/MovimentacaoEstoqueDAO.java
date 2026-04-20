package application.dao;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

import application.Conexao;
import application.model.MovimentacaoEstoqueModel;

public class MovimentacaoEstoqueDAO {

    public List<MovimentacaoEstoqueModel> listarHistorico(int idProduto,
                                                          LocalDate dataInicio,
                                                          LocalDate dataFim) {

        List<MovimentacaoEstoqueModel> lista = new ArrayList<>();

        String sql = "SELECT " +
                "DATE_FORMAT(m.data_hora, '%d/%m/%Y %H:%i') as data, " +
                "m.operacao, " +
                "p.id as idProd, " +
                "p.nome, " +
                "m.id, " +
                "m.quantidade, " +
                "u.nome as usuario " + // 🔥 AQUI
                "FROM movimentacao_estoque m " +
                "JOIN produto p ON p.id = m.produto_id " +
                "LEFT JOIN usuario u ON u.id = m.usuario_id " + 
                "WHERE (? = 0 OR p.id = ?) " +
                "AND DATE(m.data_hora) BETWEEN ? AND ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idProduto);
            ps.setInt(2, idProduto);
            ps.setDate(3, Date.valueOf(dataInicio));
            ps.setDate(4, Date.valueOf(dataFim));

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                lista.add(new MovimentacaoEstoqueModel(
                        rs.getString("data"),
                        rs.getString("operacao"),
                        rs.getInt("idProd"),
                        rs.getString("nome"),
                        rs.getInt("id"),
                        rs.getInt("quantidade"),
                        rs.getString("usuario") 
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
}