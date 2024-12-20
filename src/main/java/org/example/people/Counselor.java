package org.example.people;

import org.example.utility.JsonUtil;
import org.example.utility.api.email.SendEmail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The `Counselor` class represents a counselor with a name and email.
 * It also provides a method to send an email to a counselor based on student information.
 */
public class Counselor {
    private String name;
    private String email;

    /**
     * Constructs a new `Counselor` object with a name and an email address.
     *
     * @param name  The name of the counselor.
     * @param email The email address of the counselor.
     */
    public Counselor(String name, String email) {
        this.name = name;
        this.email = email;
    }

    /**
     * Gets the name of the counselor.
     *
     * @return The name of the counselor.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the email address of the counselor.
     *
     * @return The email address of the counselor.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sends an email to a counselor based on the student's username.
     * This method retrieves the student's information, determines the correct counselor
     * based on the student's last name, and then sends the email to the counselor.
     *
     * @param username The username of the student.
     * @throws IllegalArgumentException if the user with the given username is not found.
     */
    public static void sendCounselorEmail(String username) {
        SendEmail emailSender = new SendEmail();

        // Retrieve the list of users from the JSON file
        List<User> users = JsonUtil.readUsersFromJson();
        // Create a map for fast user lookup by username
        Map<String, User> userMap = createUserMap(users);

        // Find the user by username
        User user = Optional.ofNullable(userMap.get(username.toLowerCase()))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Find the counselor based on the user's last name
        Counselor userCounselor = StudentCounselor.findCounselor(user.getLastName());

        // Send the email to the counselor with the user's courses
        emailSender.sendCourses(userCounselor, user);
    }

    /**
     * Creates a map for faster lookup of users by their username.
     *
     * @param users The list of users to be converted into a map.
     * @return A map where the key is the username (lowercased) and the value is the corresponding `User` object.
     */
    private static Map<String, User> createUserMap(List<User> users) {
        Map<String, User> userMap = new HashMap<>();
        for (User user : users) {
            userMap.put(user.getUsername().toLowerCase(), user);
        }
        return userMap;
    }
}