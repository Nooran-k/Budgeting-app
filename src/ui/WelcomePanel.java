package ui;

import javax.swing.*;
import java.awt.*;

public class WelcomePanel extends JPanel {

    public WelcomePanel(MainFrame frame) {

        setLayout(new GridBagLayout()); // center container
        setBackground(Color.white);

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(Color.white);

        JButton signup = new JButton("Sign Up");
        JButton login = new JButton("Log In");

        // style
        signup.setForeground(Color.white);
        login.setForeground(Color.white);
        signup.setBackground(new Color(15, 188, 19));
        login.setBackground(new Color(15, 188, 19));

        signup.setFocusPainted(false);
        login.setFocusPainted(false);

        // 🔹 small upgrade (padding inside button)
        signup.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        login.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // same width
        Dimension size = new Dimension(200, 40);
        signup.setMaximumSize(size);
        login.setMaximumSize(size);

        signup.setAlignmentX(Component.CENTER_ALIGNMENT);
        login.setAlignmentX(Component.CENTER_ALIGNMENT);

        signup.addActionListener(e -> frame.showScreen("signup"));
        login.addActionListener(e -> frame.showScreen("login"));

        // layout (vertical)
        box.add(signup);
        box.add(Box.createVerticalStrut(15)); // spacing
        box.add(login);

        add(box);
    }
}