package view;

import controller.GoalController;
import model.FinancialGoal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * GoalView is responsible for managing financial goals.
 * It allows users to create goals, track progress,
 * and add savings to existing goals.
 */
public class GoalView extends JPanel {

    private final GoalController gc;
    private final String userEmail;

    private JTextField txtName;
    private JTextField txtTarget;
    private JTextField txtSaved;
    private JLabel lblMsg;
    private DefaultTableModel tableModel;

    /**
     * Constructor initializes the goal view.
     *
     * @param gc Goal controller
     * @param userEmail Logged-in user's email
     */
    public GoalView(GoalController gc, String userEmail) {
        this.gc = gc;
        this.userEmail = userEmail;
        build();
        refresh();
    }

    /** Builds UI components */
    private void build() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(5, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("New Goal"));

        form.add(new JLabel("Goal Name:"));
        txtName = new JTextField();
        form.add(txtName);

        form.add(new JLabel("Target (EGP):"));
        txtTarget = new JTextField();
        form.add(txtTarget);

        form.add(new JLabel("Already Saved (EGP):"));
        txtSaved = new JTextField("0");
        form.add(txtSaved);

        lblMsg = new JLabel(" ");
        form.add(lblMsg);

        JButton btnCreate = new JButton("Create Goal");
        btnCreate.addActionListener(e -> create());
        form.add(btnCreate);

        add(form, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new String[]{"ID","Name","Target","Saved","Progress%","Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnAdd = new JButton("Add Savings to Selected Goal");
        btnAdd.addActionListener(e -> addSavings(table));
        add(btnAdd, BorderLayout.SOUTH);
    }

    /** Creates a new financial goal */
    private void create() {
        String name = txtName.getText().trim();

        if (name.isEmpty()) {
            lblMsg.setForeground(Color.RED);
            lblMsg.setText("Enter a goal name.");
            return;
        }

        double target, saved;
        try {
            target = Double.parseDouble(txtTarget.getText().trim());
            saved  = Double.parseDouble(txtSaved.getText().trim());
        } catch (NumberFormatException e) {
            lblMsg.setForeground(Color.RED);
            lblMsg.setText("Enter valid amounts.");
            return;
        }

        FinancialGoal g = gc.createGoal(userEmail, name, target, saved,
                LocalDate.now().plusMonths(6));

        if (g == null) {
            lblMsg.setForeground(Color.RED);
            lblMsg.setText("Could not create goal.");
            return;
        }

        lblMsg.setForeground(new Color(0, 140, 0));
        lblMsg.setText("Goal created!");
        txtName.setText("");
        txtTarget.setText("");
        txtSaved.setText("0");

        refresh();
    }

    /**
     * Adds savings to a selected goal.
     *
     * @param table JTable containing goals
     */
    private void addSavings(JTable table) {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a goal first.");
            return;
        }

        int goalId = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);

        String input = JOptionPane.showInputDialog(
                this, "Amount to add to \"" + name + "\" (EGP):");

        if (input == null || input.trim().isEmpty()) return;

        double amount;
        try {
            amount = Double.parseDouble(input.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount.");
            return;
        }

        gc.updateGoalProgress(goalId, userEmail, amount);
        refresh();
    }

    /** Refreshes goal table */
    private void refresh() {
        tableModel.setRowCount(0);

        List<FinancialGoal> list = gc.getAllGoals(userEmail);

        for (FinancialGoal g : list) {
            tableModel.addRow(new Object[]{
                    g.getGoalId(),
                    g.getName(),
                    String.format("%.2f", g.getTargetAmount()),
                    String.format("%.2f", g.getCurrentAmount()),
                    String.format("%.1f%%", g.calculateProgressPercentage()),
                    g.getStatus()
            });
        }
    }
}
