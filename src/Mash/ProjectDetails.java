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
        int totalTasks = project.tasks.size();
        int doneTasks = 0;
        if (project.tasks != null && !project.tasks.isEmpty()) {
            for (Task task : project.tasks) {
                CheckBox taskCheckbox = new CheckBox(task.title);
                taskCheckbox.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
                taskCheckbox.setSelected(task.isCompleted);
                if (task.isCompleted) {
                    // If this view is strictly for display (read-only), disable the checkbox so users can't click it
                    taskCheckbox.setDisable(true);
                    doneTasks++;
                }
                tasksBox.getChildren().add(taskCheckbox);
            }
        } else {
            tasksBox.getChildren().add(new Label("No tasks assigned."));
        }

        // 6. Project Progress Percentage
        project.progressPercentage = (totalTasks == 0) ? 0 : (doneTasks * 100) / totalTasks;
        Label progressLabel = new Label("Project Progress: " + project.progressPercentage + "%");
        progressLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        progressLabel.setStyle("-fx-text-fill: #0ba360;");

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