package org.example.gui.component.account;

import org.example.gui.component.MethodUtil;
import org.example.people.User;

import java.util.List;

public class CreateAccount {
    private List<User> users;

    public CreateAccount() {
        this.users = User.readUsersFromJson();
    }


    public enum AccountCreationStatus {
        SUCCESS,
        USERNAME_TAKEN,
        EMAIL_TAKEN
    }

     public AccountCreationStatus createAccount(String username, String password, String email, String firstName, String lastName) {

        if(isUsernameTaken(username)) {
            return AccountCreationStatus.USERNAME_TAKEN;
        } else if (isEmailTaken(email)) {
            return AccountCreationStatus.EMAIL_TAKEN;
        }

        User newUser = new User(username, password, email, firstName, lastName);
        users.add(newUser);
        MethodUtil.saveUsersToJson(users);

        return AccountCreationStatus.SUCCESS;
    }

    private boolean isUsernameTaken(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    private boolean isEmailTaken(String email) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

}