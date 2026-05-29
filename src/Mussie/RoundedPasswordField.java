package Mussie;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

public class RoundedPasswordField extends StackPane {

    private PasswordField passwordField;
    private SVGPath iconPath;

    public RoundedPasswordField(String placeholder) {
        passwordField = new PasswordField();
        passwordField.setPromptText(placeholder);
        
        String normalStyle = "-fx-background-color: #f0f4f2; -fx-background-radius: 15; -fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1; -fx-text-fill: #2d3748; -fx-prompt-text-fill: #8fa89b; -fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-padding: 10 15 10 40; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;";
        String focusedStyle = "-fx-background-color: #f0f4f2; -fx-background-radius: 15; -fx-border-color: #028a55; -fx-border-radius: 15; -fx-border-width: 1.5; -fx-text-fill: #2d3748; -fx-prompt-text-fill: #8fa89b; -fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-padding: 10 15 10 40; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;";

        passwordField.setStyle(normalStyle);
        
        passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            passwordField.setStyle(newVal ? focusedStyle : normalStyle);
        });

        getChildren().add(passwordField);

        iconPath = new SVGPath();
        iconPath.setFill(Color.TRANSPARENT);
        iconPath.setStroke(Color.web("#8fa89b"));
        iconPath.setStrokeWidth(1.5);
        iconPath.setContent("M 3 6 L 13 6 L 13 16 L 3 16 Z M 5 6 A 3 4 0 0 1 11 6 M 8 10 L 8 12");
        
        StackPane.setMargin(iconPath, new Insets(0, 0, 0, 12));
        StackPane.setAlignment(iconPath, Pos.CENTER_LEFT);
        iconPath.setMouseTransparent(true);
        getChildren().add(iconPath);
    }

    public char[] getPassword() {
        return passwordField.getText().toCharArray();
    }

    public void setText(String text) {
        passwordField.setText(text);
    }
}
