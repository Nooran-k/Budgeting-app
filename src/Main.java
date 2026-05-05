import ui.MainFrame;
import data.DataStore;
import controller.TransactionController;
import view.AddTransactionView;
import controller.NotificationController;
import controller.BudgetController;
import view.BudgetView;
import view.GoalView;
import controller.GoalController;
import view.ReportView;
import controller.ReportController;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        new MainFrame();
        NotificationController notifController  = new NotificationController();
        TransactionController  txController     = new TransactionController(budgetController);
        BudgetController       budgetController = new BudgetController(notifController);
        GoalController         goalController   = new GoalController(notifController);
        ReportController       reportController = new ReportController();
         SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Budget App");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(750, 600);
            frame.setLocationRelativeTo(null);

            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("Transactions",
                    new AddTransactionView(txController, notifController));
           tabs.addTab("Budgets",
                    new BudgetView(budgetController));
                           tabs.addTab("Goals",
                    new GoalView(goalController));
                     frame.add(tabs);
           tabs.addTab("Reports",
                    new ReportView(reportController));
            frame.setVisible(true);
        });
    }

}
