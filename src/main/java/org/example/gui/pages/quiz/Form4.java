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
 * Form4 represents a panel that displays the fourth question -- typing a future career
 *
 * <p>The form provides buttons for navigation and handles user responses using checkboxes.
 * It uses a dynamic layout and integrates with other components like the PageMenuIndicator and NotificationManager.</p>
 */
public class Form4 extends JPanel {
    /**
     * The initial height for the answer scroll pane.
     */
    private static final int INITIAL_HEIGHT = 185;

    /**
     * The maximum height for the answer scroll pane.
     */
    private static final int MAX_HEIGHT = 400;

    /**
     * The user object that contains all of their inputted data
     */
    private UserInput user;

    /**
     * The current question number in the form sequence.
     */
    private int question;

    /**
     * The width of the panel that holds the form content.
     */
    private static final int PANEL_WIDTH = 600;

    /**
     * The page menu indicator that shows the current page number in the form.
     */
    private PageMenuIndicator indicator;

    /**
     * The label that displays the current question title (e.g., "Question #1").
     */
    private JLabel questionTitle;

    /**
     * The text area where the user can enter their answer for the current question.
     */
    private JTextArea answerArea;

    /**
     * The button that allows the user to move to the next question.
     */
    private JButton nextButton;

    /**
     * The button that allows the user to move to the previous question.
     */
    private JButton backButton;

    /**
     * JFrame reference
     */
    private JFrame frame = Application.getInstance();

    /**
     * Constructs a new Form4 instance.
     *
     * @param user UserInput obj that passes the user's data.
     * @param question The current question number.
     */
    public Form4(UserInput user, int question) {
        frame.setMinimumSize(new Dimension(675, 550));
        this.user = user;
        this.question = question;
        init();
    }

    /**
     * Initializes the components of the form.
     */
    private void init() {
        setLayout(new MigLayout("fill, insets 20", "[center]", "[center]"));

        JPanel contentPanel = createContentPanel();
        add(contentPanel);
    }

    /**
     * Creates the content panel containing all UI components of the form.
     *
     * @return The content panel.
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
     * Creates the panel that displays the title of the question.
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
     * Creates a label displaying the question text.
     *
     * @return The question label.
     */
    private JLabel createQuestionLabel() {
        JLabel questionLabel = new JLabel("What career aspirations do you have for the future?");
        questionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        questionLabel.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,10%);" +
                "[dark]background:lighten(@background,10%);" +
                "font: bold +5;" +
                "foreground: @accentColor;");

        return questionLabel;
    }

    /**
     * Creates a scrollable area for the user to input their response.
     *
     * @return The scroll pane containing the answer text area.
     */
    private JScrollPane createAnswerScrollPane() {
        answerArea = new JTextArea();

        answerArea.putClientProperty(FlatClientProperties.STYLE, "font: bold +5");

        answerArea.setBorder(BorderFactory.createCompoundBorder(
                answerArea.getBorder(),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        answerArea.setLineWrap(true);
        answerArea.setWrapStyleWord(true);

        if (user.getInterest2() != null) {
            answerArea.setText(user.getInterest2());
        }

        JScrollPane scrollPane = new JScrollPane(answerArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(500, INITIAL_HEIGHT));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); // Allow scroll bars when needed

        return scrollPane;
    }

    /**
     * Creates the button panel with a single arrow for navigation to the next question.
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
     * Creates the button panel with both back and next arrows for navigation between questions.
     *
     * @return The button panel.
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
     * Handles the navigation between questions.
     * If moving to the next question, checks if the user has provided a valid response.
     * If moving to the previous question, simply goes back.
     *
     * @param isNext A boolean indicating whether to move to the next question (true) or back to the previous one (false).
     */
    private void handlePage(boolean isNext) {
        String answerText = answerArea.getText().trim();
        user.setInterests2(answerText);

        nextButton.setEnabled(false);
        if (isNext) {
            // Check if the text has at least 3 characters
            if (answerText.length() >= 3) {
                question++;
                Object formInstance = DynamicFormLoader.loadForm(question, user);
                if (formInstance != null) {
                    FormsManager.getInstance().showForm((JComponent) formInstance);
                }
            } else {
                NotificationManager.showNotification(NotificationManager.NotificationType.WARNING, "Enter a more detailed response");
                nextButton.setEnabled(true);
            }
        } else {
            question--;
            Object formInstance = DynamicFormLoader.loadForm(question, user);
            if (formInstance != null) {
                FormsManager.getInstance().showForm((JComponent) formInstance);
            }
        }
    }
}
