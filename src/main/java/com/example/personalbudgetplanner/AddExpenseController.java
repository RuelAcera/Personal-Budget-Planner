package com.example.personalbudgetplanner;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

public class AddExpenseController {

    @FXML
    private TextField expenseNameField;

    @FXML
    private TextField expenseAmountField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private Button backButton;

    private final String currentUser = Session.currentUser;

    @FXML
    private void initialize() {
        // Load predefined categories
        categoryComboBox.getItems().addAll("Food", "Transportation", "Utilities", "Entertainment", "Other");
    }

    @FXML
    private void onAddExpenseSubmit() {
        String name = expenseNameField.getText();
        String amountText = expenseAmountField.getText();
        String category = categoryComboBox.getValue();
        LocalDate date = datePicker.getValue();

        // Validate user input
        if (name.isEmpty() || amountText.isEmpty() || date == null || category == null) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please fill in all fields.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Amount must be a number.");
            return;
        }

        // Format: name,amount,date,category
        String line = String.format("%s,%.2f,%s,%s%n", name, amount, date, category);

        try (FileWriter writer = new FileWriter("expenses_" + currentUser + ".txt", true)) {
            writer.write(line);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save expense.");
            return;
        }

        showAlert(Alert.AlertType.INFORMATION, "Success", "Expense added successfully!");

        // Clear form
        expenseNameField.clear();
        expenseAmountField.clear();
        datePicker.setValue(null);
        categoryComboBox.getSelectionModel().clearSelection();
    }

    @FXML
    private void onBackClick() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("Dashboard.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(loader.load());
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Dashboard");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load dashboard.");
        }
    }

    private static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
