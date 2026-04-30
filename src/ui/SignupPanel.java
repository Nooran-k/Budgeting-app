package ui;

import service.AuthController;

import javax.swing.*;
import java.awt.*;

public class SignupPanel extends JPanel {

    public SignupPanel(MainFrame frame) {

        setLayout(new GridBagLayout()); // centers everything
        setBackground(Color.white);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.white);

        // Fields
        JTextField name = new JTextField();
        JTextField email = new JTextField();
        JPasswordField pass = new JPasswordField();
        JPasswordField confirm = new JPasswordField();

        JLabel msg = new JLabel();
        msg.setForeground(Color.RED);

        JButton create = new JButton("Create");
        create.setForeground(Color.white);
        create.setBackground(new Color(15, 188, 19));

        // style inputs
        Dimension fieldSize = new Dimension(200, 30);
        name.setMaximumSize(fieldSize);
        email.setMaximumSize(fieldSize);
        pass.setMaximumSize(fieldSize);
        confirm.setMaximumSize(fieldSize);

        create.setAlignmentX(Component.CENTER_ALIGNMENT);

        create.addActionListener(e -> {
            String result = AuthController.register(
                    name.getText(),
                    email.getText(),
                    new String(pass.getPassword()),
                    new String(confirm.getPassword())
            );

            if (result.equals("SUCCESS")) {
                frame.showDashboard(name.getText());
            } else {
                msg.setText(result);
            }
        });

        // Add components (label ABOVE field)
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
        form.add(Box.createVerticalStrut(10));
        form.add(msg);

        add(form);
    }
}