package application.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import application.Conexao;
import application.model.UsuarioModel;

public class UsuarioDAO {

    // 🔐 LOGIN
    public UsuarioModel validarLogin(String login, String senha) {

        UsuarioModel usuario = null;

        String sql = "SELECT * FROM usuario WHERE login = ? AND senha = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            stmt.setString(2, senha);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                usuario = new UsuarioModel();

                usuario.setId(rs.getInt("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setLogin(rs.getString("login"));
                usuario.setSenha(rs.getString("senha"));
                usuario.setTipo(rs.getString("tipo"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return usuario;
    }

    // 💾 SALVAR USUÁRIO
    public void salvar(UsuarioModel usuario) {

        String sql = "INSERT INTO usuario (nome, login, senha, tipo) VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getLogin());
            stmt.setString(3, usuario.getSenha());
            stmt.setString(4, usuario.getTipo());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔍 VERIFICAR LOGIN DUPLICADO
    public boolean existeLogin(String login) {

        String sql = "SELECT * FROM usuario WHERE login = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);

            ResultSet rs = stmt.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // 🔒 VALIDAR GERENTE (ADICIONADO)
    public boolean validarGerente(String senha) {

        String sql = "SELECT * FROM usuario WHERE senha = ? AND tipo = 'GERENTE'";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, senha);

            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}