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

/**
 * {@code PasswordStrengthStatus} is a custom {@link JPanel} that visually displays
 * the strength of a password entered in a {@link JPasswordField}.
 * It provides feedback on the password's strength by displaying a colored status bar
 * and a corresponding label indicating whether the password is weak, moderate, or strong.
 */
public class PasswordStrengthStatus extends JPanel {

    /**
     * The JPasswordField where the user inputs their password. This field is monitored to track changes
     * in the password for updating the strength status.
     */
    private JPasswordField passwordField;

    /**
     * A DocumentListener attached to the password field to detect changes in the password input.
     * It listens for insertions, deletions, or changes in the password field's content.
     */
    private DocumentListener documentListener;

    /**
     * A JLabel that displays the password strength status (e.g., weak, moderate, or strong).
     * It is dynamically updated as the password field content changes.
     */
    private JLabel label;

    /**
     * An integer that represents the current strength of the password.
     * The value is 1 for weak, 2 for moderate, and 3 for strong.
     */
    private int type;

    /**
     * Initializes the {@code PasswordStrengthStatus} panel.
     * Sets the layout, client properties, and label for password strength feedback.
     */
    public PasswordStrengthStatus() {
        init();
    }

    /**
     * Initializes the layout and components of the password strength status panel.
     * Configures the label to show the password strength status and sets up the layout.
     */
    private void init() {
        putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null");
        setLayout(new MigLayout("fill,insets 0", "3[100,fill,grow0][]", "[fill,grow 0]"));
        label = new JLabel("none");
        label.setVisible(false);
        add(new LabelStatus());
        add(label);
    }

    /**
     * Returns the color corresponding to the password strength type.
     *
     * @param type The strength type (1 for weak, 2 for moderate, 3 for strong).
     * @return The {@link Color} representing the strength level.
     */
    private Color getStrengthColor(int type) {
        if (type == 1) {
            return Color.decode("#FF4D4D"); // Weak
        } else if (type == 2) {
            return Color.decode("#FFB04D"); // Moderate
        } else {
            return Color.decode("#16a34a"); // Strong
        }
    }

    /**
     * Checks the password strength and updates the label and color accordingly.
     *
     * @param password The password to check for strength.
     */
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

    /**
     * Initializes the password field for monitoring password changes.
     * Sets up a document listener to track changes in the password field.
     *
     * @param txt The {@link JPasswordField} to monitor for password changes.
     */
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

    /**
     * A custom {@link JLabel} subclass that visually represents the password strength
     * with a color-coded bar indicating the strength of the password.
     */
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

            // Draw color blocks based on password strength
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

    /**
     * Checks the strength of a password and returns a strength score.
     *
     * @param password The password to evaluate.
     * @return An integer representing the password strength (1 = weak, 2 = moderate, 3 = strong).
     */
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