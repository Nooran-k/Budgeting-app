package ui;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {

    public DashboardPanel(MainFrame frame, String name) {
        setBackground(new Color(144, 238, 160));

        JLabel welcome = new JLabel("Welcome, " + name);
        welcome.setFont(new Font("Arial", Font.BOLD, 18));

        add(welcome);
    }
}