package view;

//package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import controller.SistemaController;
import model.Emprestimo;
import model.Transacoes;
import model.Cliente;
import model.Conta;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

// NOVOS IMPORTS para a funcionalidade de download
import java.io.PrintWriter;
import java.io.File;
import java.time.LocalDate;
import java.util.stream.Collectors; // Necessário para contagem de clientes ATIVO

public class TelaGestor extends javax.swing.JPanel {

    private SistemaController sistemaController;

    public TelaGestor() {
        this.sistemaController = new SistemaController();
        initComponents();

        // Configura o layout do painel de conteúdo
        jPanel1.setLayout(new java.awt.BorderLayout());

        configurarListeners();
        mostrarTelaInicial();
    }

    private void configurarListeners() {
        jButton1.addActionListener(e -> mostrarAprovarEmprestimos());
        jButton2.addActionListener(e -> mostrarRelatorios());
        jButton3.addActionListener(e -> mostrarLimitesCredito());
        jButton4.addActionListener(e -> mostrarMovimentacoesSuspeitas());
        jButton5.addActionListener(e -> sair());
    }

    private void mostrarTelaInicial() {
        jPanel1.removeAll();

        JPanel panelInicial = new JPanel(new BorderLayout());
        panelInicial.setBackground(Color.WHITE);
        panelInicial.setBorder(BorderFactory.createEmptyBorder(50, 30, 50, 30));

        // Painel de boas-vindas com estatísticas
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        statsPanel.setBackground(Color.WHITE);

        // Estatísticas rápidas
        statsPanel.add(criarCardEstatistica("📊 Total Clientes",
                String.valueOf(sistemaController.listarClientes().size()), Color.BLUE));
        statsPanel.add(criarCardEstatistica("🏦 Total Contas",
                String.valueOf(sistemaController.listarContas().size()), Color.GREEN));
        statsPanel.add(criarCardEstatistica("💰 Empréstimos Pendentes",
                String.valueOf(contarEmprestimosPendentes()), Color.ORANGE));
        statsPanel.add(criarCardEstatistica("💳 Transações Hoje",
                String.valueOf(sistemaController.visualizarTodasTransacoes().size()), Color.RED));

        JLabel lblMensagem = new JLabel("Bem-vindo, Gestor! Selecione uma operação", JLabel.CENTER);
        lblMensagem.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        lblMensagem.setForeground(new Color(120, 120, 120));
        lblMensagem.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        panelInicial.add(lblMensagem, BorderLayout.NORTH);
        panelInicial.add(statsPanel, BorderLayout.CENTER);

        jPanel1.add(panelInicial, BorderLayout.CENTER);
        jPanel1.revalidate();
        jPanel1.repaint();
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
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblValor.setForeground(cor);

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);

        return card;
    }

    private int contarEmprestimosPendentes() {
        // Este método precisaria ser implementado no SistemaController
        // Por enquanto retornamos 0
        return 0;
    }

    private void mostrarAprovarEmprestimos() {
        jPanel1.removeAll();

        JPanel panelEmprestimos = new JPanel(new BorderLayout());
        panelEmprestimos.setBackground(Color.WHITE);
        panelEmprestimos.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Título
        JLabel titulo = new JLabel("APROVAÇÃO DE EMPRÉSTIMOS", JLabel.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(new Color(0, 0, 139));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panelEmprestimos.add(titulo, BorderLayout.NORTH);

        // Botão para simular solicitação de empréstimo (para teste)
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.WHITE);

        JButton btnSimularEmprestimo = new JButton("➕ Simular Solicitação de Empréstimo");
        btnSimularEmprestimo.setBackground(new Color(0, 100, 0));
        btnSimularEmprestimo.setForeground(Color.WHITE);
        btnSimularEmprestimo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSimularEmprestimo.addActionListener(e -> simularSolicitacaoEmprestimo());
        topPanel.add(btnSimularEmprestimo);

        panelEmprestimos.add(topPanel, BorderLayout.NORTH);

        // Tabela de empréstimos
        String[] colunas = {"ID", "Cliente", "Valor (MT)", "Prazo (meses)", "Taxa Juros", "Status", "Ação"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Apenas a coluna "Ação" é editável
            }
        };

        JTable tabela = new JTable(model);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabela.setRowHeight(35);

        // Adicionar botão de ação na tabela
        tabela.getColumn("Ação").setCellRenderer(new ButtonRenderer());
        tabela.getColumn("Ação").setCellEditor(new ButtonEditor(new JCheckBox(), sistemaController));

        // Preencher tabela com dados de exemplo
        preencherTabelaEmprestimos(model);

        JScrollPane scrollPane = new JScrollPane(tabela);
        panelEmprestimos.add(scrollPane, BorderLayout.CENTER);

        jPanel1.add(panelEmprestimos, BorderLayout.CENTER);
        jPanel1.revalidate();
        jPanel1.repaint();
    }

    private void simularSolicitacaoEmprestimo() {
        // Simular uma solicitação de empréstimo para teste
        List<Conta> contas = sistemaController.listarContas();
        if (!contas.isEmpty()) {
            sistemaController.solicitarEmprestimo(contas.get(0).getIdConta(), 5000.0, 12);
            JOptionPane.showMessageDialog(this,
                    "Solicitação de empréstimo simulada com sucesso!\n" +
                            "Valor: 5.000 MZN\nPrazo: 12 meses",
                    "Empréstimo Simulado",
                    JOptionPane.INFORMATION_MESSAGE);

            // Atualizar a tela
            mostrarAprovarEmprestimos();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Nenhuma conta disponível para simular empréstimo!",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void preencherTabelaEmprestimos(DefaultTableModel model) {
        // Em um sistema real, buscaria os empréstimos do controller
        // Por enquanto, adicionamos dados de exemplo
        model.addRow(new Object[]{1, "João Silva", "5.000,00", 12, "5%", "PENDENTE", "Aprovar"});
        model.addRow(new Object[]{2, "Maria Santos", "3.000,00", 6, "5%", "PENDENTE", "Aprovar"});
        model.addRow(new Object[]{3, "Carlos Oliveira", "7.500,00", 24, "5%", "APROVADO", "Visualizar"});
    }

    // =================================================================
    // MUDANÇA PRINCIPAL: mostrarRelatorios() atualizado
    // =================================================================
    private void mostrarRelatorios() {
        jPanel1.removeAll();

        JPanel panelRelatorios = new JPanel(new BorderLayout());
        panelRelatorios.setBackground(Color.WHITE);
        panelRelatorios.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // --- NOVO: Header com Título e Botão ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JLabel titulo = new JLabel("RELATÓRIOS DE DESEMPENHO", JLabel.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(new Color(0, 0, 139));

        // Novo Botão "Baixar Relatório Completo"
        JButton btnBaixarCompleto = criarBotaoRelatorioDownload("⬇️ BAIXAR RELATÓRIO COMPLETO");
        btnBaixarCompleto.addActionListener(e -> baixarRelatorioCompletoGestor());

        JPanel tituloContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        tituloContainer.setBackground(Color.WHITE);
        tituloContainer.add(titulo);

        JPanel downloadPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        downloadPanel.setBackground(Color.WHITE);
        downloadPanel.add(btnBaixarCompleto);

        headerPanel.add(tituloContainer, BorderLayout.CENTER);
        headerPanel.add(downloadPanel, BorderLayout.EAST);

        panelRelatorios.add(headerPanel, BorderLayout.NORTH);
        // --- FIM NOVO: Header com Título e Botão ---

        JPanel botoesPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        botoesPanel.setBackground(Color.WHITE);
        botoesPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Botões de relatórios funcionais
        botoesPanel.add(criarBotaoRelatorio("📊 Relatório Financeiro", e -> gerarRelatorioFinanceiro()));
        botoesPanel.add(criarBotaoRelatorio("👥 Relatório de Clientes", e -> gerarRelatorioClientes()));
        botoesPanel.add(criarBotaoRelatorio("💰 Performance do Banco", e -> gerarPerformanceBanco()));
        botoesPanel.add(criarBotaoRelatorio("📈 Relatório de Caixa", e -> gerarRelatorioCaixa()));
        botoesPanel.add(criarBotaoRelatorio("⚠️ Transações Suspeitas", e -> gerarTransacoesSuspeitas()));
        botoesPanel.add(criarBotaoRelatorio("🎯 Análise Completa", e -> gerarAnaliseCompleta()));

        panelRelatorios.add(botoesPanel, BorderLayout.CENTER);
        jPanel1.add(panelRelatorios, BorderLayout.CENTER);
        jPanel1.revalidate();
        jPanel1.repaint();
    }

    // =================================================================
    // NOVO MÉTODO: Cria o botão de download com estilo Gestor
    // =================================================================
    private JButton criarBotaoRelatorioDownload(String texto) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 12));
        botao.setBackground(new Color(255, 165, 0)); // Laranja para destacar
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                botao.setBackground(new Color(255, 185, 0));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                botao.setBackground(new Color(255, 165, 0));
            }
        });

        return botao;
    }

    // =================================================================
    // NOVO MÉTODO: Lógica de compilação e download do relatório completo
    // =================================================================
    private void baixarRelatorioCompletoGestor() {
        // 1. Coletar dados
        Map<String, Object> performance = sistemaController.analisarPerformanceBanco();
        String relatorioCaixa = sistemaController.gerarRelatorioCaixa();
        List<Cliente> clientes = sistemaController.listarClientes();
        List<Conta> contas = sistemaController.listarContas();
        List<Transacoes> transacoes = sistemaController.visualizarTodasTransacoes();

        // 2. Montar o conteúdo completo do relatório
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("         RELATÓRIO DE GESTÃO E DESEMPENHO           \n");
        sb.append("         Data: ").append(LocalDate.now()).append("\n");
        sb.append("====================================================\n\n");

        // --- SEÇÃO 1: PERFORMANCE DO BANCO ---
        sb.append("====================================================\n");
        sb.append("           1. PERFORMANCE GERAL DO BANCO            \n");
        sb.append("====================================================\n");
        for (Map.Entry<String, Object> entry : performance.entrySet()) {
            sb.append(String.format("%-25s: %s\n", entry.getKey(), entry.getValue()));
        }

        long clientesAtivos = clientes.stream()
                .filter(c -> c.getStatus() != null && c.getStatus().name().equals("ATIVO")).count();
        double saldoTotal = contas.stream().mapToDouble(Conta::getSaldo).sum();

        sb.append(String.format("Total Clientes Ativos: %d\n", clientesAtivos));
        sb.append(String.format("Total Contas Registradas: %d\n", contas.size()));
        sb.append(String.format("Saldo Total Agregado: %.2f MZN\n", saldoTotal));
        sb.append(String.format("Total Transações: %d\n\n", transacoes.size()));

        // --- SEÇÃO 2: RELATÓRIO DE CAIXA ---
        sb.append("====================================================\n");
        sb.append("               2. RELATÓRIO DE CAIXA                \n");
        sb.append("====================================================\n");
        sb.append(relatorioCaixa);
        sb.append("\n");

        // --- SEÇÃO 3: TRANSAÇÕES SUSPEITAS (Alto Valor) ---
        sb.append("====================================================\n");
        sb.append("         3. TRANSAÇÕES DE ALTO VALOR (>5000)        \n");
        sb.append("====================================================\n");

        int countSuspeitas = 0;
        for (Transacoes t : transacoes) {
            if (t.getValor() > 5000) {
                sb.append(String.format("🚨 ALTO VALOR: %.2f MZN | Tipo: %s | Data: %s\n",
                        t.getValor(), t.getCategoria(), t.getData()));
                countSuspeitas++;
            }
        }
        if (countSuspeitas == 0) {
            sb.append("Nenhuma transação de alto valor identificada.\n");
        }
        sb.append(String.format("\nTotal de transações de alto valor: %d\n", countSuspeitas));


        // 4. Simular download (usando JFileChooser para salvar arquivo)
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar Relatório de Gestão");

        // Sugerir nome do arquivo
        fileChooser.setSelectedFile(new File("Relatorio_Gestor_BancoNexus_" + LocalDate.now() + ".txt"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (PrintWriter out = new PrintWriter(fileToSave)) {
                out.println(sb.toString());
                JOptionPane.showMessageDialog(this,
                        "Relatório salvo com sucesso em:\n" + fileToSave.getAbsolutePath(),
                        "Download Concluído",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (java.io.FileNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar o arquivo: " + ex.getMessage(), "Erro de I/O", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Download cancelado pelo usuário.", "Cancelado", JOptionPane.WARNING_MESSAGE);
        }
    }
    // =================================================================
    // FIM NOVO MÉTODO
    // =================================================================


    private void gerarRelatorioFinanceiro() {
        Map<String, Object> performance = sistemaController.analisarPerformanceBanco();

        StringBuilder relatorio = new StringBuilder();
        relatorio.append("=== RELATÓRIO FINANCEIRO ===\n\n");

        for (Map.Entry<String, Object> entry : performance.entrySet()) {
            relatorio.append(String.format("%s: %s\n", entry.getKey(), entry.getValue()));
        }

        relatorio.append("\n=== DETALHES ADICIONAIS ===\n");
        relatorio.append(String.format("Total de Clientes: %d\n", sistemaController.listarClientes().size()));
        relatorio.append(String.format("Total de Contas: %d\n", sistemaController.listarContas().size()));
        relatorio.append(String.format("Total de Transações: %d\n", sistemaController.visualizarTodasTransacoes().size()));

        exibirRelatorioTexto("Relatório Financeiro", relatorio.toString());
    }

    private void gerarRelatorioClientes() {
        List<Cliente> clientes = sistemaController.listarClientes();

        StringBuilder relatorio = new StringBuilder();
        relatorio.append("=== RELATÓRIO DE CLIENTES ===\n\n");

        if (clientes.isEmpty()) {
            relatorio.append("Nenhum cliente cadastrado.\n");
        } else {
            for (Cliente cliente : clientes) {
                relatorio.append(String.format("ID: %d | Nome: %s | NUIT: %d | Status: %s\n",
                        cliente.getIdCliente(), cliente.getNomeCli(), cliente.getNuitCli(), cliente.getStatus()));
            }
            relatorio.append(String.format("\nTotal: %d clientes\n", clientes.size()));
        }

        exibirRelatorioTexto("Relatório de Clientes", relatorio.toString());
    }

    private void gerarPerformanceBanco() {
        Map<String, Object> performance = sistemaController.analisarPerformanceBanco();

        StringBuilder relatorio = new StringBuilder();
        relatorio.append("=== PERFORMANCE DO BANCO ===\n\n");

        double volumeFinanceiro = (Double) performance.getOrDefault("VolumeFinanceiro", 0.0);
        int totalTransacoes = (Integer) performance.getOrDefault("TotalTransacoes", 0);

        relatorio.append(String.format("📈 Volume Financeiro Total: %.2f MZN\n", volumeFinanceiro));
        relatorio.append(String.format("🔄 Total de Transações: %d\n", totalTransacoes));
        relatorio.append(String.format("👥 Clientes Ativos: %d\n", sistemaController.listarClientes().size()));
        relatorio.append(String.format("🏦 Contas Ativas: %d\n", sistemaController.listarContas().size()));

        if (totalTransacoes > 0) {
            double mediaTransacao = volumeFinanceiro / totalTransacoes;
            relatorio.append(String.format("💰 Valor Médio por Transação: %.2f MZN\n", mediaTransacao));
        }

        relatorio.append(String.format("\n📅 Data do Relatório: %s\n", java.time.LocalDate.now()));

        exibirRelatorioTexto("Performance do Banco", relatorio.toString());
    }

    private void gerarRelatorioCaixa() {
        String relatorio = sistemaController.gerarRelatorioCaixa();
        exibirRelatorioTexto("Relatório de Caixa", relatorio);
    }

    private void gerarTransacoesSuspeitas() {
        List<Transacoes> transacoes = sistemaController.visualizarTodasTransacoes();

        StringBuilder relatorio = new StringBuilder();
        relatorio.append("=== TRANSAÇÕES DE ALTO VALOR ===\n\n");

        int countSuspeitas = 0;
        for (Transacoes transacao : transacoes) {
            if (transacao.getValor() > 10000) { // Transações acima de 10.000 MZN
                relatorio.append(String.format("🚨 ALTO VALOR: %.2f MZN | Tipo: %s | Data: %s\n",
                        transacao.getValor(), transacao.getCategoria(), transacao.getData()));
                countSuspeitas++;
            }
        }

        if (countSuspeitas == 0) {
            relatorio.append("Nenhuma transação de alto valor identificada.\n");
        } else {
            relatorio.append(String.format("\nTotal de transações suspeitas: %d\n", countSuspeitas));
        }

        exibirRelatorioTexto("Transações Suspeitas", relatorio.toString());
    }

    private void gerarAnaliseCompleta() {
        StringBuilder relatorio = new StringBuilder();
        relatorio.append("=== ANÁLISE COMPLETA DO SISTEMA ===\n\n");

        // Dados de clientes
        List<Cliente> clientes = sistemaController.listarClientes();
        relatorio.append("📊 CLIENTES:\n");
        relatorio.append(String.format("  • Total: %d clientes\n", clientes.size()));

        // Dados de contas
        List<Conta> contas = sistemaController.listarContas();
        relatorio.append("\n🏦 CONTAS:\n");
        relatorio.append(String.format("  • Total: %d contas\n", contas.size()));

        double saldoTotal = 0;
        for (Conta conta : contas) {
            saldoTotal += conta.getSaldo();
        }
        relatorio.append(String.format("  • Saldo Total: %.2f MZN\n", saldoTotal));

        // Transações
        List<Transacoes> transacoes = sistemaController.visualizarTodasTransacoes();
        relatorio.append("\n💳 TRANSAÇÕES:\n");
        relatorio.append(String.format("  • Total: %d transações\n", transacoes.size()));

        // Performance
        Map<String, Object> performance = sistemaController.analisarPerformanceBanco();
        relatorio.append("\n📈 PERFORMANCE:\n");
        for (Map.Entry<String, Object> entry : performance.entrySet()) {
            relatorio.append(String.format("  • %s: %s\n", entry.getKey(), entry.getValue()));
        }

        relatorio.append(String.format("\n🕒 Relatório gerado em: %s\n", new java.util.Date()));

        exibirRelatorioTexto("Análise Completa", relatorio.toString());
    }

    private void exibirRelatorioTexto(String titulo, String conteudo) {
        JTextArea textArea = new JTextArea(conteudo);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JOptionPane.showMessageDialog(this, scrollPane, titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarLimitesCredito() {
        jPanel1.removeAll();

        JPanel panelLimites = new JPanel(new BorderLayout());
        panelLimites.setBackground(Color.WHITE);
        panelLimites.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel titulo = new JLabel("GERENCIAR LIMITES DE CRÉDITO", JLabel.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(new Color(0, 0, 139));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        panelLimites.add(titulo, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);

        // Campo Cliente
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(criarLabel("Nº Cliente:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 1.0;
        JTextField txtCliente = criarTextField();
        formPanel.add(txtCliente, gbc);

        // Campo Taxa de Juros
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(criarLabel("Nova Taxa de Juros (%):"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.weightx = 1.0;
        JTextField txtTaxa = criarTextField();
        txtTaxa.setText("5.0"); // Valor padrão
        formPanel.add(txtTaxa, gbc);

        // Botão Aplicar
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        JButton btnAplicar = criarBotaoAcao("📊 APLICAR TAXA", new Color(0, 100, 0));
        btnAplicar.addActionListener(e -> aplicarTaxaJuros(txtTaxa.getText()));
        formPanel.add(btnAplicar, gbc);

        panelLimites.add(formPanel, BorderLayout.CENTER);
        jPanel1.add(panelLimites, BorderLayout.CENTER);
        jPanel1.revalidate();
        jPanel1.repaint();
    }

    private void aplicarTaxaJuros(String taxaStr) {
        if (taxaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite a taxa de juros!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double novaTaxa = Double.parseDouble(taxaStr) / 100.0; // Converter para decimal
            sistemaController.definirTaxasJuros(novaTaxa);

            JOptionPane.showMessageDialog(this,
                    "Taxa de juros atualizada com sucesso!\n" +
                            "Nova taxa: " + taxaStr + "%",
                    "Taxa Aplicada",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Taxa de juros inválida!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarMovimentacoesSuspeitas() {
        jPanel1.removeAll();

        JPanel panelMovimentacoes = new JPanel(new BorderLayout());
        panelMovimentacoes.setBackground(Color.WHITE);
        panelMovimentacoes.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Título
        JLabel titulo = new JLabel("MOVIMENTAÇÕES SUSPEITAS", JLabel.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(new Color(0, 0, 139));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panelMovimentacoes.add(titulo, BorderLayout.NORTH);

        // Botão para ver transações
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.WHITE);

        JButton btnVerTodas = new JButton("👁️ Ver Todas as Transações");
        btnVerTodas.setBackground(new Color(70, 130, 180));
        btnVerTodas.setForeground(Color.WHITE);
        btnVerTodas.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnVerTodas.addActionListener(e -> mostrarTodasTransacoes());
        topPanel.add(btnVerTodas);

        panelMovimentacoes.add(topPanel, BorderLayout.NORTH);

        // Tabela de transações suspeitas
        String[] colunas = {"Data/Hora", "Cliente", "Valor (MT)", "Tipo", "Descrição", "Status"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0);

        JTable tabela = new JTable(model);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabela.setRowHeight(30);

        // Preencher com transações de alto valor
        preencherTransacoesSuspeitas(model);

        JScrollPane scrollPane = new JScrollPane(tabela);
        panelMovimentacoes.add(scrollPane, BorderLayout.CENTER);

        jPanel1.add(panelMovimentacoes, BorderLayout.CENTER);
        jPanel1.revalidate();
        jPanel1.repaint();
    }

    private void preencherTransacoesSuspeitas(DefaultTableModel model) {
        List<Transacoes> transacoes = sistemaController.visualizarTodasTransacoes();

        for (Transacoes transacao : transacoes) {
            if (transacao.getValor() > 5000) { // Transações acima de 5.000 MZN
                String clienteNome = "Cliente ID: " + transacao.getIdCliente(); // Em sistema real, buscar nome do cliente

                model.addRow(new Object[]{
                        transacao.getData().toString(),
                        clienteNome,
                        String.format("%.2f MZN", transacao.getValor()),
                        transacao.getCategoria(),
                        "Transação de alto valor",
                        "ANALISAR"
                });
            }
        }

        if (model.getRowCount() == 0) {
            // Adicionar mensagem se não houver transações suspeitas
            JLabel lblVazio = new JLabel("Nenhuma movimentação suspeita identificada", JLabel.CENTER);
            lblVazio.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            lblVazio.setForeground(Color.GRAY);
            // Não podemos adicionar diretamente ao model, então mostramos mensagem
        }
    }

    private void mostrarTodasTransacoes() {
        List<Transacoes> transacoes = sistemaController.visualizarTodasTransacoes();

        StringBuilder sb = new StringBuilder();
        sb.append("=== TODAS AS TRANSAÇÕES ===\n\n");

        if (transacoes.isEmpty()) {
            sb.append("Nenhuma transação registrada.\n");
        } else {
            for (Transacoes t : transacoes) {
                sb.append(String.format("📅 %s | 💰 %.2f MZN | 📋 %s | 👤 Cliente ID: %d\n",
                        t.getData(), t.getValor(), t.getCategoria(), t.getIdCliente()));
            }
            sb.append(String.format("\nTotal: %d transações\n", transacoes.size()));
        }

        exibirRelatorioTexto("Todas as Transações", sb.toString());
    }

    private void sair() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente sair do painel do gestor?",
                "Confirmação de Saída",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.dispose();
        }
    }

    // MÉTODOS AUXILIARES
    private JLabel criarLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(60, 60, 60));
        return label;
    }

    private JTextField criarTextField() {
        JTextField campo = new JTextField();
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setPreferredSize(new Dimension(300, 35));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return campo;
    }

    private JButton criarBotaoAcao(String texto, Color cor) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 16));
        botao.setBackground(cor);
        botao.setForeground(Color.WHITE);
        botao.setPreferredSize(new Dimension(250, 40));
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(cor.darker(), 2),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));

        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                botao.setBackground(cor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                botao.setBackground(cor);
            }
        });

        return botao;
    }

    private JButton criarBotaoRelatorio(String texto, java.awt.event.ActionListener listener) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botao.setBackground(new Color(70, 130, 180));
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        botao.addActionListener(listener);

        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                botao.setBackground(new Color(100, 149, 237));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                botao.setBackground(new Color(70, 130, 180));
            }
        });

        return botao;
    }

    // Classes para renderizar botões na tabela
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private SistemaController controller;

        public ButtonEditor(JCheckBox checkBox, SistemaController controller) {
            super(checkBox);
            this.controller = controller;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                // Aqui você pode implementar a lógica de aprovação do empréstimo
                JOptionPane.showMessageDialog(button, "Empréstimo aprovado com sucesso!");
                // controller.autorizarEmprestimo(idEmprestimo);
            }
            isPushed = false;
            return label;
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    // ... (RESTANTE DO CÓDIGO GERADO PELO NETBEANS - MANTIDO ORIGINAL)
    // Variables declaration - do not modify
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    // End of variables declaration

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 554, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel3.setBackground(new java.awt.Color(0, 0, 139));
        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton1.setText("Aprovar Empréstimos");

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton2.setText("Relatórios");

        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton3.setText("Limites de Crédito");

        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton4.setText("Movimentações Suspeitas");

        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton5.setText("Sair");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Menu Gestor");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(33, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 139));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Painel do Gestor");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
                        .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>

    // MAIN para teste
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Banco Nexus - Gestor");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 600);
            frame.setLocationRelativeTo(null);

            TelaGestor gestor = new TelaGestor();
            frame.add(gestor);

            frame.setVisible(true);
        });
    }
}