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

public class JsonUtil {
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


    public static void saveUsersToJsonPassword(List<User> users) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);


        try {
            objectMapper.writeValue(new File(User.USERS_FILE), users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
