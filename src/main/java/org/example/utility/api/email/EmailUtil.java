package org.example.utility.api.email;

import org.example.people.User;
import org.example.utility.JsonUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The `EmailUtil` class provides utility methods for validating email addresses
 * and checking email registrations. It includes functionality for verifying
 * the email format and checking whether the email is registered in the system.
 */
public class EmailUtil {

    /**
     * Checks if the provided input contains a valid email address with specific domains.
     * The supported domains are: gmail, yahoo, hotmail, outlook, and ocdsb.
     *
     * @param input The string to be checked for a valid email address.
     * @return `true` if the input contains a valid email address with the specified domains,
     *         otherwise `false`.
     */
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

    /**
     * Checks if an email address is registered in the system.
     * This method compares the provided email address against a list of registered users
     * and returns `true` if a matching email address is found.
     *
     * @param emailAddress The email address to be checked.
     * @return `true` if the email is registered, otherwise `false`.
     */
    public static boolean emailRegistered(String emailAddress) {
        // Retrieve the list of registered users from JSON
        List<User> users = JsonUtil.readUsersFromJson();

        // Iterate through the users to check if the email matches any user
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(emailAddress)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves the username associated with the provided email address.
     * If the email address exists in the system, this method returns the corresponding
     * username; otherwise, it returns an empty string.
     *
     * @param emailAddress The email address for which to retrieve the username.
     * @return The username associated with the email address, or an empty string if no match is found.
     */
    public static String getUsernameWithEmail(String emailAddress) {
        // Retrieve the list of registered users from JSON
        List<User> users = JsonUtil.readUsersFromJson();

        // Iterate through the users to find a match for the email address
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(emailAddress)) {
                return user.getUsername();
            }
        }
        return "";
    }
}