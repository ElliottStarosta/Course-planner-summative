package org.example.GUI.component;

import org.example.utility.SendEmail;

import javax.swing.*;
import java.util.Random;

public class TwoFactorAuthentication {

    public static String generateAndSendCode(String email, String username) {
        SendEmail sendEmail = new SendEmail();
        String generatedCode;


        generatedCode = generateRandomCode();
        String subject = "Course Recommender Verification Code";

        sendEmail.send2FA(email, username, subject, generatedCode);
        return generatedCode;
    }

    private static String generateRandomCode() {
        String characters = "ABCDEFGHJKLMNPQRSTUVXYZabcdefghijkmnopqrstuvwxyz23456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < 6; i++) { // Generate a 6-character code
            code.append(characters.charAt(random.nextInt(characters.length())));
        }

        return code.toString();
    }

    public static boolean verifyCode(String generatedCode, String code) {
        return generatedCode.equals(code);
    }

    public static boolean verifyCodeAndClose(JTextField[] codeFields, String generatedCode) {
        StringBuilder code = new StringBuilder();
        for (JTextField field : codeFields) {
            code.append(field.getText());
        }

        if (verifyCode(generatedCode,code.toString())) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean areFieldsFull(JTextField[] codeFields) {
        boolean allFieldsFull = true;

        // Check if all code fields are filled
        for (JTextField field : codeFields) {
            if (field.getText().isEmpty()) {
                allFieldsFull = false;
                break;
            }
        }
        return allFieldsFull;
    }
}
