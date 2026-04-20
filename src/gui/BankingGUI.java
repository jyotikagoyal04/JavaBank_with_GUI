package gui;

import model.Account;
import model.Transaction;
import model.User;
import service.BankService;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * JavaBank GUI  — Minimal, humane Swing frontend.
 * Palette: warm off-white bg, slate text, teal accent, soft card shadows.
 */
public class BankingGUI extends JFrame {

    // ── Palette ─────────────────────────────────────────────────────────────
    private static final Color BG        = new Color(0xF7F5F2);   // warm off-white
    private static final Color CARD      = new Color(0xFFFFFF);
    private static final Color ACCENT    = new Color(0x2A7C6F);   // deep teal
    private static final Color ACCENT2   = new Color(0x3FA08F);   // lighter teal
    private static final Color TEXT      = new Color(0x1E2A2A);
    private static final Color SUBTEXT   = new Color(0x6B7E7E);
    private static final Color DANGER    = new Color(0xC0392B);
    private static final Color SUCCESS   = new Color(0x27AE60);
    private static final Color DIVIDER   = new Color(0xE2DDD8);
    private static final Color FIELD_BG  = new Color(0xF0EEEB);

    // ── Fonts ────────────────────────────────────────────────────────────────
    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD,  22);
    private static final Font FONT_LABEL  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FONT_BOLD   = new Font("Segoe UI", Font.BOLD,  14);
    private static final Font FONT_MONO   = new Font("Consolas",  Font.PLAIN, 12);
    private static final Font FONT_NUM    = new Font("Segoe UI", Font.BOLD,  26);

    private final BankService bank = new BankService();
    private User currentUser = null;

    // ── Card panels (pages) ──────────────────────────────────────────────────
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel     rootPanel  = new JPanel(cardLayout);

    public BankingGUI() {
        super("JavaBank");
        seedDemoData();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(860, 580));
        setPreferredSize(new Dimension(960, 660));
        getContentPane().setBackground(BG);
        getContentPane().add(rootPanel);

        rootPanel.setBackground(BG);
        rootPanel.add(buildLoginPanel(),     "LOGIN");
        rootPanel.add(buildRegisterPanel(),  "REGISTER");
        rootPanel.add(buildDashboard(),      "DASHBOARD");

        cardLayout.show(rootPanel, "LOGIN");
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ════════════════════════════════════════════════════════════════════════
    // LOGIN PAGE
    // ════════════════════════════════════════════════════════════════════════
    private JPanel buildLoginPanel() {
        JPanel outer = centeredPage();

        JPanel card = roundCard(420, 460);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(44, 48, 44, 48));

        // Logo / brand
        JLabel logo = new JLabel("🏦 JavaBank");
        logo.setFont(FONT_TITLE);
        logo.setForeground(ACCENT);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = label("Your simple, secure banking companion.", SUBTEXT, FONT_SMALL);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel heading = label("Sign in", TEXT, FONT_BOLD);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField userField = styledField("Username");
        JPasswordField passField = styledPass("Password");

        JLabel errLabel = label("", DANGER, FONT_SMALL);
        errLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton loginBtn = accentButton("Sign In");
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel switchLabel = label("Don't have an account?  ", SUBTEXT, FONT_SMALL);
        JButton regLink = linkButton("Register here");
        JPanel switchRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        switchRow.setBackground(CARD);
        switchRow.add(switchLabel);
        switchRow.add(regLink);

        loginBtn.addActionListener(e -> {
            String u = userField.getText().trim();
            String p = new String(passField.getPassword()).trim();
            if (u.isEmpty() || p.isEmpty()) { errLabel.setText("Please fill in all fields."); return; }
            currentUser = bank.login(u, p);
            if (currentUser == null) {
                errLabel.setText("Incorrect username or password.");
                passField.setText("");
            } else {
                errLabel.setText("");
                refreshDashboard();
                cardLayout.show(rootPanel, "DASHBOARD");
            }
        });
        regLink.addActionListener(e -> cardLayout.show(rootPanel, "REGISTER"));

        card.add(logo);
        card.add(vgap(4));
        card.add(sub);
        card.add(vgap(32));
        card.add(heading);
        card.add(vgap(14));
        card.add(labelFor("Username"));
        card.add(vgap(4));
        card.add(userField);
        card.add(vgap(14));
        card.add(labelFor("Password"));
        card.add(vgap(4));
        card.add(passField);
        card.add(vgap(6));
        card.add(errLabel);
        card.add(vgap(22));
        card.add(loginBtn);
        card.add(vgap(18));
        card.add(switchRow);

        outer.add(card);
        return outer;
    }

    // ════════════════════════════════════════════════════════════════════════
    // REGISTER PAGE
    // ════════════════════════════════════════════════════════════════════════
    private JPanel buildRegisterPanel() {
        JPanel outer = centeredPage();

        JPanel card = roundCard(420, 500);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(40, 48, 40, 48));

        JLabel logo = new JLabel("🏦 JavaBank");
        logo.setFont(FONT_TITLE);
        logo.setForeground(ACCENT);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel heading = label("Create an account", TEXT, FONT_BOLD);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField nameField = styledField("Full Name");
        JTextField userField = styledField("Username");
        JPasswordField passField = styledPass("Password");
        JTextField initField  = styledField("Opening Deposit (₹)");

        JLabel errLabel = label("", DANGER, FONT_SMALL);
        errLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel okLabel  = label("", SUCCESS, FONT_SMALL);
        okLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton regBtn = accentButton("Create Account");
        regBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel switchLabel = label("Already have an account?  ", SUBTEXT, FONT_SMALL);
        JButton loginLink = linkButton("Sign in");
        JPanel switchRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        switchRow.setBackground(CARD);
        switchRow.add(switchLabel);
        switchRow.add(loginLink);

        regBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String u    = userField.getText().trim();
            String p    = new String(passField.getPassword()).trim();
            String init = initField.getText().trim();
            if (name.isEmpty() || u.isEmpty() || p.isEmpty()) {
                errLabel.setText("Please fill in Name, Username and Password.");
                okLabel.setText(""); return;
            }
            double dep = 0;
            if (!init.isEmpty()) {
                try { dep = Double.parseDouble(init); if (dep < 0) throw new NumberFormatException(); }
                catch (NumberFormatException ex) { errLabel.setText("Enter a valid deposit amount."); return; }
            }
            if (bank.register(u, p, name)) {
                User nu = bank.login(u, p);
                if (dep > 0) bank.openAccount(nu, dep);
                errLabel.setText("");
                okLabel.setText("Account created! Please sign in.");
                nameField.setText(""); userField.setText(""); passField.setText(""); initField.setText("");
            } else {
                errLabel.setText("Username already taken. Choose another.");
                okLabel.setText("");
            }
        });
        loginLink.addActionListener(e -> cardLayout.show(rootPanel, "LOGIN"));

        card.add(logo);
        card.add(vgap(24));
        card.add(heading);
        card.add(vgap(12));
        card.add(labelFor("Full Name"));       card.add(vgap(4)); card.add(nameField); card.add(vgap(10));
        card.add(labelFor("Username"));        card.add(vgap(4)); card.add(userField); card.add(vgap(10));
        card.add(labelFor("Password"));        card.add(vgap(4)); card.add(passField); card.add(vgap(10));
        card.add(labelFor("Opening Deposit (₹) — optional")); card.add(vgap(4)); card.add(initField); card.add(vgap(6));
        card.add(errLabel);
        card.add(okLabel);
        card.add(vgap(18));
        card.add(regBtn);
        card.add(vgap(14));
        card.add(switchRow);

        outer.add(card);
        return outer;
    }

    // ════════════════════════════════════════════════════════════════════════
    // DASHBOARD
    // ════════════════════════════════════════════════════════════════════════
    private JPanel dashRoot;
    private JPanel accountsArea;
    private JLabel greetLabel;
    private JLabel totalBalLabel;

    private JPanel buildDashboard() {
        dashRoot = new JPanel(new BorderLayout(0, 0));
        dashRoot.setBackground(BG);

        // ── Sidebar ──────────────────────────────────────────────────────────
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(0x1C3535));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(new EmptyBorder(32, 20, 32, 20));

        JLabel brandLbl = new JLabel("🏦 JavaBank");
        brandLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        brandLbl.setForeground(Color.WHITE);
        brandLbl.setAlignmentX(LEFT_ALIGNMENT);

        sidebar.add(brandLbl);
        sidebar.add(vgap(32));

        String[] navLabels = { "Accounts", "Deposit", "Withdraw", "Transfer", "History", "New Account" };
        String[] navIcons  = { "💳", "⬇", "⬆", "↔", "📋", "✚" };
        JPanel[] navBtns   = new JPanel[navLabels.length];

        for (int i = 0; i < navLabels.length; i++) {
            final int idx = i;
            navBtns[i] = navItem(navIcons[i], navLabels[i]);
            navBtns[i].addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { handleNavClick(idx); }
                @Override public void mouseEntered(MouseEvent e) { navBtns[idx].setBackground(new Color(0x2A4A4A)); }
                @Override public void mouseExited(MouseEvent e)  { navBtns[idx].setBackground(new Color(0x1C3535)); }
            });
            sidebar.add(navBtns[i]);
            sidebar.add(vgap(4));
        }

        sidebar.add(Box.createVerticalGlue());

        JPanel logoutBtn = navItem("⎋", "Logout");
        logoutBtn.setBackground(new Color(0x3D1515));
        logoutBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                currentUser = null;
                cardLayout.show(rootPanel, "LOGIN");
            }
            @Override public void mouseEntered(MouseEvent e) { logoutBtn.setBackground(new Color(0x5A1F1F)); }
            @Override public void mouseExited(MouseEvent e)  { logoutBtn.setBackground(new Color(0x3D1515)); }
        });
        sidebar.add(logoutBtn);

        // ── Main area ─────────────────────────────────────────────────────────
        JPanel main = new JPanel(new BorderLayout(0, 0));
        main.setBackground(BG);

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(CARD);
        topBar.setBorder(new CompoundBorder(
                new MatteBorder(0,0,1,0, DIVIDER),
                new EmptyBorder(16,28,16,28)
        ));
        greetLabel   = label("Welcome!", TEXT, FONT_BOLD);
        totalBalLabel = label("", ACCENT, new Font("Segoe UI", Font.BOLD, 13));
        topBar.add(greetLabel,    BorderLayout.WEST);
        topBar.add(totalBalLabel, BorderLayout.EAST);

        // Content area (CardLayout for different views)
        accountsArea = new JPanel(new CardLayout());
        accountsArea.setBackground(BG);

        accountsArea.add(buildAccountsView(), "ACCOUNTS");
        accountsArea.add(buildTransactionForm("DEPOSIT"),  "DEPOSIT");
        accountsArea.add(buildTransactionForm("WITHDRAW"), "WITHDRAW");
        accountsArea.add(buildTransferView(),  "TRANSFER");
        accountsArea.add(buildHistoryView(),   "HISTORY");
        accountsArea.add(buildOpenAccountView(), "NEWACCOUNT");

        main.add(topBar,       BorderLayout.NORTH);
        main.add(accountsArea, BorderLayout.CENTER);

        dashRoot.add(sidebar, BorderLayout.WEST);
        dashRoot.add(main,    BorderLayout.CENTER);
        return dashRoot;
    }

    private void handleNavClick(int idx) {
        CardLayout cl = (CardLayout) accountsArea.getLayout();
        switch (idx) {
            case 0 -> { refreshAccountsView(); cl.show(accountsArea, "ACCOUNTS"); }
            case 1 -> { refreshTxnForm("DEPOSIT");  cl.show(accountsArea, "DEPOSIT"); }
            case 2 -> { refreshTxnForm("WITHDRAW"); cl.show(accountsArea, "WITHDRAW"); }
            case 3 -> cl.show(accountsArea, "TRANSFER");
            case 4 -> { refreshHistoryView(); cl.show(accountsArea, "HISTORY"); }
            case 5 -> cl.show(accountsArea, "NEWACCOUNT");
        }
    }

    // ── Accounts overview ────────────────────────────────────────────────────
    private JPanel accountsViewPanel;
    private JPanel accountCardsContainer;

    private JPanel buildAccountsView() {
        accountsViewPanel = new JPanel(new BorderLayout());
        accountsViewPanel.setBackground(BG);
        accountsViewPanel.setBorder(new EmptyBorder(28, 32, 28, 32));

        JLabel heading = label("My Accounts", TEXT, new Font("Segoe UI", Font.BOLD, 18));

        accountCardsContainer = new JPanel();
        accountCardsContainer.setLayout(new BoxLayout(accountCardsContainer, BoxLayout.Y_AXIS));
        accountCardsContainer.setBackground(BG);

        JScrollPane scroll = new JScrollPane(accountCardsContainer);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setBackground(BG);
        scroll.getViewport().setBackground(BG);

        accountsViewPanel.add(heading, BorderLayout.NORTH);
        accountsViewPanel.add(vgap(16), BorderLayout.CENTER);
        accountsViewPanel.add(scroll, BorderLayout.CENTER);
        return accountsViewPanel;
    }

    private void refreshAccountsView() {
        accountCardsContainer.removeAll();
        if (currentUser == null) return;
        List<Account> accs = currentUser.getAccounts();
        if (accs.isEmpty()) {
            JLabel empty = label("No accounts yet. Use 'New Account' to open one.", SUBTEXT, FONT_LABEL);
            empty.setAlignmentX(LEFT_ALIGNMENT);
            accountCardsContainer.add(vgap(16));
            accountCardsContainer.add(empty);
        } else {
            for (Account a : accs) {
                accountCardsContainer.add(buildAccountCard(a));
                accountCardsContainer.add(vgap(12));
            }
        }
        accountCardsContainer.revalidate();
        accountCardsContainer.repaint();
    }

    private JPanel buildAccountCard(Account a) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(DIVIDER, 1, true),
                new EmptyBorder(18, 22, 18, 22)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JLabel accNo  = label(a.getAccountNumber(), SUBTEXT, FONT_MONO);
        JLabel bal    = new JLabel(String.format("₹ %.2f", a.getBalance()));
        bal.setFont(FONT_NUM);
        bal.setForeground(ACCENT);

        JLabel txnCount = label(a.getTransactions().size() + " transactions", SUBTEXT, FONT_SMALL);

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBackground(CARD);
        left.add(accNo);
        left.add(vgap(4));
        left.add(txnCount);

        card.add(left, BorderLayout.WEST);
        card.add(bal,  BorderLayout.EAST);
        return card;
    }

    // ── Deposit / Withdraw form ───────────────────────────────────────────────
    private JComboBox<String> depositAccCombo, withdrawAccCombo;
    private JTextField depositAmtField, withdrawAmtField;
    private JLabel depositStatus, withdrawStatus;

    private JPanel buildTransactionForm(String type) {
        JPanel outer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 60));
        outer.setBackground(BG);

        JPanel card = roundCard(400, 340);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(36, 44, 36, 44));

        boolean isDeposit = type.equals("DEPOSIT");
        String title  = isDeposit ? "Deposit Funds" : "Withdraw Funds";
        String icon   = isDeposit ? "⬇" : "⬆";
        Color  color  = isDeposit ? SUCCESS : DANGER;

        JLabel heading = label(icon + "  " + title, TEXT, FONT_BOLD);
        heading.setAlignmentX(LEFT_ALIGNMENT);

        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(FONT_MONO);
        combo.setBackground(FIELD_BG);
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JTextField amtField = styledField("Amount (₹)");
        JLabel     status   = label("", color, FONT_SMALL);
        status.setAlignmentX(LEFT_ALIGNMENT);

        JButton btn = new JButton(title);
        styleButton(btn, color);
        btn.setAlignmentX(CENTER_ALIGNMENT);

        if (isDeposit) { depositAccCombo = combo; depositAmtField = amtField; depositStatus = status; }
        else           { withdrawAccCombo = combo; withdrawAmtField = amtField; withdrawStatus = status; }

        btn.addActionListener(e -> {
            String sel = (String) combo.getSelectedItem();
            if (sel == null) { status.setText("No account selected."); return; }
            String accNo = sel.split(" ")[0];
            Account acc  = bank.findAccount(accNo);
            if (acc == null) { status.setText("Account not found."); return; }
            double amount;
            try { amount = Double.parseDouble(amtField.getText().trim()); if (amount <= 0) throw new NumberFormatException(); }
            catch (NumberFormatException ex) { status.setText("Enter a valid positive amount."); return; }

            boolean ok = isDeposit ? bank.deposit(acc, amount) : bank.withdraw(acc, amount);
            if (ok) {
                status.setForeground(SUCCESS);
                status.setText(String.format("Done! New balance: ₹%.2f", acc.getBalance()));
                amtField.setText("");
                refreshTopBar();
                refreshCombo(combo);
            } else {
                status.setForeground(DANGER);
                status.setText(isDeposit ? "Deposit failed." : "Insufficient funds.");
            }
        });

        card.add(heading);
        card.add(vgap(22));
        card.add(labelFor("Account"));
        card.add(vgap(4));
        card.add(combo);
        card.add(vgap(14));
        card.add(labelFor("Amount (₹)"));
        card.add(vgap(4));
        card.add(amtField);
        card.add(vgap(6));
        card.add(status);
        card.add(vgap(20));
        card.add(btn);

        outer.add(card);
        return outer;
    }

    private void refreshTxnForm(String type) {
        JComboBox<String> combo = type.equals("DEPOSIT") ? depositAccCombo : withdrawAccCombo;
        if (combo != null && currentUser != null) refreshCombo(combo);
    }

    // ── Transfer view ────────────────────────────────────────────────────────
    private JComboBox<String> transferFromCombo;
    private JTextField transferToField, transferAmtField;
    private JLabel transferStatus;

    private JPanel buildTransferView() {
        JPanel outer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 60));
        outer.setBackground(BG);

        JPanel card = roundCard(420, 380);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(36, 44, 36, 44));

        JLabel heading = label("↔  Transfer Funds", TEXT, FONT_BOLD);
        heading.setAlignmentX(LEFT_ALIGNMENT);

        transferFromCombo = new JComboBox<>();
        transferFromCombo.setFont(FONT_MONO);
        transferFromCombo.setBackground(FIELD_BG);
        transferFromCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        transferToField  = styledField("Destination Account Number");
        transferAmtField = styledField("Amount (₹)");
        transferStatus   = label("", SUCCESS, FONT_SMALL);
        transferStatus.setAlignmentX(LEFT_ALIGNMENT);

        JButton btn = accentButton("Send Money");
        btn.setAlignmentX(CENTER_ALIGNMENT);

        btn.addActionListener(e -> {
            String sel = (String) transferFromCombo.getSelectedItem();
            if (sel == null) { transferStatus.setForeground(DANGER); transferStatus.setText("Select a source account."); return; }
            String fromNo = sel.split(" ")[0];
            Account from  = bank.findAccount(fromNo);
            String  toNo  = transferToField.getText().trim();
            double  amt;
            try { amt = Double.parseDouble(transferAmtField.getText().trim()); if (amt <= 0) throw new NumberFormatException(); }
            catch (NumberFormatException ex) { transferStatus.setForeground(DANGER); transferStatus.setText("Enter a valid amount."); return; }

            String result = bank.transfer(from, toNo, amt);
            if ("SUCCESS".equals(result)) {
                transferStatus.setForeground(SUCCESS);
                transferStatus.setText(String.format("₹%.2f sent!  New balance: ₹%.2f", amt, from.getBalance()));
                transferAmtField.setText("");
                refreshTopBar();
                refreshCombo(transferFromCombo);
            } else {
                transferStatus.setForeground(DANGER);
                transferStatus.setText(result);
            }
        });

        card.add(heading);
        card.add(vgap(22));
        card.add(labelFor("From Account"));
        card.add(vgap(4));
        card.add(transferFromCombo);
        card.add(vgap(14));
        card.add(labelFor("To Account Number"));
        card.add(vgap(4));
        card.add(transferToField);
        card.add(vgap(14));
        card.add(labelFor("Amount (₹)"));
        card.add(vgap(4));
        card.add(transferAmtField);
        card.add(vgap(6));
        card.add(transferStatus);
        card.add(vgap(20));
        card.add(btn);

        outer.add(card);
        return outer;
    }

    // ── History view ─────────────────────────────────────────────────────────
    private JComboBox<String> histAccCombo;
    private JTable histTable;
    private DefaultTableModel histModel;

    private JPanel buildHistoryView() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(28, 32, 28, 32));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        top.setBackground(BG);
        JLabel heading = label("📋  Transaction History", TEXT, new Font("Segoe UI", Font.BOLD, 18));
        histAccCombo = new JComboBox<>();
        histAccCombo.setFont(FONT_MONO);
        histAccCombo.setPreferredSize(new Dimension(260, 32));
        JButton loadBtn = accentButton("Load");
        loadBtn.setPreferredSize(new Dimension(80, 32));

        top.add(heading);
        top.add(Box.createHorizontalStrut(24));
        top.add(histAccCombo);
        top.add(Box.createHorizontalStrut(8));
        top.add(loadBtn);

        String[] cols = { "Date & Time", "Type", "Amount (₹)", "Balance After (₹)", "Note" };
        histModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        histTable = new JTable(histModel);
        histTable.setFont(FONT_LABEL);
        histTable.setRowHeight(30);
        histTable.setBackground(CARD);
        histTable.setForeground(TEXT);
        histTable.setGridColor(DIVIDER);
        histTable.setSelectionBackground(new Color(0xD6EDE9));
        histTable.setSelectionForeground(TEXT);
        histTable.getTableHeader().setFont(FONT_BOLD);
        histTable.getTableHeader().setBackground(new Color(0xECEAE6));
        histTable.getTableHeader().setForeground(SUBTEXT);

        // Color rows by type
        histTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setBackground(sel ? new Color(0xD6EDE9) : (r % 2 == 0 ? CARD : new Color(0xF9F7F5)));
                String type = (String) t.getModel().getValueAt(r, 1);
                if (!sel) {
                    if ("DEPOSIT".equals(type) || "TRANSFER IN".equals(type))  setForeground(SUCCESS);
                    else if ("WITHDRAWAL".equals(type) || "TRANSFER OUT".equals(type)) setForeground(DANGER);
                    else setForeground(TEXT);
                }
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(histTable);
        scroll.setBorder(new LineBorder(DIVIDER, 1));
        scroll.getViewport().setBackground(CARD);

        loadBtn.addActionListener(e -> refreshHistoryTable());

        panel.add(top, BorderLayout.NORTH);
        panel.add(vgap(16), BorderLayout.AFTER_LAST_LINE);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void refreshHistoryView() {
        if (histAccCombo != null && currentUser != null) refreshCombo(histAccCombo);
        histModel.setRowCount(0);
    }

    private void refreshHistoryTable() {
        histModel.setRowCount(0);
        String sel = (String) histAccCombo.getSelectedItem();
        if (sel == null || currentUser == null) return;
        String accNo = sel.split(" ")[0];
        Account acc  = bank.findAccount(accNo);
        if (acc == null) return;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy  HH:mm");
        for (Transaction t : acc.getTransactions()) {
            String typeName = switch (t.getType()) {
                case DEPOSIT      -> "DEPOSIT";
                case WITHDRAWAL   -> "WITHDRAWAL";
                case TRANSFER_IN  -> "TRANSFER IN";
                case TRANSFER_OUT -> "TRANSFER OUT";
            };
            histModel.addRow(new Object[]{
                    t.getTimestamp().format(fmt),
                    typeName,
                    String.format("%.2f", t.getAmount()),
                    String.format("%.2f", t.getBalanceAfter()),
                    t.getNote()
            });
        }
    }

    // ── Open new account view ────────────────────────────────────────────────
    private JPanel buildOpenAccountView() {
        JPanel outer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 80));
        outer.setBackground(BG);

        JPanel card = roundCard(380, 280);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(36, 44, 36, 44));

        JLabel heading = label("✚  Open New Account", TEXT, FONT_BOLD);
        heading.setAlignmentX(LEFT_ALIGNMENT);

        JTextField initField = styledField("Initial Deposit (₹) — optional");
        JLabel     status    = label("", SUCCESS, FONT_SMALL);
        status.setAlignmentX(LEFT_ALIGNMENT);

        JButton btn = accentButton("Open Account");
        btn.setAlignmentX(CENTER_ALIGNMENT);

        btn.addActionListener(e -> {
            double dep = 0;
            String txt = initField.getText().trim();
            if (!txt.isEmpty()) {
                try { dep = Double.parseDouble(txt); if (dep < 0) throw new NumberFormatException(); }
                catch (NumberFormatException ex) { status.setForeground(DANGER); status.setText("Enter a valid deposit amount."); return; }
            }
            Account acc = bank.openAccount(currentUser, dep);
            status.setForeground(SUCCESS);
            status.setText("Opened! Account: " + acc.getAccountNumber());
            initField.setText("");
            refreshTopBar();
        });

        card.add(heading);
        card.add(vgap(22));
        card.add(labelFor("Initial Deposit (₹) — optional"));
        card.add(vgap(4));
        card.add(initField);
        card.add(vgap(6));
        card.add(status);
        card.add(vgap(22));
        card.add(btn);

        outer.add(card);
        return outer;
    }

    // ════════════════════════════════════════════════════════════════════════
    // DASHBOARD REFRESH HELPERS
    // ════════════════════════════════════════════════════════════════════════
    private void refreshDashboard() {
        refreshTopBar();
        refreshAccountsView();
        if (depositAccCombo != null)  refreshCombo(depositAccCombo);
        if (withdrawAccCombo != null) refreshCombo(withdrawAccCombo);
        if (transferFromCombo != null) refreshCombo(transferFromCombo);
        if (histAccCombo != null)     refreshCombo(histAccCombo);
        // Show accounts by default
        ((CardLayout) accountsArea.getLayout()).show(accountsArea, "ACCOUNTS");
    }

    private void refreshTopBar() {
        if (currentUser == null) return;
        greetLabel.setText("Hello, " + currentUser.getFullName() + " 👋");
        double total = currentUser.getAccounts().stream().mapToDouble(Account::getBalance).sum();
        totalBalLabel.setText("Total Balance:  ₹" + String.format("%.2f", total));
    }

    private void refreshCombo(JComboBox<String> combo) {
        combo.removeAllItems();
        if (currentUser == null) return;
        for (Account a : currentUser.getAccounts()) {
            combo.addItem(a.getAccountNumber() + "  ₹" + String.format("%.2f", a.getBalance()));
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // UI HELPERS
    // ════════════════════════════════════════════════════════════════════════
    private JPanel centeredPage() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(BG);
        return p;
    }

    private JPanel roundCard(int w, int h) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),18,18));
                g2.setColor(DIVIDER);
                g2.draw(new RoundRectangle2D.Float(0,0,getWidth()-1,getHeight()-1,18,18));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(w, h));
        return card;
    }

    private JPanel navItem(String icon, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        p.setBackground(new Color(0x1C3535));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel iconL = new JLabel(icon);
        iconL.setForeground(Color.WHITE);
        iconL.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel textL = new JLabel(text);
        textL.setForeground(new Color(0xCCE8E4));
        textL.setFont(FONT_LABEL);

        p.add(iconL);
        p.add(textL);
        return p;
    }

    private JLabel label(String text, Color color, Font font) {
        JLabel l = new JLabel(text);
        l.setForeground(color);
        l.setFont(font);
        return l;
    }

    private JLabel labelFor(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_SMALL);
        l.setForeground(SUBTEXT);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JTextField styledField(String placeholder) {
        JTextField f = new JTextField();
        f.setFont(FONT_LABEL);
        f.setBackground(FIELD_BG);
        f.setForeground(TEXT);
        f.setCaretColor(ACCENT);
        f.setBorder(new CompoundBorder(
                new LineBorder(DIVIDER, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        f.setAlignmentX(LEFT_ALIGNMENT);
        return f;
    }

    private JPasswordField styledPass(String placeholder) {
        JPasswordField f = new JPasswordField();
        f.setFont(FONT_LABEL);
        f.setBackground(FIELD_BG);
        f.setForeground(TEXT);
        f.setCaretColor(ACCENT);
        f.setBorder(new CompoundBorder(
                new LineBorder(DIVIDER, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        f.setAlignmentX(LEFT_ALIGNMENT);
        return f;
    }

    private JButton accentButton(String text) {
        JButton b = new JButton(text);
        styleButton(b, ACCENT);
        return b;
    }

    private void styleButton(JButton b, Color bg) {
        b.setFont(FONT_BOLD);
        b.setForeground(Color.WHITE);
        b.setBackground(bg);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        b.setBorder(new EmptyBorder(10, 24, 10, 24));
        b.addMouseListener(new MouseAdapter() {
            final Color orig = bg;
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(orig.darker()); }
            @Override public void mouseExited(MouseEvent e)  { b.setBackground(orig); }
        });
    }

    private JButton linkButton(String text) {
        JButton b = new JButton(text);
        b.setFont(FONT_SMALL);
        b.setForeground(ACCENT2);
        b.setBackground(CARD);
        b.setOpaque(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder());
        return b;
    }

    private Component vgap(int h) { return Box.createRigidArea(new Dimension(0, h)); }

    // ════════════════════════════════════════════════════════════════════════
    // SEED DATA
    // ════════════════════════════════════════════════════════════════════════
    private void seedDemoData() {
        bank.register("alice", "alice123", "Alice Sharma");
        bank.register("bob",   "bob123",   "Bob Verma");
        User alice = bank.login("alice", "alice123");
        User bob   = bank.login("bob",   "bob123");
        bank.openAccount(alice, 5000);
        bank.openAccount(bob,   3000);
    }

    // ════════════════════════════════════════════════════════════════════════
    // ENTRY POINT
    // ════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(BankingGUI::new);
    }
}
