package controller;

import model.Banco;
import model.Cliente;
import model.Conta;
import model.Emprestimo;
import model.Funcionario;
import model.Transacoes;
import DAO.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SistemaController {
    private double taxaJuros = 0.05; // 5% padrão

    // DAOs para persistência
    private ClienteDAO clienteDAO;
    private ContaDAO contaDAO;
    private TransacaoDAO transacaoDAO;
    private EmprestimoDAO emprestimoDAO;
    private FuncionarioDAO funcionarioDAO;
    private BancoDAO bancoDAO;
    
    private Banco banco = new Banco("Nexus Bank", 1000000); // capital inicial de exemplo
    private Random random = new Random();

    public SistemaController() {
        // Inicializa todos os DAOs
        this.clienteDAO = new ClienteDAO();
        this.contaDAO = new ContaDAO();
        this.transacaoDAO = new TransacaoDAO();
        this.emprestimoDAO = new EmprestimoDAO();
        this.funcionarioDAO = new FuncionarioDAO();
        this.bancoDAO = new BancoDAO(DAO.ConnectionFactory.getConnection());
        
        // Carrega o banco do banco de dados
        carregarBanco();
    }

    private void carregarBanco() {
        try {
            Banco bancoCarregado = bancoDAO.getBancoPorNome("Nexus Bank");
            if (bancoCarregado != null) {
                this.banco = bancoCarregado;
            } else {
                // Se não existir, insere o banco padrão
                bancoDAO.inserirBanco(banco);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void atualizarBancoNoBD() {
        try {
            bancoDAO.atualizarBanco(banco);
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar banco no BD: " + e.getMessage());
        }
    }

    public void registrarLucroTransacao(double taxa) {
        banco.registrarLucroTaxa(taxa);
        atualizarBancoNoBD();
    }

    // ===================== CLIENTE =====================

    public Cliente criarCliente(String nome, int nuit, String endereco, int telefone,
                                String email, LocalDate idade, String documento, String senha) {
        Cliente c = new Cliente(
                nome,
                0, // ID será gerado pelo banco
                nuit,
                endereco,
                telefone,
                email,
                idade,
                Cliente.Status.ATIVO,
                documento,
                senha
        );
        clienteDAO.salvar(c);
        return c;
    }

    public Cliente buscarClientePorNUIT(int nuit) {
        List<Cliente> clientes = clienteDAO.listarTodos();
        for (Cliente c : clientes) {
            if (c.getNuitCli() == nuit) {
                return c;
            }
        }
        return null;
    }

    public List<Cliente> listarClientes() {
        return clienteDAO.listarTodos();
    }

    public Cliente buscarClientePorId(int id) {
        return clienteDAO.buscarPorId(id);
    }

    public boolean editarCliente(int id, String novoNome, String novoEmail, int novoTelefone) {
        Cliente cli = clienteDAO.buscarPorId(id);
        if (cli != null) {
            cli.setNomeCli(novoNome);
            cli.setEmailCli(novoEmail);
            cli.setTelefoneCli(novoTelefone);
            clienteDAO.atualizar(cli);
            return true;
        }
        return false;
    }

    public boolean eliminarCliente(int id) {
        try {
            clienteDAO.deletar(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ===================== EMPRÉSTIMOS =====================

    public boolean solicitarEmprestimo(int idConta, double valor, int prazoMeses) {
        Conta conta = buscarContaPorId(idConta);
        if (conta == null) return false;

        Cliente cliente = conta.getClienteId();
        Emprestimo emp = new Emprestimo(
                0, // ID gerado pelo banco
                cliente,
                valor,
                this.taxaJuros,
                prazoMeses
        );
        
        emprestimoDAO.inserir(emp);
        return true;
    }

    public double consultarSaldo(int idConta) {
        Conta conta = buscarContaPorId(idConta);
        return (conta != null) ? conta.getSaldo() : -1;
    }

    // Consultar histórico completo
    public List<Transacoes> consultarHistorico(int idConta) {
        List<Transacoes> todasTransacoes = transacaoDAO.listarTodos();
        List<Transacoes> historico = new ArrayList<>();
        for (Transacoes t : todasTransacoes) {
            if (t.getContaOrigemId() == idConta || 
                (t.getContaDestinoId() != null && t.getContaDestinoId() == idConta)) {
                historico.add(t);
            }
        }
        return historico;
    }

    // ===================== CONTA =====================

    public Conta criarConta(int idCliente, Conta.TipoConta tipoConta) {
        Cliente cliente = clienteDAO.buscarPorId(idCliente);
        if (cliente == null) return null;

        int numeroConta = gerarNumeroConta();
        int niubConta = gerarNiub();
        int nib = gerarNib();

        Conta conta = new Conta(
                0, // ID gerado pelo banco
                numeroConta,
                tipoConta,
                cliente,
                niubConta,
                nib
        );

        contaDAO.salvar(conta);
        
        // Atualiza banco
        banco.adicionarConta(conta);
        atualizarBancoNoBD();
        
        return conta;
    }

    public List<Conta> listarContas() {
        return contaDAO.listarTodas();
    }

    public Conta buscarContaPorId(int idConta) {
        List<Conta> contas = contaDAO.listarTodas();
        return contas.stream()
                .filter(c -> c.getIdConta() == idConta)
                .findFirst()
                .orElse(null);
    }

    public boolean editarConta(int idConta, Conta.TipoConta novoTipo) {
        Conta conta = buscarContaPorId(idConta);
        if (conta != null) {
            conta.setTipoConta(novoTipo);
            contaDAO.atualizar(conta);
            return true;
        }
        return false;
    }

    public boolean eliminarConta(int idConta) {
        try {
            contaDAO.deletar(idConta);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean depositar(int idConta, double valor) {
        Conta conta = buscarContaPorId(idConta);
        if (conta != null && valor > 0) {
            conta.depositar(valor);
            contaDAO.atualizar(conta);
            
            registrarTransacao(idConta, "Depósito", valor);
            return true;
        }
        return false;
    }

    public boolean sacar(int idConta, double valor) {
        Conta conta = buscarContaPorId(idConta);
        if (conta != null && valor > 0) {
            double taxa = valor * 0.005; // 0.5%
            double totalDebitar = valor + taxa;
            
            if (conta.getSaldo() >= totalDebitar) {
                // Debita valor + taxa
                conta.sacar(totalDebitar);
                contaDAO.atualizar(conta);
                
                // REGISTRA O LUCRO DA TAXA NO BANCO
                banco.registrarLucroTaxa(taxa);
                atualizarBancoNoBD();
                
                registrarTransacao(idConta, "Saque", valor);
                return true;
            }
        }
        return false;
    }

    public boolean transferirMesmaInstituicao(int idOrigem, int idDestino, double valor) {
        Conta origem = buscarContaPorId(idOrigem);
        Conta destino = buscarContaPorId(idDestino);

        if (origem == null || destino == null || origem == destino) return false;
        if (valor <= 0 || origem.getSaldo() < valor) return false;

        origem.sacar(valor);
        destino.depositar(valor);
        
        contaDAO.atualizar(origem);
        contaDAO.atualizar(destino);
        
        registrarTransacao(idOrigem, "Transferência Interna", valor);
        
        return true;
    }

    public boolean transferirOutroBanco(int idOrigem, String nibDestino, double valor) {
        Conta origem = buscarContaPorId(idOrigem);
        if (origem == null || valor <= 0) return false;

        double taxa = valor * 0.01; // 1%
        double totalDebitar = valor + taxa;
        
        if (origem.getSaldo() >= totalDebitar) {
            origem.sacar(totalDebitar);
            contaDAO.atualizar(origem);

            // REGISTRA O LUCRO DA TAXA NO BANCO
            banco.registrarLucroTaxa(taxa);
            atualizarBancoNoBD();

            registrarTransacao(idOrigem, "Transferência Externa", valor);
            return true;
        }
        return false;
    }

    public boolean transferirCarteiraMovel(int idOrigem, String numeroTelefone, double valor) {
        Conta origem = buscarContaPorId(idOrigem);
        if (origem == null || valor <= 0 || origem.getSaldo() < valor) return false;

        origem.sacar(valor);
        contaDAO.atualizar(origem);
        
        registrarTransacao(idOrigem, "Transferência Carteira Móvel", valor);
        return true;
    }

    public void creditar(int idConta, double valor) {
        Conta conta = buscarContaPorId(idConta);
        if (conta != null) {
            conta.depositar(valor);
            contaDAO.atualizar(conta);
            registrarTransacao(idConta, "Crédito", valor);
        }
    }

    public void debitar(int idConta, double valor) {
        Conta conta = buscarContaPorId(idConta);
        if (conta != null && conta.getSaldo() >= valor) {
            conta.sacar(valor);
            contaDAO.atualizar(conta);
            registrarTransacao(idConta, "Débito", valor);
        }
    }

    public Conta.StatusConta consultarStatus(int idConta) {
        Conta conta = buscarContaPorId(idConta);
        return (conta != null) ? conta.getStatus() : null;
    }

    public boolean alterarStatus(int idConta, Conta.StatusConta novoStatus) {
        Conta conta = buscarContaPorId(idConta);
        if (conta != null) {
            conta.setStatus(novoStatus);
            contaDAO.atualizar(conta);
            return true;
        }
        return false;
    }

    // ===================== OPERAÇÕES DE FUNCIONÁRIO =====================

    public boolean registrarDeposito(int idConta, double valor) {
        return depositar(idConta, valor);
    }

    public boolean registrarLevantamento(int idConta, double valor) {
        return sacar(idConta, valor);
    }

    public boolean registrarDepositoFlexivel(Integer idConta, Integer numeroConta, Double valor, String referencia, String entidade) {
        if (valor == null || valor <= 0) return false;

        Conta conta = null;
        if (idConta != null) {
            conta = buscarContaPorId(idConta);
        }
        if (conta == null && numeroConta != null) {
            conta = buscarContaPorNumero(numeroConta);
        }

        if (conta != null) {
            conta.depositar(valor);
            contaDAO.atualizar(conta);
            registrarTransacaoCompleta(conta.getIdConta(), "Depósito Flexível", valor, referencia, entidade);
            return true;
        }
        return false;
    }

    public boolean registrarLevantamentoFlexivel(Integer idConta, Integer numeroConta, Double valor, String referencia, String entidade) {
        if (valor == null || valor <= 0) return false;

        Conta conta = null;
        if (idConta != null) {
            conta = buscarContaPorId(idConta);
        }
        if (conta == null && numeroConta != null) {
            conta = buscarContaPorNumero(numeroConta);
        }

        if (conta != null && conta.getSaldo() >= valor) {
            double taxa = valor * 0.005; // 0.5%
            double totalDebitar = valor + taxa;
            
            if (conta.getSaldo() >= totalDebitar) {
                conta.sacar(totalDebitar);
                contaDAO.atualizar(conta);
                
                // REGISTRA O LUCRO DA TAXA NO BANCO
                banco.registrarLucroTaxa(taxa);
                atualizarBancoNoBD();
                
                registrarTransacaoCompleta(conta.getIdConta(), "Saque Flexível", valor, referencia, entidade);
                return true;
            }
        }
        return false;
    }

    private Transacoes registrarTransacaoCompleta(int idConta, String categoria, double valor, 
                                                 String referencia, String entidade) {
        Conta conta = buscarContaPorId(idConta);
        if (conta == null) return null;

        Transacoes.TipoTransacao tipo = Transacoes.TipoTransacao.DEPOSITO;
        if (categoria != null) {
            String c = categoria.toLowerCase();
            if (c.contains("saque") || c.contains("levant")) tipo = Transacoes.TipoTransacao.SAQUE;
            else if (c.contains("transfer")) tipo = Transacoes.TipoTransacao.TRANSFERENCIA;
        }

        Transacoes t = new Transacoes(
            0, // ID gerado pelo banco
            tipo,
            valor,
            LocalDateTime.now(),
            conta.getIdConta(),
            null,
            Transacoes.StatusTransacao.CONCLUIDA,
            categoria,
            conta,
            categoria,
            LocalDateTime.now(),
            false
        );
        
        // Se sua classe Transacoes tiver esses campos:
        try {
            t.getClass().getMethod("setReferencia", String.class).invoke(t, referencia);
            t.getClass().getMethod("setEntidade", String.class).invoke(t, entidade);
        } catch (Exception e) {
            // Campos não existem, ignora
        }

        transacaoDAO.inserir(t);
        return t;
    }

    public String emitirRecibo(int idTransacao) {
        Transacoes t = transacaoDAO.buscarPorId(idTransacao);
        if (t != null) {
            return "RECIBO - Transação: " + t.getCategoria() +
                " | Valor: " + t.getValor() +
                " | Data: " + t.getDataTransacao();
        }
        return "Transação não encontrada.";
    }

    public Transacoes consultarOperacao(int idTransacao) {
        return transacaoDAO.buscarPorId(idTransacao);
    }

    public List<Cliente> pesquisarClientes(String termo, String tipo) {
        if (termo == null || termo.trim().isEmpty()) return listarClientes();
        termo = termo.trim();
        List<Cliente> encontrados = new ArrayList<>();

        if ("ID".equalsIgnoreCase(tipo)) {
            try {
                int id = Integer.parseInt(termo);
                Cliente c = buscarClientePorId(id);
                if (c != null) encontrados.add(c);
                return encontrados;
            } catch (NumberFormatException e) {
                return encontrados;
            }
        }

        if ("Nome".equalsIgnoreCase(tipo)) {
            List<Cliente> todosClientes = listarClientes();
            for (Cliente c : todosClientes) {
                if (c.getNomeCli() != null && c.getNomeCli().toLowerCase().contains(termo.toLowerCase())) {
                    encontrados.add(c);
                }
            }
            return encontrados;
        }

        if ("Nº Conta".equalsIgnoreCase(tipo) || "NUM_CONTA".equalsIgnoreCase(tipo)) {
            try {
                int numero = Integer.parseInt(termo);
                List<Conta> todasContas = listarContas();
                for (Conta ct : todasContas) {
                    if (ct.getNumeroConta() == numero) {
                        Cliente cliente = ct.getClienteId();
                        if (cliente != null) {
                            encontrados.add(cliente);
                        }
                    }
                }
            } catch (NumberFormatException e) {
                // termo não numérico -> nada
            }
            return encontrados;
        }

        // fallback: pesquisa por nome
        List<Cliente> todosClientes = listarClientes();
        for (Cliente c : todosClientes) {
            if (c.getNomeCli() != null && c.getNomeCli().toLowerCase().contains(termo.toLowerCase())) 
                encontrados.add(c);
        }
        return encontrados;
    }

    public Conta abrirContaCliente(String nomeCli, int nuitcli, String endereco, int telefone, String email, String documento, Conta.TipoConta tipoConta) {
        // Verifica se cliente já existe pelo NUIT
        Cliente clienteExistente = buscarClientePorNUIT(nuitcli);
        if (clienteExistente != null) {
            // Se já existe, cria conta para este cliente
            return criarConta(clienteExistente.getIdCliente(), tipoConta);
        }

        // Se não existe, cria novo cliente
        Cliente novoCliente = new Cliente(
            nomeCli,
            0, // ID gerado pelo banco
            nuitcli,
            endereco,
            telefone,
            email,
            LocalDate.now(),
            Cliente.Status.ATIVO,
            documento,
            "1234"
        );

        clienteDAO.salvar(novoCliente);
        Conta novaConta = criarConta(novoCliente.getIdCliente(), tipoConta);
        return novaConta;
    }

    public boolean atualizarDadosCliente(int id, String novoNome, String novoEmail, int novoTelefone) {
        return editarCliente(id, novoNome, novoEmail, novoTelefone);
    }

    public boolean encerrarContaCliente(int idConta) {
        return alterarStatus(idConta, Conta.StatusConta.INATIVA);
    }

    public Cliente consultarDadosCliente(int idCliente) {
        return buscarClientePorId(idCliente);
    }

    public String reemitirCartao(int idConta) {
        Conta conta = buscarContaPorId(idConta);
        if (conta != null) {
            String novoCartao = "CARTAO-" + gerarNumeroConta();
            return novoCartao;
        }
        return "Conta não encontrada.";
    }

    public String fornecerSuporte(int idCliente, String descricao) {
        return "Ticket de suporte criado para cliente " + idCliente + ": " + descricao;
    }

    // ===================== TRANSAÇÕES =====================

    public Transacoes registrarTransacao(int idConta, String categoria, double valor) {
        Conta conta = buscarContaPorId(idConta);
        if (conta == null) return null;

        Transacoes.TipoTransacao tipo = Transacoes.TipoTransacao.DEPOSITO;
        if (categoria != null) {
            String c = categoria.toLowerCase();
            if (c.contains("saque") || c.contains("levant")) tipo = Transacoes.TipoTransacao.SAQUE;
            else if (c.contains("transfer")) tipo = Transacoes.TipoTransacao.TRANSFERENCIA;
            else tipo = Transacoes.TipoTransacao.DEPOSITO;
        }

        Transacoes t = new Transacoes(
            0, // ID gerado pelo banco
            tipo,
            valor,
            LocalDateTime.now(),
            conta.getIdConta(),
            null,
            Transacoes.StatusTransacao.CONCLUIDA,
            categoria,
            conta,
            categoria,
            LocalDateTime.now(),
            false
        );

        transacaoDAO.inserir(t);
        return t;
    }

    public boolean definirCategoria(int idTransacao, String novaCategoria) {
        Transacoes t = transacaoDAO.buscarPorId(idTransacao);
        if (t != null) {
            t.setCategoria(novaCategoria);
            transacaoDAO.atualizar(t);
            return true;
        }
        return false;
    }

    public Transacoes consultarTransacao(int idTransacao) {
        return transacaoDAO.buscarPorId(idTransacao);
    }

    public boolean estornarTransacao(int idTransacao) {
        Transacoes t = transacaoDAO.buscarPorId(idTransacao);
        if (t != null && !t.isEstornada()) {
            Conta conta = buscarContaPorId(t.getContaOrigemId());
            if (conta != null) {
                conta.depositar(t.getValor());
                contaDAO.atualizar(conta);
                t.setEstornada(true);
                transacaoDAO.atualizar(t);
                return true;
            }
        }
        return false;
    }

    // ===================== ADMINISTRADOR =====================

    public Funcionario criarFuncionario(String nomeCompletoFunc, String cargo) {
        Funcionario f = new Funcionario(
            0, // ID gerado pelo banco
            nomeCompletoFunc,
            cargo,
            0.0, // salário padrão
            ""   // contacto vazio
        );
        funcionarioDAO.inserir(f);
        return f;
    }

    public boolean editarFuncionario(int idFuncionario, String novoNome, String novoCargo) {
        Funcionario f = funcionarioDAO.buscarPorId(idFuncionario);
        if (f != null) {
            f.setNomeCompletoFunc(novoNome);
            f.setCargo(novoCargo);
            funcionarioDAO.atualizar(f);
            return true;
        }
        return false;
    }

    public boolean removerFuncionario(int idFuncionario) {
        try {
            funcionarioDAO.deletar(idFuncionario);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Transacoes> visualizarTodasTransacoes() {
        return transacaoDAO.listarTodos();
    }

    public void definirTaxasJuros(double novaTaxa) {
        this.taxaJuros = novaTaxa;
    }

    public Map<String, Object> analisarPerformanceBanco() {
        Map<String, Object> analise = new HashMap<>();
        List<Transacoes> todasTransacoes = transacaoDAO.listarTodos();
        double totalValor = 0.0;

        for (Transacoes t : todasTransacoes) {
            totalValor += t.getValor();
        }

        analise.put("TotalTransacoes", todasTransacoes.size());
        analise.put("VolumeFinanceiro", totalValor);
        analise.put("TotalClientes", clienteDAO.listarTodos().size());
        analise.put("TotalContas", contaDAO.listarTodas().size());
        analise.put("CapitalBanco", banco.getCapital());
        analise.put("LucroTotal", banco.getLucro());
        analise.put("LucroTaxas", banco.getLucroTaxas());

        return analise;
    }

    // ===================== GESTOR =====================

    public boolean autorizarEmprestimo(int idSolicitacao) {
        Emprestimo emprestimo = emprestimoDAO.buscarPorId(idSolicitacao);
        if (emprestimo != null) {
            emprestimo.setStatus(Emprestimo.emprestimos.CONFIRMADA);
            emprestimoDAO.atualizar(emprestimo);

            // CALCULA E REGISTRA LUCRO DO EMPRÉSTIMO
            double juros = emprestimo.getValorSolicitado() * emprestimo.getTaxaJuro();
            banco.registrarLucro(juros);
            atualizarBancoNoBD();

            return true;
        }
        return false;
    }

    public boolean bloquearConta(int idConta) {
        return alterarStatus(idConta, Conta.StatusConta.BLOQUEADA);
    }

    public boolean ativarConta(int idConta) {
        return alterarStatus(idConta, Conta.StatusConta.ATIVA);
    }

    public Map<String, Double> gerarRelatorioFinanceiro(LocalDateTime inicio, LocalDateTime fim) {
        Map<String, Double> relatorio = new HashMap<>();
        double entradas = 0.0;
        double saidas = 0.0;

        List<Transacoes> todasTransacoes = transacaoDAO.listarTodos();
        for (Transacoes t : todasTransacoes) {
            if ((t.getDataTransacao().isAfter(inicio) || t.getDataTransacao().isEqual(inicio)) &&
                (t.getDataTransacao().isBefore(fim) || t.getDataTransacao().isEqual(fim))) {
                
                if (t.getTipoTransacao() == Transacoes.TipoTransacao.DEPOSITO) {
                    entradas += t.getValor();
                } else {
                    saidas += t.getValor();
                }
            }
        }

        relatorio.put("Entradas", entradas);
        relatorio.put("Saídas", saidas);
        relatorio.put("Saldo Final", entradas - saidas);

        return relatorio;
    }

    public boolean encerrarConta(int idConta) {
        return alterarStatus(idConta, Conta.StatusConta.INATIVA);
    }

    public boolean sacarComTaxa(int idConta, double valor) {
        return sacar(idConta, valor); // Já inclui taxa automaticamente
    }

    public boolean transferirComTaxa(int idOrigem, int idDestino, double valor) {
        Conta origem = buscarContaPorId(idOrigem);
        Conta destino = buscarContaPorId(idDestino);
        if (origem != null && destino != null && valor > 0) {
            double taxa = valor * 0.002; // 0.2%
            double totalDebitar = valor + taxa;
            
            if (origem.getSaldo() >= totalDebitar) {
                origem.sacar(totalDebitar);
                destino.depositar(valor);
                
                contaDAO.atualizar(origem);
                contaDAO.atualizar(destino);
                
                // REGISTRA O LUCRO DA TAXA NO BANCO
                banco.registrarLucroTaxa(taxa);
                atualizarBancoNoBD();

                registrarTransacao(idOrigem, "Transferência com Taxa", valor);
                return true;
            }
        }
        return false;
    }

    // ===================== AUTENTICAÇÃO =====================

    public String autenticarUsuario(String identificador, String senha) {
        if (identificador == null || senha == null) {
            return "invalido";
        }
        
        identificador = identificador.trim().toLowerCase();
        
        // 1. Verifica se é Administrador
        if (autenticarAdmin(identificador, senha)) {
            return "admin";
        }
        
        // 2. Verifica se é Gestor
        if (autenticarGestor(identificador, senha)) {
            return "gestor";
        }
        
        // 3. Verifica se é Funcionário
        if (autenticarFuncionario(identificador, senha)) {
            return "funcionario";
        }
        
        // 4. Verifica se é Cliente (por email ou ID)
        if (autenticarCliente(identificador, senha)) {
            return "cliente";
        }
        
        return "invalido";
    }

    private boolean autenticarAdmin(String identificador, String senha) {
        return ("admin".equals(identificador) || "administrador".equals(identificador)) 
               && "admin123".equals(senha);
    }

    private boolean autenticarGestor(String identificador, String senha) {
        if (identificador.startsWith("gestor")) {
            if ("gestor".equals(identificador) && "gestor123".equals(senha)) {
                return true;
            }
            try {
                String idStr = identificador.substring(6);
                if (!idStr.isEmpty()) {
                    int id = Integer.parseInt(idStr);
                    List<Funcionario> funcionarios = funcionarioDAO.listarTodos();
                    for (Funcionario func : funcionarios) {
                        if (func.getIdFuncionario() == id && 
                            "Gestor".equalsIgnoreCase(func.getCargo()) &&
                            "gestor123".equals(senha)) {
                            return true;
                        }
                    }
                }
            } catch (NumberFormatException e) {
                // Não é um ID numérico
            }
        }
        return false;
    }

    private boolean autenticarFuncionario(String identificador, String senha) {
        if (identificador.startsWith("func")) {
            if ("funcionario".equals(identificador) && "funcionario123".equals(senha)) {
                return true;
            }
            try {
                String idStr = identificador.substring(4);
                if (!idStr.isEmpty()) {
                    int id = Integer.parseInt(idStr);
                    List<Funcionario> funcionarios = funcionarioDAO.listarTodos();
                    for (Funcionario func : funcionarios) {
                        if (func.getIdFuncionario() == id && 
                            "funcionario123".equals(senha)) {
                            return true;
                        }
                    }
                }
            } catch (NumberFormatException e) {
                // Não é um ID numérico
            }
        }
        
        List<Funcionario> funcionarios = funcionarioDAO.listarTodos();
        for (Funcionario func : funcionarios) {
            if (func.getNomeCompletoFunc().toLowerCase().contains(identificador) &&
                "funcionario123".equals(senha)) {
                return true;
            }
        }
        
        return false;
    }

    private boolean autenticarCliente(String identificador, String senha) {
        List<Cliente> clientes = clienteDAO.listarTodos();
        for (Cliente cliente : clientes) {
            // Verifica por email
            if (cliente.getEmailCli() != null && 
                cliente.getEmailCli().toLowerCase().equals(identificador) &&
                cliente.getSenhacli().equals(senha)) {
                return true;
            }
            
            // Verifica por ID
            try {
                int id = Integer.parseInt(identificador);
                if (cliente.getIdCliente() == id && cliente.getSenhacli().equals(senha)) {
                    return true;
                }
            } catch (NumberFormatException e) {
                // Não é um ID numérico
            }
            
            // Verifica por nome
            if (cliente.getNomeCli() != null && 
                cliente.getNomeCli().toLowerCase().equals(identificador) &&
                cliente.getSenhacli().equals(senha)) {
                return true;
            }
        }
        return false;
    }

    public Cliente getClienteAutenticado(String identificador) {
        List<Cliente> clientes = clienteDAO.listarTodos();
        for (Cliente cliente : clientes) {
            if (cliente.getEmailCli() != null && 
                cliente.getEmailCli().equalsIgnoreCase(identificador)) {
                return cliente;
            }
            
            try {
                int id = Integer.parseInt(identificador);
                if (cliente.getIdCliente() == id) {
                    return cliente;
                }
            } catch (NumberFormatException e) {
                // Não é ID numérico
            }
            
            if (cliente.getNomeCli() != null && 
                cliente.getNomeCli().equalsIgnoreCase(identificador)) {
                return cliente;
            }
        }
        return null;
    }

    public String getNomeUsuario(String identificador, String tipoUsuario) {
        switch (tipoUsuario) {
            case "admin":
                return "Administrador";
            case "gestor":
                return "Gestor do Sistema";
            case "funcionario":
                return "Funcionário";
            case "cliente":
                Cliente cliente = getClienteAutenticado(identificador);
                return cliente != null ? cliente.getNomeCli() : "Cliente";
            default:
                return "Usuário";
        }
    }

    // ===================== RELATÓRIOS =====================

    public double calcularSaldoTotal() {
        List<Conta> contas = contaDAO.listarTodas();
        double total = 0.0;
        for (Conta conta : contas) {
            total += conta.getSaldo();
        }
        return total;
    }

    public String gerarRelatorioCaixa() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RELATÓRIO DO CAIXA ===\n\n");
        sb.append("Total de Contas: ").append(contaDAO.listarTodas().size()).append("\n");
        sb.append("Saldo Total no Banco: ").append(String.format("%.2f", calcularSaldoTotal())).append(" MZN\n");
        sb.append("Total de Depósitos: ").append(String.format("%.2f", getTotalDepositos())).append(" MZN\n");
        sb.append("Total de Saques: ").append(String.format("%.2f", getTotalSaques())).append(" MZN\n");
        sb.append("Lucro de Taxas: ").append(String.format("%.2f", banco.getLucroTaxas())).append(" MZN\n");
        sb.append("\n-----------------------------\n");
        sb.append("Data: ").append(java.time.LocalDate.now()).append("\n");
        return sb.toString();
    }

    public double getTotalDepositos() {
        List<Transacoes> transacoes = transacaoDAO.listarTodos();
        double total = 0.0;
        for (Transacoes t : transacoes) {
            if (t.getTipoTransacao() == Transacoes.TipoTransacao.DEPOSITO) {
                total += t.getValor();
            }
        }
        return total;
    }

    public double getTotalSaques() {
        List<Transacoes> transacoes = transacaoDAO.listarTodos();
        double total = 0.0;
        for (Transacoes t : transacoes) {
            if (t.getTipoTransacao() == Transacoes.TipoTransacao.SAQUE) {
                total += t.getValor();
            }
        }
        return total;
    }

    public List<Transacoes> listarDepositos() {
        List<Transacoes> todas = transacaoDAO.listarTodos();
        List<Transacoes> depositos = new ArrayList<>();
        for (Transacoes t : todas) {
            if (t.getTipoTransacao() == Transacoes.TipoTransacao.DEPOSITO) {
                depositos.add(t);
            }
        }
        return depositos;
    }

    public List<Transacoes> listarSaques() {
        List<Transacoes> todas = transacaoDAO.listarTodos();
        List<Transacoes> saques = new ArrayList<>();
        for (Transacoes t : todas) {
            if (t.getTipoTransacao() == Transacoes.TipoTransacao.SAQUE) {
                saques.add(t);
            }
        }
        return saques;
    }

    public Conta buscarContaPorNumero(int numeroConta) {
        return contaDAO.buscarPorNumero(String.valueOf(numeroConta));
    }

    // ===================== GETTERS PARA BANCO =====================

    public double getLucroTotal() {
        return banco.getLucro();
    }

    public double getLucroTaxas() {
        return banco.getLucroTaxas();
    }

    public double getCapitalBanco() {
        return banco.getCapital();
    }

    public Map<String, Double> getResumoFinanceiro() {
        Map<String, Double> resumo = new HashMap<>();
        resumo.put("Capital", banco.getCapital());
        resumo.put("Lucro Total", banco.getLucro());
        resumo.put("Lucro de Taxas", banco.getLucroTaxas());
        resumo.put("Lucro de Empréstimos", banco.getLucro() - banco.getLucroTaxas());
        return resumo;
    }

    // ===================== GERADORES =====================

    private int gerarNumeroConta() {
        return 100000 + random.nextInt(900000);
    }

    private int gerarNiub() {
        return 10000000 + random.nextInt(90000000);
    }

    private int gerarNib() {
        return 200000000 + random.nextInt(100000000);
    }

    public static void main(String[] args) {
        SistemaController sistema = new SistemaController();
        System.out.println("Sistema Controller inicializado com DAOs");
        
        // Teste básico
        System.out.println("Total de clientes: " + sistema.listarClientes().size());
        System.out.println("Total de contas: " + sistema.listarContas().size());
        System.out.println("Capital do banco: " + sistema.getCapitalBanco());
        System.out.println("Lucro de taxas: " + sistema.getLucroTaxas());
    }

    public List<Transacoes> visualizarTransacoes(int idConta) {
        return List.of();
    }
}