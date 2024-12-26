package org.example.gui.pages.main;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import org.example.gui.manager.DynamicFormLoader;
import org.example.gui.manager.FormsManager;
import org.example.gui.pages.login.LoginForm;
import org.example.people.Counselor;
import org.example.people.User;
import org.example.utility.JsonUtil;
import org.example.utility.api.APIClient;
import org.example.utility.courses.Course;
import org.example.utility.courses.CourseAssembly;
import org.example.utility.courses.ExcelUtility;
import org.example.utility.courses.JsonToPdfConverter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * DashboardForm represents the main dashboard interface for a user.
 * It displays user-specific content such as courses, recommendations, and quiz functionality.
 */
public class DashboardForm extends JPanel {

    /** Current question index for quiz functionality. */
    private int question = 0;

    /** The logged-in user. */
    private User user;

    /** Main content panel of the dashboard. */
    private JPanel panel;

    /** Welcome label displayed at the top of the dashboard. */
    private JLabel welcomeLabel;

    /** Button to initiate the quiz. */
    private JButton takeQuizButton;

    /** Panel containing action buttons. */
    private JPanel buttonPanel;

    /** User's responses stored as key-value pairs. */
    private HashMap<String, String> userResponses = new HashMap<>();

    /** Logout button for exiting the dashboard. */
    private JButton logoutBtn;

    /** Username of the logged-in user. */
    private String username;

    /** Full name of the logged-in user. */
    private String name;

    /** Matrix representation of recommended courses. */
    private String[][] data;

    /** ComboBox array for course name selection. */
    private JComboBox[] courseName;

    /** List of all available courses. */
    private final String[] courses = ExcelUtility.getAllCourseNames();

    /** Flag to determine if editing mode is active. */
    private boolean isEditing = false;

    /** Button to toggle edit mode. */
    private JButton editButton;

    /** Map to associate edit buttons with grade data. */
    private Map<JButton, String[]> gradeEditMap = new HashMap<>();

    /**
     * Constructs a DashboardForm with the given username and name.
     *
     * @param username the username of the user
     * @param name the full name of the user
     */
    public DashboardForm(String username, String name) {
        this.username = username;
        this.name = name;
        new CourseAssembly();
        init();
    }

    /**
     * Constructs a DashboardForm for the given user.
     *
     * @param user the User object representing the logged-in user
     */
    public DashboardForm(User user) {
        this.user = user;
        this.username = user.getUsername();
        this.name = user.getFirstName();
        userResponses.put("username", username);
        new CourseAssembly();
        init();
    }

    /**
     * Initializes the layout and components of the dashboard.
     */
    private void init() {
        setLayout(new BorderLayout());

        logoutBtn = (JButton) createLogoutButton();
        boolean hasRecommendations = Course.readRecommendedCoursesFromFile(username);

        panel = new JPanel(new MigLayout("wrap,fillx,insets 35 45 30 45", "fill, 400:600"));
        panel.setOpaque(false);
        panel.putClientProperty(FlatClientProperties.STYLE,
                "arc:20;" +
                        "[light]background:darken(@background,3%);" +
                        "[dark]background:lighten(@background,3%)");

        createTopWelcome(panel, hasRecommendations);
        JButton takeQuizButton = (JButton) createQuizButtonPanel();

        if (!hasRecommendations) {
            APIClient.runAPI();
            panel.add(takeQuizButton, "gapy 40");
        } else {
            data = JsonUtil.readRecommendedCoursesToMatrix(this.username);
            for (String[] gradeData : data) {
                panel.add(createGradePanel(gradeData), "gapy 10");
            }

            buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setOpaque(false);
            buttonPanel.add(additionalButtons());

            panel.add(buttonPanel, "align right");
        }

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        if (!hasRecommendations) {
            gbc.insets = new Insets(-150, 0, 0, 0);
        } else {
            gbc.insets = new Insets(-45, 0, 0, 0);
        }

        wrapper.add(panel, gbc);
        add(wrapper, BorderLayout.CENTER);

        JPanel topRightPanel = new JPanel(new BorderLayout());
        topRightPanel.setOpaque(false);
        topRightPanel.add(logoutBtn, BorderLayout.EAST);
        topRightPanel.setPreferredSize(new Dimension(0, 40));

        JPanel wrapperTopPanel = new JPanel(new BorderLayout());
        wrapperTopPanel.setOpaque(false);
        wrapperTopPanel.setBorder(new EmptyBorder(15, 0, 0, 20));
        wrapperTopPanel.add(topRightPanel, BorderLayout.CENTER);
        add(wrapperTopPanel, BorderLayout.NORTH);

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

        requestFocusInWindow();

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int parentWidth = getWidth();

                if (!hasRecommendations) {
                    int panelWidth = parentWidth - 300;
                    panel.setPreferredSize(new Dimension(panelWidth, 250));
                } else {
                    int panelWidth = parentWidth - 40;
                    panel.setPreferredSize(new Dimension(panelWidth, 742));
                }
                panel.revalidate();
                panel.repaint();
            }
        });
    }

    /**
     * Creates the logout button.
     *
     * @return the logout button component
     */
    private Component createLogoutButton() {
        logoutBtn = new JButton("Logout");
        logoutBtn.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,10%);" +
                "[dark]background:lighten(@background,10%);" +
                "borderWidth:0;" +
                "innerFocusWidth:0;" + "font: bold +2" );

        logoutBtn.setPreferredSize(new Dimension(logoutBtn.getPreferredSize().width, 45));
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> FormsManager.getInstance().showForm(new LoginForm()));
        return logoutBtn;
    }
    /**
     * Creates a grade panel for the given grade data.
     *
     * @param gradeData the grade data to display
     * @return the panel displaying the grade data
     */
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

        int grade = Integer.parseInt(gradeData[0].replaceAll("\\D", ""));

        String[] gradeCourses = filterCoursesByGrade(courses, grade);

        for (int i = 0; i < 8; i++) {
            courseName[i] = new JComboBox<>(gradeCourses);
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
        editButton.putClientProperty(FlatClientProperties.STYLE,
                "arc:5;" +
                        "[light]background:darken(@background,3%);" +
                        "[dark]background:lighten(@background,25%)");
        editButton.addActionListener(new EditButtonListener(gradeData, courseName, editButton, username, data));
        editButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        gradePanel.add(editButton,"gapy 10, gapx 5");
        gradeEditMap.put(editButton, gradeData);

        return gradePanel;
    }


    /**
     * Filters courses by grade level.
     *
     * @param courses the array of course strings
     * @param targetGrade the target grade level to filter by
     * @return the array of filtered course strings
     */
    public String[] filterCoursesByGrade(String[] courses, int targetGrade) {

        // Use streams to filter and map courses
        return Arrays.stream(courses)
                .map(courseString -> courseString.split(" - ")[0]) // Extract course code
                .map(CourseAssembly::getCourse) // Convert code to Course object
                .filter(course -> course != null && course.getGradeLevel() == targetGrade) // Filter by grade
                .map(course -> course.getCourseCode() + " - " + course.getCourseName()) // Format the course string
                .toArray(String[]::new); // Collect results into an array
    }


    private Component additionalButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT,5,0));

        panel.setOpaque(false);

        JButton saveBtn = new JButton("Export as PDF");
        JButton sendCounselorBtn = new JButton("Send to Counselor");


        saveBtn.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,10%);" +
                "[dark]background:lighten(@background,10%);" +
                "borderWidth:0;" +
                "innerFocusWidth:0;" +
                "arc: 10;" +
                "font: bold +2");

        sendCounselorBtn.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,10%);" +
                "[dark]background:lighten(@background,10%);" +
                "borderWidth:0;" +
                "innerFocusWidth:0;" +
                "arc: 10;"
                + "font: bold +2");


        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendCounselorBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Dimension saveBtnSize = saveBtn.getPreferredSize();
        Dimension sendCounselorBtnSize = sendCounselorBtn.getPreferredSize();

        saveBtn.setPreferredSize(new Dimension(saveBtnSize.width + 30, saveBtnSize.height + 10));
        sendCounselorBtn.setPreferredSize(new Dimension(sendCounselorBtnSize.width + 30, sendCounselorBtnSize.height + 10));



        panel.add(saveBtn);
        panel.add(sendCounselorBtn);

        sendCounselorBtn.addActionListener(e -> Counselor.sendCounselorEmail(username));

        saveBtn.addActionListener(e -> {
            try {
                JsonToPdfConverter.convertJsonToPdf(name,username);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        return panel;

    }

    /**
     * Method when login button is triggered
     */
    private void handleLogin() {
        question++;
        Object formInstance = DynamicFormLoader.loadForm(question, userResponses);
        if (formInstance != null) {
            FormsManager.getInstance().showForm((JComponent) formInstance);
//            FormsManager.getInstance().showForm((new Form5(userResponses, question)));
        }
    }

    /**
     *
     * @return the component for displaying the take quiz button
     */
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

    /**
     * Creates the welcome sign on the dashboard
     *
     * @param panel
     * @param hasRecs
     */
    private void createTopWelcome(JPanel panel, boolean hasRecs) {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.putClientProperty(FlatClientProperties.STYLE, "background:null");


        welcomeLabel = (hasRecs) ? new JLabel(String.format("Welcome back %s!", username)) :  new JLabel(String.format("Welcome to EOM Course Recommender, %s!", username));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        welcomeLabel.putClientProperty(FlatClientProperties.STYLE, "font: bold +15");
        topPanel.add(welcomeLabel, BorderLayout.CENTER);

        // Add the top panel to the center of the main panel
        panel.add(topPanel, "wrap, align center, gapy 20");
    }
}