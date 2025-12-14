package model;

import java.time.LocalDate;

public class Cliente {
   
    // atributos
    private String nomeCli;
    private int idCliente;
    private int nuitCli;
    private String enderecoCli;
    private int telefoneCli;
    private String emailCli;
    private LocalDate idadeCli;
    private Status status;
    private String documento;
    private String senhacli;

    
    public String getDocumentoCli() {
    return this.documento;  // ou o nome do teu atributo correspondente
}

    
    public enum Status {
        ATIVO, INATIVO
    }

    // getter e setter para status
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    // exemplo: construtor com status
    public Cliente(String nomeCli, int idCliente, int nuitCli, String enderecoCli, 
                   int telefoneCli, String emailCli, LocalDate idadeCli, Status status, String documento, String senhaCli) {
        this.nomeCli = nomeCli;
        this.idCliente = idCliente;
        this.nuitCli = nuitCli;
        this.enderecoCli = enderecoCli;
        this.telefoneCli = telefoneCli;
        this.emailCli = emailCli;
        this.idadeCli = idadeCli;
        this.status = status;
        this.documento = documento;
        this. senhacli = senhaCli;
    }

    public String getNomeCli() {
        return nomeCli;
    }

    public void setNomeCli(String nomeCli) {
        this.nomeCli = nomeCli;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getNuitCli() {
        return nuitCli;
    }

    public void setNuitCli(int nuitCli) {
        this.nuitCli = nuitCli;
    }

    public String getEnderecoCli() {
        return enderecoCli;
    }

    public void setEnderecoCli(String enderecoCli) {
        this.enderecoCli = enderecoCli;
    }

    public int getTelefoneCli() {
        return telefoneCli;
    }

    public void setTelefoneCli(int telefoneCli) {
        this.telefoneCli = telefoneCli;
    }

    public String getEmailCli() {
        return emailCli;
    }

    public void setEmailCli(String emailCli) {
        this.emailCli = emailCli;
    }

    public LocalDate getIdadeCli() {
        return idadeCli;
    }

    public void setIdadeCli(LocalDate idadeCli) {
        this.idadeCli = idadeCli;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getSenhacli() {
        return senhacli;
    }

    public void setSenhacli(String senhacli) {
        this.senhacli = senhacli;
    }
private int id; // no Cliente
public int getId() { return id; }

    
    

}
