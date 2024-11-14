package org.example.utility.api.email;

import com.formdev.flatlaf.json.Json;
import org.example.people.User;
import org.example.utility.JSON.JsonUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailUtil {
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
        users = JsonUtil.readUsersFromJson();

        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(emailAddress)) {
                return true;
            }
        }
        return false;
    }

    public static String getUsernameWithEmail(String emailAddress) {
        List<User> users;
        users = JsonUtil.readUsersFromJson();

        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(emailAddress)) {
                return user.getUsername();
            }
        }
        return "";
    }
}
