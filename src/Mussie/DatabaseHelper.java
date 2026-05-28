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

    // Helper: get a database connection to the specific database
    private static Connection getConnection() throws SQLException {
        return new Database().con;
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



        // 3. Insert new user
        String insertSql = "INSERT INTO users (name, username, email, password_hash, salt) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            
            // Generate unique salt and hash the password
            String salt = PasswordHasher.generateSalt();
            String hash = PasswordHasher.hashPassword(password, salt);

            insertStmt.setString(1, name);
            insertStmt.setString(2, username);
            insertStmt.setString(3, email);
            insertStmt.setString(4, hash);
            insertStmt.setString(5, salt);

            int rows = insertStmt.executeUpdate();
            return rows > 0 ? RegistrationResult.SUCCESS : RegistrationResult.ERROR;
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
}
