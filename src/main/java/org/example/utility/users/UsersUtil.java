package org.example.utility.users;

import org.example.people.User;
import org.example.utility.JSON.JsonUtil;

import java.util.List;

public class UsersUtil {
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
