package ui;

import service.AuthController;
import model.User;

import javax.swing.*;
import java.awt.*;

/**
 * LoginPanel provides the user interface for user authentication.
 * It allows users to enter their email and password,
 * validates them using AuthController, and redirects
 * to the dashboard upon successful login.
 */
public class LoginPanel extends JPanel {

    /**
     * Constructs the LoginPanel and initializes UI components.
     *
     * @param frame The main application frame used for navigation
     */
    public LoginPanel(MainFrame frame) {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createTitledBorder("Log In"));

        // Input Fields
        JTextField txtEmail = new JTextField();
        JPasswordField txtPass = new JPasswordField();

        // Set preferred size
        txtEmail.setPreferredSize(new Dimension(300, 40));
        txtPass.setPreferredSize(new Dimension(300, 40));

        // Set font
        txtEmail.setFont(new Font("Arial", Font.PLAIN, 16));
        txtPass.setFont(new Font("Arial", Font.PLAIN, 16));

        JLabel lblMsg = new JLabel(" ");
        lblMsg.setForeground(Color.RED);

        JButton btnLogin = makeBtn("Log In");

        JButton btnBack = new JButton("← Back");
        btnBack.setFocusPainted(false);
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);

        // Add components
        form.add(labeled("Email:", txtEmail));
        form.add(Box.createVerticalStrut(10));

        form.add(labeled("Password:", txtPass));
        form.add(Box.createVerticalStrut(10));

        form.add(lblMsg);
        form.add(Box.createVerticalStrut(10));

        form.add(btnLogin);
        form.add(Box.createVerticalStrut(10));

        form.add(btnBack);

        // Login Action
        btnLogin.addActionListener(e -> {
            lblMsg.setText(" ");

            User user = AuthController.login(
                    txtEmail.getText(),
                    new String(txtPass.getPassword())
            );

            if (user != null) {
                frame.showDashboard(user);
            } else {
                lblMsg.setText("Invalid email or password.");
            }
        });

        // Back Action
        btnBack.addActionListener(e -> frame.showScreen("welcome"));

        add(form);
    }

    /**
     * Creates a labeled input field panel.
     *
     * @param label Text label
     * @param field Input component
     * @return JPanel containing label and field
     */
    private JPanel labeled(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(10, 5));
        p.setBackground(Color.WHITE);

        JLabel lbl = new JLabel(label);
        lbl.setPreferredSize(new Dimension(80, 40));

        p.add(lbl, BorderLayout.WEST);
        p.add(field, BorderLayout.CENTER);

        return p;
    }

    /**
     * Creates a styled button.
     *
     * @param text Button text
     * @return JButton with custom style
     */
    private JButton makeBtn(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(15, 188, 19));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Arial", Font.BOLD, 14));
        b.setPreferredSize(new Dimension(300, 40));
        return b;
    }
}