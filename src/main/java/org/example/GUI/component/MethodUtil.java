package org.example.GUI.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.GUI.login.PasswordChange;
import org.example.GUI.manager.FormsManager;
import org.example.people.User;
import org.example.utility.Course;
import org.example.utility.CourseAssembly;
import raven.toast.Notifications;

import javax.swing.*;
import java.io.File;
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
        String regex = "\\b[A-Za-z0-9._%+-]+@(gmail|yahoo|hotmail|outlook)\\.com\\b";

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

    public static void isCorrectCode(JTextField codeFields[], String generatedCode, String email) {
        boolean correctCode = TwoFactorAuthentication.verifyCodeAndClose(codeFields, generatedCode);
        if (correctCode) {
            FormsManager.getInstance().showForm(new PasswordChange(email));
            Notifications.getInstance().show(Notifications.Type.SUCCESS, "Code verified successfully.");
        } else {
            Notifications.getInstance().show(Notifications.Type.ERROR, "Please ensure all required fields are completed.");
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

}