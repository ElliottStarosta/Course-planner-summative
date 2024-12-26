package org.example.gui.pages.quiz;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import org.example.people.UserInput;
import org.example.utility.courses.Course;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CountDownLatch;

/**
 * The FillCourses class represents a panel in the GUI where the user can input additional interests for courses.
 * It allows the user to provide input, which is used to fill courses for a student.
 */
public class FillCourses extends JPanel {

    /**
     * The initial height of the answer area.
     */
    private static final int INITIAL_HEIGHT = 185;

    /**
     * The maximum height of the answer area.
     */
    private static final int MAX_HEIGHT = 400;

    /**
     * The width of the panel.
     */
    private static final int PANEL_WIDTH = 600;

    /**
     * Label displaying the title of the section.
     */
    private JLabel questionTitle;

    /**
     * Text area for the user to provide their answer regarding course interests.
     */
    private JTextArea answerArea;

    /**
     * Button that proceeds to the next step in the process.
     */
    private JButton nextButton;

    /**
     * The student whose courses are being filled.
     */
    private UserInput student;

    /**
     * CountDownLatch used to synchronize the completion of the task.
     */
    private CountDownLatch latch;

    /**
     * Constructs a FillCourses panel with the provided student and latch.
     *
     * @param student The student whose courses are being filled.
     * @param latch   The CountDownLatch used to synchronize tasks.
     */
    public FillCourses(UserInput student, CountDownLatch latch) {
        this.student = student;
        this.latch = latch;
        init();
    }

    /**
     * Initializes the panel by setting the layout and adding the content panel.
     */
    private void init() {
        setLayout(new MigLayout("fill, insets 20", "[center]", "[center]"));
        JPanel contentPanel = createContentPanel();
        add(contentPanel);
    }

    /**
     * Creates the content panel that holds the title, question label, answer area, and the next button.
     *
     * @return A JPanel containing the content for the FillCourses screen.
     */
    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new MigLayout("wrap, fillx, insets 35 45 30 45", "[grow]"));
        contentPanel.setPreferredSize(new Dimension(PANEL_WIDTH, 450));
        contentPanel.setFocusable(true);

        contentPanel.putClientProperty(FlatClientProperties.STYLE,
                "arc:20;" +
                        "[light]background:darken(@background,3%);" +
                        "[dark]background:lighten(@background,3%)");

        contentPanel.add(createTitlePanel(), "wrap, align center, gapy 20");
        contentPanel.add(createQuestionLabel(), "wrap, align center, gapy 20");
        contentPanel.add(createSubtitleLabel(), "wrap, align center, gapy 5");
        contentPanel.add(createAnswerScrollPane(), "wrap, align center, grow, gapy 45");
        contentPanel.add(createButtonPanelSingleArrow(), "span, align right, wrap, gapy 30");

        return contentPanel;
    }

    /**
     * Creates the title panel containing the question title.
     *
     * @return A JPanel containing the question title.
     */
    private JPanel createTitlePanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.putClientProperty(FlatClientProperties.STYLE, "background:null");

        questionTitle = new JLabel("Extra Section");
        questionTitle.setHorizontalAlignment(SwingConstants.CENTER);
        questionTitle.putClientProperty(FlatClientProperties.STYLE, "font: bold +15");

        topPanel.add(questionTitle, BorderLayout.NORTH);
        return topPanel;
    }

    /**
     * Creates the label displaying the main question.
     *
     * @return A JLabel containing the main question text.
     */
    private JLabel createQuestionLabel() {
        JLabel questionLabel = new JLabel("Your input was insufficient to fill your courses. Do you have additional interests to share?");
        questionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        questionLabel.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,10%);" +
                "[dark]background:lighten(@background,10%);" +
                "font: bold +5;" +
                "foreground: @accentColor;");

        return questionLabel;
    }

    /**
     * Creates the subtitle label providing further instruction.
     *
     * @return A JLabel containing the subtitle text.
     */
    private JLabel createSubtitleLabel() {
        JLabel subtitleLabel = new JLabel("Leave the answer blank if you prefer us to select classes for you.");
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,10%);" +
                "[dark]background:lighten(@background,10%);" +
                "font: bold +2;" +
                "foreground: lighten(@background,30%);");

        return subtitleLabel;
    }

    /**
     * Creates a JScrollPane that contains the answer text area for user input.
     *
     * @return A JScrollPane containing the answer text area.
     */
    private JScrollPane createAnswerScrollPane() {
        answerArea = new JTextArea();
        answerArea.putClientProperty(FlatClientProperties.STYLE, "font: bold +5");

        answerArea.setBorder(BorderFactory.createCompoundBorder(
                answerArea.getBorder(),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        answerArea.setLineWrap(true);
        answerArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(answerArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(500, INITIAL_HEIGHT));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); // Allow scroll bars when needed

        return scrollPane;
    }

    /**
     * Creates the panel containing the next button that navigates to the next screen.
     *
     * @return A JPanel containing the next button.
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

        nextButton.addActionListener(e -> handlePage());

        return buttonPanel;
    }

    /**
     * Handles the page transition after the user clicks the next button.
     * It processes the user's input and updates the courses with new interests.
     */
    private void handlePage() {
        String answerText = answerArea.getText().trim();
        Course.getNonFilledClassesResponse(student, answerText); // Fill the user's courses with their new interests
        latch.countDown();
    }
}