package com.example.personalbudgetplanner;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ViewReportsController {

    @FXML
    private Button backButton;

    @FXML
    private TextArea reportTextArea;

    @FXML
    private ComboBox<String> monthComboBox;

    private final String currentUser = Session.currentUser;

    @FXML
    private void initialize() {
        reportTextArea.clear();

        // Initialize monthComboBox with months
        monthComboBox.getItems().addAll(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        );
    }

    @FXML
    private void onBackClick() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("Dashboard.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(loader.load());
            javafx.stage.Stage stage = (javafx.stage.Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Dashboard");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load dashboard.");
        }
    }

    @FXML
    private void onGenerateReportClick() {
        String selectedMonthStr = monthComboBox.getSelectionModel().getSelectedItem();
        if (selectedMonthStr == null || selectedMonthStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Month Selected", "Please select a month to generate the report.");
            return;
        }

        String report = generateReportFromFiles(selectedMonthStr);

        if (report.isEmpty() || report.trim().isEmpty()) {
            reportTextArea.setText("No data to display.");
        } else {
            reportTextArea.setText(report);
        }
    }

    @FXML
    private void onResetClick() {
        reportTextArea.clear();
        monthComboBox.getSelectionModel().clearSelection();
    }

    private String generateReportFromFiles(String selectedMonthStr) {
        double totalExpenses = 0;
        double budgetAmount = 0;
        int selectedMonth = monthNameToNumber(selectedMonthStr);

        Map<String, Double> categoryTotals = new HashMap<>();
        List<String> expenseLines = new ArrayList<>();

        // Load budget
        Map<String, String> budgetMap = BudgetManager.loadUserBudget(currentUser);
        String monthlyBudgetStr = budgetMap.get("budget_" + selectedMonthStr);
        if (monthlyBudgetStr != null) {
            try {
                budgetAmount = Double.parseDouble(monthlyBudgetStr.trim());
            } catch (NumberFormatException e) {
                budgetAmount = 0;
            }
        }

        // Read expenses file and filter by selected month
        File expenseFile = new File("expenses_" + currentUser + ".txt");
        boolean foundExpenses = false;
        if (expenseFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(expenseFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 4) {
                        String name = parts[0].trim();
                        double amount;
                        String date = parts[2].trim();
                        String category = parts[3].trim();

                        try {
                            amount = Double.parseDouble(parts[1].trim());
                        } catch (NumberFormatException e) {
                            continue;
                        }

                        if (isDateInMonth(date, selectedMonth)) {
                            totalExpenses += amount;
                            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + amount);
                            expenseLines.add(String.format("- %s: ₱%.2f (Date: %s, Category: %s)", name, amount, date, category));
                            foundExpenses = true;
                        }
                    }
                }
            } catch (IOException e) {
                // ignore read error
            }
        }

        if (budgetAmount == 0 && !foundExpenses) {
            return "No budget or expenses recorded for " + selectedMonthStr + ".";
        }

        return buildReport(budgetAmount, totalExpenses, categoryTotals, expenseLines);
    }

    private String buildReport(double budgetAmount, double totalExpenses, Map<String, Double> categoryTotals, List<String> expenseLines) {
        double remainingBudget = budgetAmount - totalExpenses;
        StringBuilder report = new StringBuilder();

        report.append("===== Personal Budget Report =====\n");
        report.append(String.format("Total Budget: ₱%.2f\n", budgetAmount));
        report.append(String.format("Total Expenses: ₱%.2f\n", totalExpenses));
        report.append(String.format("Remaining Budget: ₱%.2f\n\n", remainingBudget));

        report.append("Category Breakdown:\n");
        if (categoryTotals.isEmpty()) {
            report.append("No categories found.\n");
        } else {
            for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
                report.append(String.format("- %s: ₱%.2f\n", entry.getKey(), entry.getValue()));
            }
        }

        report.append("\nExpense Details:\n");
        if (expenseLines.isEmpty()) {
            report.append("No expenses recorded.\n");
        } else {
            for (String line : expenseLines) {
                report.append(line).append("\n");
            }
        }

        report.append("\nThank you for using Personal Budget Planner!");
        return report.toString();
    }

    private boolean isDateInMonth(String date, int month) {
        if (date == null || date.length() < 7) return false;
        try {
            int expenseMonth = Integer.parseInt(date.substring(5, 7));
            return expenseMonth == month;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private int monthNameToNumber(String monthName) {
        switch (monthName.toLowerCase()) {
            case "january": return 1;
            case "february": return 2;
            case "march": return 3;
            case "april": return 4;
            case "may": return 5;
            case "june": return 6;
            case "july": return 7;
            case "august": return 8;
            case "september": return 9;
            case "october": return 10;
            case "november": return 11;
            case "december": return 12;
            default: return 0;
        }
    }

    private static void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
