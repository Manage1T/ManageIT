import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoundedButton extends JButton {
    private boolean isOutline = false;
    private Color fillColor = Color.decode("#028a55");
    private Color hoverFillColor = Color.decode("#016f44");
    private Color pressedFillColor = Color.decode("#015635");
    private Color textColor = Color.WHITE;
    private Color outlineColor = Color.WHITE;
    private Color hoverOutlineBg = new Color(255, 255, 255, 30);
    private Color pressedOutlineBg = new Color(255, 255, 255, 60);

    private boolean isHovered = false;
    private boolean isPressed = false;

    public RoundedButton(String text, boolean isOutline) {
        super(text);
        this.isOutline = isOutline;
        
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        
        setForeground(textColor);
        setFont(new Font("Segoe UI", Font.BOLD, 12));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                isPressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int arc = height - 1; // Pill shape

        if (isOutline) {
            // Outline Button Drawing
            if (isPressed) {
                g2.setColor(pressedOutlineBg);
                g2.fillRoundRect(0, 0, width - 1, height - 1, arc, arc);
            } else if (isHovered) {
                g2.setColor(hoverOutlineBg);
                g2.fillRoundRect(0, 0, width - 1, height - 1, arc, arc);
            }
            
            g2.setColor(outlineColor);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(0, 0, width - 1, height - 1, arc, arc);
            g2.setColor(textColor);
        } else {
            // Solid Button Drawing
            if (isPressed) {
                g2.setColor(pressedFillColor);
            } else if (isHovered) {
                g2.setColor(hoverFillColor);
            } else {
                g2.setColor(fillColor);
            }
            g2.fillRoundRect(0, 0, width - 1, height - 1, arc, arc);
            
            g2.setColor(textColor);
        }

        // Draw centered text
        FontMetrics fm = g2.getFontMetrics();
        int x = (width - fm.stringWidth(getText())) / 2;
        int y = (height - fm.getHeight()) / 2 + fm.getAscent();
        
        g2.setFont(getFont());
        g2.drawString(getText(), x, y);

        g2.dispose();
    }
}
