package Main;

import Database.Database;
import Models.User;
import Mussie.DatabaseHelper;
import Mussie.ForgotPasswordPane;
import Mussie.SlidingContainer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;

public class Main extends Application {
    User user;
    @Override
    public void start(Stage primaryStage) {
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
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        // GLOBAL VARIABLES : TELL IN TELEGRAM IF CHANGING
        Database db = new Database();
        Connection conn = db.con;

        if (conn == null) {
            System.out.println("WARNING: Could not connect to database. App will launch without database features.\n");
        } else {
            // Ensure the database schema is up to date
            DatabaseHelper.ensureSchema();
        }

        launch(args);
    }

    public void setUser(User user) {
        System.out.println("Got user : " + user.username);
        this.user = user;
    }
}