package Mash;

import Database.Database;
import Main.Main;
import Models.Project;
import Models.Tag;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.ArrayList;

public class EditProjectDisplay extends VBox {
    private Main mainApp;
    private Database database;
    private Project project;

    private TextField titleInput = new TextField();
    private TextArea descriptionInput = new TextArea();
    private TextField taskInput = new TextField();
    private TextField tagInput = new TextField();
    private ToggleGroup statusInput = new ToggleGroup();

    private TagsDisplay tagsDisplay = new TagsDisplay();
    private TasksDisplay tasksDisplay = new TasksDisplay();
    private CollectionDisplay imagesDisplay = new CollectionDisplay();

    private ArrayList<String> images = new ArrayList<>();
    private ArrayList<Tag> tags = new ArrayList<>();
    private ArrayList<String> tasks = new ArrayList<>();

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

        taskInput.getStyleClass().add("custom-text-field");
        taskInput.setPromptText("Enter New Task");
        
        tagInput.getStyleClass().add("custom-text-field");
        tagInput.setPromptText("Enter New Tag");

        // Initialize status toggles
        RadioButton complete = new RadioButton("complete");
        RadioButton inProgress = new RadioButton("in_progress");
        RadioButton start = new RadioButton("new");
        complete.getStyleClass().add("normal-text");
        inProgress.getStyleClass().add("normal-text");
        start.getStyleClass().add("normal-text");
        complete.setToggleGroup(statusInput);
        inProgress.setToggleGroup(statusInput);
        start.setToggleGroup(statusInput);
        if ("complete".equalsIgnoreCase(project.status)) {
            complete.setSelected(true);
        } else if ("in_progress".equalsIgnoreCase(project.status)) {
            inProgress.setSelected(true);
        } else {
            start.setSelected(true);
        }

        // Initialize existing tags, tasks, and images in visual displays
        if (project.tags != null) {
            for (Tag t : project.tags) {
                tagsDisplay.addTag(t);
            }
        }
        if (project.tasks != null) {
            for (Models.Task t : project.tasks) {
                tasksDisplay.addTaskTitle(t.title);
            }
        }
        if (project.images != null) {
            for (Models.ProjectImage img : project.images) {
                imagesDisplay.addString(img.getImageUrl());
            }
        }

        // Buttons for adding items (tags, images, tasks)
        Button addTag = new Button("Add Tag");
        addTag.getStyleClass().add("custom-button");
        addTag.setOnAction(e -> handleAddTag());

        Button addImage = new Button("Add Image");
        addImage.getStyleClass().add("custom-button");
        addImage.setOnAction(e -> handleImageUpload());

        Button addTask = new Button("Add task");
        addTask.getStyleClass().add("custom-button");
        addTask.setOnAction(e -> handleAddTask());

        Button saveChanges = new Button("Save changes");
        saveChanges.getStyleClass().add("custom-button");
        saveChanges.setStyle("-fx-font-size: 16px; -fx-padding: 12 30;");
        saveChanges.setOnAction(e -> save());

        Button backBtn = new Button("Back to Dashboard");
        backBtn.getStyleClass().add("secondary-button");
        backBtn.setOnAction(e -> mainApp.switchToDashboard());

        // Layout rows
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

        Label imageLabel = new Label("Image : ");
        imageLabel.getStyleClass().add("normal-text");
        imageLabel.setStyle("-fx-font-weight: bold;");
        HBox imageRow = new HBox(15, imageLabel, addImage, imagesDisplay);
        imageRow.setAlignment(Pos.CENTER_LEFT);

        Label taskLabel = new Label("Task : ");
        taskLabel.getStyleClass().add("normal-text");
        taskLabel.setStyle("-fx-font-weight: bold;");
        HBox taskRow = new HBox(15, taskLabel, taskInput, addTask);
        taskRow.setAlignment(Pos.CENTER_LEFT);

        Label statusLabel = new Label("Project Status : ");
        statusLabel.getStyleClass().add("normal-text");
        statusLabel.setStyle("-fx-font-weight: bold;");
        HBox statusRow = new HBox(15, statusLabel, start, inProgress, complete);
        statusRow.setAlignment(Pos.CENTER_LEFT);

        Label tagLabel = new Label("Tag : ");
        tagLabel.getStyleClass().add("normal-text");
        tagLabel.setStyle("-fx-font-weight: bold;");
        HBox tagRow = new HBox(15, tagLabel, tagInput, addTag);
        tagRow.setAlignment(Pos.CENTER_LEFT);

        HBox buttonRow = new HBox(15, saveChanges, backBtn);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        VBox formContent = new VBox(20, titleRow, descriptionRow, imageRow, taskRow, tasksDisplay, statusRow, tagRow, tagsDisplay, buttonRow);
        formContent.setAlignment(Pos.CENTER_LEFT);
        formContent.setMaxWidth(900);
        formContent.getStyleClass().add("project-card");

        this.getChildren().addAll(formTitle, formContent);
    }

    private void handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Project Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(this.getScene().getWindow());

        if (selectedFile != null) {
            String imageUrlString = selectedFile.toURI().toString();
            images.add(imageUrlString);
            imagesDisplay.addString(imageUrlString);
            System.out.println("Image added successfully");
        } else {
            System.out.println("File selection was cancelled.");
        }
    }

    private void handleAddTag() {
        if (!tagInput.getText().isBlank()) {
            Tag t = new Tag((int)Math.floor(Math.random() * 1000000), tagInput.getText(), false, mainApp.user.id);
            tags.add(t);
            tagsDisplay.addTag(t);
            tagInput.setText("");
        }
    }

    private void handleAddTask() {
        String taskTitle = taskInput.getText();
        if (!taskTitle.isBlank()) {
            tasks.add(taskTitle);
            tasksDisplay.addTaskTitle(taskTitle);
            taskInput.setText("");
        }
    }

    private void save() {
        project.title = titleInput.getText();
        project.description = descriptionInput.getText();
        RadioButton selected = (RadioButton) statusInput.getSelectedToggle();
        project.status = selected.getText();

        boolean success = database.updateProjectDetailed(project, tags, images, tasks);
        if (success) {
            System.out.println("Project updated successfully.");
        }
        mainApp.switchToDashboard();
    }
}
