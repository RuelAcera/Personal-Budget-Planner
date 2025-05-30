// SetBudgetController.java

package com.example.personalbudgetplanner;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SetBudgetController {

    @FXML
    private ComboBox<String> monthComboBox;

    @FXML
    private ComboBox<String> yearComboBox;

    @FXML
    private TextField budgetAmountField;

    @FXML
    private Button saveButton;

    @FXML
    private Button backButton;

    private final String currentUser = Session.getCurrentUser();
    private Map<String, String> budgetMap = new HashMap<>();

    @FXML
    public void initialize() {
        // Month options
        monthComboBox.getItems().addAll(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        );

        // Year options
        yearComboBox.getItems().addAll("2023", "2024", "2025");

        // Load user budget data
        budgetMap = BudgetManager.loadUserBudget(currentUser);
    }

    @FXML
    private void onSaveClick() {
        String month = monthComboBox.getValue();
        String year = yearComboBox.getValue();
        String amountStr = budgetAmountField.getText();

        if (month == null || year == null || amountStr.isEmpty()) {
            showAlert("Please fill in all fields.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount < 0) {
                showAlert("Budget amount must be positive.");
                return;
            }

            String key = "budget_" + month + "_" + year;
            budgetMap.put(key, String.valueOf(amount));

            BudgetManager.saveUserBudget(currentUser, budgetMap);
            showAlert("Budget saved successfully for " + month + " " + year + "!");
            budgetAmountField.clear();
            monthComboBox.getSelectionModel().clearSelection();
            yearComboBox.getSelectionModel().clearSelection();

        } catch (NumberFormatException e) {
            showAlert("Please enter a valid number for budget amount.");
        }
    }

    @FXML
    private void onBackClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
