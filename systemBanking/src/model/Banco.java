package model;

import java.util.ArrayList;
import java.util.List;

public class Banco {
    private String nome = "Nexus BANK";
    private double capital;
    private double lucro;
    private List<Conta> contas;
    private double lucroTaxas; // lucro apenas de transações

    public Banco(String nome, double capitalInicial) {
        this.nome = nome;
        this.capital = capitalInicial;
        this.lucro = 0;
        this.lucroTaxas = 0; //  INICIALIZAR
        this.contas = new ArrayList<>();
    }

    public void registrarLucroTaxa(double valor) {
        this.lucroTaxas += valor;
        registrarLucro(valor); // aumenta capital e lucro total
    }

    public void registrarLucro(double valor) {
        this.lucro += valor;
        this.capital += valor;
    }

    public void adicionarConta(Conta conta) {
        contas.add(conta);
    }

    //  GETTERS COMPLETOS
    public String getNome() {
        return nome;
    }

    public double getCapital() {
        return capital;
    }

    public double getLucro() {
        return lucro;
    }

    public double getLucroTaxas() {
        return lucroTaxas;
    }

    public List<Conta> getContas() {
        return contas;
    }

    //  SETTERS PARA CARREGAR DO BANCO
    public void setCapital(double capital) {
        this.capital = capital;
    }

    public void setLucro(double lucro) {
        this.lucro = lucro;
    }

    public void setLucroTaxas(double lucroTaxas) {
        this.lucroTaxas = lucroTaxas;
    }
}