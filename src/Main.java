import ui.MainFrame;
import data.DataStore;
import controller.TransactionController;
import view.AddTransactionView;
import controller.NotificationController;


import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        new MainFrame();
        NotificationController notifController  = new NotificationController();
        TransactionController  txController     = new TransactionController(budgetController);
         SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Budget App");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(750, 600);
            frame.setLocationRelativeTo(null);

            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("Transactions",
                    new AddTransactionView(txController, notifController));
    

            frame.add(tabs);
            frame.setVisible(true);
        });
    }

}
