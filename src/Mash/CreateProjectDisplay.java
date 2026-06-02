package Mash;

import Database.Database;
import Main.Main;
import Models.Project;
import Models.Tag;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class CreateProjectDisplay extends VBox {
    private Main mainApp;
    private Database database;

    private TextField titleInput = new TextField();
    private TextArea descriptionInput = new TextArea();
    private TextField taskInput = new TextField();
    private TextField tagInput = new TextField();
    private ToggleGroup statusInput = new ToggleGroup();

    private TagsDisplay tagsDisplay = new TagsDisplay();
    private TasksDisplay tasksDisplay = new TasksDisplay();
    private CollectionDisplay imagesDisplay = new CollectionDisplay();

    private ArrayList<String> images = new ArrayList<String>();
    private ArrayList<Tag> tags = new ArrayList<Tag>();
    private ArrayList<String> tasks = new ArrayList<String>();

    public CreateProjectDisplay(Database db,Main mainApp) {
        // Sort out dependencies
        this.mainApp = mainApp;
        this.database = db;

        // Apply Stylesheet and Layout
        try {
            this.getStylesheets().add(getClass().getResource("mash.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("Could not load css: " + e.getMessage());
        }
        this.getStyleClass().add("root-container");
        this.setSpacing(20);
        this.setPadding(new Insets(40));
        this.setAlignment(Pos.CENTER);

        Label formTitle = new Label("Create New Project");
        formTitle.getStyleClass().add("title-text");

        // Accept status
        RadioButton complete = new RadioButton("complete");
        RadioButton start = new RadioButton("new");
        complete.getStyleClass().add("normal-text");
        start.getStyleClass().add("normal-text");
        complete.setToggleGroup(statusInput);
        start.setToggleGroup(statusInput);
        complete.setSelected(true);

        // Accept and display tags
        Button addTag = new Button("Add Tag");
        addTag.getStyleClass().add("custom-button");
        addTag.setOnAction((e) -> {handleAddTag();});

        // Accept and display images
        Button addImage = new Button("Add Image");
        addImage.getStyleClass().add("custom-button");
        addImage.setOnAction((e) -> {handleImageUpload();});

        // Accept and add tasks for the project
        Button addTask = new Button("Add task");
        addTask.getStyleClass().add("custom-button");
        addTask.setOnAction((e) -> {handleAddTask();});

        // Final button to create the project and add it to db
        Button createProject = new Button("Create Project");
        createProject.getStyleClass().add("custom-button");
        createProject.setStyle("-fx-font-size: 16px; -fx-padding: 12 30;"); // slightly larger
        createProject.setOnAction((e) -> {
            create();
        });

        // Inputs styling
        titleInput.getStyleClass().add("custom-text-field");
        titleInput.setPromptText("Enter Project Title");
        
        descriptionInput.getStyleClass().add("custom-text-area");
        descriptionInput.setPromptText("Enter Project Description");
        descriptionInput.setPrefRowCount(3);
        
        taskInput.getStyleClass().add("custom-text-field");
        taskInput.setPromptText("Enter Task");
        
        tagInput.getStyleClass().add("custom-text-field");
        tagInput.setPromptText("Enter Tag");

        // Organize the components into rows
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
        HBox statusRow = new HBox(15, statusLabel, start, complete);
        statusRow.setAlignment(Pos.CENTER_LEFT);

        Label tagLabel = new Label("Tag : ");
        tagLabel.getStyleClass().add("normal-text");
        tagLabel.setStyle("-fx-font-weight: bold;");
        HBox tagRow = new HBox(15, tagLabel, tagInput, addTag);
        tagRow.setAlignment(Pos.CENTER_LEFT);
        
        // Wrapping inputs in a layout box for neatness
        VBox formContent = new VBox(20, titleRow, descriptionRow, imageRow, taskRow, tasksDisplay, statusRow, tagRow, tagsDisplay);
        formContent.setAlignment(Pos.CENTER_LEFT);
        formContent.setMaxWidth(900);
        formContent.getStyleClass().add("project-card");

        // Add all the components to the UI
        this.getChildren().addAll(formTitle, formContent, createProject);
    }

    private void create() {
        // Create the project
        Project project = new Project();

        // Set attributes for the project
        project.title = titleInput.getText();
        project.userId = mainApp.user.id;
        project.description = descriptionInput.getText();
        project.createdAt = LocalDateTime.now();
        // Get the selected toggle text for the status
        RadioButton selected = (RadioButton) statusInput.getSelectedToggle();
        project.status = selected.getText();

        // Create the project with database
        boolean res = database.createProject(project, tags, images, tasks);
        if (!res) System.out.println("Creating project failed!");
        else System.out.println("Project created successfully, redirecting to dashboard.");
        mainApp.switchToDashboard();
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
        Tag t = new Tag((int)Math.floor(Math.random() * 1000000), tagInput.getText(), false, mainApp.user.id);
        tags.add(t);
        tagsDisplay.addTag(t);
        tagInput.setText("");
    }

    private void handleAddTask() {
        String taskTitle = taskInput.getText();
        if (Objects.equals(taskTitle, "")) return;
        tasks.add(taskTitle);
        tasksDisplay.addTaskTitle(taskTitle);

        // clear the display
        taskInput.setText("");
    }
}
