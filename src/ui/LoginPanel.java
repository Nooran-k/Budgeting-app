package ui;

import model.User;
import service.AuthController;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {

    public LoginPanel(MainFrame frame) {

        setLayout(new GridBagLayout()); // center everything
        setBackground(Color.white);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.white);

        JTextField email = new JTextField();
        JPasswordField pass = new JPasswordField();

        JLabel msg = new JLabel();
        msg.setForeground(Color.RED);

        JButton login = new JButton("Login");
        login.setForeground(Color.white);
        login.setBackground(new Color(15, 188, 19));
        login.setFocusPainted(false);

        // consistent field size
        Dimension fieldSize = new Dimension(200, 30);
        email.setMaximumSize(fieldSize);
        pass.setMaximumSize(fieldSize);

        login.setAlignmentX(Component.CENTER_ALIGNMENT);

        login.addActionListener(e -> {
            User user = AuthController.login(
                    email.getText(),
                    new String(pass.getPassword())
            );

            if (user != null) {
                frame.showDashboard(user.getName());
            } else {
                msg.setText("Invalid email or password");
            }
        });

        // layout (label ABOVE field)
        form.add(new JLabel("Email"));
        form.add(email);
        form.add(Box.createVerticalStrut(10));

        form.add(new JLabel("Password"));
        form.add(pass);
        form.add(Box.createVerticalStrut(15));

        form.add(login);
        form.add(Box.createVerticalStrut(10));
        form.add(msg);

        add(form);
    }
}