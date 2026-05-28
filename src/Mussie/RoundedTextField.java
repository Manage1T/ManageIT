package Mussie;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class RoundedTextField extends JTextField {
    public enum IconType {
        NONE, USER, EMAIL, LOCK
    }

    private String placeholder = "";
    private IconType iconType = IconType.NONE;
    private Color backgroundColor = Color.decode("#f0f4f2");
    private Color placeholderColor = Color.decode("#8fa89b");
    private Color iconColor = Color.decode("#8fa89b");
    private Color activeBorderColor = Color.decode("#028a55");
    private Color inactiveBorderColor = Color.decode("#e0e0e0");
    private boolean isFocused = false;

    public RoundedTextField(String placeholder, IconType iconType) {
        this.placeholder = placeholder;
        this.iconType = iconType;
        
        setOpaque(false);
        // Add left padding for the icon and right padding for clean spacing
        setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 15));
        setForeground(Color.decode("#2d3748"));
        setCaretColor(Color.decode("#2d3748"));
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                repaint();
            }
        });
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Fill background
        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, width - 1, height - 1, 15, 15);

        // Draw border
        if (isFocused) {
            g2.setColor(activeBorderColor);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(0, 0, width - 1, height - 1, 15, 15);
        } else {
            g2.setColor(inactiveBorderColor);
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawRoundRect(0, 0, width - 1, height - 1, 15, 15);
        }

        // Draw Icon
        drawIcon(g2);

        // Draw Placeholder if empty
        if (getText().isEmpty() && !placeholder.isEmpty()) {
            g2.setColor(placeholderColor);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int y = (height - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(placeholder, 40, y); // Start at 40 because of left padding
        }

        g2.dispose();
        super.paintComponent(g);
    }

    private void drawIcon(Graphics2D g2) {
        if (iconType == IconType.NONE) return;

        g2.setColor(iconColor);
        g2.setStroke(new BasicStroke(1.5f));

        int x = 12; // Start x for icon
        int y = (getHeight() - 16) / 2; // Vertically center a 16x16 icon

        switch (iconType) {
            case USER:
                // Draw head
                g2.drawOval(x + 4, y, 8, 8);
                // Draw shoulders
                g2.drawArc(x, y + 9, 16, 10, 0, 180);
                break;
            case EMAIL:
                // Draw envelope body
                g2.drawRect(x, y + 2, 16, 12);
                // Draw envelope flap (V lines)
                g2.drawLine(x, y + 2, x + 8, y + 8);
                g2.drawLine(x + 8, y + 8, x + 16, y + 2);
                break;
            case LOCK:
                // Draw lock body
                g2.drawRoundRect(x + 2, y + 6, 12, 10, 3, 3);
                // Draw shackle
                g2.drawArc(x + 4, y + 1, 8, 10, 0, 180);
                g2.drawLine(x + 4, y + 6, x + 4, y + 6);
                g2.drawLine(x + 12, y + 6, x + 12, y + 6);
                // Draw keyhole dot
                g2.fillOval(x + 7, y + 9, 2, 2);
                break;
        }
    }
}
