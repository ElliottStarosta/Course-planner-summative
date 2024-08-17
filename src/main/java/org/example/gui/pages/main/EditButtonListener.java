package org.example.gui.pages.main;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditButtonListener implements ActionListener {
    private String[] gradeData;
    private JComboBox[] courseName;
    private JButton button;
    private boolean isEditing = false;

    public EditButtonListener(String[] gradeData, JComboBox[] courseName, JButton button) {
        this.gradeData = gradeData;
        this.courseName = courseName;
        this.button = button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isEditing) {
            // Save changes
            for (int i = 0; i < 8; i++) {
                gradeData[i + 1] = (String) courseName[i].getSelectedItem();
            }
            button.setText("Edit");
        } else {
            // Switch to edit mode
            button.setText("Save");
        }
        isEditing = !isEditing;

        // Update courseName JComboBoxes based on the current mode
        for (JComboBox cb : courseName) {
            cb.setEnabled(isEditing);
        }
    }
}
