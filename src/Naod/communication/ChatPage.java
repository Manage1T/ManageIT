package Naod.communication;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

public class ChatPage extends Application {

    private ChatPanel chatPanel;

    @Override
    public void start(Stage stage) {
        String username = promptForUsername(stage);
        if (username == null) {
            Platform.exit();
            return;
        }

        chatPanel = new ChatPanel(username);

        stage.setScene(new Scene(chatPanel, 900, 600));
        stage.setTitle("ManageIT Chat - " + username);
        stage.setOnCloseRequest(e -> chatPanel.disconnect());
        stage.show();
    }

    private String promptForUsername(Stage stage) {
        while (true) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("ManageIT Chat");
            dialog.setHeaderText("Enter your registered username");
            dialog.setContentText("Username:");

            String input = dialog.showAndWait().orElse("").trim();
            if (input.isEmpty()) {
                return null;
            }

            String resolved = ChatDatabaseHelper.resolveUsername(input);
            if (resolved != null) {
                return resolved;
            }

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(stage);
            alert.setTitle("Unknown Username");
            alert.setHeaderText("That username is not registered.");
            alert.setContentText("Create an account in the app first, then try again.\nRegistered users include: test, user");
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        ChatDatabaseHelper.ensureSchema();
        launch(args);
    }
}
