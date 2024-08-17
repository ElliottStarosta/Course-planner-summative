package org.example.gui.pages.main;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import org.example.gui.component.MethodUtil;
import org.example.gui.manager.FormsManager;
import org.example.gui.manager.DynamicFormLoader;
import org.example.people.User;
import org.example.utility.api.APIClient;
import org.example.utility.courses.Course;
import org.example.utility.courses.ExcelUtility;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;


public class DashboardForm extends JPanel {
    private int question = 0;
    private User user;
    private JPanel panel;
    private JLabel welcomeLabel;
    private JButton takeQuizButton;
    private HashMap<String, String> userResponses = new HashMap<>();
    private JButton settingsButton;

    private String username;
    private String[][] data;

    private JComboBox[] courseName;

    private final String[] courses = ExcelUtility.getAllCourseNames();

    private boolean isEditing = false;
    private JButton editButton;
    private Map<JButton, String[]> gradeEditMap = new HashMap<>();


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
//        panel.setBackground(Color.RED);



        JLabel description = new JLabel("Please sign in below to access your account");
        description.putClientProperty(FlatClientProperties.STYLE,
                "[light]foreground:lighten(@foreground,30%);" +
                        "[dark]foreground:darken(@foreground,30%)");

        createTopWelcome(panel);

        JButton takeQuizButton = (JButton) createQuizButtonPanel();
        boolean hasRecommendations = Course.readRecommendedCoursesFromFile(username);
        if (!hasRecommendations) {
            APIClient.deployAPI();
            panel.add(takeQuizButton, "gapy 40");
        } else {
            data = MethodUtil.readRecommendedCoursesToMatrix(this.username);
            for (String[] gradeData : data) {
                panel.add(createGradePanel(gradeData),"gapy 10");
            }

            //TODO: add two buttons @ the button that say: Send to counselors and save as PDP
        }

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));


        // Create a container panel to manage the size of the main panel
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0,0));
        centerPanel.setOpaque(false); // Make it transparent
        centerPanel.setPreferredSize(new Dimension(300, 300));

        centerPanel.add(panel);

        wrapper.add(centerPanel, BorderLayout.CENTER);

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

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int parentWidth = getWidth();
                int panelWidth = parentWidth - 40;
                panel.setPreferredSize(new Dimension(panelWidth, 700));
                panel.revalidate();
                panel.repaint();
            }
        });
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




    private JPanel createGradePanel(String[] gradeData) {
        JPanel gradePanel = new JPanel(new MigLayout("wrap 1", "[left]"));
        gradePanel.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:20;" +
                "[light]background:darken(@background,3%);" +
                "[dark]background:lighten(@background,3%)");

        JLabel gradeLabel = new JLabel(gradeData[0]);
        gradeLabel.putClientProperty(FlatClientProperties.STYLE, "font: bold +12;");
        gradePanel.setOpaque(false);
        gradePanel.add(gradeLabel);



        JPanel coursePanel = new JPanel(new GridLayout(1, 6, 10, 0));
        coursePanel.putClientProperty(FlatClientProperties.STYLE,
                "arc:20;" +
                        "[light]background:darken(@background,3%);" +
                        "[dark]background:lighten(@background,3%)");
        coursePanel.setOpaque(false);

        courseName = new JComboBox[8];

        for (int i = 0; i < 8; i++) {
            courseName[i] = new JComboBox<>(courses);
            courseName[i].setSelectedItem(gradeData[i + 1]);
            courseName[i].setMinimumSize(new Dimension(20, 30));
            courseName[i].setEnabled(false);
            courseName[i].setFont(new Font("Arial", Font.BOLD, 10));
            courseName[i].putClientProperty(FlatClientProperties.STYLE, "arc:0;");
            courseName[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            coursePanel.add(courseName[i]);
        }

        gradePanel.add(coursePanel);

        editButton = new JButton("Edit");
        editButton.addActionListener(new EditButtonListener(gradeData, courseName, editButton));
        editButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        gradePanel.add(editButton,"gapy 10, gapx 5");
        gradeEditMap.put(editButton, gradeData);

        return gradePanel;
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
        panel.add(topPanel, "wrap, align center, gapy 20");
    }
}