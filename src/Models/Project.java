package Models;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Project {
    public int id;
    public int userId;
    public String title;
    public String description;
    public String status;
    public int progressPercentage;
    public String nextMilestone;
    public LocalDateTime createdAt;
    public ArrayList<Tag> tags = new ArrayList<Tag>();

    public Project() {}

    public Project(int id, int userId, String title, String description, String status,
                   int progressPercentage, String nextMilestone, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.progressPercentage = progressPercentage;
        this.nextMilestone = nextMilestone;
        this.createdAt = createdAt;
    }

    public void addTags(ArrayList<Tag>  tags) {
        this.tags.addAll(tags);
    }
}