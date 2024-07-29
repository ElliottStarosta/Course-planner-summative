package org.example.GUI.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.people.User;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
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

        MethodUtil.saveUsersToJsonPassword(users);
    }


    private static User findUserWithEmail(String email) {
        new forgotPasswordUtil();
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }

}
