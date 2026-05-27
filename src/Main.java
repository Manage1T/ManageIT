import javax.swing.*;

public class Main {
    private static String dbError = null;

    public static void main(String[] args) {
        // Set system look and feel for native components (like caret, etc.) if available
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // Attempt database initialization
        try {
            DatabaseHelper.initializeDatabase();
        } catch (Exception e) {
            dbError = e.getMessage();
            System.err.println("Database initialization failed: " + dbError);
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

            // Alert the user if MySQL initialization failed
            if (dbError != null) {
                CustomDialog.show(slidingContainer, "Database Offline / Access Denied", 
                    "Could not connect to MySQL. Update 'src/db.properties' with your password.", false);
            }
        });
    }
}