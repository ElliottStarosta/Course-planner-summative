package org.example.gui.pages;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import org.example.gui.pages.login.LoginForm;
import org.example.gui.manager.FormsManager;
import org.example.utility.api.PythonAPI;
import raven.toast.Notifications;

import javax.swing.*;
import java.awt.*;


public class Application extends JFrame {

    public Application() {
        init();
    }

    private void init() {
        setTitle("Course Recommender");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(1500, 900));
        setResizable(false);
        setLocationRelativeTo(null);
        setContentPane(new LoginForm());
        Notifications.getInstance().setJFrame(this);
        FormsManager.getInstance().initApplication(this);

    }

    public static void main(String[] args) {
        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("themes");
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
        FlatMacDarkLaf.setup();
        PythonAPI.runPythonAPI();
        EventQueue.invokeLater(() -> new Application().setVisible(true));
    }
}
