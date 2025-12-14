package view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PainelCliente extends JFrame {
    
    // Cores do tema
    private static final Color PRIMARY_COLOR = new Color(3, 2, 19);
    private static final Color SECONDARY_COLOR = new Color(236, 236, 240);
    private static final Color BACKGROUND_COLOR = new Color(255, 255, 255);
    private static final Color CARD_COLOR = new Color(255, 255, 255);
    private static final Color MUTED_COLOR = new Color(113, 113, 130);
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);
    private static final Color ERROR_COLOR = new Color(239, 68, 68);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    
    // Componentes principais
    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private CardLayout contentCardLayout;
    private JLabel titleLabel;
    private JLabel balanceLabel;
    private boolean showBalance = true;
    
    // Dados mock
    private final DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    
    public PainelCliente() {
        initializeFrame();
        createSidebar();
        createContentPanel();
        setupLayout();
        showDashboard(); // Mostrar dashboard por padrão
    }
    
    private void initializeFrame() {
        setTitle("Banco Digital - Painel do Cliente");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setBackground(BACKGROUND_COLOR);
        
        // Look and Feel moderno
        try {
          //  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void createSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(CARD_COLOR);
        sidebarPanel.setBorder(new MatteBorder(0, 0, 0, 1, BORDER_COLOR));
        sidebarPanel.setPreferredSize(new Dimension(280, 0));
        
        // Header da sidebar
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(CARD_COLOR);
        headerPanel.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_COLOR));
        
        JLabel logoLabel = new JLabel("Banco Digital");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 18));
        logoLabel.setForeground(PRIMARY_COLOR);
        headerPanel.add(logoLabel);
        
        JLabel subtitleLabel = new JLabel("Painel do Cliente");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitleLabel.setForeground(MUTED_COLOR);
        headerPanel.add(subtitleLabel);
        
        sidebarPanel.add(headerPanel);
        
        // Menu items
        String[] menuItems = {
            "📊 Dashboard",
            "👤 Perfil", 
            "📋 Histórico",
            "💸 Enviar Dinheiro",
            "🔍 Verificar NIN/NUIB",
            "📈 Movimentos",
            "🧾 Faturas",
            "⭐ Favoritos",
            "💼 Tipo de Conta",
            "🔔 Notificações (3)",
            "💰 Taxas",
            "📱 Transferência Móvel",
            "⚙️ Configurações"
        };
        
        String[] actions = {
            "dashboard", "profile", "transactions", "send-money", "verify",
            "movements", "invoices", "favorites", "account", "notifications",
            "rates", "mobile-transfer", "settings"
        };
        
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(CARD_COLOR);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        for (int i = 0; i < menuItems.length; i++) {
            JButton menuButton = createMenuButton(menuItems[i], actions[i]);
            menuPanel.add(menuButton);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        
        sidebarPanel.add(menuPanel);
        
        // User info no footer
        sidebarPanel.add(Box.createVerticalGlue());
        
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userPanel.setBackground(CARD_COLOR);
        userPanel.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_COLOR));
        
        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(new Font("Arial", Font.PLAIN, 20));
        userPanel.add(userIcon);
        
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setBackground(CARD_COLOR);
        
        JLabel userName = new JLabel("João Silva");
        userName.setFont(new Font("Arial", Font.BOLD, 12));
        userInfoPanel.add(userName);
        
        JLabel userType = new JLabel("Premium");
        userType.setFont(new Font("Arial", Font.PLAIN, 10));
        userType.setForeground(MUTED_COLOR);
        userInfoPanel.add(userType);
        
        userPanel.add(userInfoPanel);
        sidebarPanel.add(userPanel);
    }
    
    private JButton createMenuButton(String text, String action) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setPreferredSize(new Dimension(250, 40));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBackground(CARD_COLOR);
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(SECONDARY_COLOR);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(CARD_COLOR);
            }
        });
        
        button.addActionListener(e -> switchContent(action));
        
        return button;
    }
    
    private void createContentPanel() {
        contentCardLayout = new CardLayout();
        contentPanel = new JPanel(contentCardLayout);
        contentPanel.setBackground(BACKGROUND_COLOR);
        
        // Criar todas as telas
        contentPanel.add(createDashboardPanel(), "dashboard");
        contentPanel.add(createProfilePanel(), "profile");
        contentPanel.add(createTransactionsPanel(), "transactions");
        contentPanel.add(createSendMoneyPanel(), "send-money");
        contentPanel.add(createVerifyPanel(), "verify");
        contentPanel.add(createPlaceholderPanel("Movimentos e Tipos de Pagamento"), "movements");
        contentPanel.add(createPlaceholderPanel("Faturas"), "invoices");
        contentPanel.add(createPlaceholderPanel("Favoritos"), "favorites");
        contentPanel.add(createPlaceholderPanel("Tipo de Conta e Patrimônio"), "account");
        contentPanel.add(createPlaceholderPanel("Notificações"), "notifications");
        contentPanel.add(createPlaceholderPanel("Taxas"), "rates");
        contentPanel.add(createPlaceholderPanel("Transferência para Conta Móvel"), "mobile-transfer");
        contentPanel.add(createPlaceholderPanel("Configurações"), "settings");
    }
    
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(BACKGROUND_COLOR);
        
        JLabel titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        
        JLabel subtitleLabel = new JLabel("Bem-vindo de volta, João Silva");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(MUTED_COLOR);
        titlePanel.add(subtitleLabel);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        
        JButton updateButton = new JButton("Atualizar Dados");
        updateButton.setBackground(CARD_COLOR);
        updateButton.setBorder(new LineBorder(BORDER_COLOR));
        headerPanel.add(updateButton, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Balance Cards
        JPanel balancePanel = new JPanel(new GridLayout(1, 4, 15, 15));
        balancePanel.setBackground(BACKGROUND_COLOR);
        balancePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        balancePanel.add(createBalanceCard("Saldo Principal", "245.840,00 AOA", "+2.5%", SUCCESS_COLOR, true));
        balancePanel.add(createBalanceCard("Poupança", "89.250,00 AOA", "Meta: 150.000", MUTED_COLOR, false));
        balancePanel.add(createBalanceCard("Investimentos", "156.780,00 AOA", "+8.2%", SUCCESS_COLOR, false));
        balancePanel.add(createBalanceCard("Cartão Crédito", "45.200,00 AOA", "Limite: 100.000", MUTED_COLOR, false));
        
        panel.add(balancePanel, BorderLayout.CENTER);
        
        // Quick Actions
        JPanel actionsPanel = new JPanel(new BorderLayout());
        actionsPanel.setBackground(BACKGROUND_COLOR);
        actionsPanel.setBorder(createCardBorder());
        
        JLabel actionsTitle = new JLabel("Ações Rápidas");
        actionsTitle.setFont(new Font("Arial", Font.BOLD, 16));
        actionsTitle.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        actionsPanel.add(actionsTitle, BorderLayout.NORTH);
        
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        buttonsPanel.setBackground(CARD_COLOR);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        
        buttonsPanel.add(createActionButton("💸", "Transferir"));
        buttonsPanel.add(createActionButton("💳", "Pagar Conta"));
        buttonsPanel.add(createActionButton("📈", "Investir"));
        buttonsPanel.add(createActionButton("🐷", "Poupar"));
        
        actionsPanel.add(buttonsPanel, BorderLayout.CENTER);
        panel.add(actionsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createBalanceCard(String title, String amount, String subtitle, Color subtitleColor, boolean hasToggle) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(createCardBorder());
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_COLOR);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        titleLabel.setForeground(MUTED_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        if (hasToggle) {
            JButton toggleButton = new JButton(showBalance ? "👁️" : "🔒");
            toggleButton.setFont(new Font("Arial", Font.PLAIN, 12));
            toggleButton.setBorder(null);
            toggleButton.setBackground(CARD_COLOR);
            toggleButton.setFocusPainted(false);
            toggleButton.addActionListener(e -> {
                showBalance = !showBalance;
                toggleButton.setText(showBalance ? "👁️" : "🔒");
                // Aqui atualizaria os valores mostrados
            });
            headerPanel.add(toggleButton, BorderLayout.EAST);
        }
        
        card.add(headerPanel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(CARD_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JLabel amountLabel = new JLabel(showBalance ? amount : "••••••");
        amountLabel.setFont(new Font("Arial", Font.BOLD, 18));
        amountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(amountLabel);
        
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        subtitleLabel.setForeground(subtitleColor);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(subtitleLabel);
        
        card.add(contentPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JButton createActionButton(String icon, String text) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setBackground(CARD_COLOR);
        button.setBorder(new LineBorder(BORDER_COLOR));
        button.setPreferredSize(new Dimension(120, 80));
        button.setFocusPainted(false);
        
        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        button.add(iconLabel, BorderLayout.CENTER);
        
        JLabel textLabel = new JLabel(text, SwingConstants.CENTER);
        textLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        button.add(textLabel, BorderLayout.SOUTH);
        
        return button;
    }
    
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel titleLabel = new JLabel("Perfil do Usuário");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton editButton = new JButton("✏️ Editar Perfil");
        editButton.setBackground(CARD_COLOR);
        editButton.setBorder(new LineBorder(BORDER_COLOR));
        headerPanel.add(editButton, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Content
        JPanel contentPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // Photo Card
        JPanel photoCard = createProfilePhotoCard();
        contentPanel.add(photoCard);
        
        // Personal Info Card
        JPanel personalCard = createPersonalInfoCard();
        contentPanel.add(personalCard);
        
        // Account Details Card
        JPanel accountCard = createAccountDetailsCard();
        contentPanel.add(accountCard);
        
        // Security Card
        JPanel securityCard = createSecurityCard();
        contentPanel.add(securityCard);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createProfilePhotoCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(createCardBorder());
        
        JLabel titleLabel = new JLabel("Foto de Perfil");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        card.add(titleLabel, BorderLayout.NORTH);
        
        JPanel photoPanel = new JPanel();
        photoPanel.setLayout(new BoxLayout(photoPanel, BoxLayout.Y_AXIS));
        photoPanel.setBackground(CARD_COLOR);
        photoPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        
        // Avatar placeholder
        JLabel avatar = new JLabel("👤", SwingConstants.CENTER);
        avatar.setFont(new Font("Arial", Font.PLAIN, 48));
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        photoPanel.add(avatar);
        
        photoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JLabel nameLabel = new JLabel("João Silva");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        photoPanel.add(nameLabel);
        
        JLabel typeLabel = new JLabel("Cliente Premium");
        typeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        typeLabel.setForeground(MUTED_COLOR);
        typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        photoPanel.add(typeLabel);
        
        photoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JLabel badgeLabel = new JLabel("✅ Verificado");
        badgeLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        badgeLabel.setForeground(SUCCESS_COLOR);
        badgeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        photoPanel.add(badgeLabel);
        
        card.add(photoPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createPersonalInfoCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(createCardBorder());
        
        JLabel titleLabel = new JLabel("Informações Pessoais");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        card.add(titleLabel, BorderLayout.NORTH);
        
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        
        formPanel.add(new JLabel("Nome Completo:"));
        formPanel.add(new JLabel("João Silva Santos"));
        
        formPanel.add(new JLabel("Email:"));
        formPanel.add(new JLabel("joao.silva@email.com"));
        
        formPanel.add(new JLabel("Telefone:"));
        formPanel.add(new JLabel("+244 923 456 789"));
        
        formPanel.add(new JLabel("Data Nascimento:"));
        formPanel.add(new JLabel("15/03/1985"));
        
        card.add(formPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createAccountDetailsCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(createCardBorder());
        
        JLabel titleLabel = new JLabel("💳 Detalhes da Conta");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        card.add(titleLabel, BorderLayout.NORTH);
        
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(CARD_COLOR);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        
        detailsPanel.add(createDetailRow("Número da Conta:", "0045-123456-78"));
        detailsPanel.add(createDetailRow("IBAN:", "AO06.0045.1234.5678.0000.0001"));
        detailsPanel.add(createDetailRow("Tipo de Conta:", "Premium"));
        detailsPanel.add(createDetailRow("Status:", "✅ Ativa"));
        detailsPanel.add(createDetailRow("Data de Abertura:", "15/01/2020"));
        
        card.add(detailsPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createSecurityCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(createCardBorder());
        
        JLabel titleLabel = new JLabel("🔒 Segurança");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        card.add(titleLabel, BorderLayout.NORTH);
        
        JPanel securityPanel = new JPanel();
        securityPanel.setLayout(new BoxLayout(securityPanel, BoxLayout.Y_AXIS));
        securityPanel.setBackground(CARD_COLOR);
        securityPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        
        securityPanel.add(createDetailRow("Autenticação 2FA:", "✅ Ativada"));
        securityPanel.add(createDetailRow("Última Verificação:", "Hoje, 09:15"));
        securityPanel.add(createDetailRow("Nível de Segurança:", "Alto"));
        
        securityPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JButton changePasswordBtn = new JButton("Alterar Senha");
        changePasswordBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        changePasswordBtn.setMaximumSize(new Dimension(200, 30));
        securityPanel.add(changePasswordBtn);
        
        card.add(securityPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createDetailRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(CARD_COLOR);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Arial", Font.PLAIN, 12));
        labelComp.setForeground(MUTED_COLOR);
        row.add(labelComp, BorderLayout.WEST);
        
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Arial", Font.PLAIN, 12));
        row.add(valueComp, BorderLayout.EAST);
        
        return row;
    }
    
    private JPanel createTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel titleLabel = new JLabel("Histórico de Transações");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton exportButton = new JButton("📥 Exportar");
        exportButton.setBackground(CARD_COLOR);
        exportButton.setBorder(new LineBorder(BORDER_COLOR));
        headerPanel.add(exportButton, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Filters
        JPanel filtersPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        filtersPanel.setBackground(CARD_COLOR);
        filtersPanel.setBorder(createCardBorder());
        filtersPanel.setBorder(BorderFactory.createCompoundBorder(
            createCardBorder(),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JTextField searchField = new JTextField("🔍 Procurar transações...");
        searchField.setForeground(MUTED_COLOR);
        filtersPanel.add(searchField);
        
        JComboBox<String> typeFilter = new JComboBox<>(new String[]{"Todos os tipos", "Depósitos", "Transferências", "Pagamentos"});
        filtersPanel.add(typeFilter);
        
        JComboBox<String> statusFilter = new JComboBox<>(new String[]{"Todos", "Concluídas", "Pendentes", "Falhadas"});
        filtersPanel.add(statusFilter);
        
        JComboBox<String> periodFilter = new JComboBox<>(new String[]{"Este mês", "Esta semana", "Hoje", "Este ano"});
        filtersPanel.add(periodFilter);
        
        // Transaction Table
        String[] columnNames = {"Data/Hora", "Descrição", "Categoria", "Referência", "Status", "Valor"};
        Object[][] data = {
            {"15/01/2024 14:30", "Transferência para Maria Silva", "Transferência", "REF123456", "✅ Concluída", "-15.000,00 AOA"},
            {"15/01/2024 09:00", "Salário - Empresa XYZ", "Salário", "SAL202401", "✅ Concluída", "+250.000,00 AOA"},
            {"14/01/2024 16:45", "Pagamento Conta de Luz", "Utilidades", "EDEL789", "✅ Concluída", "-8.500,00 AOA"},
            {"14/01/2024 11:20", "Compra Online - Loja ABC", "Compras", "ABC456", "✅ Concluída", "-25.000,00 AOA"},
            {"13/01/2024 08:15", "Depósito ATM", "Depósito", "ATM001", "✅ Concluída", "+50.000,00 AOA"},
            {"12/01/2024 19:30", "Transferência MPESA", "Móvel", "MPESA123", "⏳ Pendente", "-12.000,00 AOA"}
        };
        
        JTable table = new JTable(data, columnNames);
        table.setBackground(CARD_COLOR);
        table.setRowHeight(40);
        table.setGridColor(BORDER_COLOR);
        table.getTableHeader().setBackground(SECONDARY_COLOR);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Colorir valores positivos e negativos
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (column == 5 && value != null) { // Coluna Valor
                    String val = value.toString();
                    if (val.startsWith("+")) {
                        setForeground(SUCCESS_COLOR);
                    } else if (val.startsWith("-")) {
                        setForeground(ERROR_COLOR);
                    } else {
                        setForeground(Color.BLACK);
                    }
                } else {
                    setForeground(Color.BLACK);
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(createCardBorder());
        
        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setBackground(BACKGROUND_COLOR);
        centerPanel.add(filtersPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSendMoneyPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel titleLabel = new JLabel("Enviar Dinheiro");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Main content
        JPanel mainPanel = new JPanel(new BorderLayout(20, 0));
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Form Panel
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBorder(createCardBorder());
        formPanel.setPreferredSize(new Dimension(600, 0));
        
        JLabel formTitle = new JLabel("💸 Nova Transferência");
        formTitle.setFont(new Font("Arial", Font.BOLD, 16));
        formTitle.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        formPanel.add(formTitle, BorderLayout.NORTH);
        
        // Form fields
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setBackground(CARD_COLOR);
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        
        // Transfer type buttons
        JPanel typePanel = new JPanel(new GridLayout(1, 3, 10, 0));
        typePanel.setBackground(CARD_COLOR);
        typePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        JButton bankBtn = new JButton("🏦 Banco");
        JButton mobileBtn = new JButton("📱 Móvel");
        JButton intlBtn = new JButton("🌍 Internacional");
        
        bankBtn.setBackground(PRIMARY_COLOR);
        bankBtn.setForeground(Color.WHITE);
        mobileBtn.setBackground(SECONDARY_COLOR);
        intlBtn.setBackground(SECONDARY_COLOR);
        
        typePanel.add(bankBtn);
        typePanel.add(mobileBtn);
        typePanel.add(intlBtn);
        
        fieldsPanel.add(createFieldLabel("Tipo de Transferência"));
        fieldsPanel.add(typePanel);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Recipient fields
        fieldsPanel.add(createFieldLabel("Nome do Destinatário"));
        fieldsPanel.add(createTextField("Nome completo"));
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        fieldsPanel.add(createFieldLabel("Número da Conta"));
        fieldsPanel.add(createTextField("0000-000-000"));
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        fieldsPanel.add(createFieldLabel("Banco"));
        JComboBox<String> bankCombo = new JComboBox<>(new String[]{
            "Selecionar banco", "BAI - Banco Angolano de Investimentos", 
            "BIC - Banco BIC", "BPC - Banco de Poupança e Crédito"
        });
        bankCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        fieldsPanel.add(bankCombo);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Amount
        fieldsPanel.add(createFieldLabel("Valor (AOA)"));
        JTextField amountField = createTextField("0,00");
        fieldsPanel.add(amountField);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        fieldsPanel.add(createFieldLabel("Descrição"));
        JTextArea descArea = new JTextArea(3, 0);
        descArea.setBorder(new LineBorder(BORDER_COLOR));
        descArea.setBackground(BACKGROUND_COLOR);
        fieldsPanel.add(new JScrollPane(descArea));
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Summary
        JPanel summaryPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        summaryPanel.setBackground(SECONDARY_COLOR);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        summaryPanel.add(new JLabel("Valor a transferir:"));
        summaryPanel.add(new JLabel("15.000,00 AOA"));
        summaryPanel.add(new JLabel("Taxa:"));
        summaryPanel.add(new JLabel("150,00 AOA"));
        summaryPanel.add(new JLabel("Total:"));
        JLabel totalLabel = new JLabel("15.150,00 AOA");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 12));
        summaryPanel.add(totalLabel);
        
        fieldsPanel.add(summaryPanel);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        JButton sendBtn = new JButton("💸 Enviar Agora");
        sendBtn.setBackground(PRIMARY_COLOR);
        sendBtn.setForeground(Color.WHITE);
        sendBtn.setFocusPainted(false);
        
        JButton scheduleBtn = new JButton("⏰ Agendar");
        scheduleBtn.setBackground(SECONDARY_COLOR);
        scheduleBtn.setFocusPainted(false);
        
        buttonPanel.add(sendBtn);
        buttonPanel.add(scheduleBtn);
        
        fieldsPanel.add(buttonPanel);
        
        formPanel.add(fieldsPanel, BorderLayout.CENTER);
        
        // Sidebar
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(BACKGROUND_COLOR);
        sidebarPanel.setPreferredSize(new Dimension(300, 0));
        
        // Favorites
        JPanel favoritesCard = new JPanel(new BorderLayout());
        favoritesCard.setBackground(CARD_COLOR);
        favoritesCard.setBorder(createCardBorder());
        favoritesCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        
        JLabel favTitle = new JLabel("⭐ Contatos Favoritos");
        favTitle.setFont(new Font("Arial", Font.BOLD, 14));
        favTitle.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        favoritesCard.add(favTitle, BorderLayout.NORTH);
        
        JPanel favList = new JPanel();
        favList.setLayout(new BoxLayout(favList, BoxLayout.Y_AXIS));
        favList.setBackground(CARD_COLOR);
        favList.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        
        String[] favorites = {"Maria Silva - 0045-123-456", "João Santos - +244 923 456 789", "Ana Costa - 0044-987-654"};
        for (String fav : favorites) {
            JButton favBtn = new JButton(fav);
            favBtn.setHorizontalAlignment(SwingConstants.LEFT);
            favBtn.setBackground(CARD_COLOR);
            favBtn.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            favBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
            favList.add(favBtn);
        }
        
        favoritesCard.add(favList, BorderLayout.CENTER);
        sidebarPanel.add(favoritesCard);
        
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Quick amounts
        JPanel quickCard = new JPanel(new BorderLayout());
        quickCard.setBackground(CARD_COLOR);
        quickCard.setBorder(createCardBorder());
        quickCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        
        JLabel quickTitle = new JLabel("Valores Rápidos");
        quickTitle.setFont(new Font("Arial", Font.BOLD, 14));
        quickTitle.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        quickCard.add(quickTitle, BorderLayout.NORTH);
        
        JPanel quickGrid = new JPanel(new GridLayout(3, 2, 5, 5));
        quickGrid.setBackground(CARD_COLOR);
        quickGrid.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        
        String[] amounts = {"5.000", "10.000", "25.000", "50.000", "100.000", "250.000"};
        for (String amount : amounts) {
            JButton amtBtn = new JButton(amount + " AOA");
            amtBtn.setFont(new Font("Arial", Font.PLAIN, 10));
            amtBtn.setBackground(SECONDARY_COLOR);
            amtBtn.setFocusPainted(false);
            quickGrid.add(amtBtn);
        }
        
        quickCard.add(quickGrid, BorderLayout.CENTER);
        sidebarPanel.add(quickCard);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(sidebarPanel, BorderLayout.EAST);
        
        panel.add(mainPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createVerifyPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel titleLabel = new JLabel("Verificação NIN/NUIB");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JLabel statusLabel = new JLabel("✅ Conta Verificada");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statusLabel.setForeground(SUCCESS_COLOR);
        statusLabel.setBackground(new Color(220, 252, 231));
        statusLabel.setOpaque(true);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        headerPanel.add(statusLabel, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Content
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // NIN Card
        JPanel ninCard = new JPanel(new BorderLayout());
        ninCard.setBackground(CARD_COLOR);
        ninCard.setBorder(createCardBorder());
        
        JLabel ninTitle = new JLabel("🔒 Verificação NIN");
        ninTitle.setFont(new Font("Arial", Font.BOLD, 16));
        ninTitle.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        ninCard.add(ninTitle, BorderLayout.NORTH);
        
        JPanel ninForm = new JPanel();
        ninForm.setLayout(new BoxLayout(ninForm, BoxLayout.Y_AXIS));
        ninForm.setBackground(CARD_COLOR);
        ninForm.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        
        ninForm.add(createFieldLabel("Número de Identificação Nacional (NIN)"));
        JTextField ninField = createTextField("000000000LA000");
        ninForm.add(ninField);
        ninForm.add(Box.createRigidArea(new Dimension(0, 5)));
        
        JLabel formatLabel = new JLabel("Formato: 9 dígitos + 2 letras + 3 dígitos");
        formatLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        formatLabel.setForeground(MUTED_COLOR);
        ninForm.add(formatLabel);
        ninForm.add(Box.createRigidArea(new Dimension(0, 15)));
        
        JButton verifyNinBtn = new JButton("🔍 Verificar NIN");
        verifyNinBtn.setBackground(PRIMARY_COLOR);
        verifyNinBtn.setForeground(Color.WHITE);
        verifyNinBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        verifyNinBtn.setMaximumSize(new Dimension(200, 35));
        ninForm.add(verifyNinBtn);
        ninForm.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // NIN Status
        JPanel ninStatus = new JPanel();
        ninStatus.setLayout(new BoxLayout(ninStatus, BoxLayout.Y_AXIS));
        ninStatus.setBackground(new Color(220, 252, 231));
        ninStatus.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        ninStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        ninStatus.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        JLabel ninStatusTitle = new JLabel("✅ NIN Verificado");
        ninStatusTitle.setFont(new Font("Arial", Font.BOLD, 12));
        ninStatusTitle.setForeground(SUCCESS_COLOR);
        ninStatus.add(ninStatusTitle);
        
        JLabel ninStatusDesc = new JLabel("Seu NIN foi verificado com sucesso em 15/01/2024 às 10:30");
        ninStatusDesc.setFont(new Font("Arial", Font.PLAIN, 10));
        ninStatusDesc.setForeground(new Color(21, 128, 61));
        ninStatus.add(ninStatusDesc);
        
        ninForm.add(ninStatus);
        ninForm.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // NIN Info
        ninForm.add(createFieldLabel("Informações do NIN"));
        ninForm.add(createDetailRow("NIN:", "123456789LA001"));
        ninForm.add(createDetailRow("Status:", "✅ Ativo"));
        ninForm.add(createDetailRow("Data de Emissão:", "10/03/2020"));
        ninForm.add(createDetailRow("Validade:", "10/03/2030"));
        
        ninCard.add(ninForm, BorderLayout.CENTER);
        
        // NUIB Card
        JPanel nuibCard = new JPanel(new BorderLayout());
        nuibCard.setBackground(CARD_COLOR);
        nuibCard.setBorder(createCardBorder());
        
        JLabel nuibTitle = new JLabel("📄 Verificação NUIB");
        nuibTitle.setFont(new Font("Arial", Font.BOLD, 16));
        nuibTitle.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        nuibCard.add(nuibTitle, BorderLayout.NORTH);
        
        JPanel nuibForm = new JPanel();
        nuibForm.setLayout(new BoxLayout(nuibForm, BoxLayout.Y_AXIS));
        nuibForm.setBackground(CARD_COLOR);
        nuibForm.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        
        nuibForm.add(createFieldLabel("Número Único de Identificação Bancária (NUIB)"));
        JTextField nuibField = createTextField("AO06.0000.0000.0000.0000.0000");
        nuibForm.add(nuibField);
        nuibForm.add(Box.createRigidArea(new Dimension(0, 5)));
        
        JLabel nuibFormatLabel = new JLabel("Formato IBAN: AO + 2 dígitos + 21 dígitos");
        nuibFormatLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        nuibFormatLabel.setForeground(MUTED_COLOR);
        nuibForm.add(nuibFormatLabel);
        nuibForm.add(Box.createRigidArea(new Dimension(0, 15)));
        
        JButton verifyNuibBtn = new JButton("🔍 Verificar NUIB");
        verifyNuibBtn.setBackground(SECONDARY_COLOR);
        verifyNuibBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        verifyNuibBtn.setMaximumSize(new Dimension(200, 35));
        nuibForm.add(verifyNuibBtn);
        nuibForm.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Account Info
        nuibForm.add(createFieldLabel("Informações da Conta"));
        nuibForm.add(createDetailRow("NUIB/IBAN:", "AO06.0045.1234.5678.0000.0001"));
        nuibForm.add(createDetailRow("Banco:", "Banco Digital"));
        nuibForm.add(createDetailRow("Tipo de Conta:", "Conta Corrente"));
        nuibForm.add(createDetailRow("Status:", "✅ Ativa"));
        nuibForm.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Validation Tools
        nuibForm.add(createFieldLabel("Ferramentas de Validação"));
        
        JButton validateBtn = new JButton("✅ Validar IBAN");
        validateBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        validateBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        nuibForm.add(validateBtn);
        nuibForm.add(Box.createRigidArea(new Dimension(0, 5)));
        
        JButton certBtn = new JButton("📄 Gerar Certificado");
        certBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        certBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        nuibForm.add(certBtn);
        
        nuibCard.add(nuibForm, BorderLayout.CENTER);
        
        contentPanel.add(ninCard);
        contentPanel.add(nuibCard);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField(placeholder);
        field.setForeground(MUTED_COLOR);
        field.setBorder(new LineBorder(BORDER_COLOR));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(MUTED_COLOR);
                }
            }
        });
        
        return field;
    }
    
    private JPanel createPlaceholderPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JLabel descLabel = new JLabel("Esta seção será implementada em breve...");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descLabel.setForeground(MUTED_COLOR);
        descLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(descLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private Border createCardBorder() {
        return new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        );
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        add(sidebarPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void switchContent(String section) {
        contentCardLayout.show(contentPanel, section);
        
        // Atualizar título se necessário
        switch (section) {
            case "dashboard":
                showDashboard();
                break;
            case "profile":
                // Lógica específica do perfil
                break;
            // Adicionar outros cases conforme necessário
        }
    }
    
    private void showDashboard() {
        // Lógica específica do dashboard
        contentCardLayout.show(contentPanel, "dashboard");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Configurar aparência
               // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
                
                // Criar e mostrar a janela
                PainelCliente painel = new PainelCliente();
                painel.setVisible(true);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}