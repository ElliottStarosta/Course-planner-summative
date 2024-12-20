package org.example.people;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.utility.EncryptionUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user account with personal information such as username, password, email,
 * first name, and last name. It supports serialization and deserialization to
 * and from JSON using Jackson, as well as password encryption.
 */
public class User {

    /** The file path for storing user data in JSON format. */
    public static final String USERS_FILE = "src/main/resources/users.json";

    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;

    /**
     * No-argument constructor for Jackson deserialization.
     */
    public User() {}

    /**
     * Constructor with parameters for creating a User object.
     * This constructor is used by Jackson during deserialization.
     *
     * @param username  The username of the user.
     * @param password  The password of the user.
     * @param email     The email address of the user.
     * @param firstName The first name of the user.
     * @param lastName  The last name of the user.
     */
    @JsonCreator
    public User(@JsonProperty("username") String username,
                @JsonProperty("password") String password,
                @JsonProperty("email") String email,
                @JsonProperty("firstName") String firstName,
                @JsonProperty("lastName") String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    /**
     * Gets the username of the user.
     *
     * @return The username of the user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the password of the user.
     *
     * @return The password of the user.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the email address of the user.
     *
     * @return The email address of the user.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the first name of the user.
     *
     * @return The first name of the user.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Gets the last name of the user.
     *
     * @return The last name of the user.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the username of the user.
     *
     * @param username The username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sets the password of the user. The password is encoded using
     * the {@link EncryptionUtil#encodePassword(String)} method before being stored.
     *
     * @param password The password to set.
     */
    public void setPassword(String password) {
        this.password = new String(EncryptionUtil.encodePassword(password));
    }

    /**
     * Sets the email address of the user.
     *
     * @param email The email address to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets the first name of the user.
     *
     * @param firstName The first name to set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Sets the last name of the user.
     *
     * @param lastName The last name to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Reads user data from a JSON file and deserializes it into a list of {@link User} objects.
     *
     * @return A list of {@link User} objects, or an empty list if an error occurs during reading.
     */
    public static List<User> readUsersFromJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            User[] usersArray = objectMapper.readValue(new File(USERS_FILE), User[].class);
            return new ArrayList<>(List.of(usersArray));  // Convert array to list
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();  // Return empty list on failure
        }
    }
}