package Main;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Sidebar navigation component for the HomePage.
 * Displays the app title, navigation menu items, and a logout button
 * with a green gradient background (light green to dark green, top to bottom).
 */
public class SideBar extends VBox {
    private Label selectedButton = null;
    private HomePage homePage;

    // Navigation button references
    private Label dashboardBtn;
    private Label createProjectBtn;
    private Label chatBtn;
    private Label profileBtn;

    public SideBar(HomePage homePage) {
        this.homePage = homePage;
        buildUI();
        setSelected(dashboardBtn);
    }

    private void buildUI() {
        // Sidebar layout and green gradient background (light → dark, top → bottom)
        setPrefWidth(260);
        setMinWidth(260);
        setMaxWidth(260);
        setStyle(
            "-fx-background-color: linear-gradient(to bottom, #0ba360, #014d2e);" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 2, 0);"
        );
        setPadding(new Insets(30, 15, 25, 15));
        setSpacing(5);

        // ── App Title (bold, green/white) ──
        Label title = new Label("ManageIT");
        title.setStyle(
            "-fx-text-fill: white; -fx-font-size: 26px; -fx-font-weight: bold;" +
            "-fx-font-family: 'Segoe UI';"
        );
        title.setPadding(new Insets(0, 0, 2, 10));

        Label subtitle = new Label("Project Manager");
        subtitle.setStyle(
            "-fx-text-fill: rgba(255,255,255,0.6); -fx-font-size: 12px;" +
            "-fx-font-family: 'Segoe UI';"
        );
        subtitle.setPadding(new Insets(0, 0, 20, 10));

        // ── Top Separator ──
        Region topSeparator = createSeparator();
        VBox.setMargin(topSeparator, new Insets(0, 5, 10, 5));

        // ── Menu Section Label ──
        Label menuLabel = new Label("MENU");
        menuLabel.setStyle(
            "-fx-text-fill: rgba(255,255,255,0.45); -fx-font-size: 11px;" +
            "-fx-font-weight: bold; -fx-font-family: 'Segoe UI';" +
            "-fx-padding: 0 0 5 15;"
        );

        // ── Navigation Buttons ──
        dashboardBtn = createNavButton("Dashboard");
        createProjectBtn = createNavButton("New Project");
        chatBtn = createNavButton("Chat");
        profileBtn = createNavButton("Profile");

        dashboardBtn.setOnMouseClicked(e -> homePage.showDashboard());
        createProjectBtn.setOnMouseClicked(e -> homePage.showCreateProject());
        chatBtn.setOnMouseClicked(e -> homePage.showChat());
        profileBtn.setOnMouseClicked(e -> homePage.showProfile());

        // ── Spacer (pushes logout to bottom) ──
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // ── Bottom Separator ──
        Region bottomSeparator = createSeparator();
        VBox.setMargin(bottomSeparator, new Insets(0, 5, 10, 5));

        // ── Logout Button ──
        Label logoutBtn = createLogoutButton();

        // Assemble sidebar
        getChildren().addAll(
            title, subtitle, topSeparator, menuLabel,
            dashboardBtn, createProjectBtn, chatBtn, profileBtn,
            spacer, bottomSeparator, logoutBtn
        );
    }

    /**
     * Creates a styled horizontal separator line.
     */
    private Region createSeparator() {
        Region sep = new Region();
        sep.setStyle("-fx-background-color: rgba(255,255,255,0.2);");
        sep.setPrefHeight(1);
        sep.setMinHeight(1);
        sep.setMaxHeight(1);
        sep.setMaxWidth(Double.MAX_VALUE);
        return sep;
    }

    /**
     * Creates a navigation button (Label styled as a clickable menu item).
     */
    private Label createNavButton(String text) {
        Label btn = new Label(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setCursor(Cursor.HAND);
        btn.setStyle(getDefaultStyle());

        // Hover effects: highlight when hovering, revert when leaving
        btn.setOnMouseEntered(e -> {
            if (btn != selectedButton) btn.setStyle(getHoverStyle());
        });
        btn.setOnMouseExited(e -> {
            if (btn != selectedButton) btn.setStyle(getDefaultStyle());
        });

        return btn;
    }

    /**
     * Creates the logout button with a distinct reddish tint.
     */
    private Label createLogoutButton() {
        Label btn = new Label("Logout");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setCursor(Cursor.HAND);

        String defaultStyle =
            "-fx-background-color: rgba(239, 68, 68, 0.15);" +
            "-fx-text-fill: #fca5a5;" +
            "-fx-font-size: 14px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';" +
            "-fx-background-radius: 10; -fx-padding: 12 20 12 20;";
        String hoverStyle =
            "-fx-background-color: rgba(239, 68, 68, 0.3);" +
            "-fx-text-fill: #fecaca;" +
            "-fx-font-size: 14px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';" +
            "-fx-background-radius: 10; -fx-padding: 12 20 12 20;";

        btn.setStyle(defaultStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(defaultStyle));
        btn.setOnMouseClicked(e -> homePage.handleLogout());

        return btn;
    }

    /**
     * Marks the given button as the currently selected navigation item.
     * Deselects the previously selected button.
     */
    public void setSelected(Label btn) {
        if (selectedButton != null) {
            selectedButton.setStyle(getDefaultStyle());
        }
        selectedButton = btn;
        btn.setStyle(getSelectedStyle());
    }

    // ── Public selection helpers (called from HomePage / Main) ──
    public void selectDashboard()     { setSelected(dashboardBtn); }
    public void selectCreateProject() { setSelected(createProjectBtn); }
    public void selectChat()          { setSelected(chatBtn); }
    public void selectProfile()       { setSelected(profileBtn); }

    // ── Button Style Definitions ──

    private String getDefaultStyle() {
        return "-fx-background-color: transparent;" +
               "-fx-text-fill: rgba(255,255,255,0.75);" +
               "-fx-font-size: 14px; -fx-font-family: 'Segoe UI';" +
               "-fx-background-radius: 10; -fx-padding: 12 20 12 20;";
    }

    private String getHoverStyle() {
        return "-fx-background-color: rgba(255,255,255,0.1);" +
               "-fx-text-fill: rgba(255,255,255,0.95);" +
               "-fx-font-size: 14px; -fx-font-family: 'Segoe UI';" +
               "-fx-background-radius: 10; -fx-padding: 12 20 12 20;";
    }

    private String getSelectedStyle() {
        return "-fx-background-color: rgba(255,255,255,0.2);" +
               "-fx-text-fill: white;" +
               "-fx-font-size: 14px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';" +
               "-fx-background-radius: 10; -fx-padding: 12 20 12 20;";
    }
}
