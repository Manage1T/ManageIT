package Main;

import Database.Database;
import Mash.EditProjectDisplay;
import Mash.ProjectDetails;
import Models.Project;
import Models.User;
import Mussie.DatabaseHelper;
import Mussie.ForgotPasswordPane;
import Mussie.SlidingContainer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;

public class Main extends Application {
    public User user;
    private Stage primaryStage;
    private Database db;
    private HomePage homePage;

    @Override
    public void start(Stage primaryStage) {
        // GLOBAL VARIABLES : TELL IN TELEGRAM IF CHANGING
        db = new Database();
        Connection conn = db.con;

        if (conn == null) {
            System.out.println("WARNING: Could not connect to database. App will launch without database features.\n");
        } else {
            // Ensure the database schema is up to date
            DatabaseHelper.ensureSchema();
        }
        this.primaryStage = primaryStage;

        // Start with the login screen
        showLoginScreen();
    }

    /**
     * Shows the login/registration screen.
     * Called on app start and after logout.
     */
    private void showLoginScreen() {
        // Login logic
        SlidingContainer slidingContainer = new SlidingContainer(this);
        ForgotPasswordPane forgotPasswordPane = new ForgotPasswordPane();

        Scene scene = new Scene(slidingContainer);

        // Navigate: Sign In -> Forgot Password
        slidingContainer.setOnForgotPassword(() -> {
            scene.setRoot(forgotPasswordPane);
        });

        // Navigate: Forgot Password -> Sign In
        forgotPasswordPane.setOnBackToSignIn(() -> {
            scene.setRoot(slidingContainer);
        });

        primaryStage.setTitle("ManageIT - Authenticate");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void setUser(User user) {
        System.out.println("Got user : " + user.username);
        this.user = user;
    }

    public void switchToDashboard() {
        if (homePage == null) {
            // First time after login — create the HomePage with sidebar
            homePage = new HomePage(this, db);
            Scene homeScene = new Scene(homePage, 1100, 700);
            primaryStage.setTitle("ManageIT");
            primaryStage.setScene(homeScene);
            primaryStage.show();
        } else {
            // Already on the homepage — just switch content to dashboard
            homePage.showDashboard();
        }
    }

    public void switchToCreateProject() {
        if (homePage != null) {
            homePage.showCreateProject();
        }
    }

    public void switchToProjectDisplay(Project project) {
        if (homePage != null) {
            // Show project details in the content area (sidebar stays on Dashboard)
            ProjectDetails projectDetails = new ProjectDetails(project, this);
            homePage.setViewContent("Project Details", projectDetails);
        }
    }

    public void switchToEdit(Project project) {
        if (homePage != null) {
            EditProjectDisplay editProjectDisplay = new EditProjectDisplay(db, this, project);
            homePage.setViewContent("Edit Project", editProjectDisplay);
        }
    }

    public void deleteProject(int projectId) {
        db.deleteProject(projectId);
        switchToDashboard();
    }

    /**
     * Logs the user out and returns to the login screen.
     */
    public void logout() {
        this.user = null;
        this.homePage = null;
        showLoginScreen();
    }

    public Database getDb() {
        return db;
    }
}