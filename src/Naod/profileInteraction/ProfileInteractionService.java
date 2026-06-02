package Naod.profileInteraction;

import java.io.DataOutputStream;
import java.net.Socket;

public class ProfileInteractionService {

    private final String currentUser;

    public ProfileInteractionService(String currentUser) {
        this.currentUser = currentUser;
    }

    public void sendMessage(String receiver, String message) {
        try {
            Socket socket = new Socket("localhost", 50004); // same server port
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            out.writeUTF("MSG|" + receiver + "|" + message);
            out.flush();

            socket.close();
        } catch (Exception e) {
            System.err.println("Failed to send message: " + e.getMessage());
        }
    }
}