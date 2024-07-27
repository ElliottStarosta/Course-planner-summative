package org.example.people;

import org.example.utility.SendEmail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Counselor {
    private String name;
    private String email;

    public Counselor(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    // Method to send email to a counselor based on student input
    public static void sendCounselorEmail(StudentInput student, SendEmail emailSender) {
        List<User> users = User.readUsersFromJson();
        Map<String, User> userMap = createUserMap(users);

        // Find the user by username
        User user = Optional.ofNullable(userMap.get(student.getUsername().toLowerCase()))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Find the counselor based on the user's last name
        Counselor userCounselor = StudentCounselor.findCounselor(user.getLastName());

        // Send email
        emailSender.sendCourses(userCounselor, user);
    }

    // Create a map for faster lookup of users by username
    private static Map<String, User> createUserMap(List<User> users) {
        Map<String, User> userMap = new HashMap<>();
        for (User user : users) {
            userMap.put(user.getUsername().toLowerCase(), user);
        }
        return userMap;
    }
}
