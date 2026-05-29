package Mussie;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class GradientPanel extends StackPane {
    
    public GradientPanel() {
        setStyle("-fx-background-color: linear-gradient(to bottom right, #0ba360, #028a55);");
    }

    public GradientPanel(Color start, Color end) {
        String startHex = toHexString(start);
        String endHex = toHexString(end);
        setStyle("-fx-background-color: linear-gradient(to bottom right, " + startHex + ", " + endHex + ");");
    }
    
    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255));
    }
}
