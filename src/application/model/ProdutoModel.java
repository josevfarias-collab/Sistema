package application.model;

public class ProdutoModel {

    private int id;
    private String nome;
    private String codBarras;
    private String descricao;
    private String categoria;
    private double precoCusto;
    private double precoVenda;
    private int quantidade;
    private int estoqueMinimo;

    public ProdutoModel() {}

    public ProdutoModel(int id, String nome, String codBarras, String descricao,
                        String categoria, double precoCusto, double precoVenda,
                        int quantidade, int estoqueMinimo) {

        this.id = id;
        this.nome = nome;
        this.codBarras = codBarras;
        this.descricao = descricao;
        this.categoria = categoria;
        this.precoCusto = precoCusto;
        this.precoVenda = precoVenda;
        this.quantidade = quantidade;
        this.estoqueMinimo = estoqueMinimo;
    }

    // GETTERS E SETTERS

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCodBarras() { return codBarras; }
    public void setCodBarras(String codBarras) { this.codBarras = codBarras; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public double getPrecoCusto() { return precoCusto; }
    public void setPrecoCusto(double precoCusto) { this.precoCusto = precoCusto; }

    public double getPrecoVenda() { return precoVenda; }
    public void setPrecoVenda(double precoVenda) { this.precoVenda = precoVenda; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public int getEstoqueMinimo() { return estoqueMinimo; }
    public void setEstoqueMinimo(int estoqueMinimo) { this.estoqueMinimo = estoqueMinimo; }

    public void calcularPrecoVenda(double margem) {
        this.precoVenda = precoCusto + (precoCusto * margem / 100);
    }
}