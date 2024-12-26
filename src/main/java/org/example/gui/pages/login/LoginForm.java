package org.example.gui.pages.login;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import org.example.utility.EncryptionUtil;
import org.example.gui.manager.NotificationManager;
import org.example.gui.manager.FormsManager;
import org.example.gui.pages.main.DashboardForm;
import org.example.people.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * Represents the login form in the GUI application.
 * This form handles user authentication, saving login information, and navigation to the dashboard upon successful login.
 */
public class LoginForm extends JPanel {

    /**
     * Field to capture the user's input for their username or email.
     */
    private JTextField usernameField;

    /**
     * Field to capture the user's input for their password.
     */
    private JPasswordField passwordField;

    /**
     * Button for initiating the login process.
     */
    private JButton loginButton;

    /**
     * Checkbox for remembering the user's login information.
     */
    private JCheckBox rememberMeCheck;

    /**
     * List of users available for authentication.
     */
    private List<User> users;

    /**
     * Path to the properties file used for saving login information.
     */
    private static final String PROPERTIES_FILE = "src/main/resources/login.properties";

    /**
     * Constructs a new LoginForm and initializes the user interface components.
     */
    public LoginForm() {
        this.users = User.readUsersFromJson();
        init();
        loadSavedLoginInfo();
    }

    /**
     * Initializes the layout and components of the login form.
     */
    private void init() {
        setLayout(new MigLayout("fill, insets 20","[center]","[center]"));

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Login");

        JPanel panel = new JPanel(new MigLayout("wrap,fillx,insets 35 45 30 45","fill, 250:280"));
        panel.setPreferredSize(new Dimension(450, 300));

        panel.putClientProperty(FlatClientProperties.STYLE,
                "arc:20;" +
                        "[light]background:darken(@background,3%);" +
                        "[dark]background:lighten(@background,3%)");

        passwordField.putClientProperty(FlatClientProperties.STYLE,
                "showRevealButton:true");

        usernameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your email or username");
        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your password");

        loginButton.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,10%);" +
                "[dark]background:lighten(@background,10%);" +
                "borderWidth:0;" +
                "innerFocusWidth:0;");

        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel title = new JLabel("Welcome Back Lion!");
        JLabel description = new JLabel("Please sign in below to access your account");
        title.putClientProperty(FlatClientProperties.STYLE,
                "font:bold +10;" +
                        "[light]foreground:@accentColor;" +
                        "[dark]foreground:darken(@earlYellow,10%)"
        );
        description.putClientProperty(FlatClientProperties.STYLE,
                "[light]foreground:lighten(@foreground,30%);" +
                        "[dark]foreground:darken(@foreground,30%)");

        panel.add(title);
        panel.add(description);
        panel.add(new JLabel("Email or username"), "gapy 8");
        panel.add(usernameField);
        panel.add(new JLabel("Password"), "gapy 8");
        panel.add(passwordField);
        panel.add(rememberAndForgot(), "gapy 10");
        panel.add(loginButton, "gapy 15");
        panel.add(createSignUp(), "gapy 10");
        add(panel);

        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        };

        usernameField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);
        loginButton.addActionListener(e -> handleLogin());
    }

    /**
     * Creates the "Remember Me" checkbox and "Forgot Password" link components.
     *
     * @return a component containing the checkbox and link.
     */
    private Component rememberAndForgot() {
        JPanel panel = new JPanel(new BorderLayout());

        rememberMeCheck = new JCheckBox("Remember me");
        rememberMeCheck.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panel.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null");
        JButton forgotBtn = new JButton("<html><a href=\"#\">Forgot Password?</a></html>");
        forgotBtn.putClientProperty(FlatClientProperties.STYLE, "" +
                "border:3,3,3,5");
        forgotBtn.setContentAreaFilled(false);
        forgotBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotBtn.addActionListener(e -> {
            FormsManager.getInstance().showForm(new ForgotPasswordForm());
        });

        panel.add(rememberMeCheck, BorderLayout.WEST);
        panel.add(forgotBtn, BorderLayout.EAST);

        return panel;
    }

    /**
     * Creates the "Sign Up" link for new users.
     *
     * @return a component containing the "Sign Up" link.
     */
    private Component createSignUp() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null");
        JButton registerBtn = new JButton("<html><a href=\"#\">Create Account</a></html>");
        registerBtn.putClientProperty(FlatClientProperties.STYLE, "" +
                "border:3,5,3,3");
        registerBtn.setContentAreaFilled(false);
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerBtn.addActionListener(e -> {
            FormsManager.getInstance().showForm(new RegisterForm());
        });
        JLabel label = new JLabel("Don't have an account ?");
        label.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]foreground:lighten(@foreground,30%);" +
                "[dark]foreground:darken(@foreground,30%)");
        panel.add(label);
        panel.add(registerBtn);
        return panel;
    }

    /**
     * Handles the login action by validating user credentials and navigating to the dashboard if successful.
     */
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = EncryptionUtil.encodePassword(String.valueOf(passwordField.getPassword()).trim());

        if (username.isEmpty() || password.isEmpty()) {
            NotificationManager.showNotification(NotificationManager.NotificationType.WARNING, "Please ensure all fields are completed");
            return;
        }

        Optional<User> foundUser = users.stream()
                .filter(user -> (user.getUsername().equals(username) && user.getPassword().equals(password)) || user.getEmail().equals(username) && user.getPassword().equals(password))
                .findFirst();

        boolean userFound = foundUser.isPresent();
        User user = foundUser.orElse(null);

        if (userFound) {
            if (rememberMeCheck.isSelected()) {
                saveLoginInfo(username, password);
            } else {
                clearLoginInfo();
            }
            NotificationManager.showNotification(NotificationManager.NotificationType.SUCCESS, "Login successful");

            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    Thread.sleep(3000);
                    return null;
                }
            };

            worker.execute();
            FormsManager.getInstance().showForm(new DashboardForm(user));
        } else {
            NotificationManager.showNotification(NotificationManager.NotificationType.WARNING, "No user found. Check your credentials and try again");
        }
    }

    /**
     * Saves login information to a properties file if "Remember Me" is selected.
     *
     * @param username the username to save.
     * @param password the encrypted password to save.
     */
    private void saveLoginInfo(String username, String password) {
        Properties properties = new Properties();
        try (FileOutputStream out = new FileOutputStream(PROPERTIES_FILE)) {
            properties.setProperty("username", username);
            properties.setProperty("password", password);
            properties.store(out, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads saved login information from a properties file if available.
     */
    private void loadSavedLoginInfo() {
        Properties properties = new Properties();
        try (FileInputStream in = new FileInputStream(PROPERTIES_FILE)) {
            properties.load(in);
            String savedUsername = properties.getProperty("username");
            String savedPassword = properties.getProperty("password");
            if (savedUsername != null && savedPassword != null) {
                usernameField.setText(savedUsername);
                passwordField.setText(EncryptionUtil.decodePassword(savedPassword));
                rememberMeCheck.setSelected(true);
            }
        } catch (IOException e) {
            // File may not exist or be readable
        }
    }

    /**
     * Clears saved login information from the properties file.
     */
    private void clearLoginInfo() {
        File file = new File(PROPERTIES_FILE);
        if (file.exists()) {
            file.delete();
        }
    }
}