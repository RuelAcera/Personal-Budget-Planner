// ViewReportsController.java

package com.example.personalbudgetplanner;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ViewReportsController {

    @FXML
    private ComboBox<String> monthComboBox;

    @FXML
    private ComboBox<String> yearComboBox;

    @FXML
    private Button backButton;

    @FXML
    private BarChart<String, Number> budgetChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private TextArea reportTextArea;

    private final String currentUser = Session.getCurrentUser();

    @FXML
    public void initialize() {
        monthComboBox.getItems().addAll(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        );

        yearComboBox.getItems().addAll("2023", "2024", "2025");

        monthComboBox.getSelectionModel().selectFirst();
        yearComboBox.getSelectionModel().selectFirst();

        xAxis.setLabel("Type");
        yAxis.setLabel("Amount");
        xAxis.setTickLabelRotation(45);
    }

    @FXML
    private void onBackClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onGenerateReportClick() {
        String selectedMonth = monthComboBox.getValue();
        String selectedYear = yearComboBox.getValue();

        if (selectedMonth == null || selectedYear == null) {
            reportTextArea.setText("Please select a month and year to generate report.");
            return;
        }

        budgetChart.getData().clear();

        // Get budget from BudgetManager
        double budgetAmount = BudgetManager.getBudgetForMonth(currentUser, selectedMonth, selectedYear);

        // Get expenses
        double totalExpenses = 0;
        StringBuilder expenseDetails = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader("expenses_" + currentUser + ".txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String expenseName = parts[0];
                    String expenseAmountStr = parts[1];
                    String expenseDate = parts[2];
                    String expenseTime = parts[3];
                    String expenseCategory = parts[4];

                    if (expenseDate.startsWith(selectedYear + "-" + getMonthNumber(selectedMonth))) {
                        try {
                            double expenseAmount = Double.parseDouble(expenseAmountStr);
                            totalExpenses += expenseAmount;

                            expenseDetails.append(String.format("%s: ₱%.2f on %s %s (%s)%n",
                                    expenseName, expenseAmount, expenseDate, expenseTime, expenseCategory));
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            reportTextArea.setText("Failed to load expenses.");
            return;
        }

        double savings = budgetAmount - totalExpenses;

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(selectedMonth + " " + selectedYear + " Budget Report");
        series.getData().add(new XYChart.Data<>("Budget", budgetAmount));
        series.getData().add(new XYChart.Data<>("Expenses", totalExpenses));
        series.getData().add(new XYChart.Data<>("Savings", savings));

        budgetChart.getData().add(series);

        StringBuilder report = new StringBuilder();
        report.append("Monthly Budget Report for ").append(selectedMonth).append(" ").append(selectedYear).append("\n\n");
        report.append("Budget Amount: ₱").append(String.format("%.2f", budgetAmount)).append("\n");
        report.append("Total Expenses: ₱").append(String.format("%.2f", totalExpenses)).append("\n");
        report.append("Savings: ₱").append(String.format("%.2f", savings)).append("\n\n");

        if (expenseDetails.length() > 0) {
            report.append("Expenses Breakdown:\n");
            report.append(expenseDetails);
        } else {
            report.append("No expenses recorded for this month.");
        }

        reportTextArea.setText(report.toString());
    }

    private String getMonthNumber(String monthName) {
        return switch (monthName) {
            case "January" -> "01";
            case "February" -> "02";
            case "March" -> "03";
            case "April" -> "04";
            case "May" -> "05";
            case "June" -> "06";
            case "July" -> "07";
            case "August" -> "08";
            case "September" -> "09";
            case "October" -> "10";
            case "November" -> "11";
            case "December" -> "12";
            default -> "00";
        };
    }

    @FXML
    private void onResetClick() {
        budgetChart.getData().clear();
        reportTextArea.clear();
        monthComboBox.getSelectionModel().clearSelection();
        yearComboBox.getSelectionModel().clearSelection();
    }
}
