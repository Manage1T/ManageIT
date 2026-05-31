package Naod.communication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatServer {

    private static final List<ClientHandler> clients =
            Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        ChatDatabaseHelper.ensureSchema();

        try (ServerSocket serverSocket = new ServerSocket(ChatConfig.PORT)) {
            System.out.println("Chat Server started on port " + ChatConfig.PORT);
            if (!ChatDatabaseHelper.isDatabaseAvailable()) {
                System.out.println("Running without database — messages will not be saved.");
            }

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");

                ClientHandler handler = new ClientHandler(socket, clients);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            String s = e.getMessage();
        }
    }
}
