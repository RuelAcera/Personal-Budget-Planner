package com.example.personalbudgetplanner;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

public class LoginController {

    public Button loginButton;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button fullscreenButton;

    @FXML
    private void onLoginButtonClick() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showErrorAlert("Please enter username and password.");
            return;
        }

        Map<String, String> users = UserDatabase.loadUsers();
        if (users.containsKey(username) && users.get(username).equals(password)) {
            // Store logged-in user in Session
            Session.currentUser = username;

            // Load dashboard
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/personalbudgetplanner/dashboard.fxml"));
                Scene dashboardScene = new Scene(loader.load());
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(dashboardScene);
                stage.setTitle("Dashboard - Personal Budget Planner");
            } catch (IOException e) {
                e.printStackTrace();
                showErrorAlert("Failed to load dashboard.");
            }
        } else {
            showErrorAlert("Invalid username or password.");
        }
    }

    @FXML
    private void onSignUpClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/personalbudgetplanner/signup.fxml"));
            Scene signUpScene = new Scene(loader.load());
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(signUpScene);
            stage.setTitle("Sign Up - Personal Budget Planner");
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Failed to load sign up screen.");
        }
    }

    @FXML
    private void onFullscreenToggle() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setFullScreen(!stage.isFullScreen());
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
