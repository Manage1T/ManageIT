package Database;

import Models.Project;
import Models.Tag;

import java.sql.*;
import java.util.ArrayList;

public class Database {
    public Connection con;

    public Database() {
        con = createConnection();
    }

    Connection createConnection() {
        try {
            // Create driver
            Class.forName("org.postgresql.Driver");
            // Create a connection
            String url = "jdbc:postgresql://ep-lucky-heart-apwes3oz.c-7.us-east-1.aws.neon.tech/neondb?sslmode=require";

            return DriverManager.getConnection(
                    url,
                    "neondb_owner",
                    "npg_4zxK5dmIEhZD"
            );
        } catch (Exception e) {
            System.out.println("Error when connecting to database : " + e.getMessage());
        }
        return null;
    }

    // Get all projects of a user
    public ArrayList<Project> getProjects(String username) {
        ArrayList<Project> projects = new ArrayList<>();

        String projectSql = """
        SELECT p.*
        FROM projects p
        JOIN users u ON p.user_id = u.id
        WHERE u.username = ?
        ORDER BY p.created_at DESC
        """;

        String tagSql = """
        SELECT t.*
        FROM tags t
        JOIN project_tags pt ON t.id = pt.tag_id
        WHERE pt.project_id = ?
        """;

        try (
                PreparedStatement projectStmt = this.con.prepareStatement(projectSql);
                PreparedStatement tagStmt = this.con.prepareStatement(tagSql)
        ) {
            projectStmt.setString(1, username);

            try (ResultSet projectRs = projectStmt.executeQuery()) {

                while (projectRs.next()) {
                    Project project = new Project(
                            projectRs.getInt("id"),
                            projectRs.getInt("user_id"),
                            projectRs.getString("title"),
                            projectRs.getString("description"),
                            projectRs.getString("status"),
                            projectRs.getInt("progress_percentage"),
                            projectRs.getString("next_milestone"),
                            projectRs.getTimestamp("created_at").toLocalDateTime()
                    );

                    // Load tags for this project
                    tagStmt.setInt(1, project.id);

                    ArrayList<Tag> tags = new ArrayList<>();

                    try (ResultSet tagRs = tagStmt.executeQuery()) {
                        while (tagRs.next()) {
                            tags.add(new Tag(
                                    tagRs.getInt("id"),
                                    tagRs.getString("name"),
                                    tagRs.getBoolean("is_system_defined"),
                                    (Integer) tagRs.getObject("created_by_user_id")
                            ));
                        }
                    }

                    project.addTags(tags);
                    projects.add(project);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return projects;
    }
}
