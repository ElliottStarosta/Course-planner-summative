package org.example.gui.pages.quiz;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import org.example.gui.component.jcomponents.ComboBox;
import org.example.gui.manager.DynamicFormLoader;
import org.example.gui.manager.NotificationManager;
import org.example.gui.component.jcomponents.PageMenuIndicator;
import org.example.gui.manager.FormsManager;
import org.example.gui.pages.main.DashboardForm;
import org.example.people.UserInput;
import org.example.people.User;
import org.example.utility.courses.Course;
import org.example.utility.courses.CourseAssembly;
import org.example.utility.UsersUtil;

import javax.swing.*;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.example.utility.courses.ExcelUtility.getAllCourseNames;

/**
 * Form5 represents a panel that displays the fifth question -- selecting previous courses
 *
 * <p>The form provides buttons for navigation and handles user responses using checkboxes.
 * It uses a dynamic layout and integrates with other components like the PageMenuIndicator, NotificationManager, ComoboBox.</p>
 */
public class Form5 extends JPanel {

    /**
     * A HashMap to store the user's responses to the quiz questions.
     * The key is the question identifier, and the value is the user's response.
     */
    private HashMap<String, String> userResponses;

    /**
     * The current question number in the quiz. It helps track the progression of the form.
     */
    private int question;

    /**
     * The width of the panel where the quiz form will be displayed. This is used for layout purposes.
     */
    private static final int PANEL_WIDTH = 600;

    /**
     * A PageMenuIndicator component that shows the current page number of the quiz.
     * It helps users navigate through the form by visually indicating which page they are on.
     */
    private PageMenuIndicator indicator;

    /**
     * A JLabel to display the title of the current question.
     * The title will dynamically update based on the current question number.
     */
    private JLabel questionTitle;

    /**
     * A ComboBox component used to display the dropdown menu of courses.
     * This allows the user to select from a list of available courses.
     */
    private ComboBox courseComboBox;

    /**
     * A JButton for navigating to the next page in the quiz.
     * This button will be enabled or disabled based on user actions.
     */
    private JButton nextButton;

    /**
     * A JButton for navigating to the previous page in the quiz.
     * This button is used for going back to the previous question in the form.
     */
    private JButton backButton;

    /**
     * A flag that tracks whether the "Submit" button has been clicked.
     * It prevents multiple submissions by disabling the button once clicked.
     */
    private boolean isSubmitClicked = false;


    /**
     * Constructs a new Form5 panel.
     *
     * @param userResponses A map containing user responses from previous questions.
     * @param question The question number.
     */
    public Form5(HashMap<String,String> userResponses, int question) {
        this.userResponses = userResponses;
        this.question = question;
        init();
    }

    /**
     * Initializes the form by setting up the layout and adding content to the panel.
     */
    private void init() {
        setLayout(new MigLayout("fill, insets 20", "[center]", "[center]"));

        JPanel contentPanel = createContentPanel();
        add(contentPanel);
    }

    /**
     * Populates the given combo box with a list of all available course names.
     *
     * @param combo The combo box to populate.
     */
    private void classData(JComboBox combo) {
        combo.setModel(new javax.swing.DefaultComboBoxModel<>(getAllCourseNames()));
    }

    /**
     * Creates the content panel for the form.
     *
     * @return The content panel.
     */
    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new MigLayout("wrap, fillx, insets 35 45 30 45", "[grow]"));
        contentPanel.setPreferredSize(new Dimension(PANEL_WIDTH, 300));
        contentPanel.setFocusable(true);

        contentPanel.putClientProperty(FlatClientProperties.STYLE,
                "arc:20;" +
                        "[light]background:darken(@background,3%);" +
                        "[dark]background:lighten(@background,3%)");

        indicator = new PageMenuIndicator();
        indicator.setPageNumber(question - 1);

        contentPanel.add(indicator, "align left, wrap");
        contentPanel.add(createTitlePanel(), "wrap, align center, gapy 20");
        contentPanel.add(createQuestionLabel(), "wrap, align center, gapy 20");
        contentPanel.add(createCourseComboBoxPanel(), "wrap, align center, gapy 20");
        contentPanel.add(question > 1 ? createButtonPanelDoubleArrow() : createButtonPanelSingleArrow(), "span, align right, wrap, gapy 30");

        return contentPanel;
    }

    /**
     * Creates the title panel for the form.
     *
     * @return The title panel.
     */
    private JPanel createTitlePanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.putClientProperty(FlatClientProperties.STYLE, "background:null");

        questionTitle = new JLabel(String.format("Question #%s", question));
        questionTitle.setHorizontalAlignment(SwingConstants.CENTER);
        questionTitle.putClientProperty(FlatClientProperties.STYLE, "font: bold +15");

        topPanel.add(questionTitle, BorderLayout.NORTH);
        return topPanel;
    }

    /**
     * Creates the label displaying the question for the user.
     *
     * @return The question label.
     */
    private JLabel createQuestionLabel() {
        JLabel questionLabel = new JLabel("Please select all of your previous classes from the dropdown menu below:");
        questionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        questionLabel.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,10%);" +
                "[dark]background:lighten(@background,10%);" +
                "font: bold +5;" +
                "foreground: @accentColor;");

        return questionLabel;
    }

    /**
     * Creates the panel that contains the course selection combo box.
     *
     * @return The course combo box panel.
     */
    private JPanel createCourseComboBoxPanel() {
        JPanel comboBoxPanel = new JPanel();
        comboBoxPanel.setLayout(new BorderLayout());

        courseComboBox = new ComboBox();
        classData(courseComboBox);
        courseComboBox.setPreferredSize(new Dimension(600, 75));

        // Customizing the popup
        ComboPopup popup = (ComboPopup) courseComboBox.getUI().getAccessibleChild(courseComboBox, 0);
        ((JComponent) popup).setPreferredSize(new Dimension(600, 200));
        ((JComponent) popup).setLayout(new GridLayout(1, 1));

        comboBoxPanel.add(courseComboBox, BorderLayout.CENTER);

        comboBoxPanel.putClientProperty(FlatClientProperties.STYLE,
                "arc:10;" +
                        "[light]background:darken(@earlYellow,5%);" +
                        "[dark]background:lighten(@earlYellow,5%)");
        comboBoxPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (userResponses.containsKey("previousClasses")) {
            String previousClasses = userResponses.get("previousClasses");

            // Convert list to array if needed
            String[] classesArray = Course.cleanPreviousCourses(previousClasses);

            for (String c: classesArray) {
                courseComboBox.setSelectedItem(c);
            }
        }

        return comboBoxPanel;
    }

    /**
     * Creates the panel with a single arrow button (used for navigation).
     *
     * @return The button panel.
     */
    private JPanel createButtonPanelSingleArrow() {
        nextButton = new JButton("→");
        nextButton.setFont(new Font("Arial", Font.BOLD, 30));
        nextButton.setPreferredSize(new Dimension(60, 30));
        nextButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        nextButton.setOpaque(false);
        nextButton.setContentAreaFilled(false);
        nextButton.putClientProperty(FlatClientProperties.STYLE,
                "borderWidth:0;" +
                        "foreground: @earlYellow;" +
                        "innerFocusWidth:0;");

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(nextButton, BorderLayout.EAST);

        nextButton.addActionListener(e -> handlePage(true));

        return buttonPanel;
    }

    /**
     * Creates the panel with both back and next buttons (used for navigation).
     *
     * @return The button panel.
     */
    private JPanel createButtonPanelDoubleArrow() {
        nextButton = new JButton("Submit");

        nextButton.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,10%);" +
                "[dark]background:lighten(@background,10%);" +
                "borderWidth:0;" +
                "innerFocusWidth:0;" +
                "font: bold +10");

        nextButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        nextButton.setPreferredSize(new Dimension(100, 30));

        backButton = new JButton("←");
        backButton.setFont(new Font("Arial", Font.BOLD, 30));
        backButton.setPreferredSize(new Dimension(60, 30));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setOpaque(false);
        backButton.setContentAreaFilled(false);
        backButton.putClientProperty(FlatClientProperties.STYLE,
                "borderWidth:0;" +
                        "foreground: @earlYellow;" +
                        "innerFocusWidth:0;");

        nextButton.addActionListener(e -> handlePage(true));
        backButton.addActionListener(e -> handlePage(false));

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setPreferredSize(new Dimension(900, 30));
        buttonPanel.setOpaque(false);

        buttonPanel.add(backButton, BorderLayout.WEST);  // Place back button on the left
        buttonPanel.add(nextButton, BorderLayout.EAST);  // Place next button on the right

        return buttonPanel;
    }

    /**
     * Handles the navigation between pages based on user interaction (next or back).
     *
     * @param isNext A flag indicating whether the user pressed the next button (true) or the back button (false).
     */
    private void handlePage(boolean isNext) {
        List<Object> selectedItems = courseComboBox.getSelectedItems();

        List<String> selectedClasses = selectedItems.stream()
                .map(item -> "\"" + item + "\"")
                .collect(Collectors.toList());

        userResponses.put("previousClasses", selectedClasses.toString());

        // Retrieve values from userResponses hashmap
        String interests1 = userResponses.get("interests1");
        String interests2 = userResponses.get("interests2");
        String track = userResponses.get("track");
        int grade = Integer.parseInt(userResponses.get("grade"));
        String username = userResponses.get("username");

        // Combine interests1 and interests2
        String combinedInterests = interests1 + ", " + interests2;

        int requiredClasses = 0;

        if (isNext) {
            switch (grade) {
                case 10:
                    requiredClasses = 8;
                    break;
                case 11:
                    requiredClasses = 16;
                    break;
                case 12:
                    requiredClasses = 24;
                    break;
                default:
                    break;
            }

            if (selectedItems.size() < requiredClasses) {
                NotificationManager.showNotification(NotificationManager.NotificationType.WARNING, String.format("You need to select at least %d classes.", requiredClasses));
                return;
            }

            if (!isSubmitClicked) {
                isSubmitClicked = true;
                nextButton.setEnabled(false);

                // Create StudentInput object
                UserInput student = new UserInput(combinedInterests, selectedClasses.toString(), grade, track, username);

                // Run the assessment in a separate thread
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        CourseAssembly.runAssessment(student);
                        return null;
                    }

                    @Override
                    protected void done() {
                        // Re-enable the button or update UI if needed
                        nextButton.setEnabled(true);
                        isSubmitClicked = false;

                        // Switch to the main page
                        User user = UsersUtil.getUserWithUsername(username);
                        String name = user.getFirstName();
                        FormsManager.getInstance().showForm(new DashboardForm(username,name));
                    }
                };
                worker.execute();
            }
        } else {
            question--;
            Object formInstance = DynamicFormLoader.loadForm(question, userResponses);
            if (formInstance != null) {
                FormsManager.getInstance().showForm((JComponent) formInstance);
            }
        }
    }
}