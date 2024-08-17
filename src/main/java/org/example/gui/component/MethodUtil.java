package org.example.gui.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.gui.component.account.TwoFactorAuthentication;
import org.example.gui.manager.NotificationManager;
import org.example.gui.pages.login.PasswordChangeForm;
import org.example.gui.manager.FormsManager;
import org.example.people.User;
import org.example.utility.courses.Course;
import org.example.utility.courses.CourseAssembly;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodUtil {


    public static int checkPasswordStrength(String password) {
        int score = 0; // Initialize score to 0

        // Check if the password length is at least 8 characters
        if (password.length() >= 8) {
            score++; // Increment score if the length criterion is met
        }

        // Check if the password contains at least one uppercase letter
        boolean hasUppercase = !password.equals(password.toLowerCase());
        if (hasUppercase) {
            score++; // Increment score if an uppercase letter is present
        }

        // Check if the password contains at least one lowercase letter
        boolean hasLowercase = !password.equals(password.toUpperCase());
        if (hasLowercase) {
            score++; // Increment score if a lowercase letter is present
        }

        // Check if the password contains at least one digit
        boolean hasDigit = password.matches(".*\\d.*");
        if (hasDigit) {
            score++; // Increment score if a digit is present
        }

        // Check if the password contains at least one special character
        boolean hasSpecialChar = !password.matches("[A-Za-z0-9]*");
        if (hasSpecialChar) {
            score++; // Increment score if a special character is present
        }

        // Determine the strength of the password based on the final score
        if (score < 3) {
            return 1; // Weak password
        } else if (score < 5) {
            return 2; // Moderate password
        } else {
            return 3; // Strong password
        }
    }

    // Method to check if the string contains an email address with specified domains
    public static boolean checkEmailAddress(String input) {
        // Define the regex pattern to match common email domains
        String regex = "\\b[A-Za-z0-9._%+-]+@(gmail|yahoo|hotmail|outlook|ocdsb)\\.(com|ca)\\b";

        // Compile the regex pattern
        Pattern pattern = Pattern.compile(regex);

        // Create a matcher for the input string
        Matcher matcher = pattern.matcher(input);

        // Check if there is a match
        return matcher.find();
    }

    public static boolean emailRegistered(String emailAddress) {
        List<User> users;
        users = User.readUsersFromJson();

        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(emailAddress)) {
                return true;
            }
        }

        return false;
    }

    public static String getUserNameWithEmail(String emailAddress) {
        List<User> users;
        users = User.readUsersFromJson();

        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(emailAddress)) {
                return user.getUsername();
            }
        }
        return "";
    }

    public static void isCorrectCode(JTextField[] codeFields, String generatedCode, String email) {
        boolean correctCode = TwoFactorAuthentication.verifyCodeAndClose(codeFields, generatedCode);
        if (correctCode) {
            FormsManager.getInstance().showForm(new PasswordChangeForm(email));
            NotificationManager.showNotification(NotificationManager.NotificationType.SUCCESS, "Code verified successfully");
        } else {
            NotificationManager.showNotification(NotificationManager.NotificationType.WARNING, "Please ensure all required fields are completed");

        }
    }

    private void getUserData(String username) throws IOException {

        final String JSON_FILE_PATH = "src/main/resources/user_class_info/recommended_course_code_" + username + ".json";

        ObjectMapper objectMapper = new ObjectMapper();
        List<Course.FileCourseData> courseEntries = objectMapper.readValue(new File(JSON_FILE_PATH), new TypeReference<List<Course.FileCourseData>>() {});

        for (Course.FileCourseData entry : courseEntries) {
            int grade = entry.getGrade();
            String[] courses = entry.getCourses().split(",\\s*"); // Split the courses string into an array
            CourseAssembly.recommendedCoursesByGrade.put(grade, courses);
        }

    }


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

        String[][] data = new String[jsonArray.length()][9];
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            int grade = jsonObject.getInt("grade");
            String courses = jsonObject.getString("courses");
            String[] courseArray = courses.split(", ");

            data[i][0] = "Grade " + grade;
            for (int j = 0; j < Math.min(courseArray.length, 8); j++) {
                data[i][j + 1] = courseArray[j];
            }
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
}