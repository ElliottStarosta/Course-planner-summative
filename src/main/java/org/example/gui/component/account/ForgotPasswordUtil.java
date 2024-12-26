package org.example.gui.component.account;

import org.example.people.User;
import org.example.utility.JsonUtil;

import java.util.List;

/**
 * A utility class for handling the "Forgot Password" functionality.
 * It provides methods to find a user by email and update the user's password.
 */
public class ForgotPasswordUtil {
    /**
     * A list of users, used to store and retrieve user data from a JSON file.
     */
    private static List<User> users;

    /**
     * Constructs a new {@code ForgotPasswordUtil} instance and initializes the list of users
     * by reading the data from a JSON file.
     */
    public ForgotPasswordUtil() {
        this.users = JsonUtil.readUsersFromJson();
    }

    /**
     * Resets the password for a user identified by their email address.
     * If the email is found, the user's password is updated with the new password provided.
     *
     * @param email The email address associated with the account.
     * @param newPassword The new password to set for the user.
     */
    public static void forgotPassword(String email, String newPassword) {
        User user = findUserWithEmail(email);

        // If a user with the given email is found, update the password
        updateUser(user, newPassword);
    }

    /**
     * Updates the user's password in the list and saves the updated list to the JSON file.
     *
     * @param user The user whose password is to be updated.
     * @param password The new password to set for the user.
     */
    private static void updateUser(User user, String password) {
        if (user != null) {
            user.setPassword(password);
            JsonUtil.saveUsersToJsonPassword(users);
        }
    }

    /**
     * Finds a user by their email address.
     * Searches the list of users and returns the user if found, otherwise returns {@code null}.
     *
     * @param email The email address to search for.
     * @return The {@link User} associated with the email address, or {@code null} if no user is found.
     */
    private static User findUserWithEmail(String email) {
        new ForgotPasswordUtil(); // Initializes the users list
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }
}