package org.example.gui.pages.login;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import org.example.gui.manager.NotificationManager;
import org.example.gui.component.account.TwoFactorAuthentication;
import org.example.gui.pages.Application;
import org.example.utility.EncryptionUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * A panel that represents the verification form for entering a 6-digit verification code.
 * It allows users to input the code, submit it for verification, and handles the behavior
 * for navigating through the fields using the keyboard.
 */
public class VerificationForm extends JPanel {
    /**
     * The generated verification code that the user must enter to verify their identity.
     */
    private final String generatedCode;

    /**
     * Array of JTextFields to hold the 6 individual digits of the verification code input.
     */
    private JTextField[] codeFields;

    /**
     * JLabel to display the title or instruction for the verification process.
     */
    private JLabel title;

    /**
     * JButton that triggers the verification process when clicked.
     */
    private JButton submitBtn;

    /**
     * The email address associated with the verification process.
     */
    private String email;

    /**
     * Boolean flag to indicate whether the verification is part of a 2FA login process.
     */
    private boolean is2FALogin;

    /**
     * JFrame reference
     */
    private JFrame frame = Application.getInstance();

    /**
     * Constructor to initialize the verification form.
     *
     * @param generatedCode The generated verification code to be matched.
     * @param email The email associated with the account being verified.
     * @param is2FALogin A flag indicating whether this is part of a two-factor authentication login.
     */
    public VerificationForm(String generatedCode, String email, boolean is2FALogin) {
        frame.setMinimumSize(new Dimension(600, 350));
        this.generatedCode = generatedCode;
        this.email = email;
        this.is2FALogin = is2FALogin;
        init();
    }

    /**
     * Initializes the UI components and sets up the layout for the verification form.
     */
    private void init() {
        setLayout(new MigLayout("fill, insets 20", "[center]", "[center]"));

        // Create the main panel with styling and layout for the verification code input
        JPanel panel = new JPanel(new MigLayout("wrap, fillx, insets 35 45 30 45", "[fill, 360]"));
        panel.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:20;" +
                "[light]background:darken(@background,3%);" +
                "[dark]background:lighten(@background,3%)");

        // Title label for the verification code entry
        title = new JLabel("Please Enter Your Verification Code Below");
        title.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +10;" +
                "[light]foreground:@earlYellow;" +
                "[dark]foreground:darken(@earlYellow,10%);");

        panel.add(title);
        panel.add(new JSeparator(), "gapy 5 5");

        // Initialize an array to hold the code input fields
        codeFields = new JTextField[6];

        // Create a panel with a GridLayout to arrange code fields horizontally
        JPanel codePanel = new JPanel(new GridLayout(1, 6, 5, 0));
        codePanel.putClientProperty(FlatClientProperties.STYLE,
                "arc:20;" +
                        "[light]background:darken(@background,3%);" +
                        "[dark]background:lighten(@background,3%)");

        // Set up individual code input fields with custom behavior for key events
        for (int i = 0; i < 6; i++) {
            final int index = i;
            codeFields[i] = new JTextField(5);
            codeFields[i].setMinimumSize(new Dimension(20, 65));
            codeFields[i].setHorizontalAlignment(JTextField.CENTER);
            codeFields[i].putClientProperty(FlatClientProperties.STYLE, "" + "font: bold +10");

            // Key listener for handling typing, backspace, delete, and space key behavior
            codeFields[i].addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();
                    if (c == KeyEvent.VK_BACK_SPACE) {
                        if (index > 0) {
                            JTextField previousField = codeFields[index - 1];
                            if (codeFields[index].getText().isEmpty()) {
                                // Move focus to the previous field if the current one is empty
                                previousField.requestFocus();
                                String previousText = previousField.getText();
                                if (!previousText.isEmpty()) {
                                    previousField.setText(previousText.substring(0, previousText.length() - 1)); // Delete last character
                                }
                                e.consume(); // Consume the event to prevent further processing
                            }
                        }
                    } else if (c == KeyEvent.VK_DELETE) {
                        if (index < codeFields.length - 1) {
                            JTextField nextField = codeFields[index + 1];
                            if (codeFields[index].getText().isEmpty()) {
                                // Move focus to the next field if the current one is empty
                                nextField.requestFocus();
                                e.consume(); // Consume the event to prevent further processing
                            }
                        }
                    } else if (c == KeyEvent.VK_SPACE || codeFields[index].getText().length() > 0) {
                        e.consume(); // Consume the event to prevent entry of spaces or more than one character
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                        if (codeFields[index].getText().isEmpty() && index > 0) {
                            // Move focus to the previous field if current field is empty
                            codeFields[index].requestFocus();
                        }
                    } else if (index < codeFields.length - 1 && codeFields[index].getText().length() == 1) {
                        // Move focus to the next field if the current field is filled
                        codeFields[index + 1].requestFocus();
                    }
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        boolean allFieldsFull = TwoFactorAuthentication.areFieldsFull(codeFields);

                        if (allFieldsFull) {
                            EncryptionUtil.isCorrectCode(codeFields, generatedCode, email, is2FALogin);
                        } else {
                            NotificationManager.showNotification(NotificationManager.NotificationType.WARNING, "Please ensure all required fields are completed");
                        }
                    }
                }
            });

            codePanel.add(codeFields[i]);
        }

        // Listen for paste events in the first text field to handle pasting a full code
        codeFields[0].addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_V) {
                    String text = codeFields[0].getText();
                    if (text.length() == 6) {
                        for (int i = 0; i < 6; i++) {
                            codeFields[i].setText(text.substring(i, i + 1));
                        }
                        EncryptionUtil.isCorrectCode(codeFields, generatedCode, email, is2FALogin);
                        codeFields[5].requestFocus();
                    } else {
                        e.consume();
                        codeFields[0].setText("");
                    }
                }
            }
        });

        panel.add(codePanel, "gapy 25");

        // Submit button to verify the code
        submitBtn = new JButton("Verify Code");
        submitBtn.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,10%);" +
                "[dark]background:lighten(@background,10%);" +
                "borderWidth:0;" +
                "innerFocusWidth:0;" + "font: bold +2");

        submitBtn.setPreferredSize(new Dimension(submitBtn.getPreferredSize().width, 45));
        submitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Action listener for the submit button to verify the entered code
        submitBtn.addActionListener(e -> {
            boolean allFieldsFull = TwoFactorAuthentication.areFieldsFull(codeFields);

            if (allFieldsFull) {
                EncryptionUtil.isCorrectCode(codeFields, generatedCode, email, is2FALogin);
            } else {
                NotificationManager.showNotification(NotificationManager.NotificationType.WARNING, "Please ensure all required fields are completed");
            }
        });

        panel.add(submitBtn, "gapy 15");
        add(panel);
    }
}