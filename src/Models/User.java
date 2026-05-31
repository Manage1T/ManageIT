package Models;
import java.time.LocalDateTime;

public class User {
    public int id;
    public String username;
    public String passwordHash;
    public String profilePictureUrl;
    public LocalDateTime createdAt;

    // Default constructor (often required by frameworks like Hibernate/Jackson)
    public User() {}

    // Full constructor
    public User(int id, String username, String passwordHash, String profilePictureUrl, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.profilePictureUrl = profilePictureUrl;
        this.createdAt = createdAt;
    }
}