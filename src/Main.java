import data.Database;
import ui.MainFrame;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        Database.init();  // create tables if they don't exist
        SwingUtilities.invokeLater(MainFrame::new);
    }
}