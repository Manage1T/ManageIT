package Mussie;

import Main.Main;
import Models.User;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class SlidingContainer extends AnchorPane {
    private final int CONTAINER_WIDTH = 850;
    private final int CONTAINER_HEIGHT = 550;
    private final int OVERLAY_WIDTH = 340;
    private final int FORM_WIDTH = 510;

    private Pane formPanel;
    private Pane formInnerPanel;
    private Pane overlayPanel;
    private Pane overlayInnerPanel;

    // Fields for Sign In
    private RoundedTextField signInUsernameField;
    private RoundedPasswordField signInPasswordField;

    // Fields for Sign Up
    private RoundedTextField signUpNameField;
    private RoundedTextField signUpUsernameField;
    private RoundedTextField signUpEmailField;
    private RoundedPasswordField signUpPasswordField;

    private DoubleProperty progress = new SimpleDoubleProperty(0.0);
    private Timeline timeline;

    // Callback for forgot password navigation
    private Runnable onForgotPassword;

    // Added by mahder
    public Main mainApp;

    public SlidingContainer(Main mainApp) {
        this.mainApp = mainApp;

        setPrefSize(CONTAINER_WIDTH, CONTAINER_HEIGHT);
        setStyle("-fx-background-color: white;");

        // Form Panel
        formPanel = new Pane();
        formPanel.setPrefSize(FORM_WIDTH, CONTAINER_HEIGHT);
        formPanel.setLayoutX(0);
        formPanel.setLayoutY(0);
        formPanel.setStyle("-fx-background-color: white;");
        javafx.scene.shape.Rectangle formClip = new javafx.scene.shape.Rectangle(FORM_WIDTH, CONTAINER_HEIGHT);
        formPanel.setClip(formClip);

        formInnerPanel = new Pane();
        formInnerPanel.setPrefSize(FORM_WIDTH * 2, CONTAINER_HEIGHT);
        formInnerPanel.setLayoutX(0);
        formInnerPanel.setLayoutY(0);
        formPanel.getChildren().add(formInnerPanel);

        Pane signInForm = createSignInForm();
        signInForm.setLayoutX(0);
        formInnerPanel.getChildren().add(signInForm);

        Pane signUpForm = createSignUpForm();
        signUpForm.setLayoutX(FORM_WIDTH);
        formInnerPanel.getChildren().add(signUpForm);

        // Overlay Panel
        overlayPanel = new Pane();
        overlayPanel.setPrefSize(OVERLAY_WIDTH, CONTAINER_HEIGHT);
        overlayPanel.setLayoutX(FORM_WIDTH);
        overlayPanel.setLayoutY(0);
        overlayPanel.setStyle("-fx-background-color: linear-gradient(to bottom right, #0ba360, #028a55);");
        javafx.scene.shape.Rectangle overlayClip = new javafx.scene.shape.Rectangle(OVERLAY_WIDTH, CONTAINER_HEIGHT);
        overlayPanel.setClip(overlayClip);

        overlayInnerPanel = new Pane();
        overlayInnerPanel.setPrefSize(OVERLAY_WIDTH * 2, CONTAINER_HEIGHT);
        overlayInnerPanel.setLayoutX(0);
        overlayInnerPanel.setLayoutY(0);
        overlayPanel.getChildren().add(overlayInnerPanel);

        Pane signUpPrompt = createSignUpPrompt();
        signUpPrompt.setLayoutX(0);
        overlayInnerPanel.getChildren().add(signUpPrompt);

        Pane signInPrompt = createSignInPrompt();
        signInPrompt.setLayoutX(OVERLAY_WIDTH);
        overlayInnerPanel.getChildren().add(signInPrompt);

        getChildren().addAll(overlayPanel, formPanel);

        progress.addListener((obs, oldVal, newVal) -> updatePositions(newVal.doubleValue()));
        updatePositions(0.0);
    }

    private void updatePositions(double t) {
        formPanel.setLayoutX(t * OVERLAY_WIDTH);
        formInnerPanel.setLayoutX(-t * FORM_WIDTH);
        overlayPanel.setLayoutX(FORM_WIDTH - t * FORM_WIDTH);
        overlayInnerPanel.setLayoutX(-t * OVERLAY_WIDTH);
    }

    public void animateToState(boolean signUp) {
        if (timeline != null && timeline.getStatus() == Timeline.Status.RUNNING) {
            timeline.stop();
        }

        double target = signUp ? 1.0 : 0.0;
        timeline = new Timeline(
            new KeyFrame(Duration.millis(600), 
                new KeyValue(progress, target, Interpolator.EASE_BOTH))
        );
        timeline.play();
    }

    private Pane createSignInForm() {
        Pane panel = new Pane();
        panel.setPrefSize(FORM_WIDTH, CONTAINER_HEIGHT);

        Label titleLabel = new Label("Sign In");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));
        titleLabel.setTextFill(Color.web("#028a55"));
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setLayoutX(50);
        titleLabel.setLayoutY(70);
        titleLabel.setPrefWidth(410);

        signInUsernameField = new RoundedTextField("Username", RoundedTextField.IconType.USER);
        signInUsernameField.setLayoutX(75);
        signInUsernameField.setLayoutY(170);
        signInUsernameField.setPrefSize(360, 45);

        signInPasswordField = new RoundedPasswordField("Password");
        signInPasswordField.setLayoutX(75);
        signInPasswordField.setLayoutY(230);
        signInPasswordField.setPrefSize(360, 45);

        Label forgotLabel = new Label("Forgot your password ?");
        forgotLabel.setFont(Font.font("Segoe UI", 13));
        forgotLabel.setTextFill(Color.web("#777777"));
        forgotLabel.setAlignment(Pos.CENTER);
        forgotLabel.setCursor(Cursor.HAND);
        forgotLabel.setLayoutX(155);
        forgotLabel.setLayoutY(290);
        forgotLabel.setPrefSize(200, 30);

        // Hover effect
        forgotLabel.setOnMouseEntered(e -> forgotLabel.setTextFill(Color.web("#028a55")));
        forgotLabel.setOnMouseExited(e -> forgotLabel.setTextFill(Color.web("#777777")));

        // Navigate to forgot password page
        forgotLabel.setOnMouseClicked(e -> {
            if (onForgotPassword != null) {
                onForgotPassword.run();
            }
        });

        RoundedButton signInBtn = new RoundedButton("SIGN IN", false);
        signInBtn.setLayoutX(155);
        signInBtn.setLayoutY(345);
        signInBtn.setPrefSize(200, 45);
        signInBtn.setOnAction(e -> handleSignIn());

        panel.getChildren().addAll(titleLabel, signInUsernameField, signInPasswordField, forgotLabel, signInBtn);
        return panel;
    }

    private Pane createSignUpForm() {
        Pane panel = new Pane();
        panel.setPrefSize(FORM_WIDTH, CONTAINER_HEIGHT);

        Label titleLabel = new Label("Create Account");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.web("#028a55"));
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setLayoutX(50);
        titleLabel.setLayoutY(50);
        titleLabel.setPrefWidth(410);

        signUpNameField = new RoundedTextField("Name", RoundedTextField.IconType.USER);
        signUpNameField.setLayoutX(75);
        signUpNameField.setLayoutY(120);
        signUpNameField.setPrefSize(360, 43);

        signUpUsernameField = new RoundedTextField("Username", RoundedTextField.IconType.USER);
        signUpUsernameField.setLayoutX(75);
        signUpUsernameField.setLayoutY(175);
        signUpUsernameField.setPrefSize(360, 43);

        signUpEmailField = new RoundedTextField("Email", RoundedTextField.IconType.EMAIL);
        signUpEmailField.setLayoutX(75);
        signUpEmailField.setLayoutY(230);
        signUpEmailField.setPrefSize(360, 43);

        signUpPasswordField = new RoundedPasswordField("Password");
        signUpPasswordField.setLayoutX(75);
        signUpPasswordField.setLayoutY(285);
        signUpPasswordField.setPrefSize(360, 43);

        RoundedButton signUpBtn = new RoundedButton("SIGN UP", false);
        signUpBtn.setLayoutX(155);
        signUpBtn.setLayoutY(355);
        signUpBtn.setPrefSize(200, 45);
        signUpBtn.setOnAction(e -> handleSignUp());

        panel.getChildren().addAll(titleLabel, signUpNameField, signUpUsernameField, signUpEmailField, signUpPasswordField, signUpBtn);
        return panel;
    }

    private Pane createSignUpPrompt() {
        Pane panel = new Pane();
        panel.setPrefSize(OVERLAY_WIDTH, CONTAINER_HEIGHT);

        Label titleLabel = new Label("Hello, Friend!");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setLayoutX(20);
        titleLabel.setLayoutY(130);
        titleLabel.setPrefWidth(300);

        Label descLabel = new Label("Enter your personal details\nand start journey with us");
        descLabel.setFont(Font.font("Segoe UI", 13));
        descLabel.setTextFill(Color.WHITE);
        descLabel.setTextAlignment(TextAlignment.CENTER);
        descLabel.setAlignment(Pos.CENTER);
        descLabel.setLayoutX(20);
        descLabel.setLayoutY(185);
        descLabel.setPrefSize(300, 60);

        RoundedButton toggleBtn = new RoundedButton("SIGN UP", true);
        toggleBtn.setLayoutX(90);
        toggleBtn.setLayoutY(275);
        toggleBtn.setPrefSize(160, 40);
        toggleBtn.setOnAction(e -> animateToState(true));

        panel.getChildren().addAll(titleLabel, descLabel, toggleBtn);
        return panel;
    }

    private Pane createSignInPrompt() {
        Pane panel = new Pane();
        panel.setPrefSize(OVERLAY_WIDTH, CONTAINER_HEIGHT);

        Label titleLabel = new Label("Welcome Back!");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setLayoutX(20);
        titleLabel.setLayoutY(130);
        titleLabel.setPrefWidth(300);

        Label descLabel = new Label("To keep connected with us please\nlogin with your personal info");
        descLabel.setFont(Font.font("Segoe UI", 13));
        descLabel.setTextFill(Color.WHITE);
        descLabel.setTextAlignment(TextAlignment.CENTER);
        descLabel.setAlignment(Pos.CENTER);
        descLabel.setLayoutX(20);
        descLabel.setLayoutY(185);
        descLabel.setPrefSize(300, 60);

        RoundedButton toggleBtn = new RoundedButton("SIGN IN", true);
        toggleBtn.setLayoutX(90);
        toggleBtn.setLayoutY(275);
        toggleBtn.setPrefSize(160, 40);
        toggleBtn.setOnAction(e -> animateToState(false));

        panel.getChildren().addAll(titleLabel, descLabel, toggleBtn);
        return panel;
    }

    private void handleSignIn() {
        String username = signInUsernameField.getText().trim();
        String password = new String(signInPasswordField.getPassword());

        if (username.isEmpty()) {
            CustomDialog.show(this, "Validation Error", "Please enter your username.", false);
            return;
        }

        if (password.isEmpty()) {
            CustomDialog.show(this, "Validation Error", "Please enter your password.", false);
            return;
        }

        boolean success = DatabaseHelper.authenticateUser(username, password);
        if (success) {
            CustomDialog.show(this, "Authentication Successful", "Welcome back! Login verified.", true);
            signInUsernameField.setText("");
            signInPasswordField.setText("");

            // Give user to main application
            User user = DatabaseHelper.getUserByUsername(username);
            if (user == null) {
                CustomDialog.show(this, "Internal Error", "Failed to fetch user from database!", false);
                return;
            }
            mainApp.setUser(user);
        } else {
            CustomDialog.show(this, "Authentication Failed", "Invalid username or password.", false);
        }
    }

    private void handleSignUp() {
        String name = signUpNameField.getText().trim();
        String username = signUpUsernameField.getText().trim();
        String email = signUpEmailField.getText().trim();
        String password = new String(signUpPasswordField.getPassword());

        if (name.isEmpty()) {
            CustomDialog.show(this, "Validation Error", "Please enter your name.", false);
            return;
        }

        if (username.isEmpty() || username.length() < 3) {
            CustomDialog.show(this, "Validation Error", "Username must be at least 3 characters.", false);
            return;
        }

        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            CustomDialog.show(this, "Validation Error", "Username can only contain letters, numbers, and underscores.", false);
            return;
        }

        if (email.isEmpty() || !isValidEmail(email)) {
            CustomDialog.show(this, "Validation Error", "Please enter a valid email address.", false);
            return;
        }

        if (password.isEmpty() || password.length() < 6) {
            CustomDialog.show(this, "Validation Error", "Password must be at least 6 characters.", false);
            return;
        }

        DatabaseHelper.RegistrationResult result = DatabaseHelper.registerUser(name, username, email, password);
        switch (result) {
            case SUCCESS:
                String key = DatabaseHelper.lastGeneratedRecoveryKey;
                String successMsg = "Account created successfully!\n\nYour Recovery Key is:\n" + key 
                        + "\n\nSave this key! You will need it to reset your password if you ever forget it.";
                CustomDialog.show(this, "Registration Successful", successMsg, true);
                signUpNameField.setText("");
                signUpUsernameField.setText("");
                signUpEmailField.setText("");
                signUpPasswordField.setText("");
                animateToState(false);
                break;
            case USERNAME_ALREADY_EXISTS:
                CustomDialog.show(this, "Registration Failed", "This username is already taken.", false);
                break;
            case EMAIL_ALREADY_EXISTS:
                CustomDialog.show(this, "Registration Failed", "This email address is already registered.", false);
                break;
            default:
                CustomDialog.show(this, "System Error", "Registration failed. Verify database connectivity in db.properties.", false);
                break;
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    /**
     * Sets the callback to execute when the user clicks "Forgot your password?".
     */
    public void setOnForgotPassword(Runnable callback) {
        this.onForgotPassword = callback;
    }
}
