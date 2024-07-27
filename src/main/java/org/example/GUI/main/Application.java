package org.example.GUI.main;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import org.example.GUI.login.Login;
import org.example.GUI.manager.FormsManager;
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
        setSize(new Dimension(1200, 700));
        setResizable(false);
        setLocationRelativeTo(null);
        setContentPane(new Login());
//        setContentPane(new PasswordChange());
        Notifications.getInstance().setJFrame(this);
        FormsManager.getInstance().initApplication(this);
    }

    public static void main(String[] args) {
        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("themes");
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
        FlatMacDarkLaf.setup();
        EventQueue.invokeLater(() -> new Application().setVisible(true));
    }
}