package org.example.gui.component.account;

import org.example.utility.api.email.SendEmail;

import javax.swing.*;
import java.util.Random;

/**
 * A utility class for handling Two-Factor Authentication (2FA) functionality.
 * It provides methods to generate a verification code, send it via email, and verify user input.
 */
public class TwoFactorAuthentication {

    /**
     * Generates a random 6-character verification code and sends it to the specified email address.
     *
     * @param email The recipient's email address to send the verification code.
     * @param username The username of the recipient, used in the email subject.
     * @return The generated verification code.
     */
    public static String generateAndSendCode(String email, String username) {
        SendEmail sendEmail = new SendEmail();
        String generatedCode;

        generatedCode = generateRandomCode();  // Generate a random 6-character code
        String subject = "Course Recommender Verification Code";

        // Send the code via email
        sendEmail.send2FA(email, username, subject, generatedCode);
        return generatedCode;
    }

    /**
     * Generates a random 6-character code using a predefined set of characters.
     * The code is composed of uppercase letters, lowercase letters, and digits, excluding easily confused characters.
     *
     * @return A randomly generated 6-character code.
     */
    private static String generateRandomCode() {
        String characters = "ABCDEFGHJKLMNPQRSTUVXYZabcdefghijkmnopqrstuvwxyz23456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder();

        // Generate a 6-character code
        for (int i = 0; i < 6; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }

        return code.toString();
    }

    /**
     * Verifies if the entered code matches the generated verification code.
     *
     * @param generatedCode The verification code generated and sent to the user.
     * @param code The code entered by the user.
     * @return {@code true} if the entered code matches the generated code, otherwise {@code false}.
     */
    public static boolean verifyCode(String generatedCode, String code) {
        return generatedCode.equals(code);
    }

    /**
     * Verifies if the code entered across multiple fields matches the generated verification code.
     * The fields are concatenated and compared to the generated code.
     *
     * @param codeFields An array of text fields containing the user's input.
     * @param generatedCode The verification code that was sent to the user.
     * @return {@code true} if the concatenated input from the fields matches the generated code, otherwise {@code false}.
     */
    public static boolean verifyCodeAndClose(JTextField[] codeFields, String generatedCode) {
        StringBuilder code = new StringBuilder();
        for (JTextField field : codeFields) {
            code.append(field.getText());  // Concatenate the input from each field
        }

        // Verify the entered code
        return verifyCode(generatedCode, code.toString());
    }

    /**
     * Checks if all the fields in the given array are filled with user input.
     *
     * @param codeFields An array of text fields representing the user input for the verification code.
     * @return {@code true} if all fields are filled, otherwise {@code false}.
     */
    public static boolean areFieldsFull(JTextField[] codeFields) {
        boolean allFieldsFull = true;

        // Check if all code fields are filled
        for (JTextField field : codeFields) {
            if (field.getText().isEmpty()) {
                allFieldsFull = false;
                break;  // Exit the loop early if any field is empty
            }
        }
        return allFieldsFull;
    }
}