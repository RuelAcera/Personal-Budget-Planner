package com.example.personalbudgetplanner;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DashboardController {

    @FXML
    private Button addExpenseButton;

    @FXML
    private Button setBudgetButton;

    @FXML
    private Button viewReportsButton;

    @FXML
    private Button summaryDashboardButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Button resetButton;

    @FXML
    private Button fullScreenButton;  // New button reference

    @FXML
    private void handleAddExpense() {
        loadScene("add_expense.fxml", addExpenseButton, "Add Expense");
    }

    @FXML
    private void handleSetBudget() {
        loadScene("set_budget.fxml", setBudgetButton, "Set Budget");
    }

    @FXML
    private void handleViewReports() {
        loadScene("view_reports.fxml", viewReportsButton, "View Reports");
    }

    @FXML
    private void handleSummaryDashboard() {
        loadScene("summary_dashboard.fxml", summaryDashboardButton, "Summary Dashboard");
    }

    @FXML
    private void handleLogout() {
        Session.setCurrentUser(null);
        loadScene("login.fxml", logoutButton, "Login");
    }

    @FXML
    private void handleReset() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Reset");
        confirmation.setHeaderText("Are you sure you want to reset all your data?");
        confirmation.setContentText("This will delete all your Expenses.");

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmation.getButtonTypes().setAll(yesButton, noButton);

        confirmation.showAndWait().ifPresent(response -> {
            if (response == yesButton) {
                resetAllUserData();
            }
        });
    }

    private void resetAllUserData() {
        String currentUser = Session.getCurrentUser();
        if (currentUser == null || currentUser.isEmpty()) {
            showAlert("No user session found. Please log in again.");
            return;
        }

        String[] userFiles = {
                "expenses_" + currentUser + ".txt",
                "budget_" + currentUser + ".txt",
                "reports_" + currentUser + ".txt"
        };

        boolean allCleared = true;

        for (String filename : userFiles) {
            Path filePath = Paths.get(filename);
            try {
                if (!Files.exists(filePath)) {
                    Files.createFile(filePath);
                }
                Files.write(filePath, new byte[0]);  // Clear file content
            } catch (IOException e) {
                e.printStackTrace();
                allCleared = false;
            }
        }

        if (allCleared) {
            showInfo("All your data has been reset successfully.");
        } else {
            showAlert("Failed to clear some files. Please try again or check file permissions.");
        }
    }

    private void loadScene(String fxmlFileName, Button sourceButton, String title) {
        try {
            String path = "/com/example/personalbudgetplanner/" + fxmlFileName;
            URL resource = getClass().getResource(path);

            if (resource == null) {
                System.err.println("FXML file NOT FOUND at: " + path);
                showAlert("FXML file not found: " + path);
                return;
            }

            System.out.println("FXML file found at: " + resource);

            FXMLLoader loader = new FXMLLoader(resource);

            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) sourceButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(title);

            System.out.println("Scene loaded successfully: " + title);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to load " + title + " screen.\n" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Unexpected error while loading " + title + " screen.\n" + e.getMessage());
        }
    }

    private void showAlert(String message) {
        System.err.println("Alert: " + message);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // New method: toggles fullscreen mode
    @FXML
    private void handleFullScreenToggle() {
        Stage stage = (Stage) fullScreenButton.getScene().getWindow();
        stage.setFullScreen(!stage.isFullScreen());
    }
}
