package DAO;

import model.Cliente;
import model.Conta;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContaDAO {

    public void salvar(Conta conta) {
        String sql = "INSERT INTO conta (idCliente, numeroConta, tipoConta, saldo, status, niubConta, nib) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, conta.getIdCliente());
            stmt.setInt(2, conta.getNumeroConta());
            stmt.setString(3, conta.getTipoConta().name());
            stmt.setDouble(4, conta.getSaldo());
            stmt.setString(5, conta.getStatus().name());
            stmt.setInt(6, conta.getNiubConta());    // ← CORRIGIDO: Adicionado niubConta
            stmt.setInt(7, conta.getNib());          // ← CORRIGIDO: Adicionado nib

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) conta.setIdConta(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Conta buscarPorNumero(String numeroConta) {
        String sql = "SELECT * FROM conta WHERE numeroConta=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, numeroConta);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // primeiro busca o cliente
                    Cliente cliente = new ClienteDAO().buscarPorId(rs.getInt("idCliente")); 

                    Conta conta = new Conta(
                        rs.getInt("idConta"),
                        rs.getInt("numeroConta"),
                        Conta.TipoConta.valueOf(rs.getString("tipoConta")),
                        cliente,
                        rs.getInt("niubConta"),
                        rs.getInt("nib")
                    );

                    // seta saldo e status
                    conta.setSaldo(rs.getDouble("saldo"));
                    conta.setStatus(Conta.StatusConta.valueOf(rs.getString("status")));

                    return conta;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Conta> listarTodas() {
        List<Conta> lista = new ArrayList<>();
        String sql = "SELECT * FROM conta";
        
        try (Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Busca o cliente correspondente
                Cliente cliente = new ClienteDAO().buscarPorId(rs.getInt("idCliente"));

                // Cria a conta usando o construtor correto
                Conta conta = new Conta(
                    rs.getInt("idConta"),
                    rs.getInt("numeroConta"),
                    Conta.TipoConta.valueOf(rs.getString("tipoConta")),
                    cliente,
                    rs.getInt("niubConta"),
                    rs.getInt("nib")
                );

                // Define saldo e status
                conta.setSaldo(rs.getDouble("saldo"));
                conta.setStatus(Conta.StatusConta.valueOf(rs.getString("status")));

                lista.add(conta);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public void atualizar(Conta conta) {
        String sql = "UPDATE conta SET idCliente=?, numeroConta=?, tipoConta=?, saldo=?, status=?, niubConta=?, nib=? WHERE idConta=?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, conta.getIdCliente());
            stmt.setInt(2, conta.getNumeroConta());
            stmt.setString(3, conta.getTipoConta().name());
            stmt.setDouble(4, conta.getSaldo());
            stmt.setString(5, conta.getStatus().name());
            stmt.setInt(6, conta.getNiubConta());    // ← CORRIGIDO: Adicionado niubConta
            stmt.setInt(7, conta.getNib());          // ← CORRIGIDO: Adicionado nib
            stmt.setInt(8, conta.getIdConta());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletar(int id) {
        String sql = "DELETE FROM conta WHERE idConta=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Conta buscarPorId(int idConta) {
        String sql = "SELECT * FROM conta WHERE idConta=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idConta);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = new ClienteDAO().buscarPorId(rs.getInt("idCliente"));
                    Conta conta = new Conta(
                        rs.getInt("idConta"),
                        rs.getInt("numeroConta"),
                        Conta.TipoConta.valueOf(rs.getString("tipoConta")),
                        cliente,
                        rs.getInt("niubConta"),
                        rs.getInt("nib")
                    );
                    conta.setSaldo(rs.getDouble("saldo"));
                    conta.setStatus(Conta.StatusConta.valueOf(rs.getString("status")));
                    return conta;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Método adicional para buscar conta por NUIB
    public Conta buscarPorNuib(int nuib) {
        String sql = "SELECT * FROM conta WHERE niubConta=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, nuib);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = new ClienteDAO().buscarPorId(rs.getInt("idCliente"));
                    Conta conta = new Conta(
                        rs.getInt("idConta"),
                        rs.getInt("numeroConta"),
                        Conta.TipoConta.valueOf(rs.getString("tipoConta")),
                        cliente,
                        rs.getInt("niubConta"),
                        rs.getInt("nib")
                    );
                    conta.setSaldo(rs.getDouble("saldo"));
                    conta.setStatus(Conta.StatusConta.valueOf(rs.getString("status")));
                    return conta;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Método adicional para buscar conta por NIB
    public Conta buscarPorNib(int nib) {
        String sql = "SELECT * FROM conta WHERE nib=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, nib);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = new ClienteDAO().buscarPorId(rs.getInt("idCliente"));
                    Conta conta = new Conta(
                        rs.getInt("idConta"),
                        rs.getInt("numeroConta"),
                        Conta.TipoConta.valueOf(rs.getString("tipoConta")),
                        cliente,
                        rs.getInt("niubConta"),
                        rs.getInt("nib")
                    );
                    conta.setSaldo(rs.getDouble("saldo"));
                    conta.setStatus(Conta.StatusConta.valueOf(rs.getString("status")));
                    return conta;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}