package view;

import controller.BudgetController;
import model.Budget;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class BudgetView extends JPanel {

    private final BudgetController bc;
    private final int userId = 1;

    private JComboBox<String> cbCategory;
    private JTextField        txtAmount;
    private JLabel            lblMsg;
    private DefaultTableModel tableModel;

    private final String[] CATEGORIES = {
        "Food", "Transport", "Groceries",
        "Bills", "Healthcare", "Other"
    };

    public BudgetView(BudgetController bc) {
        this.bc = bc;
        build();
        refresh();
    }

    private void build() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

       
        JPanel form = new JPanel(new GridLayout(4, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("New Budget"));

        form.add(new JLabel("Category:"));
        cbCategory = new JComboBox<>(CATEGORIES);
        form.add(cbCategory);

        form.add(new JLabel("Limit (EGP):"));
        txtAmount = new JTextField();
        form.add(txtAmount);

        lblMsg = new JLabel(" ");
        form.add(lblMsg);

        JButton btnCreate = new JButton("Create");
        btnCreate.addActionListener(e -> create());
        form.add(btnCreate);

        add(form, BorderLayout.NORTH);

        
        tableModel = new DefaultTableModel(
            new String[]{"ID","Category","Limit","Spent","Remaining","Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void create() {
        double amount;
        try {
            amount = Double.parseDouble(txtAmount.getText().trim());
        } catch (NumberFormatException e) {
            lblMsg.setForeground(Color.RED);
            lblMsg.setText("Enter a valid amount.");
            return;
        }

        LocalDate now   = LocalDate.now();
        LocalDate start = now.withDayOfMonth(1);
        LocalDate end   = now.withDayOfMonth(now.lengthOfMonth());

        Budget b = bc.createBudget(userId,
                cbCategory.getSelectedIndex(),
                (String) cbCategory.getSelectedItem(),
                amount, start, end, 80);

        if (b == null) {
            lblMsg.setForeground(Color.RED);
            lblMsg.setText("Budget already exists for this category.");
            return;
        }

        lblMsg.setForeground(new Color(0, 140, 0));
        lblMsg.setText("Budget created!");
        txtAmount.setText("");
        refresh();
    }

    private void refresh() {
        tableModel.setRowCount(0);
        LocalDate now = LocalDate.now();
        List<Budget> list = bc.getBudgetForMonth(
                userId, now.getMonthValue(), now.getYear());
        for (Budget b : list) {
            tableModel.addRow(new Object[]{
                b.getBudgetId(),
                b.getCategoryName(),
                String.format("%.2f", b.getLimitAmount()),
                String.format("%.2f", b.getSpentAmount()),
                String.format("%.2f", b.calculateRemaining()),
                b.getStatus()
            });
        }
    }
}
