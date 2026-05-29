package Mussie;

import javafx.scene.Cursor;
import javafx.scene.control.Button;

public class RoundedButton extends Button {
    private boolean isOutline;

    public RoundedButton(String text, boolean isOutline) {
        super(text);
        this.isOutline = isOutline;
        
        setCursor(Cursor.HAND);
        
        if (isOutline) {
            String defaultStyle = "-fx-background-color: transparent; -fx-border-color: white; -fx-border-width: 1.5; -fx-border-radius: 20; -fx-background-radius: 20; -fx-text-fill: white; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 12px;";
            String hoverStyle = "-fx-background-color: rgba(255, 255, 255, 0.12); -fx-border-color: white; -fx-border-width: 1.5; -fx-border-radius: 20; -fx-background-radius: 20; -fx-text-fill: white; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 12px;";
            String pressedStyle = "-fx-background-color: rgba(255, 255, 255, 0.24); -fx-border-color: white; -fx-border-width: 1.5; -fx-border-radius: 20; -fx-background-radius: 20; -fx-text-fill: white; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 12px;";

            setStyle(defaultStyle);
            
            setOnMouseEntered(e -> setStyle(hoverStyle));
            setOnMouseExited(e -> setStyle(defaultStyle));
            setOnMousePressed(e -> setStyle(pressedStyle));
            setOnMouseReleased(e -> setStyle(isHover() ? hoverStyle : defaultStyle));
            
        } else {
            String defaultStyle = "-fx-background-color: #028a55; -fx-background-radius: 20; -fx-text-fill: white; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 12px;";
            String hoverStyle = "-fx-background-color: #016f44; -fx-background-radius: 20; -fx-text-fill: white; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 12px;";
            String pressedStyle = "-fx-background-color: #015635; -fx-background-radius: 20; -fx-text-fill: white; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 12px;";

            setStyle(defaultStyle);
            
            setOnMouseEntered(e -> setStyle(hoverStyle));
            setOnMouseExited(e -> setStyle(defaultStyle));
            setOnMousePressed(e -> setStyle(pressedStyle));
            setOnMouseReleased(e -> setStyle(isHover() ? hoverStyle : defaultStyle));
        }
    }
}
