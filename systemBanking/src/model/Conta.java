package model;

import java.time.LocalDate;

public class Conta {
    private int idConta;
    private int numeroConta;
    private TipoConta tipoConta;
    private double saldo;
    private LocalDate dataAbertura;
    private StatusConta status;
    private Cliente clienteId;
    private int niubConta;
    private int nib;
    private Cliente cliente;  // atributo da conta
    private double totalDepositos;
    private double totalSaques;
    


    public enum TipoConta {
        POUPANCA, CORRENTE, DEBITO
    }

    public enum StatusConta {
        ATIVA, INATIVA, BLOQUEADA
    }

  /*   public Conta(int idConta, int numeroConta, TipoConta tipoConta,
                 Cliente clienteId, int niubConta, int nib) {
        this.idConta = idConta;
        this.numeroConta = numeroConta;
        this.tipoConta = tipoConta;
        this.saldo = 0.0;
        this.dataAbertura = LocalDate.now();
        this.status = StatusConta.INATIVA;   // ela vem inativa aqui
        this.clienteId = clienteId;
        this.niubConta = niubConta;
        this.nib = nib;
    } */
  public Conta(int idConta, int numeroConta, TipoConta tipoConta,
             Cliente cliente, int niubConta, int nib) {
    this.idConta = idConta;
    this.numeroConta = numeroConta;
    this.tipoConta = tipoConta;
    this.saldo = 0.0;
    this.dataAbertura = LocalDate.now();
    this.status = StatusConta.INATIVA;
    this.cliente = cliente;   // só um atributo
    this.niubConta = niubConta;
    this.nib = nib;
}

public double getTotalDepositos() {
    return totalDepositos;
}

public double getTotalSaques() {
    return totalSaques;
}


    // operacoes que a conta faz 
    // por exemplo a conta so fuca ativa ao ser depositado um valor maior ou igual a 500
    // a conta ja vem inativa

   /*  public void depositar(double valor) {
        this.saldo += valor;
        if (this.status == StatusConta.INATIVA && this.saldo >= 500) {
            this.status = StatusConta.ATIVA; // ativa ao atingir 500
        }
    }


    //logica para fazer um sque/levantamento
    public boolean sacar(double valor) {
        if (valor > 0 && valor <= this.saldo) {
            this.saldo -= valor;
            return true;
        }
        return false; // saldo insuficiente
    }*/

        // Depósito
        public void depositar(double valor) {
            if (valor > 0) {
                this.saldo += valor;
                this.totalDepositos += valor; // atualiza o total de depósitos

                if (this.status == StatusConta.INATIVA && this.saldo >= 500) {
                    this.status = StatusConta.ATIVA; // ativa ao atingir 500
                }
            }
        }

        // Saque
        public boolean sacar(double valor) {
            if (valor > 0 && valor <= this.saldo) {
                this.saldo -= valor;
                this.totalSaques += valor; // atualiza o total de saques
                return true;
            }
            return false; // saldo insuficiente
        }


    // getters e setter da classe "Conta"
    
        private int id;
    private boolean ativa;

   // public int getIdCliente() {
     // return cliente.getIdCliente();
    //}
    public int getIdCliente() {
    if (cliente == null) {
        System.out.println("ERRO: cliente é null na conta!");
        return 0;
    }
    return cliente.getIdCliente();
}
    


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }

    public int getIdConta() { return idConta; }
    public int getNumeroConta() { return numeroConta; }
    public TipoConta getTipoConta() { return tipoConta; }
    public double getSaldo() { return saldo; }
    public LocalDate getDataAbertura() { return dataAbertura; }
    public StatusConta getStatus() { return status; }
    public Cliente getClienteId() { return clienteId; }
    public int getNiubConta() { return niubConta; }
    public int getNib() { return nib; }

    public void setStatus(StatusConta status) { this.status = status; }
    public void setIdConta(int idConta) { this.idConta = idConta; }
    public void setNumeroConta(int numeroConta) { this.numeroConta = numeroConta; }
    public void setTipoConta(TipoConta tipoConta) { this.tipoConta = tipoConta; }
    public void setSaldo(double saldo) { this.saldo = saldo; }
    public void setDataAbertura(LocalDate dataAbertura) { this.dataAbertura = dataAbertura; }
    public void setClienteId(Cliente clienteId) { this.clienteId = clienteId; }
    public void setNiubConta(int niubConta) { this.niubConta = niubConta; }
    public void setNib(int nib) { this.nib = nib; }

    

  

   
}
