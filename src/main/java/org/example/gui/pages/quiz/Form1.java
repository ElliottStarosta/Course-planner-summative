package org.example.gui.pages.quiz;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import org.example.gui.manager.DynamicFormLoader;
import org.example.gui.manager.NotificationManager;
import org.example.gui.component.jcomponents.PageMenuIndicator;
import org.example.gui.manager.FormsManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class Form1 extends JPanel {
    private int question;

    private static final int PANEL_WIDTH = 600;

    private JPanel contentPanel;

    private JLabel questionTitle;

    private JCheckBox grade9Button;
    private JCheckBox grade10Button;
    private JCheckBox grade11Button;
    private JCheckBox grade12Button;

    private JButton nextButton;
    private JButton backButton;

    private HashMap<String, String> userResponses;

    private PageMenuIndicator indicator;



    public Form1(HashMap<String, String> userResponses, int question) {
        this.userResponses = userResponses;
        this.question = question;
        init();
    }

    private void init() {
        setLayout(new MigLayout("fill, insets 20", "[center]", "[center]"));

        contentPanel = createContentPanel();

        add(contentPanel);

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
        contentPanel.add(createTitlePanel(), "wrap, align center, gapy 75");
        contentPanel.add(createQuestionLabel(), "wrap, align center, gapy 20");
        contentPanel.add(pathSelect(), "wrap, align center, grow, gapy 20");
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
        JLabel questionLabel = new JLabel("What grade are you currently in?");
        questionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        questionLabel.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,10%);" +
                "[dark]background:lighten(@background,10%);" +
                "font: bold +5;" +
                "foreground: @accentColor;");

        return questionLabel;
    }

    private JPanel pathSelect() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10)); // Aligns buttons in a row with some spacing

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

        // Style the radio buttons
        grade9Button.putClientProperty(FlatClientProperties.STYLE,
                "font: bold +5; " +
                        "iconTextGap: 10;");
        grade9Button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        grade10Button.putClientProperty(FlatClientProperties.STYLE,
                "font: bold +5; " +
                        "iconTextGap: 10;");
        grade10Button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        grade11Button.putClientProperty(FlatClientProperties.STYLE,
                "font: bold +5; " +
                        "iconTextGap: 10;");
        grade11Button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        grade12Button.putClientProperty(FlatClientProperties.STYLE,
                "font: bold +5; " +
                        "iconTextGap: 10;");

        grade12Button.setCursor(new Cursor(Cursor.HAND_CURSOR));


        // Create a ButtonGroup to ensure only one button can be selected at a time
        ButtonGroup group = new ButtonGroup();
        group.add(grade9Button);
        group.add(grade10Button);
        group.add(grade11Button);
        group.add(grade12Button);


        // Add ActionListeners to capture clicks
        grade9Button.addActionListener(new ButtonClickListener());
        grade10Button.addActionListener(new ButtonClickListener());
        grade11Button.addActionListener(new ButtonClickListener());
        grade12Button.addActionListener(new ButtonClickListener());

        String grade = "";

        if (userResponses.containsKey("grade")) {
            grade = userResponses.get("grade");
            selectButton(grade);
        }


        // Add the buttons to the panel
        panel.add(grade9Button);
        panel.add(grade10Button);
        panel.add(grade11Button);
        panel.add(grade12Button);

        return panel;
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JCheckBox clickedButton = (JCheckBox) e.getSource();

            // Perform action based on the clicked button
            if (clickedButton == grade9Button) {
                userResponses.put("grade", "9");
            } else if (clickedButton == grade10Button) {
                userResponses.put("grade", "10");
            } else if (clickedButton == grade11Button) {
                userResponses.put("grade", "11");
            } else if (clickedButton == grade12Button) {
                userResponses.put("grade", "11");
            }
        }
    }

    private void selectButton(String grade) {
        if ("9".equalsIgnoreCase(grade)) {
            grade9Button.setSelected(true);
        } else if ("10".equalsIgnoreCase(grade)) {
            grade10Button.setSelected(true);
        } else if ("11".equalsIgnoreCase(grade)) {
            grade11Button.setSelected(true);
        } else if ("12".equalsIgnoreCase(grade)) {
            grade12Button.setSelected(true);
        }
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


    private void handlePage(boolean isNext) {

        if(isNext) {
            if (!grade9Button.isSelected() && !grade10Button.isSelected() &&
                    !grade11Button.isSelected() && !grade12Button.isSelected()) {
                NotificationManager.showNotification(NotificationManager.NotificationType.WARNING, "You must select a track option.");
                return;
            }
            question++;
            Object formInstance = DynamicFormLoader.loadForm(question, userResponses);
            if (formInstance != null) {
                // Assuming FormsManager can handle form instances without a specific base class
                FormsManager.getInstance().showForm((JComponent) formInstance);
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
