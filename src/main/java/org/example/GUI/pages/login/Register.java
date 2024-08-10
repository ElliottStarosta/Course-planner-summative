package org.example.GUI.pages.login;


import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import org.example.GUI.component.CreateAccount;
import org.example.GUI.component.MethodUtil;
import org.example.GUI.component.NotificationManager;
import org.example.GUI.component.PasswordStrengthStatus;
import org.example.GUI.manager.FormsManager;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;



public class Register extends JPanel {
    public Register() {
        init();
    }


    private void init() {
        setLayout(new MigLayout("fill,insets 20", "[center]", "[center]"));
        FirstName = new JTextField();
        LastName = new JTextField();
        Username = new JTextField();
        Password = new JPasswordField();
        Email = new JTextField();
        ConfirmPassword = new JPasswordField();
        registerBtn = new JButton("Create Account");


        setMaximumLength(Username, 30);


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


        FirstName.addKeyListener(enterKeyListener);
        LastName.addKeyListener(enterKeyListener);
        Username.addKeyListener(enterKeyListener);
        Password.addKeyListener(enterKeyListener);
        Email.addKeyListener(enterKeyListener);
        ConfirmPassword.addKeyListener(enterKeyListener);


        passwordStrengthStatus = new PasswordStrengthStatus();


        JPanel panel = new JPanel(new MigLayout("wrap,fillx,insets 35 45 30 45", "[fill,360]"));
        panel.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:20;" +
                "[light]background:darken(@background,3%);" +
                "[dark]background:lighten(@background,3%)");


        FirstName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "First name");
        LastName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Last name");
        Email.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your email");
        Username.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your username");
        Password.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your password");
        ConfirmPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Re-enter your password");
        Password.putClientProperty(FlatClientProperties.STYLE, "" +
                "showRevealButton:true");
        ConfirmPassword.putClientProperty(FlatClientProperties.STYLE, "" +
                "showRevealButton:true");


        registerBtn.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,10%);" +
                "[dark]background:lighten(@background,10%);" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0");


        JLabel title = new JLabel("Welcome to EOM Course Recommender");
        JLabel description = new JLabel("Discover the perfect courses for your high school journey with our personalized recommendations—join us today!");
        title.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +10;" +
                "[light]foreground:@earlYellow;" +
                "[dark]foreground:darken(@earlYellow,10%);");
        description.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]foreground:lighten(@foreground,30%);" +
                "[dark]foreground:darken(@foreground,30%)");


        passwordStrengthStatus.initPasswordField(Password);


        panel.add(title);
        panel.add(description);
        panel.add(new JLabel("Full Name"), "gapy 10");
        panel.add(FirstName, "split 2");
        panel.add(LastName);
        panel.add(new JSeparator(), "gapy 5 5");
        panel.add(new JLabel("Email"));
        panel.add(Email);
        panel.add(new JLabel("Username"));
        panel.add(Username);
        panel.add(new JLabel("Password"), "gapy 8");
        panel.add(Password);
        panel.add(passwordStrengthStatus, "gapy 0");
        panel.add(new JLabel("Confirm Password"), "gapy 0");
        panel.add(ConfirmPassword);
        panel.add(registerBtn, "gapy 20");
        panel.add(createLoginLabel(), "gapy 10");
        add(panel);
    }


    private void handleRegister() {
        if (!isFilled()) {
            NotificationManager.showNotification(NotificationManager.NotificationType.WARNING, "Please ensure all required fields are completed");
            if ((Username.getText().length() < 5)) {
                NotificationManager.showNotification(NotificationManager.NotificationType.WARNING, "Username must be at least 5 characters long");
            }
        } else if (!isMatchPassword()) {
            // Check if passwords do not match
            NotificationManager.showNotification(NotificationManager.NotificationType.WARNING, "Passwords do not match. Please try again");
        } else if (!MethodUtil.checkEmailAddress(Email.getText())) {
            NotificationManager.showNotification(NotificationManager.NotificationType.WARNING, "Enter a valid email address");

        } else {
            // Check the password strength
            int passwordStrength = MethodUtil.checkPasswordStrength(String.valueOf(Password.getPassword()));
            if (passwordStrength < 3) {
                NotificationManager.showNotification(NotificationManager.NotificationType.WARNING, "Password needs to be stronge");

            } else {
                // If all fields are filled, passwords match, and password strength is adequate, proceed with account creation
                String username = Username.getText();
                String password = String.valueOf(Password.getPassword());
                String firstName = FirstName.getText();
                String lastName = LastName.getText();
                String email = Email.getText();
                CreateAccount createAccount = new CreateAccount();
                CreateAccount.AccountCreationStatus status = createAccount.createAccount(username, password, email, firstName, lastName);


                if (status == CreateAccount.AccountCreationStatus.USERNAME_TAKEN) {
                    NotificationManager.showNotification(NotificationManager.NotificationType.ERROR, "This username is already taken. Please select a different username");

                } else if (status == CreateAccount.AccountCreationStatus.EMAIL_TAKEN) {
                    NotificationManager.showNotification(NotificationManager.NotificationType.ERROR, "This email is already being used. Please select a different email");

                } else {
                    NotificationManager.showNotification(NotificationManager.NotificationType.SUCCESS, "Account Created");


                    FormsManager.getInstance().showForm(new Login());
                }
            }
        }
    }


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
            FormsManager.getInstance().showForm(new Login());
        });
        JLabel label = new JLabel("Already have an account ?");
        label.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]foreground:lighten(@foreground,30%);" +
                "[dark]foreground:darken(@foreground,30%)");
        panel.add(label);
        panel.add(cmdLogin);
        return panel;
    }


    public boolean isMatchPassword() {
        String password = String.valueOf(Password.getPassword());
        String confirmPassword = String.valueOf(ConfirmPassword.getPassword());
        return password.equals(confirmPassword);
    }


    public boolean isFilled() {
        // Get the text from each field
        String username = Username.getText();
        String password = Password.getText();
        String firstName = FirstName.getText();
        String lastName = LastName.getText();
        String email = Email.getText();


        // Check if any of the fields are empty
        return !username.trim().isEmpty() &&
                username.length() > 5 &&
                !password.trim().isEmpty() &&
                !firstName.trim().isEmpty() &&
                !lastName.trim().isEmpty() &&
                !email.trim().isEmpty();
    }


    private void setMaximumLength(JTextField textField, int maxLength) {
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                if (fb.getDocument().getLength() + string.length() > maxLength) {
                    NotificationManager.showNotification(NotificationManager.NotificationType.WARNING, "Username must be less than 30 characters");
                } else {
                    super.insertString(fb, offset, string, attr);
                }
            }


            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) return;
                if (fb.getDocument().getLength() - length + text.length() > maxLength) {
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






    private JTextField FirstName;
    private JTextField LastName;
    private JTextField Username;
    private JPasswordField Password;
    private JPasswordField ConfirmPassword;
    private JButton registerBtn;
    private PasswordStrengthStatus passwordStrengthStatus;
    private JTextField Email;
}