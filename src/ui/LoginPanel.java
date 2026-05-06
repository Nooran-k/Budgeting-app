package ui;

import model.User;
import service.AuthController;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {

    public LoginPanel(MainFrame frame) {

        setLayout(new GridBagLayout());
        setBackground(Color.white);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.white);

        JTextField     email = new JTextField();
        JPasswordField pass  = new JPasswordField();

        JLabel msg = new JLabel(" ");
        msg.setForeground(Color.RED);
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);

 //Buttons
        JButton login = new JButton("Login");
        styleButton(login);

        JButton back = new JButton("← Back");
        back.setAlignmentX(Component.CENTER_ALIGNMENT);
        back.setFocusPainted(false);
        back.setBorderPainted(false);
        back.setContentAreaFilled(false);
        back.setForeground(new Color(100, 100, 100));
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

//Field sizes
        Dimension fieldSize = new Dimension(200, 30);
        email.setMaximumSize(fieldSize);
        pass.setMaximumSize(fieldSize);

//Actions
        login.addActionListener(e -> {
            msg.setText(" ");           // clear the error message before trying again

            User user = AuthController.login(
                    email.getText(),
                    new String(pass.getPassword())
            );

            if (user != null) {
                frame.showDashboard(user);

            } else {
                msg.setText("Invalid email or password");
            }
        });
        back.addActionListener(e -> frame.showScreen("welcome"));

//layout
        form.add(new JLabel("Email"));
        form.add(email);
        form.add(Box.createVerticalStrut(10));

        form.add(new JLabel("Password"));
        form.add(pass);
        form.add(Box.createVerticalStrut(15));

        form.add(login);
        form.add(Box.createVerticalStrut(8));
        form.add(msg);
        form.add(Box.createVerticalStrut(8));
        form.add(back);

        add(form);
    }

    private void styleButton(JButton btn) {
        btn.setForeground(Color.white);
        btn.setBackground(new Color(15, 188, 19));
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 35));
    }
}