import javax.swing.*;
import java.awt.*;

public class GradientPanel extends JPanel {
    private Color colorStart = Color.decode("#0ba360");
    private Color colorEnd = Color.decode("#028a55");

    public GradientPanel() {
        setOpaque(false);
    }

    public GradientPanel(Color start, Color end) {
        this.colorStart = start;
        this.colorEnd = end;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        // Gradient from top-left to bottom-right
        GradientPaint gp = new GradientPaint(0, 0, colorStart, width, height, colorEnd);
        g2.setPaint(gp);
        g2.fillRect(0, 0, width, height);
        
        g2.dispose();
        super.paintComponent(g);
    }
}
