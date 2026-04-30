package ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    CardLayout layout;
    JPanel container;

    public MainFrame() {
        setTitle("Budget App");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        layout = new CardLayout();
        container = new JPanel(layout);

        // add screens
        container.add(new WelcomePanel(this), "welcome");
        container.add(new SignupPanel(this), "signup");
        container.add(new LoginPanel(this), "login");

        add(container);
        setVisible(true);
    }

    public void showScreen(String name) {
        layout.show(container, name);
    }

    public void showDashboard(String username) {
        container.add(new DashboardPanel(this, username), "dashboard");
        layout.show(container, "dashboard");
    }
}