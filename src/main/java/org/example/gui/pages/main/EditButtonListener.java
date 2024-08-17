package org.example.gui.pages.main;

import com.formdev.flatlaf.FlatClientProperties;
import org.example.gui.manager.NotificationManager;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class EditButtonListener implements ActionListener {
    private String[] gradeData;
    private JComboBox[] courseName;
    private JButton button;
    private boolean isEditing = false;
    private String username;
    private String[][] data;

    public EditButtonListener(String[] gradeData, JComboBox<String>[] courseName, JButton button, String username, String[][] data) {
        this.gradeData = gradeData;
        this.courseName = courseName;
        this.button = button;
        this.username = username;
        this.data = data;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isEditing) {
            // Save changes
            String[] selectedCourses = new String[8];
            for (int i = 0; i < 8; i++) {
                selectedCourses[i] = (String) courseName[i].getSelectedItem();
            }

            // Check for duplicate courses
            if (hasDuplicates(selectedCourses, courseName)) {
                NotificationManager.showNotification(NotificationManager.NotificationType.WARNING, "Please ensure there are no duplicate classes before saving");
                return; // Prevent saving
            }

            // Update gradeData with new values
            System.arraycopy(selectedCourses, 0, gradeData, 1, selectedCourses.length);

            // Update data array with new values
            for (int i = 0; i < data.length; i++) {
                if (data[i][0].equals(gradeData[0])) {
                    System.arraycopy(gradeData, 1, data[i], 1, gradeData.length - 1);
                    break;
                }
            }

            // Write updated data to JSON file
            writeRecommendedCoursesToFile(data, username);

            // Reset combo boxes back to their original color
            resetComboBoxBackgrounds(courseName);

            button.setText("Edit");
        } else {
            // Switch to edit mode
            button.setText("Save");
        }
        isEditing = !isEditing;

        // Update courseName JComboBoxes based on the current mode
        for (JComboBox<String> cb : courseName) {
            cb.setEnabled(isEditing);
        }
    }

    private boolean hasDuplicates(String[] courses, JComboBox<String>[] comboBoxes) {
        Set<String> uniqueCourses = new HashSet<>();
        boolean hasDuplicates = false;

        // Reset all combo boxes to default background
        resetComboBoxBackgrounds(comboBoxes);

        for (int i = 0; i < courses.length; i++) {
            if (courses[i] != null && !courses[i].isEmpty()) {
                if (!uniqueCourses.add(courses[i])) {
                    // Duplicate found
                    comboBoxes[i].setBackground(lightenColor(UIManager.getColor("ComboBox.background"), 0.3f));
                    hasDuplicates = true;
                }
            }
        }
        return hasDuplicates;
    }

    private void resetComboBoxBackgrounds(JComboBox<String>[] comboBoxes) {
        for (JComboBox<String> cb : comboBoxes) {
            cb.setBackground(UIManager.getColor("ComboBox.background"));
        }
    }

    private Color lightenColor(Color color, float factor) {
        int r = (int) (color.getRed() * (1 - factor));
        int g = (int) (color.getGreen() * (1 - factor));
        int b = (int) (color.getBlue() * (1 - factor));
        return new Color(r, g, b);
    }



    public static void writeRecommendedCoursesToFile(String[][] data, String username) {
        String filename = "src/main/resources/user_class_info/recommended_course_name_" + username + ".json";
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < data.length; i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("grade", Integer.parseInt(data[i][0].replaceAll("\\D", ""))); // Extract grade number
            StringBuilder courses = new StringBuilder();

            // Collect up to 8 courses
            for (int j = 1; j < data[i].length; j++) {
                if (data[i][j] != null && !data[i][j].isEmpty()) {
                    if (courses.length() > 0) {
                        courses.append(", ");
                    }
                    courses.append(data[i][j]);
                }
            }

            jsonObject.put("courses", courses.toString());
            jsonArray.put(jsonObject);
        }

        // Write the JSONArray to a file
        try (FileWriter file = new FileWriter(filename)) {
            file.write(jsonArray.toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
