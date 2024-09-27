package org.example.gui.pages.login;


import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import org.example.gui.component.MethodUtil;
import org.example.gui.manager.NotificationManager;
import org.example.gui.component.account.TwoFactorAuthentication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class VerificationForm extends JPanel {
    private final String generatedCode;
    private JTextField[] codeFields;
    private JLabel title;
    private JButton submitBtn;
    private String email;

    private boolean is2FALogin;

    public VerificationForm(String generatedCode, String email, boolean is2FALogin) {
        this.generatedCode = generatedCode;
        this.email = email;
        this.is2FALogin = is2FALogin;
        init();
    }

    private void init() {
        setLayout(new MigLayout("fill, insets 20", "[center]", "[center]"));

        JPanel panel = new JPanel(new MigLayout("wrap, fillx, insets 35 45 30 45", "[fill, 360]"));
        panel.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:20;" +
                "[light]background:darken(@background,3%);" +
                "[dark]background:lighten(@background,3%)");

        title = new JLabel("Please Enter Your Verification Code Below");
        title.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +10;" +
                "[light]foreground:@earlYellow;" +
                "[dark]foreground:darken(@earlYellow,10%);");

        panel.add(title);
        panel.add(new JSeparator(), "gapy 5 5");

        codeFields = new JTextField[6];

        // Create a panel with a MigLayout for the code fields
        JPanel codePanel = new JPanel(new GridLayout(1, 6, 5, 0));
        codePanel.putClientProperty(FlatClientProperties.STYLE,
                "arc:20;" +
                        "[light]background:darken(@background,3%);" +
                        "[dark]background:lighten(@background,3%)");

        for (int i = 0; i < 6; i++) {
            final int index = i;
            codeFields[i] = new JTextField(5);
            codeFields[i].setMinimumSize(new Dimension(20, 65));
            codeFields[i].setHorizontalAlignment(JTextField.CENTER);
            codeFields[i].putClientProperty(FlatClientProperties.STYLE, "" + "font: bold +10");

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
                            MethodUtil.isCorrectCode(codeFields,generatedCode, email,is2FALogin);
                        } else {
                            NotificationManager.showNotification(NotificationManager.NotificationType.WARNING, "Please ensure all required fields are completed");


                        }
                    }
                }
            });

            codePanel.add(codeFields[i]);
        }
        // Listen for paste events in the first text field
        codeFields[0].addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_V) {
                    String text = codeFields[0].getText();
                    if (text.length() == 6) {
                        for (int i = 0; i < 6; i++) {
                            codeFields[i].setText(text.substring(i, i + 1));
                        }
                        MethodUtil.isCorrectCode(codeFields,generatedCode, email, is2FALogin);
                        codeFields[5].requestFocus();

                    } else {
                        e.consume();
                        codeFields[0].setText("");
                    }
                }
            }
        });

        panel.add(codePanel, "gapy 25");

        submitBtn = new JButton("Verify Code");

        submitBtn.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,10%);" +
                "[dark]background:lighten(@background,10%);" +
                "borderWidth:0;" +
                "innerFocusWidth:0;" + "font: bold +2" );

        submitBtn.setPreferredSize(new Dimension(submitBtn.getPreferredSize().width, 45));
        submitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        submitBtn.addActionListener(e -> {
            boolean allFieldsFull = TwoFactorAuthentication.areFieldsFull(codeFields);

            if (allFieldsFull) {
                MethodUtil.isCorrectCode(codeFields,generatedCode, email,is2FALogin);
            } else {
                NotificationManager.showNotification(NotificationManager.NotificationType.WARNING, "Please ensure all required fields are completed");

            }
        });



        panel.add(submitBtn, "gapy 15");
        add(panel);
    }

}