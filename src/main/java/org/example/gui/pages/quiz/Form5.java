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

// TODO : Make the combo box only show courses in which your grade can take; Your grade and all the courses without preqs

public class Form5 extends JPanel {

    private HashMap<String, String> userResponses;
    private int question;
    private static final int PANEL_WIDTH = 600;

    private PageMenuIndicator indicator;


    private JLabel questionTitle;
    private ComboBox courseComboBox;

    private JButton nextButton;
    private JButton backButton;

    private boolean isSubmitClicked = false;



    public Form5(HashMap<String,String> userResponses, int question) {
        this.userResponses = userResponses;
        this.question = question;
        init();
    }

    private void init() {
        setLayout(new MigLayout("fill, insets 20", "[center]", "[center]"));


        JPanel contentPanel = createContentPanel();
        add(contentPanel);
    }

    private void classData(JComboBox combo) {
        combo.setModel(new javax.swing.DefaultComboBoxModel<>(getAllCourseNames()));
    }

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



    private JPanel createTitlePanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.putClientProperty(FlatClientProperties.STYLE, "background:null");

        questionTitle = new JLabel(String.format("Question #%s", question));
        questionTitle.setHorizontalAlignment(SwingConstants.CENTER);
        questionTitle.putClientProperty(FlatClientProperties.STYLE, "font: bold +15");

        topPanel.add(questionTitle, BorderLayout.NORTH);
        return topPanel;
    }

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
                        "[light]background:darken(@background,5%);" +
                        "[dark]background:lighten(@background,5%)");
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