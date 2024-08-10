package org.example.GUI.pages;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import org.example.GUI.component.Button.RippleEffect;
import org.example.GUI.component.Button.SpecialButton;
import org.example.GUI.component.NotificationManager;
import org.example.GUI.manager.FormsManager;
import org.example.people.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class Login extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JCheckBox rememberMeCheck;


    private List<User> users;

    private static final String PROPERTIES_FILE = "src/main/resources/login.properties";

    public Login() {
        this.users = User.readUsersFromJson();
        init();
        loadSavedLoginInfo();
    }

    private void init() {
        setLayout(new MigLayout("fill, insets 20","[center]","[center]"));

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Login") {
            private final RippleEffect ripple = new RippleEffect(this);

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ripple.reder(g, getBounds());
            }
        };




        JPanel panel = new JPanel(new MigLayout("wrap,fillx,insets 35 45 30 45","fill, 250:280"));
        panel.setPreferredSize(new Dimension(450, 300));

        panel.putClientProperty(FlatClientProperties.STYLE,
                "arc:20;" +
                "[light]background:darken(@background,3%);" +
                "[dark]background:lighten(@background,3%)");

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
            FormsManager.getInstance().showForm(new ForgotPassword());
        });

        panel.add(rememberMeCheck, BorderLayout.WEST);
        panel.add(forgotBtn, BorderLayout.EAST);

        return panel;
    }

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
            FormsManager.getInstance().showForm(new Register());
        });
        JLabel label = new JLabel("Don't have an account ?");
        label.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]foreground:lighten(@foreground,30%);" +
                "[dark]foreground:darken(@foreground,30%)");
        panel.add(label);
        panel.add(registerBtn);
        return panel;
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = encodePassword(String.valueOf(passwordField.getPassword()).trim());


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

            // Create a SwingWorker to handle the delay without blocking the UI thread
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    // Introduce a delay
                    Thread.sleep(3000);
                    return null;
                }

                @Override
                protected void done() {
                    // Show the second notification
                    NotificationManager.showNotification(NotificationManager.NotificationType.SUCCESS, String.format("Welcome back %s!", user.getFirstName()));

                }
            };

            // Execute the SwingWorker
            worker.execute();

            FormsManager.getInstance().showForm(new MainPage());
        } else {
            NotificationManager.showNotification(NotificationManager.NotificationType.WARNING, "No user found. Check your credentials and try again");
        }
    }


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

    private void loadSavedLoginInfo() {
        Properties properties = new Properties();
        try (FileInputStream in = new FileInputStream(PROPERTIES_FILE)) {
            properties.load(in);
            String savedUsername = properties.getProperty("username");
            String savedPassword = properties.getProperty("password");
            if (savedUsername != null && savedPassword != null) {
                usernameField.setText(savedUsername);
                passwordField.setText(decodePassword(savedPassword));
                rememberMeCheck.setSelected(true);
            }
        } catch (IOException e) {
            // File may not exist or be readable
        }
    }

    private void clearLoginInfo() {
        File file = new File(PROPERTIES_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    private String encodePassword(String password) {
        return Base64.getEncoder().encodeToString(password.getBytes());
    }

    private String decodePassword(String encodedPassword) {
        return new String(Base64.getDecoder().decode(encodedPassword));
    }

}
