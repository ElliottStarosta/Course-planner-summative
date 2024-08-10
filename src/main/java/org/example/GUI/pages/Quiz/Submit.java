package org.example.GUI.pages.Quiz;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import org.example.GUI.component.ComboBox;
import org.example.GUI.component.NotificationManager;
import org.example.GUI.manager.FormsManager;

import javax.swing.*;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.example.utility.ExcelUtility.getAllCourseNames;


public class Submit extends JPanel {

    private HashMap<String, String> userResponses;
    private int question;
    private static final int PANEL_WIDTH = 600;


    private JLabel questionTitle;
    private ComboBox courseComboBox;

    private JButton nextButton;
    private JButton backButton;



    public Submit(HashMap<String,String> userResponses, int question) {
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
        combo.setModel(new DefaultComboBoxModel<>(getAllCourseNames()));
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new MigLayout("wrap, fillx, insets 35 45 30 45", "[grow]"));
        contentPanel.setPreferredSize(new Dimension(PANEL_WIDTH, 300));
        contentPanel.setFocusable(true);

        contentPanel.putClientProperty(FlatClientProperties.STYLE,
                "arc:20;" +
                        "[light]background:darken(@background,3%);" +
                        "[dark]background:lighten(@background,3%)");

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

        if (userResponses.containsKey("previousClasses")) {
            String previousClasses = userResponses.get("previousClasses");
            List<String> classes = Arrays.asList(previousClasses.split(","));

            for (String c : classes) {
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
        List<Object> selectedItems = courseComboBox.getSelectedItems();
        String selectedItemsString = selectedItems.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));

        userResponses.put("previousClasses", selectedItemsString);
        int grade = Integer.parseInt(userResponses.get("grade"));
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
                NotificationManager.showNotification(NotificationManager.NotificationType.WARNING,    String.format("You need to select at least %d classes.", requiredClasses));

                return;
            }

            question++;
            Object formInstance = DynamicFormLoader.loadForm(question, userResponses);
            if (formInstance != null) {
                FormsManager.getInstance().showForm((JComponent) formInstance);
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
