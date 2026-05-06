package ui;

import service.AuthController;

import javax.swing.*;
import java.awt.*;

public class SignupPanel extends JPanel {

    public SignupPanel(MainFrame frame) {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createTitledBorder("Create Account"));

        // Fields
        JTextField txtName = new JTextField();
        JTextField txtEmail = new JTextField();
        JPasswordField txtPass = new JPasswordField();
        JPasswordField txtConfirm = new JPasswordField();

        // Bigger size
        Dimension fieldSize = new Dimension(300, 40);
        txtName.setPreferredSize(fieldSize);
        txtEmail.setPreferredSize(fieldSize);
        txtPass.setPreferredSize(fieldSize);
        txtConfirm.setPreferredSize(fieldSize);

        // Bigger text
        Font fieldFont = new Font("Arial", Font.PLAIN, 16);
        txtName.setFont(fieldFont);
        txtEmail.setFont(fieldFont);
        txtPass.setFont(fieldFont);
        txtConfirm.setFont(fieldFont);

        JLabel lblMsg = new JLabel(" ");
        lblMsg.setForeground(Color.RED);

        JButton btnCreate = makeBtn("Create Account");

        JButton btnBack = new JButton("← Back");
        btnBack.setFocusPainted(false);
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);

        // Add components with spacing
        form.add(labeled("Name:", txtName));
        form.add(Box.createVerticalStrut(10));

        form.add(labeled("Email:", txtEmail));
        form.add(Box.createVerticalStrut(10));

        form.add(labeled("Password:", txtPass));
        form.add(Box.createVerticalStrut(10));

        form.add(labeled("Confirm:", txtConfirm));
        form.add(Box.createVerticalStrut(10));

        form.add(lblMsg);
        form.add(Box.createVerticalStrut(10));

        form.add(btnCreate);
        form.add(Box.createVerticalStrut(10));

        form.add(btnBack);

        // Actions
        btnCreate.addActionListener(e -> {
            lblMsg.setText(" ");

            String result = AuthController.register(
                    txtName.getText(),
                    txtEmail.getText(),
                    new String(txtPass.getPassword()),
                    new String(txtConfirm.getPassword())
            );

            if (result.equals("SUCCESS")) {
                frame.showDashboard(
                        new model.User(
                                txtName.getText().trim(),
                                txtEmail.getText().trim(),
                                ""
                        )
                );
            } else {
                lblMsg.setText(result);
            }
        });

        btnBack.addActionListener(e -> frame.showScreen("welcome"));

        add(form);
    }

    private JPanel labeled(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(10, 5));
        p.setBackground(Color.WHITE);

        JLabel lbl = new JLabel(label);
        lbl.setPreferredSize(new Dimension(120, 40)); // wider for "Confirm Password"

        p.add(lbl, BorderLayout.WEST);
        p.add(field, BorderLayout.CENTER);

        return p;
    }

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