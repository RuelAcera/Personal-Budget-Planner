package com.example.personalbudgetplanner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Scene scene = new Scene(loader.load());

            primaryStage.setScene(scene);
            primaryStage.setTitle("Login - Personal Budget Planner");
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Error loading login.fxml: " + e.getMessage());
            e.printStackTrace(); // optional, you can remove if you want it even simpler
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
