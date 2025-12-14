package DAO;

import model.Emprestimo;
import model.Cliente;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmprestimoDAO {

    // Método para inserir um novo empréstimo
    public void inserir(Emprestimo emprestimo) {
        String sql = "INSERT INTO emprestimo (idCliente, valorSolicitado, taxaJuro, prazoMeses, dataSolicitacao, dataAprovacao, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, emprestimo.getCliente().getIdCliente());
            stmt.setDouble(2, emprestimo.getValorSolicitado());
            stmt.setDouble(3, emprestimo.getTaxaJuro());
            stmt.setInt(4, emprestimo.getPrazoMeses());
            stmt.setDate(5, Date.valueOf(emprestimo.getDataSolicitacao()));
            
            // dataAprovacao pode ser nula
            if (emprestimo.getDataAprovacao() != null) {
                stmt.setDate(6, Date.valueOf(emprestimo.getDataAprovacao()));
            } else {
                stmt.setNull(6, Types.DATE);
            }

            stmt.setString(7, emprestimo.getStatus().name());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir empréstimo: " + e.getMessage(), e);
        }
    }

    // Método para listar todos os empréstimos
    public List<Emprestimo> listarTodos() {
        List<Emprestimo> emprestimos = new ArrayList<>();
        String sql = "SELECT * FROM emprestimo";

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Cliente cliente = buscarClientePorId(rs.getInt("idCliente")); // implemente este método

                Emprestimo e = new Emprestimo(
                        rs.getInt("idEmprestimo"),
                        cliente,
                        rs.getDouble("valorSolicitado"),
                        rs.getDouble("taxaJuro"),
                        rs.getInt("prazoMeses")
                );

                // Ajustar status
                e.setStatus(Emprestimo.emprestimos.valueOf(rs.getString("status")));

                // Ajustar dataAprovacao se não for nula
                Date dataAprovacao = rs.getDate("dataAprovacao");
                if (dataAprovacao != null) {
                    e.setStatus(Emprestimo.emprestimos.CONFIRMADA);
                }

                emprestimos.add(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar empréstimos: " + e.getMessage(), e);
        }
        return emprestimos;
    }

    // Buscar empréstimo pelo ID
    public Emprestimo buscarPorId(int id) {
        String sql = "SELECT * FROM emprestimo WHERE idEmprestimo = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = buscarClientePorId(rs.getInt("idCliente"));

                    Emprestimo e = new Emprestimo(
                            rs.getInt("idEmprestimo"),
                            cliente,
                            rs.getDouble("valorSolicitado"),
                            rs.getDouble("taxaJuro"),
                            rs.getInt("prazoMeses")
                    );

                    e.setStatus(Emprestimo.emprestimos.valueOf(rs.getString("status")));

                    return e;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar empréstimo: " + e.getMessage(), e);
        }
        return null;
    }

    // Atualizar empréstimo
    public void atualizar(Emprestimo emprestimo) {
        String sql = "UPDATE emprestimo SET idCliente=?, valorSolicitado=?, taxaJuro=?, prazoMeses=?, dataSolicitacao=?, dataAprovacao=?, status=? " +
                     "WHERE idEmprestimo=?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, emprestimo.getCliente().getIdCliente());
            stmt.setDouble(2, emprestimo.getValorSolicitado());
            stmt.setDouble(3, emprestimo.getTaxaJuro());
            stmt.setInt(4, emprestimo.getPrazoMeses());
            stmt.setDate(5, Date.valueOf(emprestimo.getDataSolicitacao()));

            if (emprestimo.getDataAprovacao() != null) {
                stmt.setDate(6, Date.valueOf(emprestimo.getDataAprovacao()));
            } else {
                stmt.setNull(6, Types.DATE);
            }

            stmt.setString(7, emprestimo.getStatus().name());
            stmt.setInt(8, emprestimo.getIdEmprestimo());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar empréstimo: " + e.getMessage(), e);
        }
    }

    // Deletar empréstimo
    public void deletar(int id) {
        String sql = "DELETE FROM emprestimo WHERE idEmprestimo = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar empréstimo: " + e.getMessage(), e);
        }
    }

    // --- Método auxiliar para buscar cliente pelo ID ---
    private Cliente buscarClientePorId(int id) {
        // Aqui você deve implementar a lógica de buscar o Cliente no banco ou na lista de clientes
        // Por exemplo, chamando ClienteDAO.buscarPorId(id)
        return null; // substituir pela implementação real
    }
}
