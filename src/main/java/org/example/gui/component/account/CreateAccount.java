package org.example.gui.component.account;

import org.example.people.User;
import org.example.utility.JsonUtil;

import java.util.List;

/**
 * A class responsible for creating user accounts. It performs various checks such as
 * ensuring that the username and email are unique and validating the format of the username.
 */
public class CreateAccount {
    /**
     * A list of existing users, which is used to check if a username or email is already taken.
     */
    private List<User> users;

    /**
     * Constructs a new {@code CreateAccount} instance. This constructor reads the list of users from a JSON file
     * to check against existing accounts.
     */
    public CreateAccount() {
        this.users = User.readUsersFromJson();
    }

    /**
     * Enum representing the status of account creation.
     */
    public enum AccountCreationStatus {
        /**
         * Account creation was successful.
         */
        SUCCESS,

        /**
         * The username is already taken by an existing user.
         */
        USERNAME_TAKEN,

        /**
         * The email is already taken by an existing user.
         */
        EMAIL_TAKEN,

        /**
         * The username format is incorrect (e.g., it contains spaces).
         */
        USERNAME_INCORRECT_FORMAT
    }

    /**
     * Creates a new user account. It checks if the username and email are unique and if the username format is correct.
     * If all validations pass, a new {@link User} object is created and added to the list of users.
     *
     * @param username The username for the new account.
     * @param password The password for the new account.
     * @param email The email for the new account.
     * @param firstName The first name of the new user.
     * @param lastName The last name of the new user.
     * @return The status of the account creation process.
     */
    public AccountCreationStatus createAccount(String username, String password, String email, String firstName, String lastName) {
        if (isUsernameTaken(username)) {
            return AccountCreationStatus.USERNAME_TAKEN;
        } else if (isEmailTaken(email)) {
            return AccountCreationStatus.EMAIL_TAKEN;
        } else if (isUsernameFormatCorrect(username)) {
            return AccountCreationStatus.USERNAME_INCORRECT_FORMAT;
        }

        User newUser = new User(username, password, email, firstName, lastName);
        users.add(newUser);
        JsonUtil.saveUsersToJson(users);

        return AccountCreationStatus.SUCCESS;
    }

    /**
     * Checks if a given username is already taken by an existing user.
     *
     * @param username The username to check.
     * @return {@code true} if the username is taken, {@code false} otherwise.
     */
    private boolean isUsernameTaken(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a given email is already taken by an existing user.
     *
     * @param email The email to check.
     * @return {@code true} if the email is taken, {@code false} otherwise.
     */
    private boolean isEmailTaken(String email) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validates the format of the username. The username is considered incorrect if it contains spaces.
     *
     * @param username The username to validate.
     * @return {@code true} if the username format is incorrect (contains spaces), {@code false} otherwise.
     */
    private boolean isUsernameFormatCorrect(String username) {
        return username.contains(" ");
    }
}
