package org.example.gui.component.jcomponents;


import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class PasswordStrengthStatus extends JPanel {

    private JPasswordField passwordField;
    private DocumentListener documentListener;
    private JLabel label;
    private int type;

    public PasswordStrengthStatus() {
        init();
    }

    private void init() {
        putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null");
        setLayout(new MigLayout("fill,insets 0", "3[100,fill,grow0][]", "[fill,grow 0]"));
        label = new JLabel("none");
        label.setVisible(false);
        add(new LabelStatus());
        add(label);
    }

    private Color getStrengthColor(int type) {
        if (type == 1) {
            return Color.decode("#FF4D4D");
        } else if (type == 2) {
            return Color.decode("#FFB04D");
        } else {
            return Color.decode("#16a34a");
        }
    }

    private void checkPassword(String password) {
        this.type = password.isEmpty() ? 0 : checkPasswordStrength(password);
        if (type == 0) {
            label.setText("none");
            label.setVisible(false);
        } else {
            label.setVisible(true);
            if (type == 1) {
                label.setText("Weak: Please make your password stronger.");
            } else if (type == 2) {
                label.setText("Moderate: Consider adding more complexity.");
            } else {
                label.setText("Strong: Your password is secure.");
            }
            label.setForeground(getStrengthColor(type));
        }
        repaint();
    }

    public void initPasswordField(JPasswordField txt) {
        if (documentListener == null) {
            documentListener = new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    checkPassword(String.valueOf(txt.getPassword()));
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    checkPassword(String.valueOf(txt.getPassword()));
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    checkPassword(String.valueOf(txt.getPassword()));
                }
            };
        }
        if (passwordField != null) {
            passwordField.getDocument().removeDocumentListener(documentListener);
        }
        txt.getDocument().addDocumentListener(documentListener);
        passwordField = txt;
    }

    private class LabelStatus extends JLabel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int width = getWidth();
            int height = getHeight();
            int size = (int) (height * 0.3f);
            Graphics2D g2 = (Graphics2D) g.create();
            FlatUIUtils.setRenderingHints(g2);
            int gap = UIScale.scale(5);
            int w = (width - gap * 2) / 3;
            int y = (height - size) / 2;
            Color disableColor = Color.decode(FlatLaf.isLafDark() ? "#404040" : "#CECECE");
            if (type >= 1) {
                g2.setColor(getStrengthColor(1));
            } else {
                g2.setColor(disableColor);
            }
            FlatUIUtils.paintComponentBackground(g2, 0, y, w, size, 0, 999);
            if (type >= 2) {
                g2.setColor(getStrengthColor(2));
            } else {
                g2.setColor(disableColor);
            }
            FlatUIUtils.paintComponentBackground(g2, w + gap, y, w, size, 0, 999);
            if (type >= 3) {
                g2.setColor(getStrengthColor(3));
            } else {
                g2.setColor(disableColor);
            }
            FlatUIUtils.paintComponentBackground(g2, (w + gap) * 2, y, w, size, 0, 999);
            g2.dispose();
        }
    }

    public static int checkPasswordStrength(String password) {
        int score = 0; // Initialize score to 0

        // Check if the password length is at least 8 characters
        if (password.length() >= 8) {
            score++; // Increment score if the length criterion is met
        }

        // Check if the password contains at least one uppercase letter
        boolean hasUppercase = !password.equals(password.toLowerCase());
        if (hasUppercase) {
            score++; // Increment score if an uppercase letter is present
        }

        // Check if the password contains at least one lowercase letter
        boolean hasLowercase = !password.equals(password.toUpperCase());
        if (hasLowercase) {
            score++; // Increment score if a lowercase letter is present
        }

        // Check if the password contains at least one digit
        boolean hasDigit = password.matches(".*\\d.*");
        if (hasDigit) {
            score++; // Increment score if a digit is present
        }

        // Check if the password contains at least one special character
        boolean hasSpecialChar = !password.matches("[A-Za-z0-9]*");
        if (hasSpecialChar) {
            score++; // Increment score if a special character is present
        }

        // Determine the strength of the password based on the final score
        if (score < 3) {
            return 1; // Weak password
        } else if (score < 5) {
            return 2; // Moderate password
        } else {
            return 3; // Strong password
        }
    }
}