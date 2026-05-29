package Mussie;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class ForgotPasswordPane extends AnchorPane {
    private final int CONTAINER_WIDTH = 850;
    private final int CONTAINER_HEIGHT = 550;
    private final int FORM_WIDTH = 510;
    private final int SIDE_WIDTH = 340;

    // Form fields
    private RoundedTextField emailField;
    private RoundedTextField recoveryKeyField;
    private RoundedPasswordField newPasswordField;
    private RoundedPasswordField confirmPasswordField;

    // Callback to go back to sign in
    private Runnable onBackToSignIn;

    public ForgotPasswordPane() {
        setPrefSize(CONTAINER_WIDTH, CONTAINER_HEIGHT);
        setStyle("-fx-background-color: white;");

        // Left side - Form panel
        Pane formPanel = createFormPanel();

        // Right side - Green gradient panel
        Pane sidePanel = createSidePanel();

        getChildren().addAll(formPanel, sidePanel);
    }

    private Pane createFormPanel() {
        Pane panel = new Pane();
        panel.setPrefSize(FORM_WIDTH, CONTAINER_HEIGHT);
        panel.setLayoutX(0);
        panel.setLayoutY(0);
        panel.setStyle("-fx-background-color: white;");

        // Lock icon at the top
        SVGPath lockIcon = new SVGPath();
        lockIcon.setContent("M 20 10 L 40 10 L 40 30 L 20 30 Z M 24 10 A 6 8 0 0 1 36 10 M 30 18 L 30 23");
        lockIcon.setFill(Color.TRANSPARENT);
        lockIcon.setStroke(Color.web("#028a55"));
        lockIcon.setStrokeWidth(2.0);
        lockIcon.setLayoutX(225);
        lockIcon.setLayoutY(45);

        // Title
        Label titleLabel = new Label("Forgot Password?");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.web("#028a55"));
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setLayoutX(50);
        titleLabel.setLayoutY(90);
        titleLabel.setPrefWidth(410);

        // Subtitle
        Label subtitleLabel = new Label("Securely reset your password using your unique Recovery Key.");
        subtitleLabel.setFont(Font.font("Segoe UI", 13));
        subtitleLabel.setTextFill(Color.web("#777777"));
        subtitleLabel.setTextAlignment(TextAlignment.CENTER);
        subtitleLabel.setAlignment(Pos.CENTER);
        subtitleLabel.setLayoutX(50);
        subtitleLabel.setLayoutY(135);
        subtitleLabel.setPrefSize(410, 25);

        // Email field
        emailField = new RoundedTextField("Email Address", RoundedTextField.IconType.EMAIL);
        emailField.setLayoutX(75);
        emailField.setLayoutY(170);
        emailField.setPrefSize(360, 42);

        // Recovery Key field
        recoveryKeyField = new RoundedTextField("Recovery Key (MIT-XXXX-XXXX)", RoundedTextField.IconType.LOCK);
        recoveryKeyField.setLayoutX(75);
        recoveryKeyField.setLayoutY(225);
        recoveryKeyField.setPrefSize(360, 42);

        // New Password field
        newPasswordField = new RoundedPasswordField("New Password");
        newPasswordField.setLayoutX(75);
        newPasswordField.setLayoutY(280);
        newPasswordField.setPrefSize(360, 42);

        // Confirm Password field
        confirmPasswordField = new RoundedPasswordField("Confirm Password");
        confirmPasswordField.setLayoutX(75);
        confirmPasswordField.setLayoutY(335);
        confirmPasswordField.setPrefSize(360, 42);

        // Reset Password button
        RoundedButton resetBtn = new RoundedButton("RESET PASSWORD", false);
        resetBtn.setLayoutX(130);
        resetBtn.setLayoutY(395);
        resetBtn.setPrefSize(250, 45);
        resetBtn.setOnAction(e -> handleResetPassword());

        // Back to Sign In link
        Label backLabel = new Label("\u2190  Back to Sign In");
        backLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        backLabel.setTextFill(Color.web("#028a55"));
        backLabel.setAlignment(Pos.CENTER);
        backLabel.setCursor(Cursor.HAND);
        backLabel.setLayoutX(155);
        backLabel.setLayoutY(460);
        backLabel.setPrefSize(200, 30);

        // Hover effect for back link
        backLabel.setOnMouseEntered(e -> backLabel.setTextFill(Color.web("#016f44")));
        backLabel.setOnMouseExited(e -> backLabel.setTextFill(Color.web("#028a55")));

        backLabel.setOnMouseClicked(e -> {
            if (onBackToSignIn != null) {
                onBackToSignIn.run();
            }
        });

        panel.getChildren().addAll(lockIcon, titleLabel, subtitleLabel, emailField,
                recoveryKeyField, newPasswordField, confirmPasswordField, resetBtn, backLabel);
        return panel;
    }

    private Pane createSidePanel() {
        Pane sidePanel = new Pane();
        sidePanel.setPrefSize(SIDE_WIDTH, CONTAINER_HEIGHT);
        sidePanel.setLayoutX(FORM_WIDTH);
        sidePanel.setLayoutY(0);
        sidePanel.setStyle("-fx-background-color: linear-gradient(to bottom right, #0ba360, #028a55);");

        // Key icon
        SVGPath keyIcon = new SVGPath();
        keyIcon.setContent("M 10 0 A 7 7 0 1 0 10 14 A 7 7 0 1 0 10 0 Z M 10 14 L 10 30 M 6 22 L 14 22 M 6 26 L 14 26");
        keyIcon.setFill(Color.TRANSPARENT);
        keyIcon.setStroke(Color.WHITE);
        keyIcon.setStrokeWidth(2.0);
        keyIcon.setLayoutX(155);
        keyIcon.setLayoutY(100);

        // Title
        Label titleLabel = new Label("Secure Reset");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setLayoutX(20);
        titleLabel.setLayoutY(170);
        titleLabel.setPrefWidth(300);

        // Description
        Label descLabel = new Label("Your security is our priority.\nReset your password safely\nand get back on track.");
        descLabel.setFont(Font.font("Segoe UI", 13));
        descLabel.setTextFill(Color.WHITE);
        descLabel.setTextAlignment(TextAlignment.CENTER);
        descLabel.setAlignment(Pos.CENTER);
        descLabel.setLayoutX(20);
        descLabel.setLayoutY(220);
        descLabel.setPrefSize(300, 70);

        // Sign In button on the side panel
        RoundedButton signInBtn = new RoundedButton("SIGN IN", true);
        signInBtn.setLayoutX(90);
        signInBtn.setLayoutY(320);
        signInBtn.setPrefSize(160, 40);
        signInBtn.setOnAction(e -> {
            if (onBackToSignIn != null) {
                onBackToSignIn.run();
            }
        });

        sidePanel.getChildren().addAll(keyIcon, titleLabel, descLabel, signInBtn);
        return sidePanel;
    }

    /**
     * Sets the callback to execute when the user clicks "Back to Sign In".
     */
    public void setOnBackToSignIn(Runnable callback) {
        this.onBackToSignIn = callback;
    }

    // Getters for form fields (for future functionality)
    public String getEmail() {
        return emailField.getText();
    }

    public String getRecoveryKey() {
        return recoveryKeyField.getText();
    }

    public String getNewPassword() {
        return new String(newPasswordField.getPassword());
    }

    public String getConfirmPassword() {
        return new String(confirmPasswordField.getPassword());
    }

    private void handleResetPassword() {
        String email = emailField.getText().trim();
        String recoveryKey = recoveryKeyField.getText().trim();
        String newPassword = getNewPassword();
        String confirmPassword = getConfirmPassword();

        if (email.isEmpty()) {
            CustomDialog.show(this, "Validation Error", "Please enter your email address.", false);
            return;
        }

        if (recoveryKey.isEmpty()) {
            CustomDialog.show(this, "Validation Error", "Please enter your recovery key.", false);
            return;
        }

        if (newPassword.isEmpty() || newPassword.length() < 6) {
            CustomDialog.show(this, "Validation Error", "New password must be at least 6 characters.", false);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            CustomDialog.show(this, "Validation Error", "Passwords do not match.", false);
            return;
        }

        boolean success = DatabaseHelper.resetPassword(email, recoveryKey, newPassword);
        if (success) {
            CustomDialog.show(this, "Password Reset Successful", "Your password has been securely reset!\nYou can now sign in with your new password.", true);
            emailField.setText("");
            recoveryKeyField.setText("");
            newPasswordField.setText("");
            confirmPasswordField.setText("");
            if (onBackToSignIn != null) {
                onBackToSignIn.run();
            }
        } else {
            CustomDialog.show(this, "Reset Failed", "Invalid Email Address or Recovery Key.\nPlease check your details and try again.", false);
        }
    }
}
