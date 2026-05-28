import Database.Database;
import Mussie.SlidingContainer;

import javax.swing.*;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        // GLOBAL VARIABLES : TELL IN TELEGRAM IF CHANGING
        Database db = new Database();
        Connection conn = db.con;

        if (conn == null) {
            System.out.println("Error when connecting to database. Exiting!\n");
            System.exit(1);
        }


        // Mussie Setup
        // Set system look and feel for native components (like caret, etc.) if available
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // Run UI on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("ManageIT - Authenticate");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            SlidingContainer slidingContainer = new SlidingContainer();
            frame.getContentPane().add(slidingContainer);
            
            frame.pack(); // Pack frame around SlidingContainer size (850x550)
            frame.setLocationRelativeTo(null); // Center window
            frame.setVisible(true);
        });
    }
}