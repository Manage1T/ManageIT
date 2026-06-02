package Mash;

import Database.Database;
import Main.Main;
import Models.Project;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class DashBoard extends VBox {
    public Database database;
    public Main mainApp;
    private ArrayList<Project> projects = new ArrayList<Project>();
    private VBox projectsContainer = new VBox();

    public DashBoard(Database db, Main mainApp) {
        database = db;
        this.mainApp = mainApp;

        // Apply Stylesheet and Layout
        try {
            this.getStylesheets().add(getClass().getResource("mash.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("Could not load css: " + e.getMessage());
        }
        this.getStyleClass().add("root-container");
        this.setSpacing(25);
        this.setPadding(new Insets(40));
        this.setAlignment(Pos.TOP_CENTER);

        // Fetch all the projects of the current user
        projects.addAll(db.getProjects(mainApp.user.username));

        // Add NavBar
        NavBar navBar = new NavBar(this);
        this.getChildren().add(navBar);

        // Add projects container
        projectsContainer.setSpacing(25);
        projectsContainer.setAlignment(Pos.TOP_CENTER);
        this.getChildren().add(projectsContainer);

        // Render initially
        renderProjects(projects);

        // Add a create project button so that the user can create projects
        Button createProject = new Button("New project");
        createProject.getStyleClass().add("custom-button");
        createProject.setOnAction((e) -> {mainApp.switchToCreateProject();});
        this.getChildren().add(createProject);
    }

    public ArrayList<Project> getProjects() {
        return projects;
    }

    public void renderProjects(ArrayList<Project> filteredProjects) {
        projectsContainer.getChildren().clear();
        for (Project p : filteredProjects) {
            ProjectDsiplay pd = new ProjectDsiplay(p, mainApp);
            // Set a listener to take to the project details if the project is clicked
            pd.setOnMouseClicked((e) -> {
                mainApp.switchToProjectDisplay(p);
            });
            projectsContainer.getChildren().add(pd);
        }
    }
}
