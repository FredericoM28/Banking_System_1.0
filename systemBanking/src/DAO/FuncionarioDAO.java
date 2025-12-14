package DAO;

import model.Funcionario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioDAO {

    public void inserir(Funcionario funcionario) {
        String sql = "INSERT INTO funcionario (nome, cargo, salario, contacto) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, funcionario.getNomeCompletoFunc());
            stmt.setString(2, funcionario.getCargo());
            stmt.setDouble(3, funcionario.getSalario());
            stmt.setString(4, funcionario.getContactoFunc());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir funcionario: " + e.getMessage(), e);
        }
    }

    public List<Funcionario> listarTodos() {
        List<Funcionario> funcionarios = new ArrayList<>();
        String sql = "SELECT * FROM funcionario";

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Funcionario f = new Funcionario(
                        rs.getInt("idFuncionario"),
                        rs.getString("nome"),
                        rs.getString("cargo"),
                        rs.getDouble("salario"),
                        rs.getString("contacto")
                );
                funcionarios.add(f);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar funcionarios: " + e.getMessage(), e);
        }
        return funcionarios;
    }

    public Funcionario buscarPorId(int id) {
        String sql = "SELECT * FROM funcionario WHERE idFuncionario = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Funcionario(
                            rs.getInt("idFuncionario"),
                            rs.getString("nome"),
                            rs.getString("cargo"),
                            rs.getDouble("salario"),
                            rs.getString("contacto")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar funcionario: " + e.getMessage(), e);
        }
        return null;
    }

    public void atualizar(Funcionario funcionario) {
        String sql = "UPDATE funcionario SET nome=?, cargo=?, salario=?, contacto=? WHERE idFuncionario=?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, funcionario.getNomeCompletoFunc());
            stmt.setString(2, funcionario.getCargo());
            stmt.setDouble(3, funcionario.getSalario());
            stmt.setString(4, funcionario.getContactoFunc());
            stmt.setInt(5, funcionario.getIdFuncionario());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar funcionario: " + e.getMessage(), e);
        }
    }

    public void deletar(int id) {
        String sql = "DELETE FROM funcionario WHERE idFuncionario = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar funcionario: " + e.getMessage(), e);
        }
    }
}
