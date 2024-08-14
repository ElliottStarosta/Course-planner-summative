package org.example.gui.pages.quiz;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import org.example.gui.manager.DynamicFormLoader;
import org.example.gui.manager.NotificationManager;
import org.example.gui.component.jcomponents.PageMenuIndicator;
import org.example.gui.manager.FormsManager;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;


public class Form3 extends JPanel {
    private static final int INITIAL_HEIGHT = 185;
    private static final int MAX_HEIGHT = 400;
    private HashMap<String, String> userResponses;
    private int question;
    private static final int PANEL_WIDTH = 600;

    private PageMenuIndicator indicator;



    private JLabel questionTitle;
    private JTextArea answerArea;
    private JButton nextButton;
    private JButton backButton;



    public Form3(HashMap<String,String> userResponses, int question) {
        this.userResponses = userResponses;
        this.question = question;
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

        indicator = new PageMenuIndicator();
        indicator.setPageNumber(2);

        contentPanel.add(indicator, "align left, wrap");
        contentPanel.add(createTitlePanel(), "wrap, align center, gapy 20");
        contentPanel.add(createQuestionLabel(), "wrap, align center, gapy 20");
        contentPanel.add(createAnswerScrollPane(), "wrap, align center, grow, gapy 45");
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
        JLabel questionLabel = new JLabel("What topics or subjects are you most passionate about learning or improving your skills in?");
        questionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        questionLabel.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,10%);" +
                "[dark]background:lighten(@background,10%);" +
                "font: bold +5;" +
                "foreground: @accentColor;");

        return questionLabel;
    }

    private JScrollPane createAnswerScrollPane() {
        answerArea = new JTextArea();

        answerArea.putClientProperty(FlatClientProperties.STYLE, "font: bold +5");

        answerArea.setBorder(BorderFactory.createCompoundBorder(
                answerArea.getBorder(),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        answerArea.setLineWrap(true);
        answerArea.setWrapStyleWord(true);

        if (userResponses.containsKey("interests1")) {
            answerArea.setText(userResponses.get("interests1"));
        }

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

        nextButton.addActionListener(e -> handlePage(true));


        return buttonPanel;
    }

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



    private void handlePage(boolean isNext) {
        String answerText = answerArea.getText().trim();
        userResponses.put("interests1", answerText);
        if(isNext) {
            // Check if the text has at least 10 characters
            if (answerText.length() >= 10) {
                question++;
                Object formInstance = DynamicFormLoader.loadForm(question, userResponses);
                if (formInstance != null) {
                    // Assuming FormsManager can handle form instances without a specific base class
                    FormsManager.getInstance().showForm((JComponent) formInstance);
                }
            } else {
                NotificationManager.showNotification(NotificationManager.NotificationType.WARNING, "Enter a more detailed response");
            }
        } else {
            question--;
            Object formInstance = DynamicFormLoader.loadForm(question, userResponses);
            if (formInstance != null) {
                // Assuming FormsManager can handle form instances without a specific base class
                FormsManager.getInstance().showForm((JComponent) formInstance);
            }
        }
    }

}
