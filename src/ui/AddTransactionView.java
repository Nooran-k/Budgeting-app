package ui;

import model.Notification;
import model.Transaction;
import model.TransactionType;
import service.NotificationController;
import service.TransactionController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * AddTransactionView is responsible for displaying the UI
 * that allows users to add new financial transactions
 * and view their transaction history.
 */
public class AddTransactionView extends JPanel {

    private final TransactionController tc;
    private final NotificationController nc;
    private final String userEmail;

    private JTextField txtAmount;
    private JComboBox<String> cbType;
    private JComboBox<String> cbCategory;
    private JTextField txtDesc;
    private JLabel lblMsg;
    private DefaultTableModel tableModel;

    /** List of predefined categories */
    private final String[] CATEGORIES = {
        "Food", "Transport", "Groceries",
        "Bills", "Salary", "Other"
    };

    /**
     * Constructor to initialize the transaction view.
     *
     * @param tc Transaction controller
     * @param nc Notification controller
     * @param userEmail Logged-in user's email
     */
    public AddTransactionView(TransactionController tc,
                              NotificationController nc,
                              String userEmail) {
        this.tc = tc;
        this.nc = nc;
        this.userEmail = userEmail;
        build();
        refresh();
    }

    /** Builds the UI components */
    private void build() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(6, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Add Transaction"));

        form.add(new JLabel("Type:"));
        cbType = new JComboBox<>(new String[]{"EXPENSE", "INCOME"});
        form.add(cbType);

        form.add(new JLabel("Amount (EGP):"));
        txtAmount = new JTextField();
        form.add(txtAmount);

        form.add(new JLabel("Category:"));
        cbCategory = new JComboBox<>(CATEGORIES);
        form.add(cbCategory);

        form.add(new JLabel("Description:"));
        txtDesc = new JTextField();
        form.add(txtDesc);

        lblMsg = new JLabel(" ");
        form.add(lblMsg);

        JButton btnSave = new JButton("Save");
        btnSave.addActionListener(e -> save());
        form.add(btnSave);

        add(form, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new String[]{"ID","Type","Amount","Category","Date"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    /** Handles saving a transaction */
    private void save() {
        double amount;
        try {
            amount = Double.parseDouble(txtAmount.getText().trim());
        } catch (NumberFormatException e) {
            lblMsg.setForeground(Color.RED);
            lblMsg.setText("Enter a valid amount.");
            return;
        }

        TransactionType type = cbType.getSelectedItem().equals("EXPENSE")
                ? TransactionType.EXPENSE
                : TransactionType.INCOME;

        Transaction t = tc.addTransaction(
                userEmail, amount, type,
                cbCategory.getSelectedIndex(),
                (String) cbCategory.getSelectedItem(),
                txtDesc.getText().trim(), "Cash");

        if (t == null) {
            lblMsg.setForeground(Color.RED);
            lblMsg.setText("Could not save.");
            return;
        }

        lblMsg.setForeground(new Color(0, 140, 0));
        lblMsg.setText("Saved!");
        txtAmount.setText("");
        txtDesc.setText("");
        refresh();

        Notification alert = nc.getLatestUnread(userEmail);
        if (alert != null) {
            JOptionPane.showMessageDialog(this,
                    alert.getMessage(), "Budget Alert",
                    JOptionPane.WARNING_MESSAGE);
            alert.markAsRead();
            data.Database.markNotificationRead(alert.getNotificationId());
        }
    }

    /** Refreshes the transaction table */
    private void refresh() {
        tableModel.setRowCount(0);
        List<Transaction> list = tc.getTransactionsByUser(userEmail);

        for (Transaction t : list) {
            tableModel.addRow(new Object[]{
                    t.getTransactionId(),
                    t.getType(),
                    String.format("%.2f", t.getAmount()),
                    t.getCategoryName(),
                    t.getDate().toLocalDate()
            });
        }
    }
}
