package org.example.GUI.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.people.User;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class forgotPasswordUtil {
    private static List<User> users;

    public forgotPasswordUtil() {
        this.users = User.readUsersFromJson();
    }

    public static void forgotPassword(String email, String newPassword) {
        User user = findUserWithEmail(email);

        updateUser(user, newPassword);

    }

    private static void updateUser(User user, String password) {
        user.setPassword(password);

        saveUsersToJson(users);
    }

    private static void saveUsersToJson(List<User> users) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            objectMapper.writeValue(new File(User.USERS_FILE), users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static User findUserWithEmail(String email) {
        new forgotPasswordUtil();
        for (User user : users) {
            if (user.getEmail().equals(email));
            return user;
        }
        return null;
    }
}
