package Naod.communication;

public class ChatUserRepository {

    public static String resolveUsername(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }

        String trimmed = username.trim();

        if (!ChatDatabaseHelper.isDatabaseAvailable()) {
            return trimmed;
        }

        String sql = "SELECT username FROM users WHERE LOWER(username) = LOWER(?)";

        try (var conn = java.sql.DriverManager.getConnection(
                "jdbc:postgresql://ep-lucky-heart-apwes3oz.c-7.us-east-1.aws.neon.tech/neondb"
                        + "?sslmode=require&user=neondb_owner&password=npg_4zxK5dmIEhZD");
             var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, trimmed);

            try (var rs = stmt.executeQuery()) {
                return rs.next() ? rs.getString("username") : null;
            }

        } catch (Exception e) {
            System.err.println("User lookup failed: " + e.getMessage());
            return null;
        }
    }

    public static boolean userExists(String username) {
        return resolveUsername(username) != null;
    }
}