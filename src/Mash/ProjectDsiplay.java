package Mash;

import Main.Main;
import Models.Project;
import Models.ProjectImage;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ProjectDsiplay extends VBox {
    private Project project;
    public ProjectDsiplay(Project project, Main mainApp) {
        this.project = project;

        this.getStyleClass().add("project-card");
        this.setSpacing(10);
        this.setPadding(new Insets(20));
        
        // Ensure card has a constant width
        this.setMaxWidth(600);
        this.setMinWidth(600);

        // Set all the attributes to be displayed for a project
        Text projectTitle = new Text(project.title);
        projectTitle.getStyleClass().add("subtitle-text");
        // wrap title as well just in case
        projectTitle.setWrappingWidth(560);
        this.getChildren().add(projectTitle);
        
        // Get the thumbnail of the image if it has one
        for (ProjectImage img : project.images) {
            if (img.isThumbnail()) {
                // Add this image to the project showcase
                Image image = new Image(img.getImageUrl());
                ImageView imageView = new ImageView(image);

                // A little bit of styling
                imageView.setFitWidth(200);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imageView.getStyleClass().add("project-image");
                
                // Add hover effect
                imageView.setOnMouseEntered(e -> {
                    imageView.setScaleX(1.05);
                    imageView.setScaleY(1.05);
                });
                imageView.setOnMouseExited(e -> {
                    imageView.setScaleX(1.0);
                    imageView.setScaleY(1.0);
                });

                // Add it to the component
                this.getChildren().add(imageView);
                break;
            }
        }
        Text projectDescription = new Text(project.description);
        projectDescription.getStyleClass().add("normal-text");
        // Text wrap the description to push content downward
        projectDescription.setWrappingWidth(560);
        
        this.getChildren().add(projectDescription);

        // create and display the tag area
        if (!project.tags.isEmpty()) {
            TagsDisplay tagsDisplay = new TagsDisplay(project.tags);
            this.getChildren().add(tagsDisplay);
        }

        // Action Buttons
        Button editBtn = new Button("Edit");
        editBtn.getStyleClass().add("custom-button");
        editBtn.setPrefWidth(100);
        editBtn.setOnAction(e -> {
            e.consume(); // Prevent navigating to ProjectDetails
            mainApp.switchToEdit(project);
        });

        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("danger-button");
        deleteBtn.setPrefWidth(100);
        deleteBtn.setOnAction(e -> {
            e.consume(); // Prevent navigating to ProjectDetails
            mainApp.deleteProject(project.id);
        });

        HBox btnBox = new HBox(15, editBtn, deleteBtn);
        this.getChildren().add(btnBox);
    }
}
