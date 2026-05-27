import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SlidingContainer extends JPanel {
    private final int CONTAINER_WIDTH = 850;
    private final int CONTAINER_HEIGHT = 550;
    private final int OVERLAY_WIDTH = 340;
    private final int FORM_WIDTH = 510;

    private JPanel formPanel;
    private JPanel formInnerPanel;
    private GradientPanel overlayPanel;
    private JPanel overlayInnerPanel;

    // Fields for Sign In (stored as instance variables)
    private RoundedTextField signInUsernameField;
    private RoundedPasswordField signInPasswordField;

    // Fields for Sign Up (stored as instance variables)
    private RoundedTextField signUpNameField;
    private RoundedTextField signUpUsernameField;
    private RoundedTextField signUpEmailField;
    private RoundedPasswordField signUpPasswordField;

    // State: false = Sign In view (Overlay on right), true = Sign Up view (Overlay on left)
    private boolean isSignUpState = false;
    private double progress = 0.0; // 0.0 (Sign In) to 1.0 (Sign Up)
    private Timer animationTimer;

    public SlidingContainer() {
        setPreferredSize(new Dimension(CONTAINER_WIDTH, CONTAINER_HEIGHT));
        setLayout(null);
        setBackground(Color.WHITE);

        // Initialize Form Panel (White background, moves from x=0 to x=340)
        formPanel = new JPanel();
        formPanel.setBounds(0, 0, FORM_WIDTH, CONTAINER_HEIGHT);
        formPanel.setLayout(null);
        formPanel.setOpaque(true);
        formPanel.setBackground(Color.WHITE);

        // Form Inner Panel (Contains Sign In and Sign Up forms next to each other, moves from x=0 to x=-510)
        formInnerPanel = new JPanel();
        formInnerPanel.setBounds(0, 0, FORM_WIDTH * 2, CONTAINER_HEIGHT);
        formInnerPanel.setLayout(null);
        formInnerPanel.setOpaque(true);
        formInnerPanel.setBackground(Color.WHITE);
        formPanel.add(formInnerPanel);

        // Build forms
        JPanel signInForm = createSignInForm();
        signInForm.setBounds(0, 0, FORM_WIDTH, CONTAINER_HEIGHT);
        formInnerPanel.add(signInForm);

        JPanel signUpForm = createSignUpForm();
        signUpForm.setBounds(FORM_WIDTH, 0, FORM_WIDTH, CONTAINER_HEIGHT);
        formInnerPanel.add(signUpForm);

        // Initialize Overlay Panel (Gradient background, moves from x=510 to x=0)
        overlayPanel = new GradientPanel(Color.decode("#0ba360"), Color.decode("#028a55"));
        overlayPanel.setBounds(FORM_WIDTH, 0, OVERLAY_WIDTH, CONTAINER_HEIGHT);
        overlayPanel.setLayout(null);

        // Overlay Inner Panel (Contains Sign Up prompt and Sign In prompt, moves from x=0 to x=-340)
        overlayInnerPanel = new JPanel();
        overlayInnerPanel.setBounds(0, 0, OVERLAY_WIDTH * 2, CONTAINER_HEIGHT);
        overlayInnerPanel.setLayout(null);
        overlayInnerPanel.setOpaque(false);
        overlayPanel.add(overlayInnerPanel);

        // Build prompts
        JPanel signUpPrompt = createSignUpPrompt();
        signUpPrompt.setBounds(0, 0, OVERLAY_WIDTH, CONTAINER_HEIGHT);
        overlayInnerPanel.add(signUpPrompt);

        JPanel signInPrompt = createSignInPrompt();
        signInPrompt.setBounds(OVERLAY_WIDTH, 0, OVERLAY_WIDTH, CONTAINER_HEIGHT);
        overlayInnerPanel.add(signInPrompt);

        // Add panels to main container
        add(overlayPanel);
        add(formPanel);

        // Set initial positions
        updatePositions();
    }

    private void updatePositions() {
        double t = easeInOut(progress);

        // Form Panel moves right: 0 -> 340
        int formX = (int) (t * OVERLAY_WIDTH);
        formPanel.setLocation(formX, 0);

        // Form Inner Panel moves left relative to Form Panel: 0 -> -510
        int formInnerX = (int) (-t * FORM_WIDTH);
        formInnerPanel.setLocation(formInnerX, 0);

        // Overlay Panel moves left: 510 -> 0
        int overlayX = (int) (FORM_WIDTH - t * FORM_WIDTH);
        overlayPanel.setLocation(overlayX, 0);

        // Overlay Inner Panel moves left relative to Overlay Panel: 0 -> -340
        int overlayInnerX = (int) (-t * OVERLAY_WIDTH);
        overlayInnerPanel.setLocation(overlayInnerX, 0);

        repaint();
    }

    private double easeInOut(double t) {
        // Cubic ease-in-out
        return t < 0.5 ? 4.0 * t * t * t : 1.0 - Math.pow(-2.0 * t + 2.0, 3.0) / 2.0;
    }

    public void animateToState(boolean signUp) {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        isSignUpState = signUp;
        double target = signUp ? 1.0 : 0.0;
        double step = signUp ? 0.04 : -0.04;

        animationTimer = new Timer(15, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progress += step;
                if ((step > 0 && progress >= target) || (step < 0 && progress <= target)) {
                    progress = target;
                    updatePositions();
                    animationTimer.stop();
                } else {
                    updatePositions();
                }
            }
        });
        animationTimer.start();
    }

    private JPanel createSignInForm() {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);

        // Title
        JLabel titleLabel = new JLabel("Sign In", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(Color.decode("#028a55"));
        titleLabel.setBounds(50, 70, 410, 50);
        panel.add(titleLabel);

        // Username field
        signInUsernameField = new RoundedTextField("Username", RoundedTextField.IconType.USER);
        signInUsernameField.setBounds(75, 170, 360, 45);
        panel.add(signInUsernameField);

        // Password field
        signInPasswordField = new RoundedPasswordField("Password");
        signInPasswordField.setBounds(75, 230, 360, 45);
        panel.add(signInPasswordField);

        // Forgot password
        JLabel forgotLabel = new JLabel("Forgot your password ?", SwingConstants.CENTER);
        forgotLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        forgotLabel.setForeground(Color.decode("#777777"));
        forgotLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotLabel.setBounds(155, 290, 200, 30);
        panel.add(forgotLabel);

        // SIGN IN button
        RoundedButton signInBtn = new RoundedButton("SIGN IN", false);
        signInBtn.setBounds(155, 345, 200, 45);
        signInBtn.addActionListener(e -> handleSignIn());
        panel.add(signInBtn);

        return panel;
    }

    private JPanel createSignUpForm() {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);

        // Title
        JLabel titleLabel = new JLabel("Create Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.decode("#028a55"));
        titleLabel.setBounds(50, 50, 410, 45);
        panel.add(titleLabel);

        // Name field
        signUpNameField = new RoundedTextField("Name", RoundedTextField.IconType.USER);
        signUpNameField.setBounds(75, 120, 360, 43);
        panel.add(signUpNameField);

        // Username field
        signUpUsernameField = new RoundedTextField("Username", RoundedTextField.IconType.USER);
        signUpUsernameField.setBounds(75, 175, 360, 43);
        panel.add(signUpUsernameField);

        // Email field
        signUpEmailField = new RoundedTextField("Email", RoundedTextField.IconType.EMAIL);
        signUpEmailField.setBounds(75, 230, 360, 43);
        panel.add(signUpEmailField);

        // Password field
        signUpPasswordField = new RoundedPasswordField("Password");
        signUpPasswordField.setBounds(75, 285, 360, 43);
        panel.add(signUpPasswordField);

        // SIGN UP button
        RoundedButton signUpBtn = new RoundedButton("SIGN UP", false);
        signUpBtn.setBounds(155, 355, 200, 45);
        signUpBtn.addActionListener(e -> handleSignUp());
        panel.add(signUpBtn);

        return panel;
    }

    private JPanel createSignUpPrompt() {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);

        JLabel titleLabel = new JLabel("Hello, Friend!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(20, 130, 300, 40);
        panel.add(titleLabel);

        JLabel descLabel = new JLabel("<html><center>Enter your personal details<br>and start journey with us</center></html>", SwingConstants.CENTER);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLabel.setForeground(Color.WHITE);
        descLabel.setBounds(20, 185, 300, 60);
        panel.add(descLabel);

        RoundedButton toggleBtn = new RoundedButton("SIGN UP", true);
        toggleBtn.setBounds(90, 275, 160, 40);
        toggleBtn.addActionListener(e -> animateToState(true));
        panel.add(toggleBtn);

        return panel;
    }

    private JPanel createSignInPrompt() {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);

        JLabel titleLabel = new JLabel("Welcome Back!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(20, 130, 300, 40);
        panel.add(titleLabel);

        JLabel descLabel = new JLabel("<html><center>To keep connected with us please<br>login with your personal info</center></html>", SwingConstants.CENTER);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLabel.setForeground(Color.WHITE);
        descLabel.setBounds(20, 185, 300, 60);
        panel.add(descLabel);

        RoundedButton toggleBtn = new RoundedButton("SIGN IN", true);
        toggleBtn.setBounds(90, 275, 160, 40);
        toggleBtn.addActionListener(e -> animateToState(false));
        panel.add(toggleBtn);

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
            // Clear inputs
            signInUsernameField.setText("");
            signInPasswordField.setText("");
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
                CustomDialog.show(this, "Registration Successful", "Account created successfully! You can now sign in.", true);
                // Clear fields
                signUpNameField.setText("");
                signUpUsernameField.setText("");
                signUpEmailField.setText("");
                signUpPasswordField.setText("");
                // Transition to sign in
                animateToState(false);
                break;
            case USERNAME_ALREADY_EXISTS:
                CustomDialog.show(this, "Registration Failed", "This username is already taken.", false);
                break;
            case EMAIL_ALREADY_EXISTS:
                CustomDialog.show(this, "Registration Failed", "This email address is already registered.", false);
                break;
            case ERROR:
            default:
                CustomDialog.show(this, "System Error", "Registration failed. Verify database connectivity in db.properties.", false);
                break;
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }
}
