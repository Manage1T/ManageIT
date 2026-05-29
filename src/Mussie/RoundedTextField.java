package Mussie;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

public class RoundedTextField extends StackPane {
    public enum IconType { NONE, USER, EMAIL, LOCK }

    private TextField textField;
    private SVGPath iconPath;

    public RoundedTextField(String placeholder, IconType iconType) {
        textField = new TextField();
        textField.setPromptText(placeholder);
        
        // Remove default focus ring
        textField.getStyleClass().add("rounded-text-field");

        String normalStyle = "-fx-background-color: #f0f4f2; -fx-background-radius: 15; -fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1; -fx-text-fill: #2d3748; -fx-prompt-text-fill: #8fa89b; -fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-padding: 10 15 10 " + (iconType == IconType.NONE ? "15;" : "40;") + " -fx-focus-color: transparent; -fx-faint-focus-color: transparent;";
        String focusedStyle = "-fx-background-color: #f0f4f2; -fx-background-radius: 15; -fx-border-color: #028a55; -fx-border-radius: 15; -fx-border-width: 1.5; -fx-text-fill: #2d3748; -fx-prompt-text-fill: #8fa89b; -fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-padding: 10 15 10 " + (iconType == IconType.NONE ? "15;" : "40;") + " -fx-focus-color: transparent; -fx-faint-focus-color: transparent;";

        textField.setStyle(normalStyle);
        
        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            textField.setStyle(newVal ? focusedStyle : normalStyle);
        });

        getChildren().add(textField);

        if (iconType != IconType.NONE) {
            iconPath = new SVGPath();
            iconPath.setFill(Color.TRANSPARENT);
            iconPath.setStroke(Color.web("#8fa89b"));
            iconPath.setStrokeWidth(1.5);
            
            switch (iconType) {
                case USER:
                    iconPath.setContent("M 8 8 A 4 4 0 1 0 8 0 A 4 4 0 1 0 8 8 Z M 0 16 A 8 5 0 0 1 16 16");
                    break;
                case EMAIL:
                    iconPath.setContent("M 0 2 L 16 2 L 16 14 L 0 14 Z M 0 2 L 8 8 L 16 2");
                    break;
                case LOCK:
                    iconPath.setContent("M 3 6 L 13 6 L 13 16 L 3 16 Z M 5 6 A 3 4 0 0 1 11 6 M 8 10 L 8 12");
                    break;
            }
            
            StackPane.setMargin(iconPath, new Insets(0, 0, 0, 12));
            StackPane.setAlignment(iconPath, Pos.CENTER_LEFT);
            iconPath.setMouseTransparent(true);
            getChildren().add(iconPath);
        }
    }

    public String getText() {
        return textField.getText();
    }

    public void setText(String text) {
        textField.setText(text);
    }
}
