package application.model;

public class ItemCarrinho {

    private int produtoId;
    private String nome;
    private int quantidade;
    private double preco;

    public ItemCarrinho(int produtoId, String nome, int quantidade, double preco) {
        this.produtoId = produtoId;
        this.nome = nome;
        this.quantidade = quantidade;
        this.preco = preco;
    }

    public int getProdutoId() { return produtoId; }
    public String getNome() { return nome; }
    public int getQuantidade() { return quantidade; }
    public double getPreco() { return preco; }

    // 🔥 ADICIONADO (IMPORTANTE)
    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public double getTotal() {
        return preco * quantidade;
    }
}