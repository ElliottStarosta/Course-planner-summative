package org.example.gui.component.account;

import org.example.gui.component.MethodUtil;
import org.example.people.User;

import java.util.List;

public class ForgotPasswordUtil {
    private static List<User> users;

    public ForgotPasswordUtil() {
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
        new ForgotPasswordUtil();
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }

}
