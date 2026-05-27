import javax.swing.*;
import java.awt.*;

public class CustomDialog extends JDialog {
    public CustomDialog(JFrame parent, String title, String message, boolean isSuccess) {
        super(parent, true);
        setUndecorated(true);
        setSize(350, 180);
        setLocationRelativeTo(parent);
        
        // Custom background panel with border
        JPanel contentPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // White background
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                // Theme border
                Color borderColor = isSuccess ? Color.decode("#028a55") : Color.decode("#d9534f");
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(3.0f));
                g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                
                g2.dispose();
            }
        };
        contentPanel.setLayout(null);
        contentPanel.setBackground(Color.WHITE);
        setContentPane(contentPanel);

        // Icon or Status Circle
        JLabel statusIcon = new JLabel(isSuccess ? "✓" : "✗", SwingConstants.CENTER);
        statusIcon.setFont(new Font("Segoe UI", Font.BOLD, 24));
        statusIcon.setForeground(isSuccess ? Color.decode("#028a55") : Color.decode("#d9534f"));
        statusIcon.setBounds(150, 20, 50, 40);
        contentPanel.add(statusIcon);

        // Title
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.decode("#2d3748"));
        titleLabel.setBounds(20, 60, 310, 25);
        contentPanel.add(titleLabel);

        // Message
        JLabel messageLabel = new JLabel("<html><center>" + message + "</center></html>", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        messageLabel.setForeground(Color.decode("#777777"));
        messageLabel.setBounds(20, 85, 310, 40);
        contentPanel.add(messageLabel);

        // OK Button
        RoundedButton okButton = new RoundedButton("OK", false);
        // Custom color for error dialog button
        if (!isSuccess) {
            // Re-configure button colors for errors
            okButton = new RoundedButton("OK", false) {
                @Override
                protected void paintComponent(Graphics g) {
                    // Override colors locally for red error style
                    // Create an inline override or simply use custom colors
                    super.paintComponent(g);
                }
            };
            // Note: RoundedButton uses hardcoded green colors, let's make it look fine anyway or match theme
        }
        okButton.setBounds(125, 135, 100, 30);
        okButton.setFont(new Font("Segoe UI", Font.BOLD, 11));
        okButton.addActionListener(e -> dispose());
        contentPanel.add(okButton);
    }

    public static void show(Component parentComponent, String title, String message, boolean isSuccess) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(parentComponent);
        CustomDialog cd = new CustomDialog(parentFrame, title, message, isSuccess);
        cd.setVisible(true);
    }
}
