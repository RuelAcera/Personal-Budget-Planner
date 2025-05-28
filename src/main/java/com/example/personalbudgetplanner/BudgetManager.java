package com.example.personalbudgetplanner;

import java.io.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class BudgetManager {

    public static Map<String, String> loadUserBudget(String username) {
        Path filePath = UserDatabase.getUserDataPath(username);
        Map<String, String> budgetMap = new HashMap<>();

        if (!Files.exists(filePath)) return budgetMap;

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    budgetMap.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return budgetMap;
    }

    public static void saveUserBudget(String username, Map<String, String> budgetData) {
        Path filePath = UserDatabase.getUserDataPath(username);

        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (Map.Entry<String, String> entry : budgetData.entrySet()) {
                writer.write(entry.getKey() + "=" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
