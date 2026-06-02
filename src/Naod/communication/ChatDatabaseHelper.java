package Naod.communication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ChatDatabaseHelper {

    private static Boolean databaseAvailable;

    private static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "PostgreSQL driver not found. Add lib/postgresql-42.7.5.jar to your classpath.", e);
        }

        String url = "jdbc:postgresql://localhost:5432/school_db";
        return DriverManager.getConnection(url, "postgres", "6915");
    }

    public static boolean isDatabaseAvailable() {
        if (databaseAvailable == null) {
            try (Connection conn = getConnection()) {
                databaseAvailable = conn != null && !conn.isClosed();
            } catch (SQLException e) {
                databaseAvailable = false;
                System.err.println("Chat database unavailable: " + e.getMessage());
                System.err.println(
                        "Use this classpath when running from terminal:\n"
                                + "  -cp out/production/ManageIT:lib/postgresql-42.7.5.jar\n"
                                + "Chat will run without history and username validation until the database is reachable."
                );
            }
        }
        return databaseAvailable;
    }

    public static void ensureSchema() {
        if (!isDatabaseAvailable()) {
            return;
        }

        String createTable = "CREATE TABLE IF NOT EXISTS chat_messages ("
                + "id SERIAL PRIMARY KEY, "
                + "username VARCHAR(255) NOT NULL, "
                + "message TEXT NOT NULL, "
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                + ")";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTable);
            System.out.println("Chat schema verified successfully.");
        } catch (SQLException e) {
            databaseAvailable = false;
            System.err.println("Failed to ensure chat schema: " + e.getMessage());
        }
    }

    public static String resolveUsername(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        String trimmed = username.trim();
        if (!isDatabaseAvailable()) {
            return trimmed;
        }

        String sql = "SELECT username FROM users WHERE LOWER(username) = LOWER(?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, trimmed);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getString("username") : null;
            }
        } catch (SQLException e) {
            System.err.println("Failed to check username: " + e.getMessage());
            return null;
        }
    }

    public static boolean userExists(String username) {
        return resolveUsername(username) != null;
    }

    public static void saveChatMessage(String username, String message) {
        if (!isDatabaseAvailable()) {
            return;
        }

        String sql = "INSERT INTO chat_messages (username, message) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, message);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to save chat message: " + e.getMessage());
        }
    }

    public static List<String> getRecentChatMessages(int limit) {
        if (!isDatabaseAvailable()) {
            return List.of();
        }

        List<String> messages = new ArrayList<>();
        String sql = "SELECT username, message FROM chat_messages ORDER BY created_at DESC LIMIT ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(0, rs.getString("username") + ": " + rs.getString("message"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to load chat history: " + e.getMessage());
        }
        return messages;
    }
}
