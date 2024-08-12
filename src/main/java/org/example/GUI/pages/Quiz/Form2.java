package org.example.GUI.pages.Quiz;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import org.example.GUI.component.NotificationManager;
import org.example.GUI.component.PageMenuIndicator;
import org.example.GUI.manager.FormsManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class Form2 extends JPanel {
    private int question;
    private static final int PANEL_WIDTH = 600;

    private JPanel contentPanel;

    private JLabel questionTitle;

    private JCheckBox universityButton;
    private JCheckBox collegeButton;
    private JCheckBox tradeButton;
    private JCheckBox dontKnowButton;
    
    private JButton nextButton;
    private JButton backButton;
    
    private HashMap<String, String> userResponses;

    private PageMenuIndicator indicator;



    public Form2(HashMap<String, String> userResponses, int question) {
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
        indicator.setPageNumber(1);

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
        JLabel questionLabel = new JLabel("What track are you currently pursuing or planning to pursue?");
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
                "foreground: @accentColor;");

        universityButton = new JCheckBox("University");
        collegeButton = new JCheckBox("College");
        tradeButton = new JCheckBox("Trade");
        dontKnowButton = new JCheckBox("I don't know");

        // Style the radio buttons
        universityButton.putClientProperty(FlatClientProperties.STYLE,
                "font: bold +5; " +
                        "iconTextGap: 10;");
        collegeButton.putClientProperty(FlatClientProperties.STYLE,
                "font: bold +5; " +
                        "iconTextGap: 10;");
        tradeButton.putClientProperty(FlatClientProperties.STYLE,
                "font: bold +5; " +
                        "iconTextGap: 10;");
        dontKnowButton.putClientProperty(FlatClientProperties.STYLE,
                "font: bold +5; " +
                        "iconTextGap: 10;");

        String track = "";

        if (userResponses.containsKey("track")) {
            track = userResponses.get("track");
            selectButton(track);
        }

        // Create a ButtonGroup to ensure only one button can be selected at a time
        ButtonGroup group = new ButtonGroup();
        group.add(universityButton);
        group.add(collegeButton);
        group.add(tradeButton);
        group.add(dontKnowButton);

        // Add ActionListeners to capture clicks
        universityButton.addActionListener(new ButtonClickListener());
        collegeButton.addActionListener(new ButtonClickListener());
        tradeButton.addActionListener(new ButtonClickListener());
        dontKnowButton.addActionListener(new ButtonClickListener());


        // Add the buttons to the panel
        panel.add(universityButton);
        panel.add(collegeButton);
        panel.add(tradeButton);
        panel.add(dontKnowButton);

        return panel;
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JCheckBox clickedButton = (JCheckBox) e.getSource();

            // Perform action based on the clicked button
            if (clickedButton == universityButton) {
                userResponses.put("track", "University");
            } else if (clickedButton == collegeButton) {
                userResponses.put("track", "College");
            } else if (clickedButton == tradeButton) {
                userResponses.put("track", "Open");
            } else if (clickedButton == dontKnowButton) {
                userResponses.put("track", "Open");
            }
        }
    }

    private void selectButton(String track) {
        if ("University".equalsIgnoreCase(track)) {
            universityButton.setSelected(true);
        } else if ("College".equalsIgnoreCase(track)) {
            collegeButton.setSelected(true);
        } else if ("Trade".equalsIgnoreCase(track)) {
            tradeButton.setSelected(true);
        } else if ("null".equalsIgnoreCase(track)) {
            dontKnowButton.setSelected(true);
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
            if (!universityButton.isSelected() && !collegeButton.isSelected() &&
                    !tradeButton.isSelected() && !dontKnowButton.isSelected()) {
                NotificationManager.showNotification(NotificationManager.NotificationType.WARNING, "You must select a career path option.");
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




