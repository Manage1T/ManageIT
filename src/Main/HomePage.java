package Main;

import Database.Database;
import Mash.CreateProjectDisplay;
import Mash.DashBoard;
import Naod.communication.ChatPanel;
import Naod.profileDisplay.ProfilePage;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Main application homepage that contains the sidebar navigation
 * and a dynamic content area for displaying different views.
 * Layout: [SideBar | Title Bar + Scrollable Content]
 */
public class HomePage extends HBox {
    private Main mainApp;
    private Database db;
    private SideBar sideBar;
    private Label viewTitle;
    private VBox scrollContent;

    public HomePage(Main mainApp, Database db) {
        this.mainApp = mainApp;
        this.db = db;

        // ── Sidebar (left) ──
        sideBar = new SideBar(this);

        // ── Content Area (right) ──
        VBox contentArea = new VBox();
        contentArea.setStyle("-fx-background-color: #FFFFFF;");
        HBox.setHgrow(contentArea, Priority.ALWAYS);

        // Title bar header — shows current view name and a welcome message
        VBox titleBar = new VBox(4);
        titleBar.setPadding(new Insets(25, 35, 15, 35));
        titleBar.setStyle(
            "-fx-background-color: #FFFFFF;" +
            "-fx-border-color: transparent transparent #E5E7EB transparent;" +
            "-fx-border-width: 0 0 1 0;"
        );

        viewTitle = new Label("Dashboard");
        viewTitle.setStyle(
            "-fx-font-size: 22px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';" +
            "-fx-text-fill: #1a202c;"
        );

        Label welcomeLabel = new Label("Welcome, " + mainApp.user.username);
        welcomeLabel.setStyle(
            "-fx-font-size: 13px; -fx-font-family: 'Segoe UI';" +
            "-fx-text-fill: #718096;"
        );

        titleBar.getChildren().addAll(viewTitle, welcomeLabel);

        // Scrollable content container — holds the active view
        scrollContent = new VBox();

        ScrollPane scrollPane = new ScrollPane(scrollContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle(
            "-fx-background: #FFFFFF; -fx-background-color: #FFFFFF;" +
            "-fx-border-color: transparent;"
        );
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        contentArea.getChildren().addAll(titleBar, scrollPane);

        // Assemble: sidebar on the left, content area on the right
        getChildren().addAll(sideBar, contentArea);

        // Show the dashboard by default
        showDashboard();
    }

    /**
     * Replaces the current content area with the given view node and updates the title.
     */
    public void setViewContent(String title, Node content) {
        viewTitle.setText(title);
        scrollContent.getChildren().clear();
        scrollContent.getChildren().add(content);
    }

    /**
     * Switches the content area to the Dashboard view.
     */
    public void showDashboard() {
        sideBar.selectDashboard();
        DashBoard dashboard = new DashBoard(db, mainApp);
        setViewContent("Dashboard", dashboard);
    }

    /**
     * Switches the content area to the Create New Project view.
     */
    public void showCreateProject() {
        sideBar.selectCreateProject();
        CreateProjectDisplay createProject = new CreateProjectDisplay(db, mainApp);
        setViewContent("Create New Project", createProject);
    }

    /**
     * Switches the content area to the Chat view.
     */
    public void showChat() {
        sideBar.selectChat();
        ChatPanel chatPanel = new ChatPanel(mainApp.user.username);
        setViewContent("Chat", chatPanel);
    }

    /**
     * Switches the content area to the Profile view.
     */
    public void showProfile() {
        sideBar.selectProfile();
        ProfilePage profilePage = new ProfilePage(mainApp, db);
        setViewContent("Profile", profilePage);
    }

    /**
     * Handles user logout by delegating to the Main application.
     */
    public void handleLogout() {
        mainApp.logout();
    }

    public SideBar getSideBar() {
        return sideBar;
    }
}
