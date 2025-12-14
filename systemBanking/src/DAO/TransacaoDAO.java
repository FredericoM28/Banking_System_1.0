package DAO;

import model.Transacoes;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class TransacaoDAO {

    public void inserir(Transacoes transacao) {
        String sql = "INSERT INTO transacao (contaOrigemId, contaDestinoId, tipoTransacao, valor, dataTransacao, status, descricaoTransacao, categoria, estornada) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, transacao.getContaOrigemId());
            if (transacao.getContaDestinoId() != null) {
                stmt.setInt(2, transacao.getContaDestinoId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setString(3, transacao.getTipoTransacao().name());
            stmt.setDouble(4, transacao.getValor());
            stmt.setTimestamp(5, Timestamp.valueOf(transacao.getDataTransacao()));
            stmt.setString(6, transacao.getStatus().name());
            stmt.setString(7, transacao.getDescricaoTrancacao());
            stmt.setString(8, transacao.getCategoria());
            stmt.setBoolean(9, transacao.isEstornada());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    transacao.setIdTransacao(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir transacao: " + e.getMessage(), e);
        }
    }

    public List<Transacoes> listarTodos() {
        List<Transacoes> transacoes = new ArrayList<>();
        String sql = "SELECT * FROM transacao";

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Transacoes t = new Transacoes(
                    rs.getInt("idTransacao"),
                    Transacoes.TipoTransacao.valueOf(rs.getString("tipoTransacao")),
                    rs.getDouble("valor"),
                    rs.getTimestamp("dataTransacao").toLocalDateTime(),
                    rs.getInt("contaOrigemId"),
                    rs.getObject("contaDestinoId") != null ? rs.getInt("contaDestinoId") : null,
                    Transacoes.StatusTransacao.valueOf(rs.getString("status")),
                    rs.getString("descricaoTransacao"),
                    null, // Conta associada pode ser buscada depois se necessário
                    rs.getString("categoria"),
                    rs.getTimestamp("dataTransacao").toLocalDateTime(),
                    rs.getBoolean("estornada")
                );
                transacoes.add(t);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar transacoes: " + e.getMessage(), e);
        }
        return transacoes;
    }

    public Transacoes buscarPorId(int id) {
        String sql = "SELECT * FROM transacao WHERE idTransacao = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Transacoes(
                        rs.getInt("idTransacao"),
                        Transacoes.TipoTransacao.valueOf(rs.getString("tipoTransacao")),
                        rs.getDouble("valor"),
                        rs.getTimestamp("dataTransacao").toLocalDateTime(),
                        rs.getInt("contaOrigemId"),
                        rs.getObject("contaDestinoId") != null ? rs.getInt("contaDestinoId") : null,
                        Transacoes.StatusTransacao.valueOf(rs.getString("status")),
                        rs.getString("descricaoTransacao"),
                        null,
                        rs.getString("categoria"),
                        rs.getTimestamp("dataTransacao").toLocalDateTime(),
                        rs.getBoolean("estornada")
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar transacao: " + e.getMessage(), e);
        }

        return null;
    }

    public void atualizar(Transacoes transacao) {
        String sql = "UPDATE transacao SET contaOrigemId=?, contaDestinoId=?, tipoTransacao=?, valor=?, dataTransacao=?, status=?, descricaoTransacao=?, categoria=?, estornada=? WHERE idTransacao=?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, transacao.getContaOrigemId());
            if (transacao.getContaDestinoId() != null) {
                stmt.setInt(2, transacao.getContaDestinoId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setString(3, transacao.getTipoTransacao().name());
            stmt.setDouble(4, transacao.getValor());
            stmt.setTimestamp(5, Timestamp.valueOf(transacao.getDataTransacao()));
            stmt.setString(6, transacao.getStatus().name());
            stmt.setString(7, transacao.getDescricaoTrancacao());
            stmt.setString(8, transacao.getCategoria());
            stmt.setBoolean(9, transacao.isEstornada());
            stmt.setInt(10, transacao.getIdTransacao());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar transacao: " + e.getMessage(), e);
        }
    }

    public void deletar(int id) {
        String sql = "DELETE FROM transacao WHERE idTransacao = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar transacao: " + e.getMessage(), e);
        }
    }
}
