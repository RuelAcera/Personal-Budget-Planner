package com.example.personalbudgetplanner;

import java.io.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class UserDatabase {

    private static final String USER_FILE = "users.txt";
    private static final String USER_DATA_FOLDER = "users";

    public static Map<String, String> loadUsers() {
        Map<String, String> users = new HashMap<>();
        Path path = Paths.get(USER_FILE);

        if (!Files.exists(path)) {
            return users; // empty if no file yet
        }

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    users.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return users;
    }

    public static boolean addUser(String username, String password) {
        Map<String, String> users = loadUsers();

        if (users.containsKey(username)) {
            return false; // user exists
        }

        users.put(username, password);

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(USER_FILE))) {
            for (Map.Entry<String, String> entry : users.entrySet()) {
                writer.write(entry.getKey() + "=" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        createUserDataFile(username); // create user's personal data file
        return true;
    }

    public static void createUserDataFile(String username) {
        try {
            Path userDir = Paths.get(USER_DATA_FOLDER);
            Files.createDirectories(userDir); // create /users folder if not exist

            Path userFile = userDir.resolve(username + ".txt");
            if (!Files.exists(userFile)) {
                Files.createFile(userFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Optional: use this to load a user's personal data file
    public static Path getUserDataPath(String username) {
        return Paths.get(USER_DATA_FOLDER, username + ".txt");
    }
}
