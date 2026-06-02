package Mash;

import Main.Main;
import Models.Project;
import Models.ProjectImage;
import Models.Task;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ProjectDetails extends VBox {
    Main mainApp;
    public ProjectDetails(Project project, Main mainApp) {
        this.mainApp = mainApp;
        
        try {
            this.getStylesheets().add(getClass().getResource("mash.css").toExternalForm());
        } catch (Exception e) {}
        this.getStyleClass().add("root-container");

        // Basic spacing for the VBox to ensure it isn't completely squished
        this.setSpacing(15);
        this.setPadding(new Insets(40));

        // 1. Project Title
        Label titleLabel = new Label(project.title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titleLabel.setStyle("-fx-text-fill: #2d3748;");

        // 2. Project Description
        Label descLabel = new Label(project.description);
        descLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        descLabel.setStyle("-fx-text-fill: #4a5568;");
        descLabel.setWrapText(true); // Ensures long descriptions don't break the window width

        // 3. Project Images (HBox Component)
        Label imagesHeader = new Label("Project Images:");
        imagesHeader.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        imagesHeader.setStyle("-fx-text-fill: #2d3748;");
        HBox imagesBox = new HBox(10); // 10px spacing between images

        if (project.images != null && !project.images.isEmpty()) {
            for (ProjectImage pImage : project.images) {
                try {
                    // Load the image. (150, 150) restricts the size, true preserves ratio
                    Image img = new Image(pImage.getImageUrl(), 150, 150, true, true);
                    ImageView imageView = new ImageView(img);
                    imagesBox.getChildren().add(imageView);
                } catch (Exception e) {
                    System.err.println("Failed to load image: " + pImage.getImageUrl());
                }
            }
        } else {
            imagesBox.getChildren().add(new Label("No images attached to this project."));
        }

        // 4. Project Status
        Label statusLabel = new Label("Project Status: " + project.status);
        statusLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        statusLabel.setStyle("-fx-text-fill: #0ba360;"); // highlight status

        // 5. Project Tasks Checklist
        Label tasksHeader = new Label("Project Tasks:");
        tasksHeader.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        tasksHeader.setStyle("-fx-text-fill: #2d3748;");
        VBox tasksBox = new VBox(8); // Slight spacing between checklist items
        int totalTasks = project.tasks != null ? project.tasks.size() : 0;

        // 6. Project Progress Percentage
        Label progressLabel = new Label();
        progressLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        progressLabel.setStyle("-fx-text-fill: #0ba360;");

        // Helper to update progress live
        Runnable updateProgress = () -> {
            int done = 0;
            if (project.tasks != null) {
                for (Task t : project.tasks) {
                    if (t.isCompleted) {
                        done++;
                    }
                }
            }
            project.progressPercentage = (totalTasks == 0) ? 0 : (done * 100) / totalTasks;
            progressLabel.setText("Project Progress: " + project.progressPercentage + "%");

            // Update status based on whether all tasks are complete
            String calculatedStatus = (totalTasks > 0 && done == totalTasks) ? "complete" : "new";
            if (!calculatedStatus.equalsIgnoreCase(project.status)) {
                project.status = calculatedStatus;
                statusLabel.setText("Project Status: " + calculatedStatus);
                if (mainApp.getDb() != null) {
                    mainApp.getDb().updateProjectStatus(project.id, calculatedStatus);
                }
            }
        };

        if (project.tasks != null && !project.tasks.isEmpty()) {
            for (Task task : project.tasks) {
                CheckBox taskCheckbox = new CheckBox(task.title);
                taskCheckbox.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
                taskCheckbox.setSelected(task.isCompleted);
                
                // Set interactive action handler to save and update state
                taskCheckbox.setOnAction(e -> {
                    boolean isSel = taskCheckbox.isSelected();
                    task.isCompleted = isSel;
                    if (mainApp.getDb() != null) {
                        mainApp.getDb().updateTaskCompletion(task.id, isSel);
                    }
                    updateProgress.run();
                });
                
                tasksBox.getChildren().add(taskCheckbox);
            }
        } else {
            tasksBox.getChildren().add(new Label("No tasks assigned."));
        }

        // Initialize progress percentage and text
        updateProgress.run();

        // Create a back button to go back to the dashboard
        Button back = new Button("Back to Dashboard");
        back.getStyleClass().add("secondary-button");
        back.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        back.setOnAction((e) -> {
            mainApp.switchToDashboard();
        });

        // Add everything to this main VBox component
        VBox card = new VBox(15);
        card.getStyleClass().add("project-card");
        card.setPadding(new Insets(20));
        card.getChildren().addAll(
                titleLabel,
                descLabel,
                imagesHeader,
                imagesBox,
                statusLabel,
                tasksHeader,
                tasksBox,
                progressLabel,
                back
        );

        this.getChildren().add(card);
    }
}