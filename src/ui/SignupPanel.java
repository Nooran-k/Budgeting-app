package ui;

import service.AuthController;

import javax.swing.*;
import java.awt.*;

public class SignupPanel extends JPanel {

    public SignupPanel(MainFrame frame) {

        setLayout(new GridBagLayout());
        setBackground(Color.white);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.white);

        JTextField     name    = new JTextField();
        JTextField     email   = new JTextField();
        JPasswordField pass    = new JPasswordField();
        JPasswordField confirm = new JPasswordField();

        JLabel msg = new JLabel(" ");   // space so it reserves height
        msg.setForeground(Color.RED);
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);

//Buttons
        JButton create = new JButton("Create Account");
        styleButton(create);

        JButton back = new JButton("← Back");
        back.setAlignmentX(Component.CENTER_ALIGNMENT);
        back.setFocusPainted(false);
        back.setBorderPainted(false);
        back.setContentAreaFilled(false);
        back.setForeground(new Color(100, 100, 100));
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

//Feild sizes
        Dimension fieldSize = new Dimension(200, 30);
        name.setMaximumSize(fieldSize);
        email.setMaximumSize(fieldSize);
        pass.setMaximumSize(fieldSize);
        confirm.setMaximumSize(fieldSize);

//Actions
        create.addActionListener(e -> {
            msg.setText(" ");           // clear the error message before trying again

            String result = AuthController.register(
                    name.getText(),
                    email.getText(),
                    new String(pass.getPassword()),
                    new String(confirm.getPassword())
            );

            if (result.equals("SUCCESS")) {
                frame.showDashboard(name.getText().trim());
            } else {
                msg.setText(result);
            }
        });

        back.addActionListener(e -> frame.showScreen("welcome"));

//layout
        form.add(new JLabel("Name"));
        form.add(name);
        form.add(Box.createVerticalStrut(10));

        form.add(new JLabel("Email"));
        form.add(email);
        form.add(Box.createVerticalStrut(10));

        form.add(new JLabel("Password"));
        form.add(pass);
        form.add(Box.createVerticalStrut(10));

        form.add(new JLabel("Confirm Password"));
        form.add(confirm);
        form.add(Box.createVerticalStrut(15));

        form.add(create);
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