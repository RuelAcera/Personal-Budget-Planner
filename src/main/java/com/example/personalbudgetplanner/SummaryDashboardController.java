package com.example.personalbudgetplanner;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SummaryDashboardController {

    @FXML
    private TextArea summaryTextArea;

    @FXML
    private PieChart expensePieChart;

    @FXML
    private BarChart<String, Number> budgetBarChart;

    @FXML
    private Button backButton;

    @FXML
    private ChoiceBox<String> yearChoiceBox;

    private final String currentUser = Session.getCurrentUser();

    @FXML
    private void initialize() {
        // Initialize Year ChoiceBox
        yearChoiceBox.setItems(FXCollections.observableArrayList("2023", "2024", "2025"));
        yearChoiceBox.setValue(String.valueOf(Year.now().getValue())); // Default to current year

        yearChoiceBox.setOnAction(event -> loadSummary()); // Reload summary on year change

        loadSummary();
        playFadeInAnimation();
    }

    private void loadSummary() {
        String selectedYear = yearChoiceBox.getValue();
        List<Expense> allExpenses = ExpenseManager.loadExpenses(currentUser);

        // Filter expenses for selected year
        List<Expense> expenses = allExpenses.stream()
                .filter(expense -> String.valueOf(expense.getDate().getYear()).equals(selectedYear))
                .collect(Collectors.toList());

        Map<String, Double> budgets = BudgetManager.getAllBudgetsForYear(currentUser, selectedYear);

        List<String> monthsOrdered = List.of("January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December");

        Map<String, Double> expensePerMonth = new HashMap<>();
        Map<String, Double> categoryTotals = new HashMap<>();

        for (Expense expense : expenses) {
            String month = expense.getDate().getMonth().name();
            month = month.charAt(0) + month.substring(1).toLowerCase();

            expensePerMonth.put(month, expensePerMonth.getOrDefault(month, 0.0) + expense.getAmount());

            String category = expense.getCategory();
            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + expense.getAmount());
        }

        StringBuilder summary = new StringBuilder();
        summary.append("Summary for user: ").append(currentUser).append(" | Year: ").append(selectedYear).append("\n\n");

        for (String month : monthsOrdered) {
            double budget = budgets.getOrDefault(month, 0.0);
            double spent = expensePerMonth.getOrDefault(month, 0.0);
            double remaining = budget - spent;

            if (budget != 0 || spent != 0) { // Only show months with data
                summary.append(String.format("%s:\n - Budget: ₱%.2f\n - Expenses: ₱%.2f\n - Remaining: ₱%.2f\n\n",
                        month, budget, spent, remaining));
            }
        }

        summaryTextArea.setText(summary.toString());

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }
        expensePieChart.setData(pieChartData);
        expensePieChart.setTitle("Expenses by Category");

        expensePieChart.getData().forEach(data -> {
            FadeTransition ft = new FadeTransition(Duration.millis(1200), data.getNode());
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
        });

        XYChart.Series<String, Number> budgetSeries = new XYChart.Series<>();
        budgetSeries.setName("Budget");

        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Expenses");

        budgetBarChart.getData().clear();
        for (String month : monthsOrdered) {
            double budget = budgets.getOrDefault(month, 0.0);
            double spent = expensePerMonth.getOrDefault(month, 0.0);

            if (budget != 0 || spent != 0) { // Only show months with data
                budgetSeries.getData().add(new XYChart.Data<>(month, budget));
                expenseSeries.getData().add(new XYChart.Data<>(month, spent));
            }
        }

        budgetBarChart.getData().addAll(budgetSeries, expenseSeries);
    }

    private void playFadeInAnimation() {
        FadeTransition ft = new FadeTransition(Duration.seconds(1.5), summaryTextArea);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    @FXML
    private void onBackClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/personalbudgetplanner/dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
