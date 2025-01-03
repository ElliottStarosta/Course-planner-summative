package org.example.gui.pages.login;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import org.example.gui.manager.NotificationManager;
import org.example.gui.component.account.TwoFactorAuthentication;
import org.example.gui.manager.FormsManager;
import org.example.gui.pages.Application;
import org.example.utility.api.email.EmailUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * This class represents the Forgot Password form UI, allowing users to reset their password by entering their email.
 */
public class ForgotPasswordForm extends JPanel {

    /**
     * Input field for the user to enter their email address.
     */
    private JTextField emailField;

    /**
     * Button to trigger the email sending process.
     */
    private JButton emailBtn;
    /**
     * JFrame reference
     */
    private JFrame frame = Application.getInstance();

    /**
     * Constructs the ForgotPasswordForm and initializes the UI components.
     */
    public ForgotPasswordForm() {
        frame.setMinimumSize(new Dimension(400, 400));
        init();
    }

    /**
     * Initializes the layout and components of the Forgot Password form.
     */
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
            boolean isEmailValid = EmailUtil.checkEmailAddress(email);
            boolean isEmailRegistered = EmailUtil.emailRegistered(email);

            if (!isEmailValid) {
                NotificationManager.showNotification(NotificationManager.NotificationType.WARNING, "Enter a valid email address");
                emailBtn.setEnabled(true);
            } else if (!isEmailRegistered) {
                NotificationManager.showNotification(NotificationManager.NotificationType.ERROR, "Email not in system, please create an account");
                emailBtn.setEnabled(true);
            } else {
                String username = EmailUtil.getUsernameWithEmail(email);

                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() {
                        String generatedCode = TwoFactorAuthentication.generateAndSendCode(email, username);
                        FormsManager.getInstance().showForm(new VerificationForm(generatedCode, emailField.getText(), false));
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

    /**
     * Creates the "Remember your Password?" section with a link to navigate to the login form.
     *
     * @return a component containing the "Remember your Password?" section.
     */
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
            FormsManager.getInstance().showForm(new LoginForm());
        });

        JLabel label = new JLabel("Remember your Password ?");
        label.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]foreground:lighten(@foreground,30%);" +
                "[dark]foreground:darken(@foreground,30%)");

        panel.add(label);
        panel.add(registerBtn);
        return panel;
    }
}