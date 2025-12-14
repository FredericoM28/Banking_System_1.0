package model;

import java.time.LocalDate;

public class Gestor {

     private String nomeGest;
    private int idGest;
    private int nuitGest;
    private String enderecoGest;
    private int telefoneGest;
    private String emailGest;
    private LocalDate idadeGest;
    private Status status;
    private String departamentoGest;
    private final String nivelAcesso;
    private final String senha;
    
    
     public enum Status {
        ATIVO, INATIVO
    }


     public Gestor(String nomeGest, int idGest, int nuitGest, String enderecoGest, int telefoneGest, String emailGest,
            LocalDate idadeGest, Status status, String departamentoGest, String nivelAcesso, String senha) {
        this.nomeGest = nomeGest;
        this.idGest = idGest;
        this.nuitGest = nuitGest;
        this.enderecoGest = enderecoGest;
        this.telefoneGest = telefoneGest;
        this.emailGest = emailGest;
        this.idadeGest = idadeGest;
        this.status = status;
        this.departamentoGest = departamentoGest;
        this.nivelAcesso = nivelAcesso;
        this.senha = senha;
     }


     public String getNomeGest() {
         return nomeGest;
     }


     public void setNomeGest(String nomeGest) {
         this.nomeGest = nomeGest;
     }


     public int getIdGest() {
         return idGest;
     }


     public void setIdGest(int idGest) {
         this.idGest = idGest;
     }


     public int getNuitGest() {
         return nuitGest;
     }


     public void setNuitGest(int nuitGest) {
         this.nuitGest = nuitGest;
     }


     public String getEnderecoGest() {
         return enderecoGest;
     }


     public void setEnderecoGest(String enderecoGest) {
         this.enderecoGest = enderecoGest;
     }


     public int getTelefoneGest() {
         return telefoneGest;
     }


     public void setTelefoneGest(int telefoneGest) {
         this.telefoneGest = telefoneGest;
     }


     public String getEmailGest() {
         return emailGest;
     }


     public void setEmailGest(String emailGest) {
         this.emailGest = emailGest;
     }


     public LocalDate getIdadeGest() {
         return idadeGest;
     }


     public void setIdadeGest(LocalDate idadeGest) {
         this.idadeGest = idadeGest;
     }


     public Status getStatus() {
         return status;
     }


     public void setStatus(Status status) {
         this.status = status;
     }


     public String getDepartamentoGest() {
         return departamentoGest;
     }


     public void setDepartamentoGest(String departamentoGest) {
         this.departamentoGest = departamentoGest;
     }


     public String getNivelAcesso() {
         return nivelAcesso;
     }


     public String getSenha() {
         return senha;
     }

    
}
