package com.example.personalbudgetplanner;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

public class SignUpController {

    @FXML
    private TextField newUsernameField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private void onSignUpSubmit() {
        String username = newUsernameField.getText().trim();
        String password = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Passwords do not match.");
            return;
        }

        Map<String, String> users = UserDatabase.loadUsers();
        if (users.containsKey(username)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Username already exists.");
            return;
        }

        boolean success = UserDatabase.addUser(username, password);
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Account created successfully! Please login.");

            // Redirect to login screen
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/personalbudgetplanner/login.fxml"));
                Scene loginScene = new Scene(loader.load());
                Stage stage = (Stage) newUsernameField.getScene().getWindow();
                stage.setScene(loginScene);
                stage.setTitle("Login - Personal Budget Planner");
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load login screen.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to create account.");
        }
    }

    @FXML
    private void onBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/personalbudgetplanner/login.fxml"));
            Scene loginScene = new Scene(loader.load());
            Stage stage = (Stage) newUsernameField.getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("Login - Personal Budget Planner");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load login screen.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
