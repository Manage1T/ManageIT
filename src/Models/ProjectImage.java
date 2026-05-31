package Models;

import java.time.LocalDateTime;

public class ProjectImage {
    private int id;
    private int projectId;
    private String imageUrl;
    private boolean isThumbnail;
    private LocalDateTime uploadedAt;

    public ProjectImage() {}

    public ProjectImage(int id, int projectId, String imageUrl, boolean isThumbnail, LocalDateTime uploadedAt) {
        this.id = id;
        this.projectId = projectId;
        this.imageUrl = imageUrl;
        this.isThumbnail = isThumbnail;
        this.uploadedAt = uploadedAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isThumbnail() { return isThumbnail; }
    public void setThumbnail(boolean thumbnail) { isThumbnail = thumbnail; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}
