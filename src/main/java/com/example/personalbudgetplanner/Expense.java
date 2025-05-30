package com.example.personalbudgetplanner;

import java.time.LocalDate;

public class Expense {
    private String name;
    private double amount;
    private LocalDate date;
    private String time;
    private String category;

    public Expense(String name, double amount, LocalDate date, String time, String category) {
        this.name = name;
        this.amount = amount;
        this.date = date;
        this.time = time;
        this.category = category;
    }

    // Getters
    public String getName() { return name; }
    public double getAmount() { return amount; }
    public LocalDate getDate() { return date; }
    public String getTime() { return time; }
    public String getCategory() { return category; }

    // Parse a CSV line to an Expense object
    public static Expense fromString(String line) {
        // Expected format: name,amount,date,time,category
        String[] parts = line.split(",");
        if (parts.length < 5) return null;

        try {
            String name = parts[0];
            double amount = Double.parseDouble(parts[1]);
            LocalDate date = LocalDate.parse(parts[2]);  // yyyy-MM-dd
            String time = parts[3];
            String category = parts[4];

            return new Expense(name, amount, date, time, category);
        } catch (Exception e) {
            return null;  // invalid line
        }
    }
}
