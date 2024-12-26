package org.example.gui.manager;

import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import org.example.gui.pages.Application;

import javax.swing.*;
import java.awt.*;

/**
 * Manages the display of forms within the application, ensuring a smooth transition
 * with animations using FlatLaf's animation features.
 */
public class FormsManager {
    /**
     * The application instance where forms are displayed.
     */
    private Application application;

    /**
     * Singleton instance of the FormsManager.
     */
    private static FormsManager instance;

    /**
     * Provides the singleton instance of the FormsManager. If it does not exist, it is created.
     *
     * @return The singleton instance of the FormsManager.
     */
    public static FormsManager getInstance() {
        if (instance == null) {
            instance = new FormsManager();
        }
        return instance;
    }

    /**
     * Private constructor to enforce the singleton pattern.
     */
    private FormsManager() {
    }

    /**
     * Initializes the FormsManager with the given application instance.
     * This sets up the application where forms will be displayed.
     *
     * @param application The application instance to manage forms for.
     */
    public void initApplication(Application application) {
        this.application = application;
    }

    /**
     * Displays the given form (a {@link JComponent}) in the application,
     * replacing the current content pane. Uses FlatLaf animations to provide
     * a smooth transition effect.
     *
     * @param form The form to be displayed, represented as a {@link JComponent}.
     */
    public void showForm(JComponent form) {
        EventQueue.invokeLater(() -> {
            FlatAnimatedLafChange.showSnapshot();
            application.setContentPane(form);
            application.revalidate();
            application.repaint();
            FlatAnimatedLafChange.hideSnapshotWithAnimation();
        });
    }
}
