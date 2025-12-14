package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Transacoes {

    // Atributos
    private int idTransacao;
    private TipoTransacao tipoTransacao;
    private double valor;
    private LocalDateTime dataTransacao;
    private int contaOrigemId;
    private Integer contaDestinoId; 
    private StatusTransacao status;
    private String descricaoTrancacao;
    private Conta conta;
    private String categoria;
    private LocalDateTime data;
    private boolean estornada;
    private Cliente cliente;
    private String referencia;  // novo
    private String entidade;

    public Object getDescricao() {
        return null;
    }

    //private LocalDate h;
    
    
    //public  int getIdCliente() {
   //     return conta.getCliente().getId();
   // }

   

    // Enum para tipos de transacoes
    public enum TipoTransacao {
        DEPOSITO, SAQUE, TRANSFERENCIA
    }

    // Enum para status 
    public enum StatusTransacao {
        CONCLUIDA, PENDENTE, CANCELADA
    }

    // Construtor
    /*public Transacoes(int idTransacao, TipoTransacao tipoTransacao, double valor,
                      LocalDateTime dataTransacao, int contaOrigemId, Integer contaDestinoId,
                      StatusTransacao status) {
        this.idTransacao = idTransacao;
        this.tipoTransacao = tipoTransacao;
        this.valor = valor;
        this.dataTransacao = dataTransacao;
        this.contaOrigemId = contaOrigemId;
        this.contaDestinoId = contaDestinoId;
        this.status = status;
    }*/

    
    

    // Getters e Setters

    
   

    public boolean isEstornada() {
        return estornada;
    }

    public Transacoes(int idTransacao, TipoTransacao tipoTransacao, double valor, LocalDateTime dataTransacao,
            int contaOrigemId, Integer contaDestinoId, StatusTransacao status, String descricaoTrancacao, Conta conta,
            String categoria, LocalDateTime data, boolean estornada) {
        this.idTransacao = idTransacao;
        this.tipoTransacao = tipoTransacao;
        this.valor = valor;
        this.dataTransacao = dataTransacao;
        this.contaOrigemId = contaOrigemId;
        this.contaDestinoId = contaDestinoId;
        this.status = status;
        this.descricaoTrancacao = descricaoTrancacao;
        this.conta = conta;
        this.categoria = categoria;
        this.data = data;
        this.estornada = estornada;
    }

    public void setEstornada(boolean estornada) {
        this.estornada = estornada;
    }

    public int getId() {
        return idTransacao;
    }


    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }


    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }


    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public int getIdTransacao() {
        return idTransacao;
    }

    public void setIdTransacao(int idTransacao) {
        this.idTransacao = idTransacao;
    }

    public TipoTransacao getTipoTransacao() {
        return tipoTransacao;
    }

    public void setTipoTransacao(TipoTransacao tipoTransacao) {
        this.tipoTransacao = tipoTransacao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public LocalDateTime getDataTransacao() {
        return dataTransacao;
    }

    public void setDataTransacao(LocalDateTime dataTransacao) {
        this.dataTransacao = dataTransacao;
    }

    public int getContaOrigemId() {
        return contaOrigemId;
    }

    public void setContaOrigemId(int contaOrigemId) {
        this.contaOrigemId = contaOrigemId;
    }

    public Integer getContaDestinoId() {
        return contaDestinoId;
    }

    public void setContaDestinoId(Integer contaDestinoId) {
        this.contaDestinoId = contaDestinoId;
    }

    public StatusTransacao getStatus() {
        return status;
    }

    public void setStatus(StatusTransacao status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Transacoes{" +
                "idTransacao=" + idTransacao +
                ", tipoTransacao=" + tipoTransacao +
                ", valor=" + valor +
                ", dataTransacao=" + dataTransacao +
                ", contaOrigemId=" + contaOrigemId +
                ", contaDestinoId=" + contaDestinoId +
                ", status=" + status +
                '}';
    }
public int getIdCliente() {
    if (this.conta != null && this.conta.getClienteId() != null) {
        return this.conta.getClienteId().getIdCliente(); // usa getIdCliente() do Cliente
    }
    return -1;
}

public String getDescricaoTrancacao() {
    return descricaoTrancacao;
}

public void setDescricaoTrancacao(String descricaoTrancacao) {
    this.descricaoTrancacao = descricaoTrancacao;
}


public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getEntidade() {
        return entidade;
    }

    public void setEntidade(String entidade) {
        this.entidade = entidade;
    }


    public Transacoes(int idTransacao, int idConta, String categoria, double valor,
                  LocalDate data, String referencia, String entidade) {
    this.idTransacao = idTransacao;
    this.contaOrigemId = idConta;
    this.tipoTransacao = TipoTransacao.DEPOSITO; // padrão, pode ajustar depois
    this.valor = valor;
    this.dataTransacao = data.atStartOfDay(); // converte LocalDate para LocalDateTime
    this.status = StatusTransacao.CONCLUIDA;
    this.descricaoTrancacao = categoria;
    this.categoria = categoria;
    this.referencia = referencia;
    this.entidade = entidade;
    this.estornada = false;
}

    public Cliente getCliente() {
        return cliente;
    }


   
}



  

