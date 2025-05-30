package com.example.personalbudgetplanner;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class BudgetManager {

    private static final String FILE_PREFIX = "budget_";
    private static final String FILE_SUFFIX = ".txt";

    public static Map<String, String> loadUserBudget(String username) {
        Map<String, String> budgetMap = new HashMap<>();
        File file = new File(FILE_PREFIX + username + FILE_SUFFIX);
        if (!file.exists()) {
            return budgetMap; // Return empty map if file doesn't exist
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    budgetMap.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return budgetMap;
    }

    public static double getBudgetForMonth(String username, String month, String year) {
        Map<String, String> budgetMap = loadUserBudget(username);
        String key = "budget_" + month + "_" + year;
        String amountStr = budgetMap.getOrDefault(key, "0");
        try {
            return Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static Map<String, Double> getAllBudgetsForYear(String username, String year) {
        Map<String, String> budgetMap = loadUserBudget(username);
        Map<String, Double> budgets = new HashMap<>();

        for (Map.Entry<String, String> entry : budgetMap.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("budget_") && key.endsWith("_" + year)) {
                String month = key.substring(7, key.length() - (year.length() + 1));
                try {
                    budgets.put(month, Double.parseDouble(entry.getValue()));
                } catch (NumberFormatException e) {
                    budgets.put(month, 0.0);
                }
            }
        }
        return budgets;
    }

    public static void saveUserBudget(String username, Map<String, String> budgetMap) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PREFIX + username + FILE_SUFFIX))) {
            for (Map.Entry<String, String> entry : budgetMap.entrySet()) {
                writer.write(entry.getKey() + "=" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
