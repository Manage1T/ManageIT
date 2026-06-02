package Mash;

import Database.Database;
import Main.Main;
import Models.Project;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class EditProjectDisplay extends VBox {
    private Main mainApp;
    private Database database;
    private Project project;

    private TextField titleInput = new TextField();
    private TextArea descriptionInput = new TextArea();

    public EditProjectDisplay(Database db, Main mainApp, Project project) {
        this.mainApp = mainApp;
        this.database = db;
        this.project = project;

        try {
            this.getStylesheets().add(getClass().getResource("mash.css").toExternalForm());
        } catch (Exception e) {}
        
        this.getStyleClass().add("root-container");
        this.setSpacing(20);
        this.setPadding(new Insets(40));
        this.setAlignment(Pos.CENTER);

        Label formTitle = new Label("Edit Project");
        formTitle.getStyleClass().add("title-text");

        titleInput.getStyleClass().add("custom-text-field");
        titleInput.setText(project.title);
        
        descriptionInput.getStyleClass().add("custom-text-area");
        descriptionInput.setText(project.description);
        descriptionInput.setPrefRowCount(3);

        Button saveChanges = new Button("Save changes");
        saveChanges.getStyleClass().add("custom-button");
        saveChanges.setStyle("-fx-font-size: 16px; -fx-padding: 12 30;");
        saveChanges.setOnAction(e -> save());

        Button backBtn = new Button("Back to Dashboard");
        backBtn.getStyleClass().add("secondary-button");
        backBtn.setOnAction(e -> mainApp.switchToDashboard());

        Label titleLabel = new Label("Project Title : ");
        titleLabel.getStyleClass().add("normal-text");
        titleLabel.setStyle("-fx-font-weight: bold;");
        HBox titleRow = new HBox(15, titleLabel, titleInput);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label descriptionLabel = new Label("Description : ");
        descriptionLabel.getStyleClass().add("normal-text");
        descriptionLabel.setStyle("-fx-font-weight: bold;");
        HBox descriptionRow = new HBox(15, descriptionLabel, descriptionInput);
        descriptionRow.setAlignment(Pos.CENTER_LEFT);

        HBox buttonRow = new HBox(15, saveChanges, backBtn);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        VBox formContent = new VBox(20, titleRow, descriptionRow, buttonRow);
        formContent.setAlignment(Pos.CENTER_LEFT);
        formContent.setMaxWidth(900);
        formContent.getStyleClass().add("project-card");

        this.getChildren().addAll(formTitle, formContent);
    }

    private void save() {
        boolean success = database.updateProject(project.id, titleInput.getText(), descriptionInput.getText());
        if(success) {
            System.out.println("Project updated successfully.");
        }
        mainApp.switchToDashboard();
    }
}
