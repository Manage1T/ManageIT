package Naod.profileInteraction;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class ProfileInteractionPanel extends HBox {

    private final ProfileInteractionService service;

    public ProfileInteractionPanel(String currentUser) {
        this.service = new ProfileInteractionService(currentUser);

        TextField receiverField = new TextField();
        receiverField.setPromptText("Receiver");

        TextField messageField = new TextField();
        messageField.setPromptText("Message");

        Button sendBtn = new Button("Send");

        sendBtn.setOnAction(e -> {
            String receiver = receiverField.getText();
            String msg = messageField.getText();

            if (!receiver.isEmpty() && !msg.isEmpty()) {
                service.sendMessage(receiver, msg);
                messageField.clear();
            }
        });

        getChildren().addAll(receiverField, messageField, sendBtn);
    }
}