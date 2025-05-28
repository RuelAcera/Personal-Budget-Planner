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
    private Button logoutButton;

    @FXML
    private Button resetButton;

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
    private void handleLogout() {
        // Clear session on logout
        Session.setCurrentUser();
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

    /**
     * Clears user-specific data files: expenses, budgets, and reports.
     */
    private void resetAllUserData() {
        String currentUser = Session.getCurrentUser();
        if (currentUser == null || currentUser.isEmpty()) {
            showAlert("No user session found. Please log in again.");
            return;
        }

        // User-specific files (assuming format is consistent with your previous code)
        String[] userFiles = {
                "expenses_" + currentUser + ".txt",
                "budget_" + currentUser + ".txt",
                "reports_" + currentUser + ".txt"
        };

        boolean allCleared = true;

        for (String filename : userFiles) {
            Path filePath = Paths.get(filename);
            try {
                // If file does not exist, create it
                if (!Files.exists(filePath)) {
                    Files.createFile(filePath);
                }
                // Clear file contents
                Files.write(filePath, new byte[0]);
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

    /**
     * Utility to load another FXML scene.
     */
    private void loadScene(String fxmlFileName, Button sourceButton, String title) {
        try {
            String path = "/com/example/personalbudgetplanner/" + fxmlFileName;
            URL resource = getClass().getResource(path);

            if (resource == null) {
                System.err.println("FXML file NOT FOUND at: " + path);
                showAlert("FXML file not found: " + path);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) sourceButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to load " + title + " screen.\n" + e.getMessage());
        }
    }

    /**
     * Show error alert with given message.
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show info alert with given message.
     */
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
