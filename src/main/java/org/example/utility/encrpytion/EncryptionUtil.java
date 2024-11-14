package org.example.utility.encrpytion;

import org.example.gui.component.account.TwoFactorAuthentication;
import org.example.gui.manager.FormsManager;
import org.example.gui.manager.NotificationManager;
import org.example.gui.pages.login.LoginForm;
import org.example.gui.pages.login.PasswordChangeForm;

import javax.swing.*;

public class EncryptionUtil {
    private static final int SHIFT = 3;
    public static String encodePassword(String input) {
        StringBuilder encrypted = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            // Shift alphabetic characters
            if (Character.isLetter(ch)) {
                char base = (Character.isLowerCase(ch)) ? 'a' : 'A';
                ch = (char) ((ch - base + SHIFT) % 26 + base);
            }
            encrypted.append(ch);
        }
        return encrypted.toString();
    }

    // Method to decode (decrypt) the password
    public static String decodePassword(String input) {
        StringBuilder decrypted = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            // Reverse the shift for alphabetic characters
            if (Character.isLetter(ch)) {
                char base = (Character.isLowerCase(ch)) ? 'a' : 'A';
                ch = (char) ((ch - base - SHIFT + 26) % 26 + base);  // Reverse the shift
            }
            decrypted.append(ch);
        }
        return decrypted.toString();
    }

    public static void isCorrectCode(JTextField[] codeFields, String generatedCode, String email, boolean is2FALogin) {
        boolean correctCode = TwoFactorAuthentication.verifyCodeAndClose(codeFields, generatedCode);
        if (correctCode) {
            if (!is2FALogin) {
                FormsManager.getInstance().showForm(new PasswordChangeForm(email));
            } else {
                FormsManager.getInstance().showForm(new LoginForm());
                NotificationManager.showNotification(NotificationManager.NotificationType.SUCCESS, "Account Created");
            }
            NotificationManager.showNotification(NotificationManager.NotificationType.SUCCESS, "Code verified successfully");
        } else {
            NotificationManager.showNotification(NotificationManager.NotificationType.ERROR, "Incorrect code. Please check to make sure the information was entered correctly");

        }
    }
}
