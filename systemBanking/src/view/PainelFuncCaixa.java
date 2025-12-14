package view;

import controller.SistemaController;
import model.Transacoes;
import model.Conta;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PainelFuncCaixa extends JFrame {

    private JPanel menuLateral, areaPrincipal, header;
    private CardLayout cardLayout;
    private SistemaController sistema;

    // Campos de depósito e saque
    private JTextField txtIdContaDeposito, txtValorDeposito, txtReferenciaDeposito, txtEntidadeDeposito;
    private JTextField txtIdContaSaque, txtNumContaSaque, txtValorSaque, txtReferenciaSaque, txtEntidadeSaque;

    // Tabelas e modelos
    private JTable tabelaDepositos;
    private DefaultTableModel modeloDepositos;
    private JTable tabelaSaques;
    private DefaultTableModel modeloSaques;

    // Labels do dashboard
    private JLabel lblSaldoVal, lblTotalDepositosVal, lblTotalSaquesVal;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public PainelFuncCaixa(SistemaController sistema, String funcionario, String cargo, String banco) {
        this.sistema = sistema;

        setTitle("Sistema Bancário - " + banco);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ============ CABEÇALHO ============
        header = new JPanel();
        header.setBackground(new Color(0, 51, 102));
        header.setPreferredSize(new Dimension(1000, 60));
        header.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 18));

        JLabel lblBanco = new JLabel(banco.toUpperCase());
        lblBanco.setForeground(Color.WHITE);
        lblBanco.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.add(lblBanco);
        add(header, BorderLayout.NORTH);

        // ============ MENU LATERAL ============
        menuLateral = new JPanel();
        menuLateral.setBackground(new Color(0, 102, 204));
        menuLateral.setPreferredSize(new Dimension(240, 0));
        menuLateral.setLayout(new BorderLayout());

        JPanel infoFuncionario = new JPanel(new GridLayout(3, 1));
        infoFuncionario.setBackground(new Color(0, 102, 204));
        infoFuncionario.setBorder(BorderFactory.createEmptyBorder(18, 14, 18, 14));

        JLabel lblFuncionario = new JLabel("<html><span style='color:white'>Funcionário: " + funcionario + "</span></html>");
        lblFuncionario.setForeground(Color.WHITE);
        lblFuncionario.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel lblCargo = new JLabel("<html><span style='color:white'>Cargo: " + cargo + "</span></html>");
        lblCargo.setForeground(Color.WHITE);
        lblCargo.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        infoFuncionario.add(new JLabel(" ", SwingConstants.CENTER));
        infoFuncionario.add(lblFuncionario);
        infoFuncionario.add(lblCargo);
        menuLateral.add(infoFuncionario, BorderLayout.NORTH);

        // Botões do menu
        JPanel botoesMenu = new JPanel(new GridLayout(6, 1, 8, 8));
        botoesMenu.setBackground(new Color(0, 102, 204));
        botoesMenu.setBorder(BorderFactory.createEmptyBorder(20, 14, 20, 14));

        JButton btnDashboard = criarBotaoMenu(" Dashboard");
        JButton btnDepositos = criarBotaoMenu(" Depósitos");
        JButton btnSaques = criarBotaoMenu(" Saques");
        JButton btnRelatorios = criarBotaoMenu(" Relatórios");
        JButton btnSair = criarBotaoMenu(" Sair");

        botoesMenu.add(btnDashboard);
        botoesMenu.add(btnDepositos);
        botoesMenu.add(btnSaques);
        botoesMenu.add(btnRelatorios);
        botoesMenu.add(btnSair);
        menuLateral.add(botoesMenu, BorderLayout.CENTER);
        add(menuLateral, BorderLayout.WEST);

        // ============ ÁREA PRINCIPAL ============
        cardLayout = new CardLayout();
        areaPrincipal = new JPanel(cardLayout);
        areaPrincipal.setBackground(new Color(235, 242, 248));

        // Painéis
        areaPrincipal.add(criarPainelDashboard(), "Dashboard");
        areaPrincipal.add(criarPainelDepositos(), "Depositos");
        areaPrincipal.add(criarPainelSaques(), "Saques");
        areaPrincipal.add(criarPainelRelatorios(), "Relatorios");
        add(areaPrincipal, BorderLayout.CENTER);

        // Eventos dos botões
        btnDashboard.addActionListener(e -> {
            atualizarDashboard();
            cardLayout.show(areaPrincipal, "Dashboard");
        });
        btnDepositos.addActionListener(e -> {
            atualizarTabelaDepositos();
            cardLayout.show(areaPrincipal, "Depositos");
        });
        btnSaques.addActionListener(e -> {
            atualizarTabelaSaques();
            cardLayout.show(areaPrincipal, "Saques");
        });
        btnRelatorios.addActionListener(e -> {
            atualizarDashboard();
            cardLayout.show(areaPrincipal, "Relatorios");
        });
        btnSair.addActionListener(e -> {
            int resp = JOptionPane.showConfirmDialog(this,
                    "Deseja realmente sair?", "Confirmação", JOptionPane.YES_NO_OPTION);
            if (resp == JOptionPane.YES_OPTION) dispose();
        });

        cardLayout.show(areaPrincipal, "Dashboard");
    }

    // ===================== DASHBOARD =====================
    private JPanel criarPainelDashboard() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(new Color(235, 242, 248));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitulo = new JLabel(" Dashboard do Caixa ", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(0, 51, 102));

        JPanel dados = new JPanel(new GridLayout(1, 3, 20, 20));
        dados.setBackground(new Color(235, 242, 248));
        dados.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JPanel p1 = criarCartaoResumo("Saldo total", "0.00 MZN");
        JPanel p2 = criarCartaoResumo("Total Depósitos", "0.00 MZN");
        JPanel p3 = criarCartaoResumo("Total Saques", "0.00 MZN");

        lblSaldoVal = (JLabel) p1.getClientProperty("valueLabel");
        lblTotalDepositosVal = (JLabel) p2.getClientProperty("valueLabel");
        lblTotalSaquesVal = (JLabel) p3.getClientProperty("valueLabel");

        dados.add(p1);
        dados.add(p2);
        dados.add(p3);

        painel.add(lblTitulo, BorderLayout.NORTH);
        painel.add(dados, BorderLayout.CENTER);

        atualizarDashboard();
        return painel;
    }

    private JPanel criarCartaoResumo(String titulo, String valorInicial) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 220)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        JLabel t = new JLabel(titulo, SwingConstants.LEFT);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setForeground(new Color(0, 51, 102));
        JLabel v = new JLabel(valorInicial, SwingConstants.CENTER);
        v.setFont(new Font("Segoe UI", Font.BOLD, 18));
        v.setForeground(new Color(0, 102, 204));

        card.add(t, BorderLayout.NORTH);
        card.add(v, BorderLayout.CENTER);
        card.putClientProperty("valueLabel", v);
        return card;
    }

    private void atualizarDashboard() {
        try {
            lblSaldoVal.setText(String.format("%.2f MZN", sistema.calcularSaldoTotal()));
            lblTotalDepositosVal.setText(String.format("%.2f MZN", sistema.getTotalDepositos()));
            lblTotalSaquesVal.setText(String.format("%.2f MZN", sistema.getTotalSaques()));
        } catch (Exception e) {
            lblSaldoVal.setText("Erro");
            lblTotalDepositosVal.setText("Erro");
            lblTotalSaquesVal.setText("Erro");
        }
    }

    // ===================== DEPÓSITOS =====================
    private JPanel criarPainelDepositos() {
        JPanel painel = new JPanel(new BorderLayout(12, 12));
        painel.setBackground(new Color(235, 242, 248));
        painel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // ========== TOPO ==========
        JPanel topo = new JPanel(new GridBagLayout());
        topo.setBackground(new Color(235, 242, 248));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("💰 Registrar Depósito");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(0, 51, 102));

        txtIdContaDeposito = new JTextField(12);
        txtValorDeposito = new JTextField(12);
        txtReferenciaDeposito = new JTextField(12);
        txtEntidadeDeposito = new JTextField(12);

        JButton btnDepositar = new JButton("Confirmar Depósito");
        JButton btnListar = new JButton("Listar Depósitos");
        JButton btnLimpar = new JButton("Limpar Campos");

        estilizarBotaoAcao(btnDepositar);
        estilizarBotaoSecundario(btnListar);
        estilizarBotaoSecundario(btnLimpar);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        topo.add(lblTitulo, gbc);

        gbc.gridwidth = 1; gbc.gridy++;
        topo.add(new JLabel("ID da Conta:"), gbc);
        gbc.gridx = 1;
        topo.add(txtIdContaDeposito, gbc);

        gbc.gridx = 0; gbc.gridy++;
        topo.add(new JLabel("Valor (MZN):"), gbc);
        gbc.gridx = 1;
        topo.add(txtValorDeposito, gbc);

        gbc.gridx = 0; gbc.gridy++;
        topo.add(new JLabel("Referência:"), gbc);
        gbc.gridx = 1;
        topo.add(txtReferenciaDeposito, gbc);

        gbc.gridx = 0; gbc.gridy++;
        topo.add(new JLabel("Entidade:"), gbc);
        gbc.gridx = 1;
        topo.add(txtEntidadeDeposito, gbc);

        gbc.gridx = 0; gbc.gridy++;
        topo.add(btnDepositar, gbc);
        gbc.gridx = 1;
        topo.add(btnLimpar, gbc);
        gbc.gridx = 0; gbc.gridy++;
        topo.add(btnListar, gbc);

        painel.add(topo, BorderLayout.NORTH);

        // ========== TABELA ==========
        String[] col = {"ID Transação", "ID Conta", "Valor", "Data", "Referência", "Entidade", "Descrição"};
        modeloDepositos = new DefaultTableModel(col, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaDepositos = new JTable(modeloDepositos);
        tabelaDepositos.setRowHeight(24);
        tabelaDepositos.getTableHeader().setReorderingAllowed(false);
        JScrollPane scroll = new JScrollPane(tabelaDepositos);
        scroll.setPreferredSize(new Dimension(700, 240));
        painel.add(scroll, BorderLayout.CENTER);

        // ========== RODAPÉ ==========
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        rodape.setBackground(new Color(235, 242, 248));
        JButton btnAtualizar = new JButton("Actualizar Tabela");
        estilizarBotaoSecundario(btnAtualizar);
        rodape.add(btnAtualizar);
        painel.add(rodape, BorderLayout.SOUTH);

        // ========== AÇÕES ==========
        btnDepositar.addActionListener(e -> processarDeposito());
        btnListar.addActionListener(e -> atualizarTabelaDepositos());
        btnLimpar.addActionListener(e -> limparCamposDeposito());
        btnAtualizar.addActionListener(e -> atualizarTabelaDepositos());

        return painel;
    }

    private void processarDeposito() {
        try {
            Integer idConta = txtIdContaDeposito.getText().trim().isEmpty() ? null : 
                            Integer.parseInt(txtIdContaDeposito.getText().trim());
            Integer numeroConta = null; // Não usado no depósito flexível
            Double valor = txtValorDeposito.getText().trim().isEmpty() ? null : 
                         Double.parseDouble(txtValorDeposito.getText().trim());
            String referencia = txtReferenciaDeposito.getText().trim();
            String entidade = txtEntidadeDeposito.getText().trim();

            if (valor == null || valor <= 0) {
                JOptionPane.showMessageDialog(this, "O valor deve ser maior que zero.");
                return;
            }

            if (idConta == null && numeroConta == null) {
                JOptionPane.showMessageDialog(this, "Informe pelo menos ID ou Número da Conta.");
                return;
            }

            // Usa método flexível do SistemaController
            boolean sucesso = sistema.registrarDepositoFlexivel(idConta, numeroConta, valor, referencia, entidade);

            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Depósito realizado com sucesso!");
                atualizarTabelaDepositos();
                atualizarDashboard();
                limparCamposDeposito();
            } else {
                JOptionPane.showMessageDialog(this, "Erro: conta não encontrada ou dados inválidos.");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Dados inválidos! Verifica o ID da conta e o valor.");
        }
    }

    private void limparCamposDeposito() {
        txtIdContaDeposito.setText("");
        txtValorDeposito.setText("");
        txtReferenciaDeposito.setText("");
        txtEntidadeDeposito.setText("");
    }

    private void atualizarTabelaDepositos() {
        modeloDepositos.setRowCount(0);
        try {
            List<Transacoes> lista = sistema.listarDepositos();
            for (Transacoes t : lista) {
                String data = t.getData() != null ? t.getData().format(dtf) : "";
                String referencia = t.getReferencia() != null ? t.getReferencia() : "";
                String entidade = t.getEntidade() != null ? t.getEntidade() : "";
                
                modeloDepositos.addRow(new Object[]{
                    t.getId(),
                    t.getConta() != null ? t.getConta().getIdConta() : "N/A",
                    String.format("%.2f MZN", t.getValor()),
                    data,
                    referencia,
                    entidade,
                    t.getCategoria()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar depósitos: " + e.getMessage());
        }
    }

    // ===================== SAQUES =====================
    private JPanel criarPainelSaques() {
        JPanel painel = new JPanel(new BorderLayout(12, 12));
        painel.setBackground(new Color(235, 242, 248));
        painel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Top - formulário e ações
        JPanel topo = new JPanel(new GridBagLayout());
        topo.setBackground(new Color(235, 242, 248));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("💸 Registrar Saque");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(0, 51, 102));

        txtIdContaSaque = new JTextField(12);
        txtNumContaSaque = new JTextField(12);
        txtValorSaque = new JTextField(12);
        txtReferenciaSaque = new JTextField(12);
        txtEntidadeSaque = new JTextField(12);
        
        JButton btnSacar = new JButton("Confirmar Saque");
        JButton btnListar = new JButton("Listar Saques");
        JButton btnLimpar = new JButton("Limpar Campos");

        estilizarBotaoAcao(btnSacar);
        estilizarBotaoSecundario(btnListar);
        estilizarBotaoSecundario(btnLimpar);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        topo.add(lblTitulo, gbc);
        gbc.gridwidth = 1;
        gbc.gridy++;
        topo.add(new JLabel("ID da Conta:"), gbc);
        gbc.gridx = 1;
        topo.add(txtIdContaSaque, gbc);
        gbc.gridx = 0; gbc.gridy++;
        topo.add(new JLabel("Número da Conta:"), gbc);
        gbc.gridx = 1;
        topo.add(txtNumContaSaque, gbc);
        gbc.gridx = 0; gbc.gridy++;
        topo.add(new JLabel("Valor (MZN):"), gbc);
        gbc.gridx = 1;
        topo.add(txtValorSaque, gbc);
        gbc.gridx = 0; gbc.gridy++;
        topo.add(new JLabel("Referência:"), gbc);
        gbc.gridx = 1;
        topo.add(txtReferenciaSaque, gbc);
        gbc.gridx = 0; gbc.gridy++;
        topo.add(new JLabel("Entidade:"), gbc);
        gbc.gridx = 1;
        topo.add(txtEntidadeSaque, gbc);
        gbc.gridx = 0; gbc.gridy++;
        topo.add(btnSacar, gbc);
        gbc.gridx = 1;
        topo.add(btnLimpar, gbc);
        gbc.gridx = 0; gbc.gridy++;
        topo.add(btnListar, gbc);

        painel.add(topo, BorderLayout.NORTH);

        // Centro - tabela de saques
        String[] col = {"ID Transação", "ID Conta", "Valor", "Data", "Referência", "Entidade", "Descrição"};
        modeloSaques = new DefaultTableModel(col, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaSaques = new JTable(modeloSaques);
        tabelaSaques.setRowHeight(24);
        tabelaSaques.getTableHeader().setReorderingAllowed(false);
        JScrollPane scroll = new JScrollPane(tabelaSaques);
        scroll.setPreferredSize(new Dimension(700, 240));
        painel.add(scroll, BorderLayout.CENTER);

        // Rodapé - ações rápidas
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        rodape.setBackground(new Color(235, 242, 248));
        JButton btnAtualizar = new JButton("Actualizar Tabela");
        estilizarBotaoSecundario(btnAtualizar);
        rodape.add(btnAtualizar);
        painel.add(rodape, BorderLayout.SOUTH);

        // Ações
        btnSacar.addActionListener(e -> processarSaque());
        btnListar.addActionListener(e -> atualizarTabelaSaques());
        btnLimpar.addActionListener(e -> limparCamposSaque());
        btnAtualizar.addActionListener(e -> atualizarTabelaSaques());

        return painel;
    }

    private void processarSaque() {
        try {
            Integer idConta = txtIdContaSaque.getText().trim().isEmpty() ? null : 
                            Integer.parseInt(txtIdContaSaque.getText().trim());
            Integer numeroConta = txtNumContaSaque.getText().trim().isEmpty() ? null : 
                               Integer.parseInt(txtNumContaSaque.getText().trim());
            Double valor = txtValorSaque.getText().trim().isEmpty() ? null : 
                         Double.parseDouble(txtValorSaque.getText().trim());
            String referencia = txtReferenciaSaque.getText().trim();
            String entidade = txtEntidadeSaque.getText().trim();

            if (valor == null || valor <= 0) {
                JOptionPane.showMessageDialog(this, "O valor deve ser maior que zero.");
                return;
            }

            if (idConta == null && numeroConta == null) {
                JOptionPane.showMessageDialog(this, "Informe pelo menos ID ou Número da Conta.");
                return;
            }

            // Usa método flexível do SistemaController
            boolean sucesso = sistema.registrarLevantamentoFlexivel(idConta, numeroConta, valor, referencia, entidade);

            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Saque realizado com sucesso!");
                atualizarTabelaSaques();
                atualizarDashboard();
                limparCamposSaque();
            } else {
                JOptionPane.showMessageDialog(this, "Erro: saldo insuficiente, conta inválida ou dados incorretos.");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Dados inválidos! Verifica os campos numéricos.");
        }
    }

    private void limparCamposSaque() {
        txtIdContaSaque.setText("");
        txtNumContaSaque.setText("");
        txtValorSaque.setText("");
        txtReferenciaSaque.setText("");
        txtEntidadeSaque.setText("");
    }

    private void atualizarTabelaSaques() {
        modeloSaques.setRowCount(0);
        try {
            List<Transacoes> lista = sistema.listarSaques();
            for (Transacoes t : lista) {
                String data = t.getData() != null ? t.getData().format(dtf) : "";
                String referencia = t.getReferencia() != null ? t.getReferencia() : "";
                String entidade = t.getEntidade() != null ? t.getEntidade() : "";
                
                modeloSaques.addRow(new Object[]{
                    t.getId(),
                    t.getConta() != null ? t.getConta().getIdConta() : "N/A",
                    String.format("%.2f MZN", t.getValor()),
                    data,
                    referencia,
                    entidade,
                    t.getCategoria()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar saques: " + e.getMessage());
        }
    }

    // ===================== RELATÓRIOS =====================
    private JPanel criarPainelRelatorios() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(new Color(235, 242, 248));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitulo = new JLabel("📊 Relatórios do Caixa", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(0, 51, 102));

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Consolas", Font.PLAIN, 12));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        JButton btnGerar = new JButton("Actualizar Relatório");
        estilizarBotaoAcao(btnGerar);
        btnGerar.addActionListener(e -> {
            try {
                area.setText(sistema.gerarRelatorioCaixa());
            } catch (Exception ex) {
                area.setText("Erro ao gerar relatório: " + ex.getMessage());
            }
        });

        painel.add(lblTitulo, BorderLayout.NORTH);
        painel.add(new JScrollPane(area), BorderLayout.CENTER);
        JPanel rod = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rod.setBackground(new Color(235, 242, 248));
        rod.add(btnGerar);
        painel.add(rod, BorderLayout.SOUTH);

        // Gerar relatório inicial
        btnGerar.doClick();

        return painel;
    }

    // ========== ESTILOS AUXILIARES ==========
    private JButton criarBotaoMenu(String texto) {
        JButton btn = new JButton(texto);
        btn.setFocusPainted(false);
        btn.setBackground(new Color(0, 76, 153));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(0, 102, 204)); }
            public void mouseExited(MouseEvent e) { btn.setBackground(new Color(0, 76, 153)); }
        });
        
        return btn;
    }

    private void estilizarBotaoAcao(JButton btn) {
        btn.setBackground(new Color(0, 102, 204));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(180, 36));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(0, 122, 255)); }
            public void mouseExited(MouseEvent e) { btn.setBackground(new Color(0, 102, 204)); }
        });
    }

    private void estilizarBotaoSecundario(JButton btn) {
        btn.setBackground(new Color(220, 230, 240));
        btn.setForeground(new Color(0, 51, 102));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(160, 32));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(200, 220, 240)); }
            public void mouseExited(MouseEvent e) { btn.setBackground(new Color(220, 230, 240)); }
        });
    }

    // ========== MAIN DE TESTE ==========
    public static void main(String[] args) {
        SistemaController sistema = new SistemaController();
        
        // Criar alguns dados de teste
        /*sistema.criarCliente("João Silva", 123456789, "Maputo", 821234567, "joao@email.com", 
                           java.time.LocalDate.of(1990, 5, 15), "BI123456", "1234");
        sistema.criarConta(1, Conta.TipoConta.CORRENTE);*/
        
        SwingUtilities.invokeLater(() ->
                new PainelFuncCaixa(sistema, "Frederico Madabula", "Caixa", "Banco Nexus").setVisible(true)
        );
    }
}