package Naod.profileDisplay;

import Database.Database;
import Main.Main;
import Models.Project;
import Models.Tag;
import Models.Task;
import Models.User;
import Naod.communication.ChatUserRepository;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfilePage extends VBox {

    private final String username;
    private final Main mainApp;
    private final Database db;

    // Legacy constructor for full backward compatibility
    public ProfilePage(String username) {
        this.username = username;
        this.mainApp = null;
        this.db = null;

        setSpacing(10);
        setPadding(new Insets(20));

        buildUI_legacy();
    }

    // New premium constructor
    public ProfilePage(Main mainApp, Database db) {
        this.mainApp = mainApp;
        this.db = db;
        this.username = (mainApp != null && mainApp.user != null) ? mainApp.user.username : null;

        setSpacing(20);
        setPadding(new Insets(30));
        setStyle("-fx-background-color: #121212;");

        buildUI();
    }

    private void buildUI() {
        if (mainApp == null || db == null || mainApp.user == null) {
            buildUI_legacy();
            return;
        }

        User user = mainApp.user;

        // Fetch projects
        ArrayList<Project> projects = db.getProjects(user.username);
        
        // Dynamic calculations
        int totalProjects = projects.size();
        int completedProjects = 0;
        int totalTasks = 0;
        int completedTasks = 0;
        HashMap<String, Integer> tagFrequency = new HashMap<>();

        for (Project p : projects) {
            if ("complete".equalsIgnoreCase(p.status)) {
                completedProjects++;
            }
            if (p.tasks != null) {
                totalTasks += p.tasks.size();
                for (Task t : p.tasks) {
                    if (t.isCompleted) {
                        completedTasks++;
                    }
                }
            }
            if (p.tags != null) {
                for (Tag t : p.tags) {
                    tagFrequency.put(t.name, tagFrequency.getOrDefault(t.name, 0) + 1);
                }
            }
        }

        // 1. Title Area
        Label profileTitle = new Label("Profile");
        profileTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI'; -fx-text-fill: white;");

        Label profileSubtitle = new Label("View all your profile details here.");
        profileSubtitle.setStyle("-fx-font-size: 14px; -fx-font-family: 'Segoe UI'; -fx-text-fill: #777777;");

        VBox titleArea = new VBox(5, profileTitle, profileSubtitle);
        titleArea.setPadding(new Insets(0, 0, 10, 0));

        // 2. Main Content Split Pane
        HBox splitPane = new HBox(30);
        HBox.setHgrow(splitPane, Priority.ALWAYS);

        // --- LEFT PANE: User details card ---
        VBox leftPane = new VBox(25);
        leftPane.setPrefWidth(320);
        leftPane.setMinWidth(320);
        leftPane.setMaxWidth(320);
        leftPane.setAlignment(Pos.TOP_CENTER);
        leftPane.setPadding(new Insets(30, 20, 30, 20));
        leftPane.setStyle("-fx-background-color: #1e1e1e; -fx-background-radius: 16; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 4);");

        // Display Name & Subscription Tier
        String displayName = (user.name != null && !user.name.isBlank()) ? user.name : user.username;
        Label nameLabel = new Label(displayName);
        nameLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-family: 'Segoe UI';");
        nameLabel.setWrapText(true);

        String tier = (totalProjects >= 5) ? "Premium User" : "Standard User";
        Label tierLabel = new Label(tier);
        tierLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #00E676; -fx-font-family: 'Segoe UI';");

        // Profile Avatar (Image or Fallback Circle)
        StackPane avatarContainer = new StackPane();
        Circle avatarBg = new Circle(75);
        
        Stop[] stops = new Stop[] { new Stop(0, Color.web("#0ba360")), new Stop(1, Color.web("#014d2e")) };
        LinearGradient gradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
        avatarBg.setFill(gradient);

        boolean hasPic = false;
        if (user.profilePictureUrl != null && !user.profilePictureUrl.isBlank()) {
            try {
                Image img = new Image(user.profilePictureUrl, true);
                ImageView iv = new ImageView(img);
                iv.setFitWidth(150);
                iv.setFitHeight(150);
                Circle clip = new Circle(75, 75, 75);
                iv.setClip(clip);
                avatarContainer.getChildren().addAll(avatarBg, iv);
                hasPic = true;
            } catch (Exception e) {
                // Fallback will be used
            }
        }

        if (!hasPic) {
            String initial = displayName.substring(0, 1).toUpperCase();
            Label initialLabel = new Label(initial);
            initialLabel.setStyle("-fx-font-size: 64px; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-family: 'Segoe UI';");
            avatarContainer.getChildren().addAll(avatarBg, initialLabel);
        }

        avatarContainer.setCursor(javafx.scene.Cursor.HAND);
        avatarContainer.setOnMouseClicked(e -> {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Select Profile Picture");
            fileChooser.getExtensionFilters().addAll(
                new javafx.stage.FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            java.io.File selectedFile = fileChooser.showOpenDialog(this.getScene().getWindow());
            if (selectedFile != null) {
                String imageUrlString = selectedFile.toURI().toString();
                if (Mussie.DatabaseHelper.updateProfilePicture(user.username, imageUrlString)) {
                    user.profilePictureUrl = imageUrlString;
                    buildUI();
                }
            }
        });

        // Info Grid
        GridPane infoGrid = new GridPane();
        infoGrid.setVgap(15);
        infoGrid.setHgap(10);
        infoGrid.setAlignment(Pos.CENTER_LEFT);

        addInfoRow(infoGrid, 0, "Username", "@" + user.username);
        addInfoRow(infoGrid, 1, "Email", (user.email != null && !user.email.isBlank()) ? user.email : "Not provided");
        
        String dateStr = "N/A";
        if (user.createdAt != null) {
            dateStr = user.createdAt.format(DateTimeFormatter.ofPattern("MMM dd, yyyy", java.util.Locale.ENGLISH));
        }
        addInfoRow(infoGrid, 2, "Member Since", dateStr);

        leftPane.getChildren().addAll(nameLabel, tierLabel, avatarContainer, infoGrid);


        // --- RIGHT PANE: Bio & Details + Statistics ---
        VBox rightPane = new VBox(25);
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        rightPane.setPadding(new Insets(30));
        rightPane.setStyle("-fx-background-color: #1e1e1e; -fx-background-radius: 16; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 4);");

        HBox rightHeader = new HBox();
        rightHeader.setAlignment(Pos.CENTER_LEFT);
        
        Label sectionHeader = new Label("Project Workspace Insights");
        sectionHeader.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-family: 'Segoe UI';");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Status dot for collaboration/online
        HBox availabilityStatus = new HBox(8);
        availabilityStatus.setAlignment(Pos.CENTER_LEFT);
        Circle statusDot = new Circle(5, Color.web("#00E676"));
        Label availabilityLabel = new Label("Online & Available");
        availabilityLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #00E676; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';");
        availabilityStatus.getChildren().addAll(statusDot, availabilityLabel);
        availabilityStatus.setStyle("-fx-background-color: rgba(0, 230, 118, 0.1); -fx-padding: 6 12; -fx-background-radius: 20;");

        rightHeader.getChildren().addAll(sectionHeader, spacer, availabilityStatus);

        // Project details fields
        GridPane detailsGrid = new GridPane();
        detailsGrid.setVgap(20);
        detailsGrid.setHgap(30);
        
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        detailsGrid.getColumnConstraints().addAll(col1, col2);

        String projectSuccessStr = (totalProjects > 0) ? ((completedProjects * 100) / totalProjects) + "% Completed (" + completedProjects + "/" + totalProjects + ")" : "No projects";
        String taskFinishRateStr = (totalTasks > 0) ? ((completedTasks * 100) / totalTasks) + "% Completed (" + completedTasks + "/" + totalTasks + ")" : "No tasks";
        int activeTasks = totalTasks - completedTasks;
        String workspaceLoadStr = activeTasks + " Active Task" + (activeTasks == 1 ? "" : "s") + " Pending";
        String collaborationStandingStr;
        if (totalProjects >= 5) {
            collaborationStandingStr = "Lead Administrator";
        } else if (totalProjects > 0) {
            collaborationStandingStr = "Active Collaborator";
        } else {
            collaborationStandingStr = "Contributor";
        }

        addDetailBlock(detailsGrid, 0, 0, "Project Success Rate", projectSuccessStr);
        addDetailBlock(detailsGrid, 1, 0, "Task Finish Rate", taskFinishRateStr);
        addDetailBlock(detailsGrid, 0, 1, "Workspace Load", workspaceLoadStr);
        addDetailBlock(detailsGrid, 1, 1, "Collaboration Standing", collaborationStandingStr);

        // Dynamic Badges Row
        VBox badgesSection = new VBox(10);
        Label badgesLabel = new Label("Badges");
        badgesLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #777777; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';");
        
        FlowPane badgesFlow = new FlowPane(10, 10);
        
        if (totalProjects > 0) {
            badgesFlow.getChildren().add(createBadge("Active Creator", "#00E676"));
        }
        badgesFlow.getChildren().add(createBadge("Top Collaborator", "#29B6F6"));
        if (completedProjects > 0) {
            badgesFlow.getChildren().add(createBadge("Goal Achiever", "#FFCA28"));
        }
        if (user.createdAt != null && user.createdAt.isBefore(java.time.LocalDateTime.now().minusMonths(3))) {
            badgesFlow.getChildren().add(createBadge("Veteran Member", "#AB47BC"));
        }
        if (badgesFlow.getChildren().isEmpty()) {
            badgesFlow.getChildren().add(new Label("No badges earned yet."));
        }
        badgesSection.getChildren().addAll(badgesLabel, badgesFlow);

        // Tags Row
        VBox tagsSection = new VBox(10);
        Label tagsLabel = new Label("Frequently Used Project Tags");
        tagsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #777777; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';");
        
        FlowPane tagsFlow = new FlowPane(8, 8);
        
        ArrayList<Map.Entry<String, Integer>> sortedTags = new ArrayList<>(tagFrequency.entrySet());
        sortedTags.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        
        int tagLimit = Math.min(sortedTags.size(), 6);
        for (int i = 0; i < tagLimit; i++) {
            tagsFlow.getChildren().add(createTagBadge("#" + sortedTags.get(i).getKey()));
        }
        
        if (tagLimit == 0) {
            tagsFlow.getChildren().add(createTagBadge("#General"));
        }
        tagsSection.getChildren().addAll(tagsLabel, tagsFlow);

        // Separator
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #333333; -fx-opacity: 0.2;");

        // User Projects Subsection
        VBox projectsSection = new VBox(12);
        Label projectsSectionTitle = new Label("My Active Projects (" + totalProjects + ")");
        projectsSectionTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-family: 'Segoe UI';");

        VBox projectsContainer = new VBox(10);
        for (int i = 0; i < Math.min(projects.size(), 3); i++) {
            Project p = projects.get(i);
            HBox projectCard = new HBox(15);
            projectCard.setAlignment(Pos.CENTER_LEFT);
            projectCard.setPadding(new Insets(12, 20, 12, 20));
            projectCard.setStyle("-fx-background-color: #252525; -fx-background-radius: 10; -fx-cursor: hand;");
            
            VBox pMeta = new VBox(4);
            Label pTitle = new Label(p.title);
            pTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-family: 'Segoe UI';");
            Label pDesc = new Label((p.description != null && p.description.length() > 50) ? p.description.substring(0, 50) + "..." : p.description);
            pDesc.setStyle("-fx-text-fill: #888888; -fx-font-size: 12px; -fx-font-family: 'Segoe UI';");
            pMeta.getChildren().addAll(pTitle, pDesc);
            
            Region pSpacer = new Region();
            HBox.setHgrow(pSpacer, Priority.ALWAYS);

            Label pStatus = new Label(p.status != null ? p.status.toUpperCase() : "NEW");
            String statusColor = "complete".equalsIgnoreCase(p.status) ? "#00E676" : "#FFCA28";
            pStatus.setStyle("-fx-text-fill: " + statusColor + "; -fx-font-weight: bold; -fx-font-size: 12px; -fx-font-family: 'Segoe UI';");

            projectCard.getChildren().addAll(pMeta, pSpacer, pStatus);
            
            projectCard.setOnMouseClicked(e -> {
                mainApp.switchToProjectDisplay(p);
            });

            projectsContainer.getChildren().add(projectCard);
        }

        if (projects.isEmpty()) {
            Label noProjects = new Label("No active projects. Start a new project to track details here.");
            noProjects.setStyle("-fx-text-fill: #666666; -fx-font-style: italic; -fx-font-size: 13px; -fx-font-family: 'Segoe UI';");
            projectsContainer.getChildren().add(noProjects);
        }

        projectsSection.getChildren().addAll(projectsSectionTitle, projectsContainer);

        rightPane.getChildren().addAll(rightHeader, detailsGrid, separator, badgesSection, tagsSection, projectsSection);

        splitPane.getChildren().addAll(leftPane, rightPane);

        getChildren().clear();
        getChildren().addAll(titleArea, splitPane);
    }

    private void addInfoRow(GridPane grid, int row, String labelText, String valueText) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 12px; -fx-text-fill: #777777; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';");
        
        Label value = new Label(valueText);
        value.setStyle("-fx-font-size: 14px; -fx-text-fill: white; -fx-font-family: 'Segoe UI';");
        value.setWrapText(true);
        value.setMaxWidth(200);

        grid.add(label, 0, row);
        grid.add(value, 1, row);
    }

    private void addDetailBlock(GridPane grid, int col, int row, String labelText, String valueText) {
        VBox block = new VBox(4);
        
        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 12px; -fx-text-fill: #777777; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';");
        
        Label value = new Label(valueText);
        value.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';");
        
        block.getChildren().addAll(label, value);
        grid.add(block, col, row);
    }

    private HBox createBadge(String text, String colorHex) {
        HBox badge = new HBox();
        badge.setAlignment(Pos.CENTER);
        badge.setPadding(new Insets(4, 10, 4, 10));
        badge.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-border-color: " + colorHex + "; -fx-border-width: 1; -fx-border-radius: 12; -fx-background-radius: 12;");
        
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 12px; -fx-text-fill: " + colorHex + "; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';");
        badge.getChildren().add(label);
        
        return badge;
    }

    private Label createTagBadge(String text) {
        Label badge = new Label(text);
        badge.setStyle("-fx-font-size: 13px; -fx-text-fill: #888888; -fx-font-family: 'Segoe UI'; -fx-background-color: #252525; -fx-padding: 5 10 5 10; -fx-background-radius: 15;");
        return badge;
    }

    private void buildUI_legacy() {
        String resolved = ChatUserRepository.resolveUsername(username != null ? username : (mainApp.user != null ? mainApp.user.username : "User"));

        Label title = new Label("User Profile");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label name = new Label("Username: " + resolved);

        Label status = new Label("Status: Online (chat system)");

        getChildren().clear();
        getChildren().addAll(title, name, status);
    }
}