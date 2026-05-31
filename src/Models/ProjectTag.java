package Models;

public class ProjectTag {
    public int projectId;
    public int tagId;

    public ProjectTag() {}

    public ProjectTag(int projectId, int tagId) {
        this.projectId = projectId;
        this.tagId = tagId;
    }
}