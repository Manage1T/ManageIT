package Database;

import Models.Project;
import Models.ProjectImage;
import Models.Tag;
import Models.Task;

import java.sql.*;
import java.time.LocalDateTime;
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

        String imageSql = """
        SELECT *
        FROM project_images
        WHERE project_id = ?
        ORDER BY uploaded_at
        """;

        String taskSql = """
        SELECT *
        FROM tasks
        WHERE project_id = ?
        ORDER BY created_at
        """;

        try (
                PreparedStatement projectStmt = con.prepareStatement(projectSql);
                PreparedStatement tagStmt = con.prepareStatement(tagSql);
                PreparedStatement imageStmt = con.prepareStatement(imageSql);
                PreparedStatement taskStmt = con.prepareStatement(taskSql)
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

                    // Load tags
                    ArrayList<Tag> tags = new ArrayList<>();

                    tagStmt.setInt(1, project.id);

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

                    // Load images
                    ArrayList<ProjectImage> images = new ArrayList<>();

                    imageStmt.setInt(1, project.id);

                    try (ResultSet imageRs = imageStmt.executeQuery()) {
                        while (imageRs.next()) {
                            images.add(new ProjectImage(
                                    imageRs.getInt("id"),
                                    imageRs.getInt("project_id"),
                                    imageRs.getString("image_url"),
                                    imageRs.getBoolean("is_thumbnail"),
                                    imageRs.getTimestamp("uploaded_at").toLocalDateTime()
                            ));
                        }
                    }

                    project.addImages(images);

                    ArrayList<Task> tasks = new ArrayList<>();

                    taskStmt.setInt(1, project.id);

                    try (ResultSet taskRs = taskStmt.executeQuery()) {
                        while (taskRs.next()) {
                            tasks.add(new Task(
                                    taskRs.getInt("id"),
                                    taskRs.getInt("project_id"),
                                    taskRs.getString("title"),
                                    taskRs.getBoolean("is_completed"),
                                    taskRs.getTimestamp("created_at").toLocalDateTime()
                            ));
                        }
                    }

                    project.addTasks(tasks);

                    projects.add(project);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return projects;
    }

    public boolean createProject(Project project, ArrayList<Tag> tags, ArrayList<String> image_urls, ArrayList<String> tasks) {

        String projectSql = "INSERT INTO projects (user_id, title, description, status, created_at) VALUES (?, ?, ?, ?, ?)";
        String tagSql = "INSERT INTO project_tags (project_id, tag_id) VALUES (?, ?)";
        String imageSql = "INSERT INTO project_images (project_id, image_url, is_thumbnail) VALUES (?, ?, ?)";
        String taskSql = "INSERT INTO tasks (project_id, title) VALUES (?, ?)";

        try {
            // 1. Start Transaction
            this.con.setAutoCommit(false);

            // STEP 1: INSERT PROJECT & GET GENERATED ID
            try (PreparedStatement pstmtProject = this.con.prepareStatement(projectSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmtProject.setInt(1, project.userId);
                pstmtProject.setString(2, project.title);
                pstmtProject.setString(3, project.description);
                pstmtProject.setString(4, project.status);
                pstmtProject.setTimestamp(5, Timestamp.valueOf(project.createdAt));

                int affectedRows = pstmtProject.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating project failed, no rows affected.");
                }
                // Retrieve the generated ID
                try (ResultSet generatedKeys = pstmtProject.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        project.id = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Creating project failed, no ID obtained.");
                    }
                }
            }

            // STEP 2: INSERT TAGS (Handled safely with ON CONFLICT)
            if (tags != null && !tags.isEmpty()) {
                // 1. Define your SQL strings properly
                String insertNewTagSql = "INSERT INTO tags (name, created_by_user_id) VALUES (?, ?) " +
                        "ON CONFLICT (name) DO UPDATE SET name = EXCLUDED.name RETURNING id";
                String linkTagSql = "INSERT INTO project_tags (project_id, tag_id) VALUES (?, ?)";

                // 2. Prepare the link statement outside the loop for batching
                try (PreparedStatement pstmtLink = this.con.prepareStatement(linkTagSql)) {

                    for (Tag tag : tags) {
                        // 3. Insert or find the tag, returning its definitive DB id
                        try (PreparedStatement insertTagStmt = this.con.prepareStatement(insertNewTagSql)) {
                            insertTagStmt.setString(1, tag.name);
                            insertTagStmt.setInt(2, project.userId);

                            // Because we use RETURNING id, we use executeQuery() instead of executeUpdate()
                            try (ResultSet rs = insertTagStmt.executeQuery()) {
                                if (rs.next()) {
                                    // This safely overrides your random Java UI ID with the actual DB primary key
                                    tag.id = rs.getInt("id");
                                }
                            }
                        }

                        // 4. Now add it to the batch linking statement
                        pstmtLink.setInt(1, project.id);
                        pstmtLink.setInt(2, tag.id); // This is now guaranteed to be a valid DB ID
                        pstmtLink.addBatch();

                        // Sync up your project object arraylist
                        project.tags.add(tag);
                    }

                    // 5. Execute all the links at once over the network
                    pstmtLink.executeBatch();
                }
            }


            // STEP 3: INSERT IMAGES (Batch Processing)
            if (image_urls != null && !image_urls.isEmpty()) {
                try (PreparedStatement pstmtImages = this.con.prepareStatement(imageSql)) {
                    for (int i = 0; i < image_urls.size(); i++) {
                        String url = image_urls.get(i);
                        boolean isThumbnail = (i == 0); // Make the very first uploaded image the thumbnail by default

                        pstmtImages.setInt(1, project.id);
                        pstmtImages.setString(2, url);
                        pstmtImages.setBoolean(3, isThumbnail);
                        pstmtImages.addBatch();

                        // Keep the Java object in sync
                        project.images.add(new ProjectImage(0, project.id, url, isThumbnail, LocalDateTime.now()));
                    }
                    pstmtImages.executeBatch();
                }
            }


            // STEP 4: INSERT TASKS (Batch Processing)
            if (tasks != null && !tasks.isEmpty()) {
                try (PreparedStatement pstmtTasks = this.con.prepareStatement(taskSql)) {
                    for (String taskTitle : tasks) {
                        pstmtTasks.setInt(1, project.id);
                        pstmtTasks.setString(2, taskTitle);
                        pstmtTasks.addBatch();

                        // Keep the Java object in sync
                        Task newTask = new Task();
                        newTask.projectId = project.id;
                        newTask.title = taskTitle;
                        newTask.isCompleted = false; // default
                        newTask.createdAt = LocalDateTime.now();
                        project.tasks.add(newTask);
                    }
                    pstmtTasks.executeBatch();
                }
            }

            // 2. If everything above succeeds, commit the transaction
            this.con.commit();
            System.out.println("Project created successfully with ID: " + project.id);
            return true;

        } catch (SQLException e) {
            // 3. If ANYTHING fails, roll back the transaction so the database stays clean
            try {
                if (this.con != null) {
                    this.con.rollback();
                    System.err.println("Transaction rolled back due to error.");
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            // 4. Always turn auto-commit back on to avoid breaking other database methods
            try {
                if (this.con != null) {
                    this.con.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean deleteProject(int projectId) {
        try {
            this.con.setAutoCommit(false);
            
            // Delete related records safely
            PreparedStatement delTasks = this.con.prepareStatement("DELETE FROM tasks WHERE project_id = ?");
            delTasks.setInt(1, projectId);
            delTasks.executeUpdate();

            PreparedStatement delImages = this.con.prepareStatement("DELETE FROM project_images WHERE project_id = ?");
            delImages.setInt(1, projectId);
            delImages.executeUpdate();

            PreparedStatement delTags = this.con.prepareStatement("DELETE FROM project_tags WHERE project_id = ?");
            delTags.setInt(1, projectId);
            delTags.executeUpdate();

            // Delete project
            PreparedStatement delProject = this.con.prepareStatement("DELETE FROM projects WHERE id = ?");
            delProject.setInt(1, projectId);
            int affected = delProject.executeUpdate();

            this.con.commit();
            return affected > 0;
        } catch (SQLException e) {
            try {
                if (this.con != null) this.con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (this.con != null) this.con.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean updateProject(int id, String title, String description) {
        String updateSql = "UPDATE projects SET title = ?, description = ? WHERE id = ?";
        try (PreparedStatement pstmt = this.con.prepareStatement(updateSql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setInt(3, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
