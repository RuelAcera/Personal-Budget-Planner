package com.example.personalbudgetplanner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExpenseManager {

    public static List<Expense> loadExpenses(String username) {
        List<Expense> expenses = new ArrayList<>();

        String filename = "expenses_" + username + ".txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;

            while ((line = reader.readLine()) != null) {
                Expense expense = Expense.fromString(line);
                if (expense != null) {
                    expenses.add(expense);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load expenses for user " + username);
            // Optionally handle exceptions or notify user
        }

        return expenses;
    }
}
