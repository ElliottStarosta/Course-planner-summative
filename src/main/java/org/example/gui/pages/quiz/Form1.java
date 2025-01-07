package org.example.gui.pages.quiz;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import org.example.gui.manager.DynamicFormLoader;
import org.example.gui.manager.NotificationManager;
import org.example.gui.component.jcomponents.PageMenuIndicator;
import org.example.gui.manager.FormsManager;
import org.example.gui.pages.Application;
import org.example.gui.pages.main.DashboardForm;
import org.example.people.UserInput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

/**
 * Form1 represents a panel that displays the first question -- selecting a grade
 *
 * <p>The form provides buttons for navigation and handles user responses using checkboxes.
 * It uses a dynamic layout and integrates with other components like the PageMenuIndicator and NotificationManager.</p>
 */
public class Form1 extends JPanel {

    /**
     * The current question number being displayed in the form.
     */
    private int question;

    /**
     * The fixed width of the panel.
     */
    private static final int PANEL_WIDTH = 600;

    /**
     * The main content panel that holds the components of the form.
     */
    private JPanel contentPanel;

    /**
     * The label that displays the current question number.
     */
    private JLabel questionTitle;

    /**
     * The checkbox for selecting Grade 9.
     */
    private JCheckBox grade9Button;

    /**
     * The checkbox for selecting Grade 10.
     */
    private JCheckBox grade10Button;

    /**
     * The checkbox for selecting Grade 11.
     */
    private JCheckBox grade11Button;

    /**
     * The checkbox for selecting Grade 12.
     */
    private JCheckBox grade12Button;

    /**
     * The button that allows the user to navigate to the next question.
     */
    private JButton nextButton;

    /**
     * The button that allows the user to navigate to the previous question.
     */
    private JButton backButton;

    /**
     * The user object that contains all of their inputted data
     */
    private UserInput user;

    /**
     * The page menu indicator that visually represents the current question number in the form.
     */
    private PageMenuIndicator indicator;


    /**
     * JFrame reference
     */
    private JFrame frame = Application.getInstance();


    /**
     * Constructs a Form1 panel.
     *
     * @param user UserInput obj that passes the user's data
     * @param question the current question number
     */
    public Form1(UserInput user, int question) {
        frame.setMinimumSize(new Dimension(650, 500));
        this.user = user;
        this.question = question;
        init();
    }

    /**
     * Initializes the layout and components of the Form1 panel.
     */
    private void init() {
        // General form set up
        setLayout(new MigLayout("fill, insets 20", "[center]", "[center]"));
        contentPanel = createContentPanel();
        add(contentPanel);

        // Delete dashboard cache to have different screen on quiz completion
        FormsManager.getInstance().deleteCache(DashboardForm.class);
    }

    /**
     * Creates the content panel that contains all the components of the form.
     *
     * @return a JPanel containing the form components
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
        contentPanel.add(createTitlePanel(), "wrap, align center, gapy 75");
        contentPanel.add(createQuestionLabel(), "wrap, align center, gapy 20");
        contentPanel.add(pathSelect(), "wrap, align center, grow, gapy 20");
        contentPanel.add(question > 1 ? createButtonPanelDoubleArrow() : createButtonPanelSingleArrow(), "span, align right, wrap, gapy 30");

        return contentPanel;
    }

    /**
     * Creates the title panel that displays the current question number.
     *
     * @return a JPanel containing the title label
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
     * Creates the label for the current question.
     *
     * @return a JLabel containing the question text
     */
    private JLabel createQuestionLabel() {
        JLabel questionLabel = new JLabel("What grade are you currently in?");
        questionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        questionLabel.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,10%);" +
                "[dark]background:lighten(@background,10%);" +
                "font: bold +5;" +
                "foreground: @accentColor;");

        return questionLabel;
    }

    /**
     * Creates a panel containing checkboxes for the user to select their grade.
     *
     * @return a JPanel containing the grade selection checkboxes
     */
    private JPanel pathSelect() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        panel.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,10%);" +
                "[dark]background:lighten(@background,10%);" +
                "font: bold +2;" +
                "foreground: @accentColor;" +
                "arc: 15;");

        grade9Button = new JCheckBox("Grade 9");
        grade10Button = new JCheckBox("Grade 10");
        grade11Button = new JCheckBox("Grade 11");
        grade12Button = new JCheckBox("Grade 12");

        styleCheckBox(grade9Button);
        styleCheckBox(grade10Button);
        styleCheckBox(grade11Button);
        styleCheckBox(grade12Button);

        ButtonGroup group = new ButtonGroup();
        group.add(grade9Button);
        group.add(grade10Button);
        group.add(grade11Button);
        group.add(grade12Button);

        grade9Button.addActionListener(new ButtonClickListener());
        grade10Button.addActionListener(new ButtonClickListener());
        grade11Button.addActionListener(new ButtonClickListener());
        grade12Button.addActionListener(new ButtonClickListener());

        if (user.getGrade() != 0) {
            int grade = user.getGrade();
            selectButton(grade);
        }

        panel.add(grade9Button);
        panel.add(grade10Button);
        panel.add(grade11Button);
        panel.add(grade12Button);

        return panel;
    }

    /**
     * Styles the given checkbox.
     *
     * @param checkBox the JCheckBox to be styled
     */
    private void styleCheckBox(JCheckBox checkBox) {
        checkBox.putClientProperty(FlatClientProperties.STYLE,
                "font: bold +5; " +
                        "iconTextGap: 10;");
        checkBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Selects the corresponding checkbox based on the grade provided.
     *
     * @param grade the grade to select
     */
    private void selectButton(int grade) {
        if (grade == 9) {
            grade9Button.setSelected(true);
        } else if (10 == grade) {
            grade10Button.setSelected(true);
        } else if (11 == grade) {
            grade11Button.setSelected(true);
        } else if (12 == grade) {
            grade12Button.setSelected(true);
        }
    }

    /**
     * Creates the panel containing the navigation buttons with arrows.
     *
     * @return a JPanel containing the back and next buttons
     */
    private JPanel createButtonPanelDoubleArrow() {
        nextButton = new JButton("→");
        styleNavigationButton(nextButton);

        backButton = new JButton("←");
        styleNavigationButton(backButton);

        nextButton.addActionListener(e -> handlePage(true));
        backButton.addActionListener(e -> handlePage(false));

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setPreferredSize(new Dimension(900, 30));
        buttonPanel.setOpaque(false);

        buttonPanel.add(backButton, BorderLayout.WEST);
        buttonPanel.add(nextButton, BorderLayout.EAST);

        return buttonPanel;
    }

    /**
     * Styles the navigation buttons.
     *
     * @param button the JButton to be styled
     */
    private void styleNavigationButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 30));
        button.setPreferredSize(new Dimension(60, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.putClientProperty(FlatClientProperties.STYLE,
                "borderWidth:0;" +
                        "foreground: @earlYellow;" +
                        "innerFocusWidth:0;");
    }

    /**
     * Creates the panel containing the single arrow navigation button.
     *
     * @return a JPanel containing the next button
     */
    private JPanel createButtonPanelSingleArrow() {
        nextButton = new JButton("→");
        styleNavigationButton(nextButton);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(nextButton, BorderLayout.EAST);

        nextButton.addActionListener(e -> handlePage(true));

        return buttonPanel;
    }

    /**
     * Handles the navigation between questions. If the user selects the "Next" button,
     * it moves to the next question; if the "Back" button is selected, it moves to the previous question.
     *
     * @param isNext true if the next question is selected, false if the previous question is selected
     */
    private void handlePage(boolean isNext) {
        if (isNext) {
            if (!grade9Button.isSelected() && !grade10Button.isSelected() &&
                    !grade11Button.isSelected() && !grade12Button.isSelected()) {
                NotificationManager.showNotification(NotificationManager.NotificationType.WARNING, "You must select a track option.");
                return;
            }
            question++;
            Object formInstance = DynamicFormLoader.loadForm(question, user);
            if (formInstance != null) {
                FormsManager.getInstance().showForm((JComponent) formInstance);
            }
        } else {
            question--;
            Object formInstance = DynamicFormLoader.loadForm(question, user);
            if (formInstance != null) {
                FormsManager.getInstance().showForm((JComponent) formInstance);
            }
        }
    }

    /**
     * ActionListener for the grade selection checkboxes. Updates the user's response based on the selected checkbox.
     */
    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JCheckBox clickedButton = (JCheckBox) e.getSource();

            if (clickedButton == grade9Button) {
                user.setGrade(9);
            } else if (clickedButton == grade10Button) {
                user.setGrade(10);
            } else if (clickedButton == grade11Button) {
                user.setGrade(11);
            } else if (clickedButton == grade12Button) {
                user.setGrade(12);
            }
        }
    }
}