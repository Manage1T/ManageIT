package Mussie;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class CustomDialog extends Stage {

    public CustomDialog(Window owner, String title, String message, boolean isSuccess) {
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UNDECORATED);

        VBox contentPanel = new VBox(10);
        contentPanel.setAlignment(Pos.CENTER);
        contentPanel.setPrefSize(380, 220);
        
        String borderColor = isSuccess ? "#028a55" : "#d9534f";
        contentPanel.setStyle("-fx-background-color: white; -fx-border-color: " + borderColor + "; -fx-border-width: 3;");

        Label statusIcon = new Label(isSuccess ? "✓" : "✗");
        statusIcon.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        statusIcon.setTextFill(Color.web(borderColor));

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.web("#2d3748"));

        Label messageLabel = new Label(message);
        messageLabel.setFont(Font.font("Segoe UI", 13));
        messageLabel.setTextFill(Color.web("#777777"));
        messageLabel.setTextAlignment(TextAlignment.CENTER);
        messageLabel.setWrapText(true);

        RoundedButton okButton = new RoundedButton("OK", false);
        // Note: For red error style we could dynamically inject CSS, but the original kept it mostly green anyway or matched.
        // Let's explicitly override if it's an error
        if (!isSuccess) {
            okButton.setStyle("-fx-background-color: #d9534f; -fx-background-radius: 20; -fx-text-fill: white; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 11px;");
            okButton.setOnMouseEntered(e -> okButton.setStyle("-fx-background-color: #c9302c; -fx-background-radius: 20; -fx-text-fill: white; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 11px;"));
            okButton.setOnMouseExited(e -> okButton.setStyle("-fx-background-color: #d9534f; -fx-background-radius: 20; -fx-text-fill: white; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 11px;"));
        }
        
        okButton.setPrefWidth(100);
        okButton.setPrefHeight(30);
        okButton.setOnAction(e -> close());

        VBox.setMargin(statusIcon, new javafx.geometry.Insets(10, 0, 0, 0));
        VBox.setMargin(okButton, new javafx.geometry.Insets(10, 0, 10, 0));

        contentPanel.getChildren().addAll(statusIcon, titleLabel, messageLabel, okButton);

        Scene scene = new Scene(contentPanel);
        scene.setFill(Color.TRANSPARENT);
        setScene(scene);
    }

    public static void show(javafx.scene.Node parentNode, String title, String message, boolean isSuccess) {
        Window owner = null;
        if (parentNode != null && parentNode.getScene() != null) {
            owner = parentNode.getScene().getWindow();
        }
        CustomDialog dialog = new CustomDialog(owner, title, message, isSuccess);
        dialog.showAndWait();
    }
}
