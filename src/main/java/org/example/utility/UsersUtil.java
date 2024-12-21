package org.example.utility;

import org.example.people.User;

import java.util.List;

/**
 * Utility class for managing and retrieving user information.
 */
public class UsersUtil {

    /**
     * Retrieves a user based on the provided username.
     *
     * This method reads the list of users from a JSON file and searches for a user
     * whose username matches the provided value (case-insensitive).
     *
     * @param username the username to search for
     * @return the User object if found, or null if no matching user is found
     */
    public static User getUserWithUsername(String username) {
        List<User> users;
        users = JsonUtil.readUsersFromJson();

        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }
}