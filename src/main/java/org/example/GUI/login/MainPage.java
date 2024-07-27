package org.example.GUI.login;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import org.example.GUI.manager.FormsManager;

import javax.swing.*;
import java.awt.*;

public class MainPage extends JPanel {

    public MainPage() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("fill, insets 20","[center]","[center]"));

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("MainPage");

        JPanel panel = new JPanel(new MigLayout("wrap,fillx,insets 35 45 30 45","fill, 250:280"));

        panel.putClientProperty(FlatClientProperties.STYLE,
                "arc:20;" +
                        "[light]background:darken(@background,3%);" +
                        "[dark]background:lighten(@background,3%)");

        passwordField.putClientProperty(FlatClientProperties.STYLE,
                "showRevealButton:true");

        usernameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your username");
        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your password");

        loginButton.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,10%);" +
                "[dark]background:lighten(@background,10%);" +
                "borderWidth:0;" +
                "innerFocusWidth:0;");

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
        panel.add(new JLabel("Username"), "gapy 8");
        panel.add(usernameField);
        panel.add(new JLabel("Password"), "gapy 8");
        panel.add(passwordField);
        panel.add(rememberAndForgot(), "gapy 10");
        panel.add(loginButton, "gapy 15");
        panel.add(createSignUp(), "gapy 10");
        add(panel);

    }

    private Component rememberAndForgot() {
        JPanel panel = new JPanel(new BorderLayout());

        JCheckBox rememberMeCheck = new JCheckBox("Remember me");

        panel.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null");
        JButton forgotBtn = new JButton("<html><a href=\"#\">Forgot Password?</a></html>");
        forgotBtn.putClientProperty(FlatClientProperties.STYLE, "" +
                "border:3,3,3,3");
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
                "border:3,3,3,3");
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

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
}
