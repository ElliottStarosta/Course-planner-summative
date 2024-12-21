package org.example.utility;

import org.example.gui.component.account.TwoFactorAuthentication;
import org.example.gui.manager.FormsManager;
import org.example.gui.manager.NotificationManager;
import org.example.gui.pages.login.LoginForm;
import org.example.gui.pages.login.PasswordChangeForm;

import javax.swing.*;

/**
 * Utility class for handling encryption, decryption, and code verification logic.
 */
public class EncryptionUtil {

    /**
     * The number of positions to shift for the Caesar cipher.
     */
    private static final int SHIFT = 3;

    /**
     * Encrypts the input string using a Caesar cipher.
     *
     * @param input the string to be encrypted
     * @return the encrypted string
     */
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

    /**
     * Decrypts the input string using a reverse Caesar cipher.
     *
     * @param input the string to be decrypted
     * @return the decrypted string
     */
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

    /**
     * Verifies the code entered the provided text fields against the generated code.
     * Depending on the verification result, it performs actions such as showing forms or notifications.
     *
     * @param codeFields   an array of text fields containing the entered code
     * @param generatedCode the expected generated code
     * @param email        the email associated with the account
     * @param is2FALogin   true if the action is related to 2FA login, false otherwise
     */
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