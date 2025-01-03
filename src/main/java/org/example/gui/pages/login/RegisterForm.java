package org.example.gui.pages.login;

/**
 * This package contains classes and components related to user authentication
 * and account management within the graphical user interface.
 */
import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import org.example.gui.component.account.CreateAccount;
import org.example.gui.component.account.TwoFactorAuthentication;
import org.example.gui.manager.NotificationManager;
import org.example.gui.component.jcomponents.PasswordStrengthStatus;
import org.example.gui.manager.FormsManager;
import org.example.gui.pages.Application;
import org.example.utility.api.email.EmailUtil;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


/**
 * The {@code RegisterForm} class represents a user registration form in the GUI.
 * It allows users to enter their details, validate inputs, and create an account.
 */
public class RegisterForm extends JPanel {

    /**
     * Field for entering the user's first name.
     */
    private JTextField firstName;

    /**
     * Field for entering the user's last name.
     */
    private JTextField lastName;

    /**
     * Field for entering the username.
     */
    private JTextField username;

    /**
     * Field for entering the password.
     */
    private JPasswordField passwordField;

    /**
     * Field for confirming the password.
     */
    private JPasswordField confirmPasswordField;

    /**
     * Button to submit the registration form.
     */
    private JButton registerBtn;

    /**
     * Component for displaying password strength status.
     */
    private PasswordStrengthStatus passwordStrengthStatus;

    /**
     * Field for entering the user's email address.
     */
    private JTextField emailField;

    /**
     * JFrame reference
     */
    private JFrame frame = Application.getInstance();

    /**
     * Constructs a new {@code RegisterForm} and initializes the components.
     */
    public RegisterForm() {
        frame.setMinimumSize(new Dimension(800, 650));
        init();
    }

    /**
     * Initializes the components and layout of the registration form.
     */
    private void init() {
        setLayout(new MigLayout("fill,insets 20", "[center]", "[center]"));
        firstName = new JTextField();
        lastName = new JTextField();
        username = new JTextField();
        passwordField = new JPasswordField();
        emailField = new JTextField();
        confirmPasswordField = new JPasswordField();
        registerBtn = new JButton("Create Account");


        setMaximumLength(username);


        registerBtn.addActionListener(e -> handleRegister());


        // Add KeyListener to all fields
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleRegister();
                }
            }
        };


        firstName.addKeyListener(enterKeyListener);
        lastName.addKeyListener(enterKeyListener);
        username.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);
        emailField.addKeyListener(enterKeyListener);
        confirmPasswordField.addKeyListener(enterKeyListener);


        passwordStrengthStatus = new PasswordStrengthStatus();


        JPanel panel = new JPanel(new MigLayout("wrap,fillx,insets 35 45 30 45", "[fill,360]"));
        panel.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:20;" +
                "[light]background:darken(@background,3%);" +
                "[dark]background:lighten(@background,3%)");


        firstName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "First name");
        lastName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Last name");
        emailField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your email");
        username.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your username");
        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your password");
        confirmPasswordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Re-enter your password");

        passwordField.putClientProperty(FlatClientProperties.STYLE, "" +
                "showRevealButton:true");
        confirmPasswordField.putClientProperty(FlatClientProperties.STYLE, "" +
                "showRevealButton:true");


        registerBtn.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,10%);" +
                "[dark]background:lighten(@background,10%);" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0");

        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));


        JLabel title = new JLabel("Welcome to EOM Course Recommender");
        JLabel description = new JLabel("Discover the perfect courses for your high school journey with our personalized recommendations—join us today!");
        title.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +10;" +
                "[light]foreground:@earlYellow;" +
                "[dark]foreground:darken(@earlYellow,10%);");
        description.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]foreground:lighten(@foreground,30%);" +
                "[dark]foreground:darken(@foreground,30%)");


        passwordStrengthStatus.initPasswordField(passwordField);


        panel.add(title);
        panel.add(description);
        panel.add(new JLabel("Full Name"), "gapy 10");
        panel.add(firstName, "split 2");
        panel.add(lastName);
        panel.add(new JSeparator(), "gapy 5 5");
        panel.add(new JLabel("Email"));
        panel.add(emailField);
        panel.add(new JLabel("Username"));
        panel.add(username);
        panel.add(new JLabel("Password"), "gapy 8");
        panel.add(passwordField);
        panel.add(passwordStrengthStatus, "gapy 0");
        panel.add(new JLabel("Confirm Password"), "gapy 0");
        panel.add(confirmPasswordField);
        panel.add(registerBtn, "gapy 20");
        panel.add(createLoginLabel(), "gapy 10");
        add(panel);
    }

    /**
     * Handles the registration process by validating inputs and creating an account.
     */
    private void handleRegister() {
        if (!isFilled()) {
            if ((username.getText().length() < 5)) {
                NotificationManager.showNotification(NotificationManager.NotificationType.WARNING, "Username must be at least 5 characters long");
            } else {
                NotificationManager.showNotification(NotificationManager.NotificationType.WARNING, "Please ensure all required fields are completed");
            }
        } else if (!isMatchPassword()) {
            // Check if passwords do not match
            NotificationManager.showNotification(NotificationManager.NotificationType.WARNING, "Passwords do not match. Please try again");
        } else if (!EmailUtil.checkEmailAddress(emailField.getText().toLowerCase())) {
            NotificationManager.showNotification(NotificationManager.NotificationType.WARNING, "Enter a valid email address");

        } else {
            // Check the password strength
            int passwordStrength = PasswordStrengthStatus.checkPasswordStrength(String.valueOf(passwordField.getPassword()));
            if (passwordStrength < 3) {
                NotificationManager.showNotification(NotificationManager.NotificationType.WARNING, "Password needs to be stronger");

            } else {
                // If all fields are filled, passwords match, and password strength is adequate, proceed with account creation
                String usernameTxt = username.getText();
                String password = String.valueOf(passwordField.getPassword());
                String firstNameTxt = firstName.getText();
                String lastNameTxt = lastName.getText();
                String email = emailField.getText().toLowerCase();
                CreateAccount createAccount = new CreateAccount();
                CreateAccount.AccountCreationStatus status = createAccount.createAccount(usernameTxt, password, email, firstNameTxt, lastNameTxt);


                if (status == CreateAccount.AccountCreationStatus.USERNAME_TAKEN) {
                    NotificationManager.showNotification(NotificationManager.NotificationType.ERROR, "This username is already taken. Please select a different username");

                } else if (status == CreateAccount.AccountCreationStatus.EMAIL_TAKEN) {
                    NotificationManager.showNotification(NotificationManager.NotificationType.ERROR, "This email is already being used. Please select a different email");

                } else if (status == CreateAccount.AccountCreationStatus.USERNAME_INCORRECT_FORMAT) {
                    NotificationManager.showNotification(NotificationManager.NotificationType.ERROR, "Username contains spaces. Please select a different username");
                }else {
                    NotificationManager.showNotification(NotificationManager.NotificationType.INFO, "Making 2FA Verification Code...");
                    String generatedCode = TwoFactorAuthentication.generateAndSendCode(email, usernameTxt);
                    NotificationManager.showNotification(NotificationManager.NotificationType.SUCCESS, "Verification code sent");
                    FormsManager.getInstance().showForm(new VerificationForm(generatedCode, emailField.getText(),true));
                }
            }
        }
    }

    /**
     * Creates a JPanel containing a label and a login button.
     * The label asks the user if they already have an account, and the button directs
     * them to the login form when clicked.
     *
     * @return a JPanel containing the label and login button.
     */
    private Component createLoginLabel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null");
        JButton cmdLogin = new JButton("<html><a href=\"#\">Sign in here</a></html>");
        cmdLogin.putClientProperty(FlatClientProperties.STYLE, "" +
                "border:3,3,3,3");
        cmdLogin.setContentAreaFilled(false);
        cmdLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cmdLogin.addActionListener(e -> {
            FormsManager.getInstance().showForm(new LoginForm());
        });
        JLabel label = new JLabel("Already have an account ?");
        label.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]foreground:lighten(@foreground,30%);" +
                "[dark]foreground:darken(@foreground,30%)");
        panel.add(label);
        panel.add(cmdLogin);
        return panel;
    }

    /**
     * Checks if the entered passwords match.
     *
     * @return {@code true} if passwords match; {@code false} otherwise.
     */
    public boolean isMatchPassword() {
        String password = String.valueOf(passwordField.getPassword());
        String confirmPassword = String.valueOf(confirmPasswordField.getPassword());
        return password.equals(confirmPassword);
    }

    /**
     * Checks if all required fields are filled.
     *
     * @return {@code true} if all fields are filled; {@code false} otherwise.
     */
    public boolean isFilled() {
        // Get the text from each field
        String usernameTxt = username.getText();
        String password = passwordField.getText();
        String firstNameTxt = firstName.getText();
        String lastNameTxt = lastName.getText();
        String email = emailField.getText().toLowerCase();


        // Check if any of the fields are empty
        return !usernameTxt.trim().isEmpty() &&
                usernameTxt.length() > 5 &&
                !password.trim().isEmpty() &&
                !firstNameTxt.trim().isEmpty() &&
                !lastNameTxt.trim().isEmpty() &&
                !email.trim().isEmpty();
    }

    /**
     * Sets a maximum character length for the specified text field.
     *
     * @param textField the text field to apply the limit to.
     */
    private void setMaximumLength(JTextField textField) {
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                if (fb.getDocument().getLength() + string.length() > 30) {
                    NotificationManager.showNotification(NotificationManager.NotificationType.WARNING, "Username must be less than 30 characters");
                } else {
                    super.insertString(fb, offset, string, attr);
                }
            }


            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) return;
                if (fb.getDocument().getLength() - length + text.length() > 30) {
                    NotificationManager.showNotification(NotificationManager.NotificationType.WARNING, "Username must be less than 30 characters");
                } else {
                    super.replace(fb, offset, length, text, attrs);
                }
            }


            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                super.remove(fb, offset, length);
            }
        });
    }

}