package application.model;

public class MovimentacaoEstoqueModel {

    private String data;
    private String tipo;
    private int idProd;
    private String nome;
    private int id;
    private int quantidade;
    private String usuario;

    public MovimentacaoEstoqueModel(String data, String tipo, int idProd,
                                    String nome, int id, int quantidade, String usuario) {

        this.data = data;
        this.tipo = tipo;
        this.idProd = idProd;
        this.nome = nome;
        this.id = id;
        this.quantidade = quantidade;
        this.usuario = usuario;
    }

    public String getData() { return data; }
    public String getTipo() { return tipo; }
    public int getIdProd() { return idProd; }
    public String getNome() { return nome; }
    public int getId() { return id; }
    public int getQuantidade() { return quantidade; }
    public String getUsuario() { return usuario; }

    // 🔥 MÉTODOS NOVOS (IMPORTANTE PRA TABELA)
    public String getNomeProduto() { return nome; }
    public String getOperacao() { return tipo; }
}