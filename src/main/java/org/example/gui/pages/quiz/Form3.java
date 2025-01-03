package org.example.gui.pages.quiz;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import org.example.gui.manager.DynamicFormLoader;
import org.example.gui.manager.NotificationManager;
import org.example.gui.component.jcomponents.PageMenuIndicator;
import org.example.gui.manager.FormsManager;
import org.example.gui.pages.Application;
import org.example.people.UserInput;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

/**
 * Form3 represents a panel that displays the third question -- typing interests
 *
 * <p>The form provides buttons for navigation and handles user responses using checkboxes.
 * It uses a dynamic layout and integrates with other components like the PageMenuIndicator and NotificationManager.</p>
 */
public class Form3 extends JPanel {
    /**
     * The initial height of the answer area (in pixels).
     */
    private static final int INITIAL_HEIGHT = 185;

    /**
     * The maximum height of the answer area (in pixels).
     */
    private static final int MAX_HEIGHT = 400;

    /**
     * The user object that contains all of their inputted data
     */
    private UserInput user;

    /**
     * The current question number being displayed.
     */
    private int question;

    /**
     * The width of the panel that contains the form (in pixels).
     */
    private static final int PANEL_WIDTH = 600;

    /**
     * The indicator for the page menu, showing the current question's progress.
     */
    private PageMenuIndicator indicator;

    /**
     * The label displaying the current question title.
     */
    private JLabel questionTitle;

    /**
     * The text area for entering the user's response to the current question.
     */
    private JTextArea answerArea;

    /**
     * The button used to move to the next question.
     */
    private JButton nextButton;

    /**
     * The button used to go back to the previous question.
     */
    private JButton backButton;

    /**
     * JFrame reference
     */
    private JFrame frame = Application.getInstance();

    /**
     * Constructor for Form3.
     *
     * @param user UserInput obj that passes the user's data
     * @param question The current question number.
     */
    public Form3(UserInput user, int question) {
        frame.setMinimumSize(new Dimension(900, 550));
        this.user = user;
        this.question = question;
        init();
    }

    /**
     * Initializes the form by setting up the layout and creating the content panel.
     */
    private void init() {
        setLayout(new MigLayout("fill, insets 20", "[center]", "[center]"));

        JPanel contentPanel = createContentPanel();
        add(contentPanel);
    }

    /**
     * Creates the content panel for the form.
     *
     * @return JPanel The content panel containing all components for the form.
     */
    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new MigLayout("wrap, fillx, insets 35 45 30 45", "[grow]"));
        contentPanel.setPreferredSize(new Dimension(PANEL_WIDTH, 450));
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
        contentPanel.add(createAnswerScrollPane(), "wrap, align center, grow, gapy 45");
        contentPanel.add(question > 1 ? createButtonPanelDoubleArrow() : createButtonPanelSingleArrow(), "span, align right, wrap, gapy 30");

        return contentPanel;
    }

    /**
     * Creates the title panel with the current question label.
     *
     * @return JPanel The title panel containing the question number.
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
     * Creates the label displaying the current question text.
     *
     * @return JLabel The question label.
     */
    private JLabel createQuestionLabel() {
        JLabel questionLabel = new JLabel("What topics or subjects are you most passionate about learning or improving your skills in?");
        questionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        questionLabel.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,10%);" +
                "[dark]background:lighten(@background,10%);" +
                "font: bold +5;" +
                "foreground: @accentColor;");

        return questionLabel;
    }

    /**
     * Creates the scroll pane for the answer area, allowing users to input their responses.
     *
     * @return JScrollPane The scroll pane containing the answer text area.
     */
    private JScrollPane createAnswerScrollPane() {
        answerArea = new JTextArea();

        answerArea.putClientProperty(FlatClientProperties.STYLE, "" +
                "font: bold +5");

        answerArea.setBorder(BorderFactory.createCompoundBorder(
                answerArea.getBorder(),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        answerArea.setLineWrap(true);
        answerArea.setWrapStyleWord(true);

        if (user.getInterest1() != null) {
            answerArea.setText(user.getInterest1());
        }

        JScrollPane scrollPane = new JScrollPane(answerArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(500, INITIAL_HEIGHT));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); // Allow scroll bars when needed

        return scrollPane;
    }

    /**
     * Creates a button panel with a single "next" arrow button for navigation.
     *
     * @return JPanel The button panel containing the "next" button.
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
     * Creates a button panel with both "next" and "back" arrow buttons for navigation.
     *
     * @return JPanel The button panel containing both "next" and "back" buttons.
     */
    private JPanel createButtonPanelDoubleArrow() {
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
     * Handles the navigation to the next or previous page based on user interaction.
     * Saves the current answer and loads the next/previous form.
     *
     * @param isNext A boolean indicating if the next button was pressed (true) or the back button was pressed (false).
     */
    private void handlePage(boolean isNext) {
        String answerText = answerArea.getText().trim();
        user.setInterests1(answerText);
        if (isNext) {
            // Check if the text has at least 3 characters
            if (answerText.length() >= 3) {
                question++;
                Object formInstance = DynamicFormLoader.loadForm(question, user);
                if (formInstance != null) {
                    // Assuming FormsManager can handle form instances without a specific base class
                    FormsManager.getInstance().showForm((JComponent) formInstance);
                }
            } else {
                NotificationManager.showNotification(NotificationManager.NotificationType.WARNING, "Enter a more detailed response");
            }
        } else {
            question--;
            Object formInstance = DynamicFormLoader.loadForm(question, user);
            if (formInstance != null) {
                // Assuming FormsManager can handle form instances without a specific base class
                FormsManager.getInstance().showForm((JComponent) formInstance);
            }
        }
    }
}