
package org.example.gui.pages.login;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import org.example.gui.manager.NotificationManager;
import org.example.gui.component.jcomponents.PasswordStrengthStatus;
import org.example.gui.component.account.ForgotPasswordUtil;
import org.example.gui.manager.FormsManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * This class represents a form for changing a user's password.
 * It provides input fields for the new password, confirmation password, and a button to reset the password.
 * The form includes password strength validation and ensures that the passwords match before updating.
 *
 * Dependencies:
 * - FlatLaf for UI styling
 * - MigLayout for layout management
 * - NotificationManager for showing notifications
 * - PasswordStrengthStatus for evaluating password strength
 * - ForgotPasswordUtil for updating the password
 * - FormsManager for navigating between forms
 */
public class PasswordChangeForm extends JPanel {

    /** Input field for the new password */
    private JPasswordField Password;

    /** Input field for confirming the new password */
    private JPasswordField ConfirmPassword;

    /** Button to reset the password */
    private JButton resetPasswordBtn;

    /** Component to display password strength */
    private PasswordStrengthStatus passwordStrengthStatus;

    /** The email associated with the account */
    private String email;

    /**
     * Constructor for creating a PasswordChangeForm.
     *
     * @param email The email of the user whose password is being reset.
     */
    public PasswordChangeForm(String email) {
        this.email = email;
        init();
    }

    /**
     * Initializes the components of the form and sets up the layout.
     */
    private void init() {
        setLayout(new MigLayout("fill,insets 20", "[center]", "[center]"));

        Password = new JPasswordField();
        ConfirmPassword = new JPasswordField();
        resetPasswordBtn = new JButton("Reset Password");

        resetPasswordBtn.addActionListener(e -> {
            if (!isFilled()) {
                // Check if any fields are not filled
                NotificationManager.showNotification(NotificationManager.NotificationType.WARNING, "Please ensure all required fields are completed");
            } else if (!isMatchPassword()) {
                // Check if passwords do not match
                NotificationManager.showNotification(NotificationManager.NotificationType.WARNING, "Passwords do not match. Please try again");
            } else {
                // Check the password strength
                int passwordStrength = PasswordStrengthStatus.checkPasswordStrength(String.valueOf(Password.getPassword()));
                if (passwordStrength < 3) {
                    NotificationManager.showNotification(NotificationManager.NotificationType.WARNING, "Password needs to be stronger");
                } else {
                    ForgotPasswordUtil.forgotPassword(email, Password.getText());
                    NotificationManager.showNotification(NotificationManager.NotificationType.SUCCESS, "Your password has been successfully updated");

                    FormsManager.getInstance().showForm(new LoginForm());
                }
            }
        });

        passwordStrengthStatus = new PasswordStrengthStatus();

        JPanel panel = new JPanel(new MigLayout("wrap,fillx,insets 35 45 30 45", "[fill,360]"));
        panel.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:20;" +
                "[light]background:darken(@background,3%);" +
                "[dark]background:lighten(@background,3%)");

        Password.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your password");
        ConfirmPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Re-enter your password");
        Password.putClientProperty(FlatClientProperties.STYLE, "" +
                "showRevealButton:true");

        ConfirmPassword.putClientProperty(FlatClientProperties.STYLE, "" +
                "showRevealButton:true");

        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (Password.getPassword().length > 0 && ConfirmPassword.getPassword().length > 0) {
                        resetPasswordBtn.doClick();
                    }
                }
            }
        };

        Password.addKeyListener(keyAdapter);
        ConfirmPassword.addKeyListener(keyAdapter);

        resetPasswordBtn.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,10%);" +
                "[dark]background:lighten(@background,10%);" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0");
        resetPasswordBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel title = new JLabel("Reset Your Password");
        title.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +10;" +
                "[light]foreground:@earlYellow;" +
                "[dark]foreground:darken(@earlYellow,10%);");

        passwordStrengthStatus.initPasswordField(Password);

        panel.add(title);
        panel.add(new JSeparator(), "gapy 5 5");
        panel.add(new JLabel("Password"), "gapy 8");
        panel.add(Password);
        panel.add(passwordStrengthStatus, "gapy 0");
        panel.add(new JLabel("Confirm Password"), "gapy 0");
        panel.add(ConfirmPassword);
        panel.add(resetPasswordBtn, "gapy 20");

        add(panel);
    }

    /**
     * Checks if the entered passwords match.
     *
     * @return {@code true} if the passwords match; {@code false} otherwise.
     */
    public boolean isMatchPassword() {
        String password = String.valueOf(Password.getPassword());
        String confirmPassword = String.valueOf(ConfirmPassword.getPassword());
        return password.equals(confirmPassword);
    }

    /**
     * Checks if all required fields are filled.
     *
     * @return {@code true} if all fields are filled; {@code false} otherwise.
     */
    public boolean isFilled() {
        String password = Password.getText();
        String passwordConfirm = ConfirmPassword.getText();
        return !passwordConfirm.trim().isEmpty() &&
                !password.trim().isEmpty();
    }
}