package ui;

import service.AuthController;

import javax.swing.*;
import java.awt.*;

/**
 * Signup screen panel.
 *
 * Responsibilities:
 * - Takes user data (name, email, password)
 * - Validates input via AuthController
 * - Creates account
 * - Redirects to dashboard on success
 */
public class SignupPanel extends JPanel {

    /**
     * Builds signup UI.
     */
    public SignupPanel(MainFrame frame) {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createTitledBorder("Create Account"));

        JTextField txtName = new JTextField();
        JTextField txtEmail = new JTextField();
        JPasswordField txtPass = new JPasswordField();
        JPasswordField txtConfirm = new JPasswordField();

        JLabel lblMsg = new JLabel(" ");
        lblMsg.setForeground(Color.RED);

        JButton btnCreate = makeBtn("Create Account");
        JButton btnBack = new JButton("← Back");

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

        btnCreate.addActionListener(e -> {
            String result = AuthController.register(
                    txtName.getText(),
                    txtEmail.getText(),
                    new String(txtPass.getPassword()),
                    new String(txtConfirm.getPassword())
            );

            if (result.equals("SUCCESS")) {
                frame.showDashboard(
                        new model.User(
                                txtName.getText(),
                                txtEmail.getText(),
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

    /**
     * @param label
     * @param field
     * @return JPanel
     */
    private JPanel labeled(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(10, 5));
        p.setBackground(Color.WHITE);
        p.add(new JLabel(label), BorderLayout.WEST);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    /**
     * @param text
     * @return JButton
     */
    private JButton makeBtn(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(15, 188, 19));
        b.setForeground(Color.WHITE);
        return b;
    }
}