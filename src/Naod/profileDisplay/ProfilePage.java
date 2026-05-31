package Naod.profileDisplay;

import Naod.communication.ChatUserRepository;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ProfilePage extends VBox {

    private final String username;

    public ProfilePage(String username) {
        this.username = username;

        setSpacing(10);
        setPadding(new Insets(20));

        buildUI();
    }

    private void buildUI() {
        String resolved = ChatUserRepository.resolveUsername(username);

        Label title = new Label("User Profile");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label name = new Label("Username: " + resolved);

        Label status = new Label("Status: Online (chat system)");

        getChildren().addAll(title, name, status);
    }
}