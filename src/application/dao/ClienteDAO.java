package application.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import application.Conexao;
import application.model.ClienteModel;

public class ClienteDAO {

    Connection conn;

    public ClienteDAO() {
        conn = Conexao.getConnection();
    }

    // 💾 SALVAR (INSERT ou UPDATE)
    public void salvar(ClienteModel cliente) {

        try {

            if (cliente.getId() > 0) {

                String sql = "UPDATE cliente SET nome=?, documento=?, email=?, telefone=?, status=? WHERE id=?";

                PreparedStatement stmt = conn.prepareStatement(sql);

                stmt.setString(1, cliente.getNome());
                stmt.setString(2, cliente.getDocumento());
                stmt.setString(3, cliente.getEmail());
                stmt.setString(4, cliente.getTelefone());
                stmt.setString(5, cliente.getStatus());
                stmt.setInt(6, cliente.getId());

                stmt.executeUpdate();

            } else {

                String sql = "INSERT INTO cliente (nome, documento, email, telefone, status) VALUES (?, ?, ?, ?, ?)";

                PreparedStatement stmt = conn.prepareStatement(sql);

                stmt.setString(1, cliente.getNome());
                stmt.setString(2, cliente.getDocumento());
                stmt.setString(3, cliente.getEmail());
                stmt.setString(4, cliente.getTelefone());
                stmt.setString(5, cliente.getStatus());

                stmt.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 📋 LISTAR TODOS
    public List<ClienteModel> listar() {

        List<ClienteModel> lista = new ArrayList<>();

        String sql = "SELECT * FROM cliente";

        try {

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                ClienteModel cliente = new ClienteModel();

                cliente.setId(rs.getInt("id"));
                cliente.setNome(rs.getString("nome"));
                cliente.setDocumento(rs.getString("documento"));
                cliente.setEmail(rs.getString("email"));
                cliente.setTelefone(rs.getString("telefone"));
                cliente.setStatus(rs.getString("status"));

                lista.add(cliente);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    // 🔍 BUSCAR (NOME, DOCUMENTO OU EMAIL)
    public List<ClienteModel> buscar(String valor) {

        List<ClienteModel> lista = new ArrayList<>();

        String sql = "SELECT * FROM cliente WHERE nome LIKE ? OR documento LIKE ? OR email LIKE ?";

        try {

            PreparedStatement stmt = conn.prepareStatement(sql);

            String busca = "%" + valor + "%";

            stmt.setString(1, busca);
            stmt.setString(2, busca);
            stmt.setString(3, busca);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                ClienteModel cliente = new ClienteModel();

                cliente.setId(rs.getInt("id"));
                cliente.setNome(rs.getString("nome"));
                cliente.setDocumento(rs.getString("documento"));
                cliente.setEmail(rs.getString("email"));
                cliente.setTelefone(rs.getString("telefone"));
                cliente.setStatus(rs.getString("status"));

                lista.add(cliente);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    // 🔥 NOVO — BUSCAR POR NOME (USADO NA VENDA)
    public ClienteModel buscarPorNome(String nome) {

        String sql = "SELECT * FROM cliente WHERE nome LIKE ? LIMIT 1";

        try {

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + nome + "%");

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                ClienteModel cliente = new ClienteModel();

                cliente.setId(rs.getInt("id"));
                cliente.setNome(rs.getString("nome"));
                cliente.setDocumento(rs.getString("documento"));
                cliente.setEmail(rs.getString("email"));
                cliente.setTelefone(rs.getString("telefone"));
                cliente.setStatus(rs.getString("status"));

                return cliente;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // ❗ VERIFICAR DOCUMENTO DUPLICADO
    public boolean documentoExiste(String doc) {

        String sql = "SELECT * FROM cliente WHERE documento = ?";

        try {

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, doc);

            ResultSet rs = stmt.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // ❌ EXCLUIR
    public void excluir(int id) {

        String sql = "DELETE FROM cliente WHERE id=?";

        try {

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}