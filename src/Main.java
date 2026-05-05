import controller.BudgetController;
import view.BudgetView;
import view.GoalView;
import controller.GoalController;
import controller.TransactionController;
import view.AddTransactionView;
import view.ReportView;
import controller.ReportController;
import controller.NotificationController;
import data.DataStore;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        DataStore.initCounters();

        NotificationController notifController  = new NotificationController();
        BudgetController       budgetController = new BudgetController(notifController);
        TransactionController  txController     = new TransactionController(budgetController);
        GoalController         goalController   = new GoalController(notifController);
        ReportController       reportController = new ReportController();

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Personal Budget App — CS251");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(750, 600);
            frame.setLocationRelativeTo(null);

            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("Transactions",new AddTransactionView(txController, notifController));
            tabs.addTab("Budgets", new BudgetView(budgetController));
            tabs.addTab("Goals",new GoalView(goalController));
            tabs.addTab("Reports", new ReportView(reportController));

            frame.add(tabs);
            frame.setVisible(true);
        });
    }
}
