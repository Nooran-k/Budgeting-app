package ui;

import javax.swing.*;
import java.awt.*;

public class WelcomePanel extends JPanel {

    public WelcomePanel(MainFrame frame) {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);

        JPanel box = new JPanel(new GridLayout(3, 1, 0, 15));
        box.setBackground(Color.WHITE);

        JLabel title = new JLabel("Budget App", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        box.add(title);

        JButton btnSignup = makeBtn("Sign Up");
        JButton btnLogin  = makeBtn("Log In");

        btnSignup.addActionListener(e -> frame.showScreen("signup"));
        btnLogin .addActionListener(e -> frame.showScreen("login"));

        box.add(btnSignup);
        box.add(btnLogin);
        add(box);
    }

    private JButton makeBtn(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(15, 188, 19));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Arial", Font.BOLD, 14));
        b.setPreferredSize(new Dimension(200, 40));
        return b;
    }
}