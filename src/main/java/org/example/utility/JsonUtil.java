package org.example.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.people.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for reading and writing JSON data related to users and courses.
 * This includes operations for saving user data to JSON files, reading course recommendations, and managing user information.
 */
public class JsonUtil {

    /**
     * Saves a list of users to a JSON file with encoded passwords.
     * This method encodes the passwords before saving the users to ensure they are securely stored.
     *
     * @param users the list of users to be saved
     */
    public static void saveUsersToJson(List<User> users) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Encode passwords before saving (sets the password to be encoded)
        for (User user : users) {
            user.setPassword(user.getPassword());
        }

        try {
            objectMapper.writeValue(new File(User.USERS_FILE), users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves a list of users to a JSON file without encoding passwords.
     * This method saves the user data without making changes to the passwords.
     *
     * @param users the list of users to be saved
     */
    public static void saveUsersToJsonPassword(List<User> users) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            objectMapper.writeValue(new File(User.USERS_FILE), users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the recommended courses for a given user and returns them as a 2D array.
     * This method reads a JSON file containing course recommendations and converts the data into a 2D string array.
     * It also handles exceptions for specific courses that have commas in their names.
     *
     * @param username the username of the user whose recommended courses are being read
     * @return a 2D array where each row represents a grade and its associated courses
     */
    public static String[][] readRecommendedCoursesToMatrix(String username) {
        JSONArray jsonArray;
        try {
            jsonArray = readJsonFromFile(username);
        } catch (Exception e) {
            return new String[0][0]; // Return an empty array on error
        }

        // List of exception courses
        List<String> exceptionCourses = Arrays.asList(
                "NBE3U - English Grade 11 - Understanding Contemporary First Nations, Métis, and Inuit Voices"
        );

        // Create a 2D array with the required number of rows and columns
        int numRows = jsonArray.length();
        String[][] data = new String[numRows][9];

        // Iterate over each JSON object
        for (int i = 0; i < numRows; i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            int grade = jsonObject.getInt("grade");
            String courses = jsonObject.getString("courses");

            // Replace commas in exception courses with a placeholder
            for (String exception : exceptionCourses) {
                courses = courses.replace(exception, exception.replace(",", "<comma>"));
            }

            String[] courseArray = courses.split(", ");

            // Replace placeholders back with commas
            for (int j = 0; j < courseArray.length; j++) {
                courseArray[j] = courseArray[j].replace("<comma>", ",");
            }

            // Set the grade in the first column
            data[i][0] = "Grade " + grade;

            // Fill up to 8 courses in the remaining columns
            int numCourses = Math.min(courseArray.length, 8);
            System.arraycopy(courseArray, 0, data[i], 1, numCourses);
        }

        return data;
    }

    /**
     * Reads JSON data from a file containing the recommended courses for a user.
     * This method reads the content of the file into a JSON array.
     *
     * @param username the username of the user whose recommended courses are being read
     * @return a JSON array containing the user's recommended courses
     * @throws IOException if there is an error reading the file
     */
    private static JSONArray readJsonFromFile(String username) throws IOException {
        String filePath = "src/main/resources/user_class_info/recommended_course_name_" + username + ".json";

        StringBuilder jsonString = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
        }

        return new JSONArray(jsonString.toString());
    }

    /**
     * Reads a list of users from a JSON file and returns it as a list.
     * This method reads the users from the file and converts the data into a list of User objects.
     *
     * @return a list of users read from the JSON file
     */
    public static List<User> readUsersFromJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            User[] usersArray = objectMapper.readValue(new File(User.USERS_FILE), User[].class); // uses reflection to map the values to an object
            return new ArrayList<>(List.of(usersArray));  // Convert array to list
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();  // Return empty list on failure
        }
    }
}