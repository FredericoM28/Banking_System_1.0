package DAO;

import model.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    // Salvar um novo cliente
    public void salvar(Cliente c) {
        String sql = "INSERT INTO cliente (nome, nuit, endereco, telefone, email, idade, status, documento, senha) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, c.getNomeCli());
            stmt.setInt(2, c.getNuitCli());
            stmt.setString(3, c.getEnderecoCli());
            stmt.setInt(4, c.getTelefoneCli());
            stmt.setString(5, c.getEmailCli());
            stmt.setDate(6, Date.valueOf(c.getIdadeCli()));
            stmt.setString(7, c.getStatus().name());
            stmt.setString(8, c.getDocumento());
            stmt.setString(9, c.getSenhacli());

            stmt.executeUpdate();

            // Atualiza o ID do cliente com o gerado pelo banco
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    c.setIdCliente(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar cliente: " + e.getMessage(), e);
        }
    }

    // Buscar cliente por ID
    public Cliente buscarPorId(int id) {
        String sql = "SELECT * FROM cliente WHERE idCliente=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Cliente(
                        rs.getString("nome"),
                        rs.getInt("idCliente"),
                        rs.getInt("nuit"),
                        rs.getString("endereco"),
                        rs.getInt("telefone"),
                        rs.getString("email"),
                        rs.getDate("idade").toLocalDate(),
                        Cliente.Status.valueOf(rs.getString("status")),
                        rs.getString("documento"),
                        rs.getString("senha")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cliente: " + e.getMessage(), e);
        }
        return null;
    }

    // Listar todos os clientes
    public List<Cliente> listarTodos() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM cliente";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new Cliente(
                    rs.getString("nome"),
                    rs.getInt("idCliente"),
                    rs.getInt("nuit"),
                    rs.getString("endereco"),
                    rs.getInt("telefone"),
                    rs.getString("email"),
                    rs.getDate("idade").toLocalDate(),
                    Cliente.Status.valueOf(rs.getString("status")),
                    rs.getString("documento"),
                    rs.getString("senha")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar clientes: " + e.getMessage(), e);
        }
        return lista;
    }

    // Atualizar cliente existente
    public void atualizar(Cliente c) {
        String sql = "UPDATE cliente SET nome=?, nuit=?, endereco=?, telefone=?, email=?, idade=?, status=?, documento=?, senha=? WHERE idCliente=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, c.getNomeCli());
            stmt.setInt(2, c.getNuitCli());
            stmt.setString(3, c.getEnderecoCli());
            stmt.setInt(4, c.getTelefoneCli());
            stmt.setString(5, c.getEmailCli());
            stmt.setDate(6, Date.valueOf(c.getIdadeCli()));
            stmt.setString(7, c.getStatus().name());
            stmt.setString(8, c.getDocumento());
            stmt.setString(9, c.getSenhacli());
            stmt.setInt(10, c.getIdCliente());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar cliente: " + e.getMessage(), e);
        }
    }

    // Deletar cliente por ID
    public void deletar(int id) {
        String sql = "DELETE FROM cliente WHERE idCliente=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar cliente: " + e.getMessage(), e);
        }
    }
}
