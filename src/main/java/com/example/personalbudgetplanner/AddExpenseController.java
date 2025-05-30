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
    private ComboBox<String> hourComboBox;

    @FXML
    private ComboBox<String> minuteComboBox;

    @FXML
    private ComboBox<String> amPmComboBox;

    @FXML
    private Button backButton;

    private final String currentUser = Session.getCurrentUser();

    @FXML
    private void initialize() {
        categoryComboBox.getItems().addAll(
                "Food", "Transportation Expenses", "Utilities", "Entertainment",
                "Electronic device", "Gadgets", "Clothing", "Health",
                "Education", "Travel Expenses", "Tax Property", "Other"
        );

        for (int i = 1; i <= 12; i++) {
            hourComboBox.getItems().add(String.format("%02d", i));
        }

        for (int i = 0; i < 60; i++) {
            minuteComboBox.getItems().add(String.format("%02d", i));
        }

        amPmComboBox.getItems().addAll("AM", "PM");
    }

    @FXML
    private void onAddExpenseSubmit() {
        String name = expenseNameField.getText().trim();
        String amountText = expenseAmountField.getText().trim();
        String category = categoryComboBox.getValue();
        LocalDate date = datePicker.getValue();

        String hour = hourComboBox.getValue();
        String minute = minuteComboBox.getValue();
        String amPm = amPmComboBox.getValue();

        if (name.isEmpty() || amountText.isEmpty() || date == null || category == null
                || hour == null || minute == null || amPm == null) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please fill in all fields, including time.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Amount must be a valid number.");
            return;
        }

        String time = hour + ":" + minute + " " + amPm;

        String line = String.format("%s,%.2f,%s,%s,%s%n", name, amount, date, time, category);

        try (FileWriter writer = new FileWriter("expenses_" + currentUser + ".txt", true)) {
            writer.write(line);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save expense.");
            return;
        }

        showAlert(Alert.AlertType.INFORMATION, "Success", "Expense added successfully!");

        // Clear inputs after adding expense
        expenseNameField.clear();
        expenseAmountField.clear();
        datePicker.setValue(null);
        categoryComboBox.getSelectionModel().clearSelection();
        hourComboBox.getSelectionModel().clearSelection();
        minuteComboBox.getSelectionModel().clearSelection();
        amPmComboBox.getSelectionModel().clearSelection();
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
