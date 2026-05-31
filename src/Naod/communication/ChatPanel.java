package Naod.communication;

import Mussie.GradientPanel;
import Mussie.RoundedButton;
import Mussie.RoundedTextField;

import Naod.profileDisplay.ProfilePage;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatPanel extends GradientPanel {

    private final String username;
    private DataInputStream in;
    private DataOutputStream out;
    private Socket socket;

    private final TextArea chatArea = new TextArea();
    private final RoundedTextField inputField =
            new RoundedTextField("Type a message...", RoundedTextField.IconType.NONE);
    private final RoundedTextField receiverField =
            new RoundedTextField("Receiver username...", RoundedTextField.IconType.NONE);
    private final RoundedButton sendButton = new RoundedButton("Send", false);

    private final AtomicBoolean connected = new AtomicBoolean(false);

    public ChatPanel(String username) {
        this.username = username;
        setPrefSize(900, 600);
        buildUi();
        loadHistory();
        setInputEnabled(false);
        connectToServer();
    }

    private void buildUi() {
        chatArea.setEditable(false);
        chatArea.setWrapText(true);

        Label title = new Label("ManageIT Team Chat");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label subtitle = new Label("Signed in as " + username);
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: rgba(255,255,255,0.85);");

        BorderPane header = new BorderPane();
        header.setCenter(title);
        header.setBottom(subtitle);

        HBox bottomBar = new HBox(10, receiverField, inputField, sendButton);
        HBox.setHgrow(inputField, Priority.ALWAYS);
        HBox.setHgrow(receiverField, Priority.NEVER);
        bottomBar.setPadding(new Insets(10, 0, 0, 0));

        BorderPane card = new BorderPane();
        card.setTop(header);
        card.setCenter(chatArea);
        card.setBottom(bottomBar);
        card.setPadding(new Insets(20));
        card.setMaxWidth(700);
        card.setMaxHeight(500);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

        getChildren().add(card);
        sendButton.setOnAction(e -> sendMessage());
    }

    private void loadHistory() {
        if (!ChatDatabaseHelper.isDatabaseAvailable()) {
            appendSystemMessage("Database offline — chat history unavailable.");
            return;
        }

        List<String> history = ChatDatabaseHelper.getRecentChatMessages(50);
        if (history.isEmpty()) {
            return;
        }
        for (String line : history) {
            chatArea.appendText(line + "\n");
        }
        chatArea.appendText("---\n");
    }

    private void connectToServer() {
        Thread connectThread = new Thread(() -> {
            try {
                socket = new Socket(ChatConfig.HOST, ChatConfig.PORT);
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());

                out.writeUTF(ChatConfig.JOIN_PREFIX + username);
                out.flush();

                String authResponse = in.readUTF();
                Platform.runLater(() -> {
                    if (!authResponse.startsWith(ChatConfig.SYSTEM_PREFIX)) {
                        appendIncomingMessage(authResponse);
                    }
                });

                if (authResponse.startsWith(ChatConfig.SYSTEM_PREFIX)
                        && authResponse.contains("Invalid")) {
                    Platform.runLater(() -> setInputEnabled(false));
                    return;
                }

                connected.set(true);
                Platform.runLater(() -> setInputEnabled(true));

                Thread listenerThread = new Thread(this::listenForMessages, "chat-listener");
                listenerThread.setDaemon(true);
                listenerThread.start();

            } catch (IOException e) {
                connected.set(false);
                Platform.runLater(() -> setInputEnabled(false));
            }
        }, "chat-connect");
        connectThread.setDaemon(true);
        connectThread.start();
    }

    private void listenForMessages() {
        try {
            while (connected.get()) {
                String msg = in.readUTF();
                Platform.runLater(() -> appendIncomingMessage(msg));
            }
        } catch (IOException e) {
            connected.set(false);
            Platform.runLater(() -> setInputEnabled(false));
        }
    }

    private void sendMessage() {
        if (!connected.get() || out == null) {
            appendSystemMessage("Not connected to server");
            return;
        }

        String message = inputField.getText().trim();
        String receiver = receiverField.getText().trim();

        if (receiver.isEmpty()) {
            appendSystemMessage("Receiver required");
            return;
        }

        if (message.isEmpty()) {
            return;
        }

        String payload = "MSG|" + receiver + "|" + message;

        try {
            out.writeUTF(payload);
            out.flush();
            inputField.setText("");
            inputField.setText("");
        } catch (IOException e) {
            connected.set(false);
            setInputEnabled(false);
            appendSystemMessage("Failed to send message");
        }
    }

    private void appendIncomingMessage(String msg) {
        if (msg.startsWith(ChatConfig.SYSTEM_PREFIX)) {
            return;
        }
        chatArea.appendText(msg + "\n");
    }

    private void appendSystemMessage(String message) {
        chatArea.appendText("[System] " + message + "\n");
    }

    private void setInputEnabled(boolean enabled) {
        sendButton.setDisable(!enabled);
        inputField.setDisable(!enabled);
    }

    public void disconnect() {
        connected.set(false);
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ignored) {
        }
    }
    private void openProfile(String user) {
        ProfilePage profile = new ProfilePage(user);

        // simplest option (temporary):
        Stage stage = new Stage();
        stage.setScene(new Scene(profile));
        stage.setTitle("Profile - " + user);
        stage.show();
    }
}
