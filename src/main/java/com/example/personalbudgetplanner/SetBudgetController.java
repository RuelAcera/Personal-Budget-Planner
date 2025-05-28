package com.example.personalbudgetplanner;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SetBudgetController {

    @FXML
    public Button backButton;

    @FXML
    public Button saveBudgetButton;

    @FXML
    public Button deleteBudgetButton;

    @FXML
    private TextField amountField;

    @FXML
    private ComboBox<String> monthComboBox;

    private final String currentUser = Session.getCurrentUser();

    // Will hold all budgets loaded from file (month->amount)
    private Map<String, String> budgetMap = new HashMap<>();

    @FXML
    public void initialize() {
        // Populate month options
        monthComboBox.getItems().addAll(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        );

        // Load all budgets for user
        budgetMap = BudgetManager.loadUserBudget(currentUser);

        // Add listener to update amount field when month is selected
        monthComboBox.setOnAction(e -> loadBudgetForSelectedMonth());
    }

    private void loadBudgetForSelectedMonth() {
        String selectedMonth = monthComboBox.getValue();
        if (selectedMonth != null && !selectedMonth.isEmpty()) {
            String key = "budget_" + selectedMonth;
            String amount = budgetMap.get(key);
            amountField.setText(amount != null ? amount : "");
        } else {
            amountField.clear();
        }
    }

    @FXML
    private void onAddOrUpdateBudget() {
        String amount = amountField.getText().trim();
        String selectedMonth = monthComboBox.getValue();

        if (amount.isEmpty() || selectedMonth == null || selectedMonth.isEmpty()) {
            showAlert("Please select a month and enter a budget amount.");
            return;
        }

        // Update or add budget for selected month
        budgetMap.put("budget_" + selectedMonth, amount);

        // Save updated budgets map
        BudgetManager.saveUserBudget(currentUser, budgetMap);

        showAlert("Budget saved successfully for " + selectedMonth + "!");
    }

    @FXML
    private void onDeleteBudget() {
        String selectedMonth = monthComboBox.getValue();
        if (selectedMonth == null || selectedMonth.isEmpty()) {
            showAlert("Please select a month to delete its budget.");
            return;
        }

        // Remove the budget for the selected month if exists
        String key = "budget_" + selectedMonth;
        if (budgetMap.containsKey(key)) {
            budgetMap.remove(key);
            BudgetManager.saveUserBudget(currentUser, budgetMap);
            amountField.clear();
            showAlert("Budget deleted successfully for " + selectedMonth + "!");
        } else {
            showAlert("No budget found for " + selectedMonth + " to delete.");
        }
    }

    @FXML
    private void onBackClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) amountField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to load Dashboard.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
