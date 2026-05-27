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

    private static String dbUrl;
    private static String dbName;
    private static String dbUser;
    private static String dbPassword;

    // Static initializer to load database properties
    static {
        Properties props = new Properties();
        try {
            // Try loading from src/db.properties first (if working directory is ManageIT)
            File srcFile = new File("src/db.properties");
            // Also try ManageIT/src/db.properties (if working directory is root manage_it)
            File rootSrcFile = new File("ManageIT/src/db.properties");
            
            File targetFile = null;
            if (srcFile.exists()) {
                targetFile = srcFile;
            } else if (rootSrcFile.exists()) {
                targetFile = rootSrcFile;
            }

            if (targetFile != null) {
                System.out.println("Loaded db.properties from file system: " + targetFile.getAbsolutePath());
                try (InputStream fis = new FileInputStream(targetFile)) {
                    props.load(fis);
                }
            } else {
                System.out.println("db.properties not found on file system (checked src/ and ManageIT/src/). Falling back to ClassLoader...");
                // Fallback to classloader resources
                try (InputStream is = DatabaseHelper.class.getClassLoader().getResourceAsStream("db.properties")) {
                    if (is != null) {
                        System.out.println("Loaded db.properties from ClassLoader.");
                        props.load(is);
                    } else {
                        System.err.println("db.properties NOT found in ClassLoader either!");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not load db.properties: " + e.getMessage());
        }

        dbUrl = props.getProperty("db.url", "jdbc:mysql://localhost:3306/");
        dbName = props.getProperty("db.name", "manage_it_db");
        dbUser = props.getProperty("db.username", "root");
        dbPassword = props.getProperty("db.password", "");
        System.out.println("Loaded DatabaseConfig -> Url: " + dbUrl + ", Name: " + dbName + ", User: " + dbUser + ", Password length: " + dbPassword.length());
    }

    // Connect to the base server to create the database if not exists
    public static synchronized void initializeDatabase() throws SQLException, ClassNotFoundException {
        // Explicitly load the driver
        Class.forName("com.mysql.cj.jdbc.Driver");

        // First connect to base URL to create database
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
            }
        }

        // Then connect to the database to create the table
        String fullUrl = dbUrl.endsWith("/") ? dbUrl + dbName : dbUrl + "/" + dbName;
        try (Connection conn = DriverManager.getConnection(fullUrl, dbUser, dbPassword)) {
            try (Statement stmt = conn.createStatement()) {
                String sql = "CREATE TABLE IF NOT EXISTS users ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY,"
                        + "name VARCHAR(100) NOT NULL,"
                        + "username VARCHAR(50) NOT NULL UNIQUE,"
                        + "email VARCHAR(100) NOT NULL,"
                        + "password_hash VARCHAR(64) NOT NULL,"
                        + "salt VARCHAR(32) NOT NULL,"
                        + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                        + ")";
                stmt.executeUpdate(sql);

                // Migration: add username column to existing tables that don't have it
                try {
                    stmt.executeUpdate("ALTER TABLE users ADD COLUMN username VARCHAR(50) NOT NULL UNIQUE AFTER name");
                } catch (SQLException ignored) {
                    // Column already exists — safe to ignore
                }

                // Migration: drop unique index/constraint on email column if exists
                try {
                    stmt.executeUpdate("ALTER TABLE users DROP INDEX email");
                } catch (SQLException ignored) {
                    // Constraint is already dropped or does not exist — safe to ignore
                }
            }
        }
    }

    // Helper: get a database connection to the specific database
    private static Connection getConnection() throws SQLException {
        String fullUrl = dbUrl.endsWith("/") ? dbUrl + dbName : dbUrl + "/" + dbName;
        return DriverManager.getConnection(fullUrl, dbUser, dbPassword);
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
