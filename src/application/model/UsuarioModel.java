package application.model;

import java.time.LocalDateTime;

public class UsuarioModel {

    private int id;
    private String nome;
    private String login;
    private String senha;
    private String tipo;

    // 🔥 NOVOS CAMPOS (OPCIONAL)
    private String email;
    private String telefone;
    private String status; // ATIVO / INATIVO
    private LocalDateTime dataCadastro;

    public UsuarioModel() {}

    public UsuarioModel(int id, String nome, String login, String senha, String tipo) {
        this.id = id;
        this.nome = nome;
        this.login = login;
        this.senha = senha;
        this.tipo = tipo;
    }

    // GETTERS
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getLogin() { return login; }
    public String getSenha() { return senha; }
    public String getTipo() { return tipo; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public String getStatus() { return status; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }

    // SETTERS
    public void setId(int id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setLogin(String login) { this.login = login; }
    public void setSenha(String senha) { this.senha = senha; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setEmail(String email) { this.email = email; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public void setStatus(String status) { this.status = status; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }

    @Override
    public String toString() {
        return nome + " (" + tipo + ")";
    }
}