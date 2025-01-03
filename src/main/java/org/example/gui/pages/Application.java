package org.example.gui.pages;

// Importing necessary libraries for GUI components, themes, notifications, and other utilities
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import org.example.gui.pages.login.LoginForm;
import org.example.gui.manager.FormsManager;
import org.example.utility.api.APIClient;
import org.example.utility.api.PythonAPI;
import raven.toast.Notifications;

import javax.swing.*;
import java.awt.*;

/**
 * Main application class for the Course Recommender.
 * Extends JFrame to provide a windowed interface for the application.
 */
public class Application extends JFrame {
    private static Application instance;
    /**
     * Constructor for the Application class.
     * Initializes the application by calling the init() method.
     */
    public Application() {
        instance = this;
        init();
    }

    /**
     * Getter for the current application instance.
     * @return the Application instance
     */
    public static Application getInstance() {
        return instance;
    }

    /**
     * Initializes the main application window.
     * Sets up the title, size, location, and initial content pane.
     * Configures notifications and initializes the FormsManager.
     */
    private void init() {
        // Set the title of the application window
        setTitle("Course Recommender");

        // Specify the default close operation to exit the application when the window is closed
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set the initial size of the application window
        setSize(new Dimension(1500, 900));

        // Optional: Uncomment to prevent resizing of the window
        // setResizable(false);

        // Center the window on the screen
        setLocationRelativeTo(null);

        // Set the initial content pane to the login form
        setContentPane(new LoginForm());

        // Configure the Notifications framework with the main JFrame
        Notifications.getInstance().setJFrame(this);

        // Initialize the application using FormsManager
        FormsManager.getInstance().initApplication(this);
    }

    /**
     * Main method to launch the application.
     * Sets up the look and feel, runs the Python API, and starts the GUI.
     *
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("themes");
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
        FlatMacDarkLaf.setup();

        // Start the Python API server (local and cloud) for ML
        PythonAPI.runAPI();
        APIClient.runAPI();

        // Schedule the GUI to be created and displayed on the Event Dispatch Thread
        EventQueue.invokeLater(() -> new Application().setVisible(true));
    }
}
