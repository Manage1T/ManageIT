package Mussie;

import Database.Database;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseHelper {
    public enum RegistrationResult {
        SUCCESS,
        EMAIL_ALREADY_EXISTS,
        USERNAME_ALREADY_EXISTS,
        ERROR
    }

    public static String lastGeneratedRecoveryKey = "";

    // Helper: get a database connection to the specific database
    private static Connection getConnection() throws SQLException {
        return new Database().con;
    }

    /**
     * Ensures the users table exists with all required columns.
     * Call this once on app startup.
     */
    public static void ensureSchema() {
        String createTable = "CREATE TABLE IF NOT EXISTS users ("
                + "id SERIAL PRIMARY KEY, "
                + "name VARCHAR(255), "
                + "username VARCHAR(255) UNIQUE NOT NULL, "
                + "email VARCHAR(255) UNIQUE NOT NULL, "
                + "password_hash VARCHAR(255) NOT NULL, "
                + "salt VARCHAR(255) NOT NULL, "
                + "recovery_key VARCHAR(255)"
                + ")";

        // Add any missing columns if the table already exists
        String[] addColumns = {
            "ALTER TABLE users ADD COLUMN IF NOT EXISTS name VARCHAR(255)",
            "ALTER TABLE users ADD COLUMN IF NOT EXISTS username VARCHAR(255)",
            "ALTER TABLE users ADD COLUMN IF NOT EXISTS email VARCHAR(255)",
            "ALTER TABLE users ADD COLUMN IF NOT EXISTS password_hash VARCHAR(255)",
            "ALTER TABLE users ADD COLUMN IF NOT EXISTS salt VARCHAR(255)",
            "ALTER TABLE users ADD COLUMN IF NOT EXISTS recovery_key VARCHAR(255)"
        };

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTable);
            for (String sql : addColumns) {
                stmt.execute(sql);
            }
            System.out.println("Database schema verified successfully.");
        } catch (SQLException e) {
            System.err.println("Failed to ensure database schema: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Generate a secure, readable recovery key (e.g. MIT-A8C3-F9E2)
    private static String generateRecoveryKey() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        java.util.Random rnd = new java.util.Random();
        StringBuilder sb = new StringBuilder("MIT-");
        for (int i = 0; i < 4; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        sb.append("-");
        for (int i = 0; i < 4; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // User Registration
    public static RegistrationResult registerUser(String name, String username, String email, String password) {
        // 1. Check if username already exists
        String checkUsernameSql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkUsernameSql)) {
            checkStmt.setString(1, username);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    return RegistrationResult.USERNAME_ALREADY_EXISTS;
                }
            }
        } catch (SQLException e) {
            System.err.println("Registration failed in step 1 (Check Username): " + e.getMessage());
            e.printStackTrace();
            return RegistrationResult.ERROR;
        }

        // 2. Check if email already exists
        String checkEmailSql = "SELECT id FROM users WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkEmailSql)) {
            checkStmt.setString(1, email);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    return RegistrationResult.EMAIL_ALREADY_EXISTS;
                }
            }
        } catch (SQLException e) {
            System.err.println("Registration failed in step 2 (Check Email): " + e.getMessage());
            e.printStackTrace();
            return RegistrationResult.ERROR;
        }

        // 3. Insert new user
        String insertSql = "INSERT INTO users (name, username, email, password_hash, salt, recovery_key) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            
            // Generate unique salt and hash the password
            String salt = PasswordHasher.generateSalt();
            String hash = PasswordHasher.hashPassword(password, salt);
            String recoveryKey = generateRecoveryKey();

            insertStmt.setString(1, name);
            insertStmt.setString(2, username);
            insertStmt.setString(3, email);
            insertStmt.setString(4, hash);
            insertStmt.setString(5, salt);
            insertStmt.setString(6, recoveryKey);

            int rows = insertStmt.executeUpdate();
            if (rows > 0) {
                lastGeneratedRecoveryKey = recoveryKey;
                return RegistrationResult.SUCCESS;
            } else {
                return RegistrationResult.ERROR;
            }
        } catch (SQLException e) {
            System.err.println("Registration failed in step 3 (Insert User): " + e.getMessage());
            e.printStackTrace();
            return RegistrationResult.ERROR;
        }
    }

    // User Authentication
    public static boolean authenticateUser(String username, String password) {
        String sql = "SELECT password_hash, salt FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    String salt = rs.getString("salt");
                    
                    // Hash the input password using the same salt
                    String calculatedHash = PasswordHasher.hashPassword(password, salt);
                    return calculatedHash.equals(storedHash);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Resets a user's password securely if the email and recovery key match.
     */
    public static boolean resetPassword(String email, String recoveryKey, String newPassword) {
        // Find user by email and matching recovery_key (case-insensitive for convenience)
        String checkSql = "SELECT id FROM users WHERE LOWER(email) = LOWER(?) AND UPPER(recovery_key) = UPPER(?)";
        try (Connection conn = getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, email.trim());
            checkStmt.setString(2, recoveryKey.trim());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (!rs.next()) {
                    return false; // Email or recovery key doesn't match
                }
            }

            // Update the password with a new salt and hash
            String updateSql = "UPDATE users SET password_hash = ?, salt = ? WHERE LOWER(email) = LOWER(?)";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                String salt = PasswordHasher.generateSalt();
                String hash = PasswordHasher.hashPassword(newPassword, salt);
                updateStmt.setString(1, hash);
                updateStmt.setString(2, salt);
                updateStmt.setString(3, email.trim());
                return updateStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Password reset failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
