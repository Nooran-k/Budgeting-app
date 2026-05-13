package ui;

import javax.swing.*;
import java.awt.*;

/**
 * The WelcomePanel is the entry screen of the application.
 *
 * Responsibilities:
 * - Display application title
 * - Provide navigation to Login and Signup screens
 *
 * This panel is shown first when the application starts.
 */
public class WelcomePanel extends JPanel {

    /**
     * Constructs the welcome panel.
     *
     * @param frame the main application frame used for navigation
     */
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

        // Navigation actions
        btnSignup.addActionListener(e -> frame.showScreen("signup"));
        btnLogin .addActionListener(e -> frame.showScreen("login"));

        box.add(btnSignup);
        box.add(btnLogin);

        add(box);
    }

    /**
     * Creates a styled button used in the welcome screen.
     *
     * @param text button label
     * @return styled JButton
     */
    private JButton makeBtn(String text) {
        JButton b = new JButton(text);

        b.setBackground(new Color(15, 188, 19)); // green theme
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);

        b.setFont(new Font("Arial", Font.BOLD, 14));
        b.setPreferredSize(new Dimension(200, 40));

        return b;
    }
}