package org.example.GUI.pages.main;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import org.example.GUI.manager.FormsManager;
import org.example.GUI.pages.Quiz.DynamicFormLoader;
import org.example.GUI.pages.Quiz.Form3;
import org.example.people.User;
import org.example.utility.Course;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;

public class MainPage extends JPanel {
    private int question = 0;
    private User user;
    private JPanel panel;
    private JLabel welcomeLabel;
    private JButton takeQuizButton;
    private HashMap<String, String> userResponses = new HashMap<>();
    private JButton settingsButton;

    public MainPage() {
        init();
    }

    public MainPage(User user) {
        this.user = user;
        init();
    }

    private void init() {
        setLayout(new BorderLayout());

        // Create and configure the settings button
        settingsButton = (JButton) createSettingsButton();

        // Panel for main content
        panel = new JPanel(new MigLayout("wrap,fillx,insets 35 45 30 45", "fill, 400:600"));
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
        boolean hasRecommendations = Course.readRecommendedCoursesFromFile(user.getUsername());
        if (!hasRecommendations) {
            panel.add(takeQuizButton, "gapy 40");
        } else {
            panel.add(createContentPanel(), "gapy 40");
        }

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(60, 0, 0, 0));


        // Create a container panel to manage the size of the main panel
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0,0));
        centerPanel.setOpaque(false); // Make it transparent
        centerPanel.setPreferredSize(new Dimension(300, 300));

        centerPanel.add(panel);

        wrapper.add(centerPanel, BorderLayout.CENTER);



        wrapper.setBorder(BorderFactory.createEmptyBorder(200, 0, 0, 0));

        add(wrapper, BorderLayout.CENTER);


        // Create a top-right panel to hold the settings button
        JPanel topRightPanel = new JPanel(new BorderLayout());
        topRightPanel.setOpaque(false); // Make it transparent
        topRightPanel.add(settingsButton, BorderLayout.EAST);
        topRightPanel.setPreferredSize(new Dimension(0, 40));

        // Add the top-right panel to the top of the MainPage
        add(topRightPanel, BorderLayout.NORTH);

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

    private Component createSettingsButton() {
        int iconSize = 35;
        ImageIcon icon = new ImageIcon(((new ImageIcon("src/main/resources/assets/hamburger.png")).getImage()).getScaledInstance(iconSize, iconSize, java.awt.Image.SCALE_SMOOTH));

        // Set up the button with the icon
        settingsButton = new JButton(icon);

        // Calculate the preferred size to include padding (20px on all sides)
        int padding = 20;
        settingsButton.setPreferredSize(new Dimension(iconSize + padding * 2, iconSize + padding * 2));

        // Set transparent border to create padding without affecting centering
        settingsButton.setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));

        // Set the icon's position to the center of the button
        settingsButton.setHorizontalAlignment(SwingConstants.CENTER);
        settingsButton.setVerticalAlignment(SwingConstants.CENTER);

//        // Other button settings
        settingsButton.setContentAreaFilled(false);
//        settingsButton.setBackground(Color.RED);
        settingsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        settingsButton.addActionListener(e -> FormsManager.getInstance().showForm(new SettingsForm()));

        return settingsButton;
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
            // Assuming FormsManager can handle form instances without a specific base class
            FormsManager.getInstance().showForm((JComponent) formInstance);
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

        welcomeLabel = new JLabel(String.format("Welcome back %s!", user.getUsername()));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        welcomeLabel.putClientProperty(FlatClientProperties.STYLE, "font: bold +15");
        topPanel.add(welcomeLabel, BorderLayout.CENTER);

        // Add the top panel to the center of the main panel
        panel.add(topPanel, "wrap, align center, gapy 20");
    }
}
