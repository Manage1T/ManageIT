package Naod.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final List<ClientHandler> clients;
    private DataInputStream in;
    private DataOutputStream out;
    private String username;
    private static final Map<String, ClientHandler> userMap = new ConcurrentHashMap<>();

    public ClientHandler(Socket socket, List<ClientHandler> clients) {
        this.socket = socket;
        this.clients = clients;
    }

    @Override
    public void run() {
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            String joinLine = in.readUTF();
            if (!joinLine.startsWith(ChatConfig.JOIN_PREFIX)) {
                sendSystem("Authentication required. Send JOIN:username first.");
                return;
            }

            username = joinLine.substring(ChatConfig.JOIN_PREFIX.length()).trim();
            String resolvedUsername = ChatDatabaseHelper.resolveUsername(username);
            if (resolvedUsername == null) {
                sendSystem("Invalid username. Use a registered account (e.g. test, user).");
                return;
            }
            username = resolvedUsername;

            synchronized (clients) {
                clients.add(this);
            }
            userMap.put(username, this);

            sendSystem("Connected as " + username);
            broadcast(ChatConfig.SYSTEM_PREFIX + username + " joined the chat", null);

            while (true) {
                String text = in.readUTF().trim();
                if (text.isEmpty()) {
                    continue;
                }

                if (text.startsWith("MSG|")) {
                    String[] parts = text.split("\\|", 3);

                    if (parts.length < 3) {
                        sendSystem("Invalid message format");
                        continue;
                    }

                    String receiver = parts[1].trim();
                    String message = parts[2].trim();

                    ChatDatabaseHelper.saveChatMessage(username, message);

                    ClientHandler target = userMap.get(receiver);

                    if (target != null) {
                        target.out.writeUTF(username + ": " + message);
                        target.out.flush();
                    } else {
                        sendSystem("User not online: " + receiver);
                    }

                } else {
                    sendSystem("Unsupported format. Use MSG|receiver|message");
                }
            }

        } catch (IOException e) {
            System.out.println("Client disconnected: " + (username != null ? username : "unknown"));
        } finally {
            if (username != null) {
                userMap.remove(username);
                broadcast(ChatConfig.SYSTEM_PREFIX + username + " left the chat", this);
            }
            close();
        }
    }

    private void sendSystem(String message) throws IOException {
        out.writeUTF(ChatConfig.SYSTEM_PREFIX + message);
        out.flush();
    }

    private void broadcast(String message, ClientHandler exclude) {
        List<ClientHandler> snapshot;
        synchronized (clients) {
            snapshot = new ArrayList<>(clients);
        }

        for (ClientHandler client : snapshot) {
            if (client == exclude || client.out == null) {
                continue;
            }
            try {
                client.out.writeUTF(message);
                client.out.flush();
            } catch (IOException ignored) {
            }
        }
    }

    private void close() {
        synchronized (clients) {
            clients.remove(this);
        }
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }
}
