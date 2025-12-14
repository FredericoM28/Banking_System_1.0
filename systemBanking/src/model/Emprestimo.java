package model;

import java.time.LocalDate;

public class Emprestimo {
    private int idEmprestimo;            // identificador único do empréstimo
    private Cliente cliente;             // cliente que solicitou
    private double valorSolicitado;      // valor total do empréstimo
    private double valorAPagar;          // valor final com juros
    private double taxaJuro;             // taxa de juros aplicada
    private int prazoMeses;              // tempo para pagar em meses
    private LocalDate dataSolicitacao;   // quando o empréstimo foi pedido
    private LocalDate dataAprovacao;     // quando foi aprovado
    private emprestimos status;          // status do empréstimo (enum)
    
    public enum emprestimos {
        CONCLUIDA,   // já pago
        PENDENTE,    // solicitado mas ainda não pago
        CONFIRMADA   // aprovado e em curso de pagamento
    }

    // Construtor
    public Emprestimo(int idEmprestimo, Cliente cliente, double valorSolicitado, double taxaJuro, int prazoMeses) {
        this.idEmprestimo = idEmprestimo;
        this.cliente = cliente;
        this.valorSolicitado = valorSolicitado;
        this.taxaJuro = taxaJuro;
        this.prazoMeses = prazoMeses;
        this.dataSolicitacao = LocalDate.now();
        this.status = emprestimos.PENDENTE;
        calcularValorAPagar();
    }

    // Método para calcular o valor final com juros
    private void calcularValorAPagar() {
        this.valorAPagar = valorSolicitado + (valorSolicitado * taxaJuro * prazoMeses / 12);
    }

    // Getters e Setters

    private int id;

public int getId() { return id; }

    public int getIdEmprestimo() {
        return idEmprestimo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public double getValorSolicitado() {
        return valorSolicitado;
    }

    public double getValorAPagar() {
        return valorAPagar;
    }

    public double getTaxaJuro() {
        return taxaJuro;
    }

    public int getPrazoMeses() {
        return prazoMeses;
    }

    public LocalDate getDataSolicitacao() {
        return dataSolicitacao;
    }

    public LocalDate getDataAprovacao() {
        return dataAprovacao;
    }

    public emprestimos getStatus() {
        return status;
    }

    public void setStatus(emprestimos status) {
        this.status = status;
        if(status == emprestimos.CONFIRMADA) {
            this.dataAprovacao = LocalDate.now();
        }
    }

    public void setDataAprovacao(LocalDate dataAprovacao) {
        this.dataAprovacao = dataAprovacao;
    }
    


}
