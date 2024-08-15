package org.example.gui.pages.main;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import org.example.gui.manager.FormsManager;
import org.example.gui.manager.DynamicFormLoader;
import org.example.people.User;
import org.example.utility.courses.Course;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;

public class DashboardForm extends JPanel {
    private int question = 0;
    private User user;
    private JPanel panel;
    private JLabel welcomeLabel;
    private JButton takeQuizButton;
    private HashMap<String, String> userResponses = new HashMap<>();
    private JButton settingsButton;

    private String username;

    public DashboardForm() {
        init();
    }

    public DashboardForm(String username) {
        this.username = username;
        init();
    }

    public DashboardForm(User user) {
        this.user = user;

        this.username = user.getUsername();

        userResponses.put("username", username);
        init();
    }

    private void init() {
        setLayout(new MigLayout("fill, insets 20","[center]","[center]"));

        // Panel for main content
        panel = new JPanel(new MigLayout("wrap,fillx,insets 40 45 40 45","fill, 250:280"));
        panel.setPreferredSize(new Dimension(600, 300));
        panel.setOpaque(false); // Make the panel background transparent
        panel.putClientProperty(FlatClientProperties.STYLE,
                "arc:20;" +
                        "[light]background:darken(@background,3%);" +
                        "[dark]background:lighten(@background,3%)");

        JLabel description = new JLabel("Please sign in below to access your account");
        description.putClientProperty(FlatClientProperties.STYLE,
                "[light]foreground:lighten(@foreground,30%);" +
                        "[dark]foreground:darken(@foreground,30%)");

        createTopWelcome(panel);

        JButton takeQuizButton = (JButton) createQuizButtonPanel();
        boolean hasRecommendations = Course.readRecommendedCoursesFromFile(username);
        if (!hasRecommendations) {
            panel.add(takeQuizButton, "gapy 50");
        } else {
            panel.add(createContentPanel(), "gapy 75");
        }

        add(panel);

        // Add KeyListener to the panel
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        };
        addKeyListener(enterKeyListener);
        takeQuizButton.addActionListener(e -> handleLogin());

        // FocusListener to automatically request focus
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                requestFocusInWindow();
            }

            @Override
            public void focusLost(FocusEvent e) {
                // No action needed on focus lost
            }
        });

        // Ensure the panel is focused
        requestFocusInWindow();
    }


    private Component createContentPanel() {
        JButton displayRecommendationsButton = new JButton("Display recommendations");

        displayRecommendationsButton.putClientProperty(FlatClientProperties.STYLE,
                "[light]background:darken(@background,10%);" +
                        "[dark]background:lighten(@background,10%);" +
                        "borderWidth:0;" +
                        "focusWidth:0;" +
                        "innerFocusWidth:0");

        displayRecommendationsButton.setPreferredSize(new Dimension(displayRecommendationsButton.getPreferredSize().width, 50));
        displayRecommendationsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return displayRecommendationsButton;
    }

    private void handleLogin() {
        question++;
        Object formInstance = DynamicFormLoader.loadForm(question, userResponses);
        if (formInstance != null) {
            FormsManager.getInstance().showForm((JComponent) formInstance);
//            FormsManager.getInstance().showForm((new Form5(userResponses, question)));
        }
    }

    private Component createQuizButtonPanel() {
        JButton takeQuizButton = new JButton("Take Recommendation Course Quiz");
        takeQuizButton.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,10%);" +
                "[dark]background:lighten(@background,10%);" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0;" +
                "font: bold +5");

        takeQuizButton.setPreferredSize(new Dimension(takeQuizButton.getPreferredSize().width, 50));
        takeQuizButton.setCursor(new Cursor(Cursor.HAND_CURSOR));


        return takeQuizButton;
    }


    private void createTopWelcome(JPanel panel) {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.putClientProperty(FlatClientProperties.STYLE, "background:null");


        welcomeLabel = new JLabel(String.format("Welcome back %s!", username));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);


        welcomeLabel.putClientProperty(FlatClientProperties.STYLE, "font: bold +15");
        topPanel.add(welcomeLabel, BorderLayout.CENTER);


        // Add the top panel to the center of the main panel
        panel.add(topPanel, "wrap, align center, gapy 45");
    }
}
