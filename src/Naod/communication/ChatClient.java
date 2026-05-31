package Naod.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        Socket socket = new Socket(ChatConfig.HOST, ChatConfig.PORT);
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        out.writeUTF(ChatConfig.JOIN_PREFIX + username);
        out.flush();

        String authResponse = in.readUTF();
        System.out.println(formatMessage(authResponse));
        if (authResponse.startsWith(ChatConfig.SYSTEM_PREFIX)
                && authResponse.contains("Invalid")) {
            socket.close();
            return;
        }

        new Thread(() -> {
            try {
                while (true) {
                    String msg = in.readUTF();
                    System.out.println(formatMessage(msg));
                }
            } catch (IOException e) {
                System.out.println("Disconnected");
            }
        }).start();

        while (true) {
            String message = scanner.nextLine();
            if (message == null) {
                break;
            }
            out.writeUTF(message);
            out.flush();
        }
    }

    private static String formatMessage(String msg) {
        if (msg.startsWith(ChatConfig.SYSTEM_PREFIX)) {
            return "[System] " + msg.substring(ChatConfig.SYSTEM_PREFIX.length());
        }
        return msg;
    }
}
