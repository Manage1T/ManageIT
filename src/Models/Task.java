package Models;

import java.time.LocalDateTime;

public class Task {
    public int id;
    public int projectId;
    public String title;
    public boolean isCompleted;
    public LocalDateTime createdAt;

    public Task() {}

    public Task(int id, int projectId, String title, boolean isCompleted, LocalDateTime createdAt) {
        this.id = id;
        this.projectId = projectId;
        this.title = title;
        this.isCompleted = isCompleted;
        this.createdAt = createdAt;
    }
}
