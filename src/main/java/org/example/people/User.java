package org.example.people;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;

    public static final String USERS_FILE = "src/main/resources/users.json";


    // No-argument constructor for Jackson deserialization
    public User() {}

    // Constructor with arguments
    public User(@JsonProperty("username") String username, @JsonProperty("password") String password, @JsonProperty("email") String email, @JsonProperty("firstName") String firstName, @JsonProperty("lastName") String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    // Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public static List<User> readUsersFromJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            User[] usersArray = objectMapper.readValue(new File(USERS_FILE), User[].class); // uses reflection to map the values to an object
            return new ArrayList<>(List.of(usersArray));  // Convert array to list
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();  // Return empty list on failure
        }
    }
}