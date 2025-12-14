package DAO;

import model.Banco;
import java.sql.*;

public class BancoDAO {
    private Connection conn;

    public BancoDAO(Connection conn) {
        this.conn = conn;
    }

    // Inserir novo banco na tabela
    public void inserirBanco(Banco banco) throws SQLException {
        String sql = "INSERT INTO bancoCapital (nome, capital, lucroTotal, lucroTaxas) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, banco.getNome());
            stmt.setDouble(2, banco.getCapital());
            stmt.setDouble(3, banco.getLucro());
            stmt.setDouble(4, banco.getLucroTaxas());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int idBanco = rs.getInt(1);
                System.out.println("Banco inserido com ID: " + idBanco);
            }
        }
    }

    // Atualizar capital e lucros
    public void atualizarBanco(Banco banco) throws SQLException {
        String sql = "UPDATE bancoCapital SET capital = ?, lucroTotal = ?, lucroTaxas = ? WHERE nome = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, banco.getCapital());
            stmt.setDouble(2, banco.getLucro());
            stmt.setDouble(3, banco.getLucroTaxas());
            stmt.setString(4, banco.getNome());
            stmt.executeUpdate();
        }
    }

    //  Obter banco pelo nome
    public Banco getBancoPorNome(String nome) throws SQLException {
        String sql = "SELECT * FROM bancoCapital WHERE nome = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                //CORREÇÃO: Criar banco e carregar TODOS os valores
                Banco banco = new Banco(
                    rs.getString("nome"),
                    rs.getDouble("capital")
                );
                
                // Usar setters em vez de registrarLucro
                banco.setLucro(rs.getDouble("lucroTotal"));
                banco.setLucroTaxas(rs.getDouble("lucroTaxas"));
                
                return banco;
            }
        }
        return null;
    }
}