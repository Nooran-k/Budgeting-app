package ui;

import service.*;
import model.User;
import ui.LoginPanel;
import ui.SignupPanel;
import ui.WelcomePanel;

import javax.swing.*;
import java.awt.*;

/**
 * MainFrame is the main window of the application.
 * It manages all screens using CardLayout and initializes controllers.
 */
public class MainFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    // Controllers
    private NotificationController nc;
    private BudgetController bc;
    private TransactionController tc;
    private GoalController gc;
    private ReportController rc;

    /**
     * Constructs the MainFrame and initializes the application.
     */
    public MainFrame() {
        setTitle("Budget App");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(750, 580);
        setLocationRelativeTo(null);

        // Initialize controllers
        nc = new NotificationController();
        bc = new BudgetController(nc);
        tc = new TransactionController(bc);
        gc = new GoalController(nc);
        rc = new ReportController();

        // Add initial screens
        cards.add(new WelcomePanel(this), "welcome");
        cards.add(new LoginPanel(this), "login");
        cards.add(new SignupPanel(this), "signup");

        add(cards);

        // Show welcome screen
        cardLayout.show(cards, "welcome");

        setVisible(true);
    }

    /**
     * Displays the dashboard after successful login/signup.
     *
     * @param user The logged-in user
     */
    public void showDashboard(User user) {

        String email = user.getEmail();
        String name = user.getName();

        // Create tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Transactions", new AddTransactionView(tc, nc, email));
        tabs.addTab("Budgets", new BudgetView(bc, email));
        tabs.addTab("Goals", new GoalView(gc, email));
        tabs.addTab("Reports", new ReportView(rc, email));

        // Top bar
        JPanel dashboard = new JPanel(new BorderLayout());

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        topBar.setBackground(new Color(15, 188, 19));

        JLabel lblWelcome = new JLabel("Welcome, " + name + "!");
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 14));

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(Color.WHITE);
        btnLogout.setForeground(new Color(15, 188, 19));
        btnLogout.setFocusPainted(false);

        // Logout action
        btnLogout.addActionListener(e -> showScreen("welcome"));

        topBar.add(lblWelcome, BorderLayout.WEST);
        topBar.add(btnLogout, BorderLayout.EAST);

        dashboard.add(topBar, BorderLayout.NORTH);
        dashboard.add(tabs, BorderLayout.CENTER);

        // Unique key per user
        String key = "dashboard_" + email;

        cards.add(dashboard, key);
        cardLayout.show(cards, key);
    }

    /**
     * Switches between different screens (welcome, login, signup).
     *
     * @param name Screen name
     */
    public void showScreen(String name) {
        cardLayout.show(cards, name);
    }
}
