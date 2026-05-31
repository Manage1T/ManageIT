package Models;

public class Tag {
    public int id;
    public String name;
    public boolean isSystemDefined;
    public Integer createdByUserId;

    public Tag() {}

    public Tag(int id, String name, boolean isSystemDefined, Integer createdByUserId) {
        this.id = id;
        this.name = name;
        this.isSystemDefined = isSystemDefined;
        this.createdByUserId = createdByUserId;
    }
}