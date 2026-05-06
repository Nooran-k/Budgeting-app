package view;

import controller.*;
import data.Database;
import model.User;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final CardLayout   cardLayout = new CardLayout();
    private final JPanel       cards      = new JPanel(cardLayout);

    // Controllers created once, reused for all views
    private NotificationController nc;
    private BudgetController       bc;
    private TransactionController  tc;
    private GoalController         gc;
    private ReportController       rc;

    public MainFrame() {
        setTitle("Budget App");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(750, 580);
        setLocationRelativeTo(null);

        // Build controllers
        nc = new NotificationController();
        bc = new BudgetController(nc);
        tc = new TransactionController(bc);
        gc = new GoalController(nc);
        rc = new ReportController();

        // Add auth screens (no user yet)
        cards.add(new WelcomePanel(this), "welcome");
        cards.add(new LoginPanel(this),   "login");
        cards.add(new SignupPanel(this),  "signup");

        add(cards);
        cardLayout.show(cards, "welcome");
        setVisible(true);
    }

    /** Called by LoginPanel and SignupPanel after successful auth */
    public void showDashboard(User user) {
        String email = user.getEmail();
        String name  = user.getName();

        // Build the dashboard tabs for this user
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Transactions", new AddTransactionView(tc, nc, email));
        tabs.addTab("Budgets",      new BudgetView(bc, email));
        tabs.addTab("Goals",        new GoalView(gc, email));
        tabs.addTab("Reports",      new ReportView(rc, email));

        // Wrap in a panel with a logout button at the top
        JPanel dashboard = new JPanel(new BorderLayout());
        JPanel topBar    = new JPanel(new BorderLayout());
        topBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        topBar.setBackground(new Color(15, 188, 19));

        JLabel lblWelcome = new JLabel("Welcome, " + name + "!");
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 14));

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(Color.WHITE);
        btnLogout.setForeground(new Color(15, 188, 19));
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> showScreen("welcome"));

        topBar.add(lblWelcome, BorderLayout.WEST);
        topBar.add(btnLogout,  BorderLayout.EAST);

        dashboard.add(topBar, BorderLayout.NORTH);
        dashboard.add(tabs,   BorderLayout.CENTER);

        // Add the dashboard panel with a unique key per user
        String key = "dashboard_" + email;
        cards.add(dashboard, key);
        cardLayout.show(cards, key);
    }

    /** Switch between welcome / login / signup screens */
    public void showScreen(String name) {
        cardLayout.show(cards, name);
    }
}