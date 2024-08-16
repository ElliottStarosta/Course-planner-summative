package org.example.gui.pages.quiz;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import org.example.people.StudentInput;
import org.example.utility.courses.Course;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CountDownLatch;



public class FillCourses extends JPanel {
    private static final int INITIAL_HEIGHT = 185;
    private static final int MAX_HEIGHT = 400;
    private static final int PANEL_WIDTH = 600;


    private JLabel questionTitle;
    private JTextArea answerArea;
    private JButton nextButton;

    private StudentInput student;
    private CountDownLatch latch;




    public FillCourses(StudentInput student, CountDownLatch latch) {
        this.student = student;
        this.latch = latch;
        init();
    }

    private void init() {
        setLayout(new MigLayout("fill, insets 20", "[center]", "[center]"));

        JPanel contentPanel = createContentPanel();
        add(contentPanel);
    }

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

    private JPanel createTitlePanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.putClientProperty(FlatClientProperties.STYLE, "background:null");

        questionTitle = new JLabel("Extra Section");
        questionTitle.setHorizontalAlignment(SwingConstants.CENTER);
        questionTitle.putClientProperty(FlatClientProperties.STYLE, "font: bold +15");

        topPanel.add(questionTitle, BorderLayout.NORTH);
        return topPanel;
    }

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



    private void handlePage() {
        String answerText = answerArea.getText().trim();
        Course.getNonFilledClassesResponse(student,answerText); // FIll the user's courses with their new interests
        latch.countDown();
    }

}
