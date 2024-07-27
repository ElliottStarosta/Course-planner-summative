package org.example.GUI.login;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import org.example.GUI.component.MethodUtil;
import org.example.GUI.component.TwoFactorAuthentication;
import org.example.GUI.manager.FormsManager;
import raven.toast.Notifications;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;

public class ForgotPassword extends JPanel {
    public ForgotPassword() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("fill, insets 20", "[center]", "[center]"));

        JPanel panel = new JPanel(new MigLayout("wrap, fillx, insets 35 45 30 45", "fill, 250:280"));

        panel.putClientProperty(FlatClientProperties.STYLE,
                "arc:20;" +
                        "[light]background:darken(@background,3%);" +
                        "[dark]background:lighten(@background,3%)");

        JLabel title = new JLabel("Forgot your password?");
        JLabel description = new JLabel("Please enter your account email");
        JLabel emailtxt = new JLabel("Email Address");

        title.putClientProperty(FlatClientProperties.STYLE,
                "font:bold +10;" +
                        "[light]foreground:@accentColor;" +
                        "[dark]foreground:darken(@earlYellow,10%)");
        description.putClientProperty(FlatClientProperties.STYLE,
                "[light]foreground:lighten(@foreground,30%);" +
                        "[dark]foreground:darken(@foreground,30%)");
        emailtxt.putClientProperty(FlatClientProperties.STYLE,
                "[light]foreground:lighten(@foreground,30%);" +
                        "[dark]foreground:darken(@foreground,30%);" + "font:bold");

        emailField = new JTextField();
        emailField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your email");
        emailField.putClientProperty(FlatClientProperties.STYLE, "arc:5;");
        emailField.setPreferredSize(new Dimension(emailField.getPreferredSize().width, 45));


        emailBtn = new JButton("Send Email");
        emailBtn.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,10%);" +
                "[dark]background:lighten(@background,10%);" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0");
        emailBtn.setPreferredSize(new Dimension(emailBtn.getPreferredSize().width, 35));
        emailBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        emailBtn.addActionListener(e -> {
            emailBtn.setEnabled(false);

            String email = emailField.getText();
            boolean isEmailValid = MethodUtil.checkEmailAddress(email);
            boolean isEmailRegistered = MethodUtil.emailRegistered(email);

            if (!isEmailValid) {
                Notifications.getInstance().show(Notifications.Type.ERROR, "Enter a valid email address");
                emailBtn.setEnabled(true);
            } else if (!isEmailRegistered) {
                Notifications.getInstance().show(Notifications.Type.ERROR, "Email not in system, please create an account");
                emailBtn.setEnabled(true);
            } else {
                String username = MethodUtil.getUserNameWithEmail(email);
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Notifications.getInstance().show(Notifications.Type.ERROR, "Verification code expired");
                    }
                }, 600000);

                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        String generatedCode = TwoFactorAuthentication.generateAndSendCode(email, username);
                        Notifications.getInstance().show(Notifications.Type.SUCCESS, "Verification code sent");
                        FormsManager.getInstance().showForm(new Verification(generatedCode, emailField.getText()));
                        return null;
                    }
                };
                worker.execute();

            }
        });
        emailField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    emailBtn.doClick();
                }
            }

        });

        panel.add(title);
        panel.add(description);
        panel.add(emailtxt, "gapy 10");
        panel.add(emailField, "gapbottom 15, growx");
        panel.add(emailBtn, "center, gapbottom 15");
        panel.add(createRememberPassword(), "gapy 10");

        add(panel);
    }

    private Component createRememberPassword() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null");
        JButton registerBtn = new JButton("<html><a href=\"#\">Sign in</a></html>");
        registerBtn.putClientProperty(FlatClientProperties.STYLE, "" +
                "border:3,3,3,3");
        registerBtn.setContentAreaFilled(false);
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerBtn.addActionListener(e -> {
            FormsManager.getInstance().showForm(new Login());
        });
        JLabel label = new JLabel("Remember your Password ?");
        label.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]foreground:lighten(@foreground,30%);" +
                "[dark]foreground:darken(@foreground,30%)");
        panel.add(label);
        panel.add(registerBtn);
        return panel;
    }

    private JTextField emailField;
    private JButton emailBtn;
}
