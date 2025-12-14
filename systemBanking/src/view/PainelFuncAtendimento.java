package view;

import controller.SistemaController;
import model.Cliente;
import model.Conta;
import model.Transacoes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class PainelFuncAtendimento extends JFrame {

    private JPanel menuLateral, painelTopo, painelPrincipal;
    private JLabel lblBanco, lblFuncionario;
    private JButton btnDashboard, btnGerirClientes, btnNovaConta, btnAtualizarDados, btnEncerrarConta, btnCartoes, btnSuporte, btnSair;
    private SistemaController controller;
    private String nomeFuncionario, cargoFuncionario;

    // Componentes do painel Clientes
    private JTable tabelaClientes;
    private DefaultTableModel modeloTabela;
    private JTextField txtPesquisar;
    private JComboBox<String> comboTipoPesquisa;
    private JButton btnPesquisar, btnEditarCliente, btnEliminarCliente, btnAtualizarTabela;

    public PainelFuncAtendimento(SistemaController controller, String nomeFuncionario, String cargoFuncionario) {
        this.controller = controller;
        this.nomeFuncionario = nomeFuncionario;
        this.cargoFuncionario = cargoFuncionario;
        configurarJanela();
        inicializarComponentes();
    }

    private void configurarJanela() {
        setTitle("Painel do Funcionário - Atendimento");
        setSize(1150, 740);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
    }

    private void inicializarComponentes() {
        // Painel lateral
        menuLateral = new JPanel();
        menuLateral.setBackground(new Color(25, 45, 90));
        menuLateral.setPreferredSize(new Dimension(220, 0));
        menuLateral.setLayout(new GridLayout(11, 1, 0, 6));

        // Informações do funcionário
        JPanel painelInfo = new JPanel(new GridLayout(3, 1));
        painelInfo.setBackground(new Color(20, 40, 80));
        lblFuncionario = new JLabel("<html><center>" + nomeFuncionario + "<br><small>" + cargoFuncionario + "</small></center></html>", SwingConstants.CENTER);
        lblFuncionario.setForeground(Color.WHITE);
        lblFuncionario.setFont(new Font("Segoe UI", Font.BOLD, 13));
        painelInfo.add(new JLabel(""));
        painelInfo.add(lblFuncionario);
        painelInfo.add(new JLabel(""));
        menuLateral.add(painelInfo);

        // Botões laterais
        btnDashboard = criarBotao(" Dashboard");
        btnGerirClientes = criarBotao(" Gerir Clientes");
        btnNovaConta = criarBotao(" Nova Conta");
        btnAtualizarDados = criarBotao(" Atualizar Dados");
        btnEncerrarConta = criarBotao(" Encerrar Conta");
        btnCartoes = criarBotao(" Cartões");
        btnSuporte = criarBotao(" Suporte");
        btnSair = criarBotao(" Sair");

        menuLateral.add(btnDashboard);
        menuLateral.add(btnGerirClientes);
        menuLateral.add(btnNovaConta);
        menuLateral.add(btnAtualizarDados);
        menuLateral.add(btnEncerrarConta);
        menuLateral.add(btnCartoes);
        menuLateral.add(btnSuporte);
        menuLateral.add(btnSair);

        // Painel topo
        painelTopo = new JPanel();
        painelTopo.setBackground(new Color(20, 70, 140));
        painelTopo.setPreferredSize(new Dimension(0, 60));
        painelTopo.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 15));
        lblBanco = new JLabel(" Banco Nexus - Atendimento");
        lblBanco.setForeground(Color.WHITE);
        lblBanco.setFont(new Font("Segoe UI", Font.BOLD, 20));
        painelTopo.add(lblBanco);

        // Painel principal
        painelPrincipal = new JPanel(new CardLayout());
        painelPrincipal.setBackground(Color.WHITE);

        // Cards
        painelPrincipal.add(criarPainelDashboard(), "dashboard");
        painelPrincipal.add(criarPainelGerirClientes(), "gerirClientes");
        painelPrincipal.add(new PainelNovaConta(controller), "novaConta");
        painelPrincipal.add(new PainelAtualizarDados(controller), "atualizarDados");
        painelPrincipal.add(new PainelEncerrarConta(controller), "encerrarConta");
        painelPrincipal.add(new PainelCartoes(controller), "cartoes");
        painelPrincipal.add(new PainelSuporte(controller), "suporte");

        // Ações dos botões
        CardLayout cl = (CardLayout) painelPrincipal.getLayout();
        btnDashboard.addActionListener(e -> cl.show(painelPrincipal, "dashboard"));
        btnGerirClientes.addActionListener(e -> {
            atualizarTabelaClientes();
            cl.show(painelPrincipal, "gerirClientes");
        });
        btnNovaConta.addActionListener(e -> cl.show(painelPrincipal, "novaConta"));
        btnAtualizarDados.addActionListener(e -> cl.show(painelPrincipal, "atualizarDados"));
        btnEncerrarConta.addActionListener(e -> cl.show(painelPrincipal, "encerrarConta"));
        btnCartoes.addActionListener(e -> cl.show(painelPrincipal, "cartoes"));
        btnSuporte.addActionListener(e -> cl.show(painelPrincipal, "suporte"));
        btnSair.addActionListener(e -> {
            int op = JOptionPane.showConfirmDialog(this, "Deseja realmente sair?", "Sair", JOptionPane.YES_NO_OPTION);
            if (op == JOptionPane.YES_OPTION) dispose();
        });

        add(menuLateral, BorderLayout.WEST);
        add(painelTopo, BorderLayout.NORTH);
        add(painelPrincipal, BorderLayout.CENTER);
    }

    private JButton criarBotao(String texto) {
        JButton botao = new JButton(texto);
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 13));
        botao.setFocusPainted(false);
        botao.setBackground(new Color(30, 60, 120));
        botao.setBorderPainted(false);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botao.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { botao.setBackground(new Color(40, 80, 160)); }
            public void mouseExited(MouseEvent e) { botao.setBackground(new Color(30, 60, 120)); }
        });
        return botao;
    }

    private JPanel criarPainelDashboard() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));

        // Painel de estatísticas
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Obter dados do controller
        int totalClientes = controller.listarClientes().size();
        int totalContas = controller.listarContas().size();
        double saldoTotal = calcularSaldoTotal();

        statsPanel.add(criarCardEstatistica("👥 Total Clientes", String.valueOf(totalClientes), new Color(70, 130, 180)));
        statsPanel.add(criarCardEstatistica("🏦 Total Contas", String.valueOf(totalContas), new Color(60, 179, 113)));
        statsPanel.add(criarCardEstatistica("💰 Saldo Total", String.format("%.2f MZN", saldoTotal), new Color(255, 165, 0)));
        statsPanel.add(criarCardEstatistica("📊 Transações", String.valueOf(controller.visualizarTodasTransacoes().size()), new Color(147, 112, 219)));

        JLabel lblTitulo = new JLabel("Bem-vindo ao Painel de Atendimento", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(20, 70, 140));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        painel.add(lblTitulo, BorderLayout.NORTH);
        painel.add(statsPanel, BorderLayout.CENTER);

        return painel;
    }

    private JPanel criarCardEstatistica(String titulo, String valor, Color cor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(cor, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel lblTitulo = new JLabel(titulo, JLabel.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setForeground(Color.DARK_GRAY);
        
        JLabel lblValor = new JLabel(valor, JLabel.CENTER);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblValor.setForeground(cor);
        
        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);
        
        return card;
    }

    private double calcularSaldoTotal() {
        double total = 0;
        for (Conta conta : controller.listarContas()) {
            total += conta.getSaldo();
        }
        return total;
    }

    /**
     * Painel Gerir Clientes
     */
    private JPanel criarPainelGerirClientes() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Topo: título + pesquisa
        JPanel topo = new JPanel(new BorderLayout(8, 8));
        topo.setBackground(Color.WHITE);
        JLabel titulo = new JLabel("👥 Gerir Clientes");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(new Color(20, 70, 140));
        topo.add(titulo, BorderLayout.WEST);

        // Painel de pesquisa
        JPanel painelPesquisa = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        painelPesquisa.setBackground(Color.WHITE);
        comboTipoPesquisa = new JComboBox<>(new String[] {"ID", "Nome", "Nº Conta"});
        comboTipoPesquisa.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPesquisar = new JTextField(20);
        txtPesquisar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnPesquisar = new JButton("🔍 Pesquisar");
        estilizarBotaoAcao(btnPesquisar);

        painelPesquisa.add(comboTipoPesquisa);
        painelPesquisa.add(txtPesquisar);
        painelPesquisa.add(btnPesquisar);
        topo.add(painelPesquisa, BorderLayout.EAST);

        painel.add(topo, BorderLayout.NORTH);

        // Tabela com scroll
        String[] colunas = {"ID", "Nome", "NUIT", "Telefone", "Email", "Status", "Nº Conta"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaClientes = new JTable(modeloTabela);
        tabelaClientes.setRowHeight(26);
        tabelaClientes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabelaClientes.getTableHeader().setBackground(new Color(30, 70, 130));
        tabelaClientes.getTableHeader().setForeground(Color.WHITE);
        tabelaClientes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(tabelaClientes);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        painel.add(scroll, BorderLayout.CENTER);

        // Rodapé: botões de ação
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        rodape.setBackground(Color.WHITE);

        btnEditarCliente = new JButton("✏️ Editar");
        btnEliminarCliente = new JButton("🗑️ Eliminar");
        btnAtualizarTabela = new JButton("🔄 Actualizar");

        Dimension smallBtn = new Dimension(120, 32);
        btnEditarCliente.setPreferredSize(smallBtn);
        btnEliminarCliente.setPreferredSize(smallBtn);
        btnAtualizarTabela.setPreferredSize(smallBtn);

        estilizarBotaoAcao(btnAtualizarTabela);
        btnEditarCliente.setBackground(new Color(20, 120, 20));
        btnEditarCliente.setForeground(Color.WHITE);
        btnEditarCliente.setFocusPainted(false);
        btnEliminarCliente.setBackground(new Color(180, 40, 40));
        btnEliminarCliente.setForeground(Color.WHITE);
        btnEliminarCliente.setFocusPainted(false);

        rodape.add(btnAtualizarTabela);
        rodape.add(btnEditarCliente);
        rodape.add(btnEliminarCliente);

        painel.add(rodape, BorderLayout.SOUTH);

        // Ações
        btnPesquisar.addActionListener(e -> pesquisarEPreencher());
        txtPesquisar.addActionListener(e -> pesquisarEPreencher());
        btnAtualizarTabela.addActionListener(e -> atualizarTabelaClientes());

        btnEliminarCliente.addActionListener(e -> eliminarCliente());
        btnEditarCliente.addActionListener(e -> editarCliente());

        return painel;
    }

    /**
     * Atualiza tabela com todos os clientes
     */
    private void atualizarTabelaClientes() {
        modeloTabela.setRowCount(0);
        try {
            List<Cliente> clientes = controller.listarClientes();
            List<Conta> contas = controller.listarContas();

            for (Cliente c : clientes) {
                String numeroConta = encontrarNumeroConta(c.getIdCliente(), contas);
                
                modeloTabela.addRow(new Object[]{
                    c.getIdCliente(),
                    safeString(c.getNomeCli()),
                    safeString(String.valueOf(c.getNuitCli())),
                    safeString(String.valueOf(c.getTelefoneCli())),
                    safeString(c.getEmailCli()),
                    safeString(c.getStatus().toString()),
                    numeroConta
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes: " + e.getMessage());
        }
    }

    private String encontrarNumeroConta(int idCliente, List<Conta> contas) {
        for (Conta conta : contas) {
            try {
                Object clienteRef = conta.getClienteId();
                if (clienteRef instanceof Cliente) {
                    if (((Cliente) clienteRef).getIdCliente() == idCliente) {
                        return String.valueOf(conta.getNumeroConta());
                    }
                } else if (clienteRef instanceof Integer) {
                    if ((Integer) clienteRef == idCliente) {
                        return String.valueOf(conta.getNumeroConta());
                    }
                }
            } catch (Exception e) {
                // Continua para a próxima conta
            }
        }
        return "N/A";
    }

    /**
     * Pesquisa e preenche tabela com resultados
     */
    private void pesquisarEPreencher() {
        String tipo = (String) comboTipoPesquisa.getSelectedItem();
        String termo = txtPesquisar.getText().trim();
        
        if (termo.isEmpty()) {
            atualizarTabelaClientes();
            return;
        }

        modeloTabela.setRowCount(0);
        try {
            List<Cliente> clientesEncontrados = controller.pesquisarClientes(termo, tipo);
            List<Conta> contas = controller.listarContas();

            for (Cliente c : clientesEncontrados) {
                String numeroConta = encontrarNumeroConta(c.getIdCliente(), contas);
                
                modeloTabela.addRow(new Object[]{
                    c.getIdCliente(),
                    safeString(c.getNomeCli()),
                    safeString(String.valueOf(c.getNuitCli())),
                    safeString(String.valueOf(c.getTelefoneCli())),
                    safeString(c.getEmailCli()),
                    safeString(c.getStatus().toString()),
                    numeroConta
                });
            }

            if (clientesEncontrados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhum cliente encontrado com os critérios de pesquisa.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro na pesquisa: " + e.getMessage());
        }
    }

    private void eliminarCliente() {
        int idx = tabelaClientes.getSelectedRow();
        if (idx == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente da tabela.");
            return;
        }
        
        int id = Integer.parseInt(modeloTabela.getValueAt(idx, 0).toString());
        String nome = modeloTabela.getValueAt(idx, 1).toString();
        
        int op = JOptionPane.showConfirmDialog(this, 
            "Eliminar cliente:\nID: " + id + "\nNome: " + nome + "?", 
            "Confirmar Eliminação", JOptionPane.YES_NO_OPTION);
            
        if (op == JOptionPane.YES_OPTION) {
            boolean ok = controller.eliminarCliente(id);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Cliente eliminado com sucesso.");
                atualizarTabelaClientes();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao eliminar cliente. Verifique se o cliente tem contas associadas.");
            }
        }
    }

    private void editarCliente() {
        int idx = tabelaClientes.getSelectedRow();
        if (idx == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para editar.");
            return;
        }
        
        int id = Integer.parseInt(modeloTabela.getValueAt(idx, 0).toString());
        Cliente cliente = controller.buscarClientePorId(id);
        
        if (cliente == null) {
            JOptionPane.showMessageDialog(this, "Cliente não encontrado.");
            return;
        }

        // Diálogo de edição
        JTextField fNome = new JTextField(cliente.getNomeCli());
        JTextField fEmail = new JTextField(cliente.getEmailCli());
        JTextField fTel = new JTextField(String.valueOf(cliente.getTelefoneCli()));
        
        Object[] msg = {
            "Nome:", fNome,
            "Email:", fEmail,
            "Telefone:", fTel
        };
        
        int op = JOptionPane.showConfirmDialog(this, msg, "Editar Cliente ID " + id, JOptionPane.OK_CANCEL_OPTION);
        if (op == JOptionPane.OK_OPTION) {
            try {
                boolean ok = controller.atualizarDadosCliente(id, fNome.getText(), fEmail.getText(), Integer.parseInt(fTel.getText()));
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Cliente atualizado com sucesso.");
                    atualizarTabelaClientes();
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao atualizar cliente.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Telefone deve conter apenas números.");
            }
        }
    }

    private String safeString(String s) {
        return s == null ? "" : s;
    }

    private void estilizarBotaoAcao(JButton btn) {
        btn.setBackground(new Color(20, 70, 140));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
    }

    // ===================== PAINÉIS INTERNOS =====================

    /**
     * Painel para criar nova conta + tabela com contas existentes.
     */
    public class PainelNovaConta extends JPanel {
        private SistemaController controller;
        private JTextField txtNome, txtNuit, txtEndereco, txtTelefone, txtEmail, txtDocumento;
        private JComboBox<Conta.TipoConta> comboTipoConta;
        private JSpinner spinnerAno, spinnerMes, spinnerDia;
        private DefaultTableModel modeloContas;
        private JTable tabelaContas;

        public PainelNovaConta(SistemaController controller) {
            this.controller = controller;
            initComponents();
            preencherTabelaContas();
        }

        private void initComponents() {
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // Top - formulário
            JPanel topo = new JPanel(new GridBagLayout());
            topo.setBackground(Color.WHITE);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6,6,6,6);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel titulo = new JLabel("🏦 Abrir Nova Conta");
            titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
            titulo.setForeground(new Color(20,70,140));
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            topo.add(titulo, gbc);
            gbc.gridwidth = 1;

            txtNome = new JTextField(18);
            txtNuit = new JTextField(12);
            txtEndereco = new JTextField(18);
            txtTelefone = new JTextField(12);
            txtEmail = new JTextField(18);
            txtDocumento = new JTextField(12);
            comboTipoConta = new JComboBox<>(Conta.TipoConta.values());

            // Componentes para data de nascimento
            JPanel painelData = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            painelData.setBackground(Color.WHITE);
            
            spinnerDia = new JSpinner(new SpinnerNumberModel(1, 1, 31, 1));
            spinnerMes = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
            spinnerAno = new JSpinner(new SpinnerNumberModel(1990, 1900, LocalDate.now().getYear(), 1));
            
            spinnerDia.setPreferredSize(new Dimension(50, 25));
            spinnerMes.setPreferredSize(new Dimension(50, 25));
            spinnerAno.setPreferredSize(new Dimension(60, 25));
            
            painelData.add(new JLabel("Dia:"));
            painelData.add(spinnerDia);
            painelData.add(new JLabel("Mês:"));
            painelData.add(spinnerMes);
            painelData.add(new JLabel("Ano:"));
            painelData.add(spinnerAno);

            int linha = 1;
            gbc.gridx = 0; gbc.gridy = linha; topo.add(new JLabel("Nome:"), gbc);
            gbc.gridx = 1; topo.add(txtNome, gbc); linha++;

            gbc.gridx = 0; gbc.gridy = linha; topo.add(new JLabel("NUIT:"), gbc);
            gbc.gridx = 1; topo.add(txtNuit, gbc); linha++;

            gbc.gridx = 0; gbc.gridy = linha; topo.add(new JLabel("Endereço:"), gbc);
            gbc.gridx = 1; topo.add(txtEndereco, gbc); linha++;

            gbc.gridx = 0; gbc.gridy = linha; topo.add(new JLabel("Telefone:"), gbc);
            gbc.gridx = 1; topo.add(txtTelefone, gbc); linha++;

            gbc.gridx = 0; gbc.gridy = linha; topo.add(new JLabel("Email:"), gbc);
            gbc.gridx = 1; topo.add(txtEmail, gbc); linha++;

            gbc.gridx = 0; gbc.gridy = linha; topo.add(new JLabel("Documento (BI):"), gbc);
            gbc.gridx = 1; topo.add(txtDocumento, gbc); linha++;

            gbc.gridx = 0; gbc.gridy = linha; topo.add(new JLabel("Data de Nascimento:"), gbc);
            gbc.gridx = 1; topo.add(painelData, gbc); linha++;

            gbc.gridx = 0; gbc.gridy = linha; topo.add(new JLabel("Tipo de Conta:"), gbc);
            gbc.gridx = 1; topo.add(comboTipoConta, gbc); linha++;

            JButton btnAbrir = new JButton("✅ Abrir Conta");
            btnAbrir.setBackground(new Color(20,120,20));
            btnAbrir.setForeground(Color.WHITE);
            btnAbrir.setPreferredSize(new Dimension(160, 34));

            gbc.gridx = 1; gbc.gridy = linha; gbc.anchor = GridBagConstraints.CENTER;
            topo.add(btnAbrir, gbc);

            add(topo, BorderLayout.NORTH);

            // Centro - tabela de contas
            String[] col = {"ID Conta","Nº Conta","Tipo","Saldo","Status","Cliente", "NUIB", "NIB"};
            modeloContas = new DefaultTableModel(col,0) {
                @Override public boolean isCellEditable(int r,int c){return false;}
            };
            tabelaContas = new JTable(modeloContas);
            tabelaContas.setRowHeight(24);
            JScrollPane scroll = new JScrollPane(tabelaContas);
            scroll.setPreferredSize(new Dimension(800, 300));
            add(scroll, BorderLayout.CENTER);

            // Rodapé - actions
            JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            rodape.setBackground(Color.WHITE);
            JButton btnListar = new JButton("🔄 Actualizar Lista");
            JButton btnVerHistorico = new JButton("📊 Ver Histórico Conta");
            btnListar.setBackground(new Color(0,120,200)); btnListar.setForeground(Color.WHITE);
            btnVerHistorico.setBackground(new Color(0,120,200)); btnVerHistorico.setForeground(Color.WHITE);
            rodape.add(btnVerHistorico);
            rodape.add(btnListar);
            add(rodape, BorderLayout.SOUTH);

            // Ações
            btnAbrir.addActionListener(e -> {
                try {
                    String nome = txtNome.getText().trim();
                    int nuit = Integer.parseInt(txtNuit.getText().trim());
                    String endereco = txtEndereco.getText().trim();
                    int telefone = Integer.parseInt(txtTelefone.getText().trim());
                    String email = txtEmail.getText().trim();
                    String documento = txtDocumento.getText().trim();
                    Conta.TipoConta tipo = (Conta.TipoConta) comboTipoConta.getSelectedItem();
                    
                    // Obter data de nascimento
                    int dia = (Integer) spinnerDia.getValue();
                    int mes = (Integer) spinnerMes.getValue();
                    int ano = (Integer) spinnerAno.getValue();
                    
                    LocalDate dataNascimento = LocalDate.of(ano, mes, dia);
                    
                    // Validar idade (maior de 18 anos)
                    LocalDate hoje = LocalDate.now();
                    int idade = Period.between(dataNascimento, hoje).getYears();
                    
                    if (idade < 18) {
                        JOptionPane.showMessageDialog(this, 
                            "❌ Cliente deve ter pelo menos 18 anos.\n" +
                            "Idade calculada: " + idade + " anos.\n" +
                            "Data de nascimento: " + dataNascimento, 
                            "Idade Insuficiente", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    // Chamar o método do controller para abrir conta
                    Conta c = controller.abrirContaCliente(nome, nuit, endereco, telefone, email, documento, tipo);
                    if (c != null) {
                        // Obter NUIB e NIB da conta criada
                        String nuib = String.valueOf(c.getNiubConta());
                        String nib = String.valueOf(c.getNib());
                        
                        JOptionPane.showMessageDialog(this, 
                            "✅ Conta criada com sucesso!\n" +
                            "ID: " + c.getIdConta() + "\n" +
                            "Nº Conta: " + c.getNumeroConta() + "\n" +
                            "NUIB: " + nuib + "\n" +
                            "NIB: " + nib,
                            "Conta Criada",
                            JOptionPane.INFORMATION_MESSAGE);
                            
                        limparFormulario();
                        preencherTabelaContas();
                    } else {
                        JOptionPane.showMessageDialog(this, "❌ Erro ao criar conta.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "❌ Campos numéricos inválidos (NUIT/Telefone).");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "❌ Erro: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });

            btnListar.addActionListener(e -> preencherTabelaContas());

            btnVerHistorico.addActionListener(e -> {
                int sel = tabelaContas.getSelectedRow();
                if (sel == -1) { 
                    JOptionPane.showMessageDialog(this, "❌ Selecione uma conta."); 
                    return; 
                }
                int idConta = Integer.parseInt(modeloContas.getValueAt(sel,0).toString());
                List<Transacoes> historico = controller.consultarHistorico(idConta);
                
                // Mostrar histórico em uma nova janela
                mostrarHistoricoTransacoes(historico, idConta);
            });
        }

        private void mostrarHistoricoTransacoes(List<Transacoes> historico, int idConta) {
            JFrame frameHistorico = new JFrame("Histórico da Conta " + idConta);
            frameHistorico.setSize(600, 400);
            frameHistorico.setLocationRelativeTo(this);
            
            String[] colunas = {"Data", "Tipo", "Valor", "Descrição"};
            DefaultTableModel modelo = new DefaultTableModel(colunas, 0);
            JTable tabela = new JTable(modelo);
            
            for (Transacoes t : historico) {
                modelo.addRow(new Object[]{
                    t.getData().toString(),
                    t.getCategoria(),
                    String.format("%.2f MZN", t.getValor()),
                    t.getDescricaoTrancacao()
                });
            }
            
            frameHistorico.add(new JScrollPane(tabela));
            frameHistorico.setVisible(true);
        }

        private void limparFormulario() {
            txtNome.setText("");
            txtNuit.setText("");
            txtEndereco.setText("");
            txtTelefone.setText("");
            txtEmail.setText("");
            txtDocumento.setText("");
            comboTipoConta.setSelectedIndex(0);
            spinnerDia.setValue(1);
            spinnerMes.setValue(1);
            spinnerAno.setValue(1990);
        }

        private void preencherTabelaContas() {
            modeloContas.setRowCount(0);
            List<Conta> contas = controller.listarContas();
            for (Conta c : contas) {
                String clienteNome = tryGetClienteNome(c);
                modeloContas.addRow(new Object[]{
                        c.getIdConta(),
                        c.getNumeroConta(),
                        c.getTipoConta(),
                        String.format("%.2f MZN", c.getSaldo()),
                        c.getStatus(),
                        clienteNome,
                        c.getNiubConta(),
                        c.getNib()
                });
            }
        }

        // tenta recuperar nome do cliente
        private String tryGetClienteNome(Conta c) {
            try {
                Cliente cli = null;
                try {
                    Object possible = c.getClienteId();
                    if (possible instanceof Cliente) cli = (Cliente) possible;
                    else {
                        // se getClienteId devolve int, procurar
                        int cid = Integer.parseInt(String.valueOf(possible));
                        cli = controller.buscarClientePorId(cid);
                    }
                } catch (Exception ex) {
                    // tenta método getCliente
                    try {
                        java.lang.reflect.Method m = c.getClass().getMethod("getCliente");
                        Object ret = m.invoke(c);
                        if (ret instanceof Cliente) cli = (Cliente) ret;
                    } catch (Exception ignored){}
                }
                return (cli != null) ? cli.getNomeCli() : "";
            } catch (Exception ex) {
                return "";
            }
        }
    }

    public class PainelEncerrarConta extends JPanel {
        private SistemaController controller;
        private DefaultTableModel modeloContas;
        private JTable tabelaContas;
        private JTextField txtIdEncerrar;

        public PainelEncerrarConta(SistemaController controller) {
            this.controller = controller;
            initComponents();
            preencherTabela();
        }

        private void initComponents() {
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(16,16,16,16));

            JLabel titulo = new JLabel("🔒 Encerrar Conta");
            titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
            titulo.setForeground(new Color(20,70,140));
            add(titulo, BorderLayout.NORTH);

            // tabela
            String[] cols = {"ID Conta","Nº Conta","Tipo","Saldo","Status"};
            modeloContas = new DefaultTableModel(cols,0) { 
                @Override public boolean isCellEditable(int r,int c){return false;} 
            };
            tabelaContas = new JTable(modeloContas);
            tabelaContas.setRowHeight(24);
            add(new JScrollPane(tabelaContas), BorderLayout.CENTER);

            // rodape com actions
            JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            rodape.setBackground(Color.WHITE);

            JButton btnListar = new JButton("🔄 Actualizar Lista");
            JButton btnEncerrarSel = new JButton("🗑️ Encerrar Selecionada");
            JButton btnEncerrarById = new JButton("🗑️ Encerrar por ID");

            txtIdEncerrar = new JTextField(8);

            btnListar.setBackground(new Color(0,120,200)); 
            btnListar.setForeground(Color.WHITE);
            btnEncerrarSel.setBackground(new Color(180,40,40)); 
            btnEncerrarSel.setForeground(Color.WHITE);
            btnEncerrarById.setBackground(new Color(180,40,40)); 
            btnEncerrarById.setForeground(Color.WHITE);

            rodape.add(new JLabel("ID:"));
            rodape.add(txtIdEncerrar);
            rodape.add(btnEncerrarById);
            rodape.add(btnEncerrarSel);
            rodape.add(btnListar);

            add(rodape, BorderLayout.SOUTH);

            // ações
            btnListar.addActionListener(e -> preencherTabela());

            btnEncerrarSel.addActionListener(e -> {
                int sel = tabelaContas.getSelectedRow();
                if (sel == -1) { 
                    JOptionPane.showMessageDialog(this, "❌ Selecione uma conta."); 
                    return; 
                }
                int idConta = Integer.parseInt(modeloContas.getValueAt(sel,0).toString());
                String numeroConta = modeloContas.getValueAt(sel,1).toString();
                
                int op = JOptionPane.showConfirmDialog(this, 
                    "Encerrar conta?\nID: " + idConta + "\nNº: " + numeroConta, 
                    "Confirmar", JOptionPane.YES_NO_OPTION);
                    
                if (op == JOptionPane.YES_OPTION) {
                    boolean ok = controller.encerrarContaCliente(idConta);
                    JOptionPane.showMessageDialog(this, ok ? "✅ Conta encerrada." : "❌ Falha ao encerrar conta.");
                    preencherTabela();
                }
            });

            btnEncerrarById.addActionListener(e -> {
                try {
                    int id = Integer.parseInt(txtIdEncerrar.getText().trim());
                    int op = JOptionPane.showConfirmDialog(this, "Encerrar conta ID " + id + " ?", "Confirmar", JOptionPane.YES_NO_OPTION);
                    if (op == JOptionPane.YES_OPTION) {
                        boolean ok = controller.encerrarContaCliente(id);
                        JOptionPane.showMessageDialog(this, ok ? "✅ Conta encerrada." : "❌ Falha ao encerrar conta.");
                        preencherTabela();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "❌ ID inválido.");
                }
            });
        }

        private void preencherTabela() {
            modeloContas.setRowCount(0);
            List<Conta> contas = controller.listarContas();
            for (Conta c : contas) {
                modeloContas.addRow(new Object[]{
                        c.getIdConta(),
                        c.getNumeroConta(),
                        c.getTipoConta(),
                        String.format("%.2f MZN", c.getSaldo()),
                        c.getStatus()
                });
            }
        }
    }

    public class PainelCartoes extends JPanel {
        private SistemaController controller;
        private DefaultTableModel modeloContas;
        private JTable tabelaContas;
        private JTextField txtIdCartao;

        public PainelCartoes(SistemaController controller) {
            this.controller = controller;
            initComponents();
            preencherTabela();
        }

        private void initComponents() {
            setLayout(new BorderLayout(8,8));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(16,16,16,16));

            JLabel titulo = new JLabel("💳 Cartões - Reemissão");
            titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
            titulo.setForeground(new Color(20,70,140));
            add(titulo, BorderLayout.NORTH);

            String[] cols = {"ID Conta","Nº Conta","Cliente","Saldo","Status"};
            modeloContas = new DefaultTableModel(cols,0) { 
                @Override public boolean isCellEditable(int r,int c){return false;} 
            };
            tabelaContas = new JTable(modeloContas);
            tabelaContas.setRowHeight(24);
            add(new JScrollPane(tabelaContas), BorderLayout.CENTER);

            JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            rodape.setBackground(Color.WHITE);
            txtIdCartao = new JTextField(8);
            JButton btnReemitir = new JButton("🔄 Reemitir (ID)");
            JButton btnReemitirSel = new JButton("🔄 Reemitir (Selecionada)");
            JButton btnList = new JButton("🔄 Actualizar Lista");

            btnReemitir.setBackground(new Color(20,70,140)); 
            btnReemitir.setForeground(Color.WHITE);
            btnReemitirSel.setBackground(new Color(20,70,140)); 
            btnReemitirSel.setForeground(Color.WHITE);
            btnList.setBackground(new Color(0,120,200)); 
            btnList.setForeground(Color.WHITE);

            rodape.add(new JLabel("ID:"));
            rodape.add(txtIdCartao);
            rodape.add(btnReemitir);
            rodape.add(btnReemitirSel);
            rodape.add(btnList);
            add(rodape, BorderLayout.SOUTH);

            btnList.addActionListener(e -> preencherTabela());

            btnReemitir.addActionListener(e -> {
                try {
                    int id = Integer.parseInt(txtIdCartao.getText().trim());
                    String r = controller.reemitirCartao(id);
                    JOptionPane.showMessageDialog(this, r);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "❌ ID inválido.");
                }
            });

            btnReemitirSel.addActionListener(e -> {
                int sel = tabelaContas.getSelectedRow();
                if (sel == -1) { 
                    JOptionPane.showMessageDialog(this, "❌ Selecione uma conta."); 
                    return; 
                }
                int id = Integer.parseInt(modeloContas.getValueAt(sel,0).toString());
                String r = controller.reemitirCartao(id);
                JOptionPane.showMessageDialog(this, r);
            });
        }

        private void preencherTabela() {
            modeloContas.setRowCount(0);
            List<Conta> contas = controller.listarContas();
            for (Conta c : contas) {
                String cliente = "";
                try {
                    Object clienteRef = c.getClienteId();
                    if (clienteRef instanceof Integer) {
                        int cid = (Integer) clienteRef;
                        cliente = controller.buscarClientePorId(cid) != null ? controller.buscarClientePorId(cid).getNomeCli() : "";
                    } else if (clienteRef instanceof model.Cliente) {
                        cliente = ((model.Cliente) clienteRef).getNomeCli();
                    }
                } catch (Exception ignore) {}
                modeloContas.addRow(new Object[]{
                        c.getIdConta(),
                        c.getNumeroConta(),
                        cliente,
                        String.format("%.2f MZN", c.getSaldo()),
                        c.getStatus()
                });
            }
        }
    }

    public class PainelAtualizarDados extends JPanel {
        private SistemaController controller;
        private DefaultTableModel modeloClientes;
        private JTable tabelaClientes;

        public PainelAtualizarDados(SistemaController controller) {
            this.controller = controller;
            initComponents();
            preencherTabela();
        }

        private void initComponents() {
            setLayout(new BorderLayout(8,8));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(16,16,16,16));

            JLabel titulo = new JLabel("✏️ Atualizar Dados dos Clientes");
            titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
            titulo.setForeground(new Color(20,70,140));
            add(titulo, BorderLayout.NORTH);

            String[] cols = {"ID","Nome","NUIT","Telefone","Email"};
            modeloClientes = new DefaultTableModel(cols,0) { 
                @Override public boolean isCellEditable(int r,int c){return false;} 
            };
            tabelaClientes = new JTable(modeloClientes);
            tabelaClientes.setRowHeight(24);
            add(new JScrollPane(tabelaClientes), BorderLayout.CENTER);

            JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            rodape.setBackground(Color.WHITE);
            JButton btnAtualizar = new JButton("🔄 Actualizar Lista");
            JButton btnEditar = new JButton("✏️ Editar Selecionado");
            JButton btnVer = new JButton("👁️ Ver Detalhes");

            btnAtualizar.setBackground(new Color(0,120,200)); 
            btnAtualizar.setForeground(Color.WHITE);
            btnEditar.setBackground(new Color(20,70,140)); 
            btnEditar.setForeground(Color.WHITE);
            btnVer.setBackground(new Color(20,70,140)); 
            btnVer.setForeground(Color.WHITE);

            rodape.add(btnVer);
            rodape.add(btnEditar);
            rodape.add(btnAtualizar);
            add(rodape, BorderLayout.SOUTH);

            btnAtualizar.addActionListener(e -> preencherTabela());

            btnVer.addActionListener(e -> {
                int sel = tabelaClientes.getSelectedRow();
                if (sel == -1) { 
                    JOptionPane.showMessageDialog(this, "❌ Selecione um cliente."); 
                    return; 
                }
                int id = Integer.parseInt(modeloClientes.getValueAt(sel,0).toString());
                Cliente c = controller.buscarClientePorId(id);
                if (c != null) {
                    String msg = "📋 Detalhes do Cliente:\n\n" +
                                "ID: " + c.getIdCliente() + "\n" +
                                "Nome: " + c.getNomeCli() + "\n" +
                                "NUIT: " + c.getNuitCli() + "\n" +
                                "Email: " + c.getEmailCli() + "\n" +
                                "Telefone: " + c.getTelefoneCli() + "\n" +
                                "Endereço: " + c.getEnderecoCli() + "\n" +
                                "Documento: " + c.getDocumentoCli() + "\n" +
                                "Status: " + c.getStatus();
                    JOptionPane.showMessageDialog(this, msg, "Detalhes Cliente", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Cliente não encontrado.");
                }
            });

            btnEditar.addActionListener(e -> {
                int sel = tabelaClientes.getSelectedRow();
                if (sel == -1) { 
                    JOptionPane.showMessageDialog(this, "❌ Selecione um cliente."); 
                    return; 
                }
                int id = Integer.parseInt(modeloClientes.getValueAt(sel,0).toString());
                Cliente c = controller.buscarClientePorId(id);
                if (c == null) { 
                    JOptionPane.showMessageDialog(this, "❌ Cliente não encontrado."); 
                    return; 
                }

                JTextField fNome = new JTextField(c.getNomeCli());
                JTextField fEmail = new JTextField(c.getEmailCli());
                JTextField fTel = new JTextField(String.valueOf(c.getTelefoneCli()));
                Object[] msg = {"Nome:", fNome, "Email:", fEmail, "Telefone:", fTel};
                int op = JOptionPane.showConfirmDialog(this, msg, "Editar Cliente ID " + id, JOptionPane.OK_CANCEL_OPTION);
                if (op == JOptionPane.OK_OPTION) {
                    try {
                        boolean ok = controller.atualizarDadosCliente(id, fNome.getText().trim(), fEmail.getText().trim(), Integer.parseInt(fTel.getText().trim()));
                        JOptionPane.showMessageDialog(this, ok ? " Atualizado com sucesso." : " Falha ao atualizar.");
                        preencherTabela();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "❌ Telefone inválido.");
                    }
                }
            });
        }

        private void preencherTabela() {
            modeloClientes.setRowCount(0);
            List<Cliente> clientes = controller.listarClientes();
            for (Cliente c : clientes) {
                modeloClientes.addRow(new Object[]{
                        c.getIdCliente(),
                        c.getNomeCli(),
                        c.getNuitCli(),
                        c.getTelefoneCli(),
                        c.getEmailCli()
                });
            }
        }
    }

    class PainelSuporte extends JPanel {
        private SistemaController controller;
        private JTextField txtIdCliente, txtDescricao;
        private JTextArea areaLog;

        public PainelSuporte(SistemaController controller) {
            this.controller = controller;
            initComponents();
        }

        private void initComponents() {
            setLayout(new BorderLayout(10, 10));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JLabel titulo = new JLabel("📞 Suporte ao Cliente");
            titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
            titulo.setForeground(new Color(20, 70, 140));
            add(titulo, BorderLayout.NORTH);

            // Painel de formulário
            JPanel painelForm = new JPanel(new GridBagLayout());
            painelForm.setBackground(Color.WHITE);
            painelForm.setBorder(BorderFactory.createTitledBorder("Registrar Problema"));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            txtIdCliente = new JTextField(10);
            txtDescricao = new JTextField(20);
            JButton btnRegistrar = new JButton("📝 Registrar Suporte");
            btnRegistrar.setBackground(new Color(20, 70, 140));
            btnRegistrar.setForeground(Color.WHITE);

            gbc.gridx = 0; gbc.gridy = 0;
            painelForm.add(new JLabel("ID Cliente:"), gbc);
            gbc.gridx = 1;
            painelForm.add(txtIdCliente, gbc);

            gbc.gridx = 0; gbc.gridy = 1;
            painelForm.add(new JLabel("Descrição:"), gbc);
            gbc.gridx = 1;
            painelForm.add(txtDescricao, gbc);

            gbc.gridx = 1; gbc.gridy = 2;
            painelForm.add(btnRegistrar, gbc);

            add(painelForm, BorderLayout.CENTER);

            // Área de log
            areaLog = new JTextArea(10, 40);
            areaLog.setEditable(false);
            areaLog.setText("📋 Histórico de Suporte:\n\n");
            JScrollPane scrollLog = new JScrollPane(areaLog);
            scrollLog.setBorder(BorderFactory.createTitledBorder("Histórico"));
            add(scrollLog, BorderLayout.SOUTH);

            // Ação do botão
            btnRegistrar.addActionListener(e -> {
                try {
                    int idCliente = Integer.parseInt(txtIdCliente.getText().trim());
                    String descricao = txtDescricao.getText().trim();
                    
                    if (descricao.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "❌ Digite uma descrição para o problema.");
                        return;
                    }

                    String resultado = controller.fornecerSuporte(idCliente, descricao);
                    areaLog.append("✅ " + resultado + "\n");
                    
                    txtIdCliente.setText("");
                    txtDescricao.setText("");
                    
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "❌ ID do cliente inválido.");
                }
            });
        }
    }

    // Main para testar
    public static void main(String[] args) {
        SistemaController sc = new SistemaController();
        
        SwingUtilities.invokeLater(() -> {
            PainelFuncAtendimento p = new PainelFuncAtendimento(sc, "Frederico", "Atendimento");
            p.setVisible(true);
        });
    }
}