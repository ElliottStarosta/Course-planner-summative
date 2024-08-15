package org.example.utility.api;

import org.example.gui.manager.NotificationManager;
import org.example.people.Counselor;
import org.example.people.User;
import org.example.utility.courses.CourseAssembly;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class SendEmail {
    final String SENDER_EMAIL = "starlliott@gmail.com";
    final String SENDER_PASSWORD = CourseAssembly.readCredentialsFromFile()[0];
    final String EMAIL_SMTPSERVER = "smtp.gmail.com";
    final String EMAIL_SERVER_PORT = "465";


    public SendEmail() {
        // Constructor left empty for now; no initialization needed here
    }



    public void send2FA(String receiverEmail, String username, String subject, String generatedCode) {
        Properties props = new Properties();
        props.put("mail.smtp.user", SENDER_EMAIL);
        props.put("mail.smtp.host", EMAIL_SMTPSERVER);
        props.put("mail.smtp.port", EMAIL_SERVER_PORT);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.port", EMAIL_SERVER_PORT);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");

        try {
            Authenticator auth = new SMTPAuthenticator();
            Session session = Session.getInstance(props, auth);
            MimeMessage msg = new MimeMessage(session);

            // Construct HTML content for the Course Recommender 2FA
            String imageUrl = "https://cdnsm5-ss13.sharpschool.com/UserFiles/Servers/Server_232765/Image/EOM_Logo.png"; // Replace with your image URL

            String htmlBody =
                    "<html><body>" +
                            "<div style='font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4;'>" +
                            "<table width='100%' border='0' cellspacing='0' cellpadding='0'>" +
                            "<tr><td style='padding: 20px 0 30px 0;'>" +
                            "<table align='center' border='0' cellpadding='0' cellspacing='0' width='600' style='border: 1px solid #cccccc; border-collapse: collapse; background-color: #ffffff;'>" +
                            "<tr><td align='center' bgcolor='#285400' style='padding: 40px 0 30px 0; color: #fff808; font-size: 28px; font-weight: bold; font-family: Arial, sans-serif;'>" +
                            "<img src='" + imageUrl + "' alt='Course Recommender Logo' width='100px' height='auto' style='display: block; margin-bottom: 20px;' />" +
                            "EOM Course Recommender" +
                            "</td></tr>" +
                            "<tr><td align='center' style='padding: 20px;'>" +
                            "</td></tr>" +
                            "<tr><td bgcolor='#ffffff' style='padding: 40px 30px 40px 30px;'>" +
                            "<table border='0' cellpadding='0' cellspacing='0' width='100%'>" +
                            "<tr><td style='color: #333333; font-family: Arial, sans-serif; font-size: 16px;'>" +
                            "<p>Dear " + username + ",</p>" +
                            "<p>We received a request to sign in to your Course Recommender account.</p>" +
                            "<p>Use this code to complete your sign-in:</p>" +
                            "<p style='font-size: 24px; font-weight: bold; text-align: center; color: #4CAF50;'>" + generatedCode + "</p>" +
                            "<p>This code will expire in 10 minutes. If you did not request this code, please ignore this email.</p>" +
                            "</td></tr></table></td></tr>" +
                            "<tr><td bgcolor='#f4f4f4' style='padding: 30px 30px 30px 30px;'>" +
                            "<table border='0' cellpadding='0' cellspacing='0' width='100%'>" +
                            "<tr><td style='color: #999999; font-family: Arial, sans-serif; font-size: 12px;'>" +
                            "<p style='margin: 0;'>Thank you for using EOM Course Recommender.</p>" +
                            "<p style='margin: 0;'>&copy; 2024 EOM Course Recommender. All rights reserved.</p>" +
                            "</td></tr></table></td></tr>" +
                            "</table></td></tr></table></div></body></html>";

            NotificationManager.showNotification(NotificationManager.NotificationType.INFO, "Processing Request...");
            msg.setContent(htmlBody, "text/html");
            msg.setSubject(subject);
            msg.setFrom(new InternetAddress(SENDER_EMAIL));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(receiverEmail));
            Transport.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendCourses(Counselor counselor, User user) {
        Properties props = new Properties();
        props.put("mail.smtp.user", SENDER_EMAIL);
        props.put("mail.smtp.host", EMAIL_SMTPSERVER);
        props.put("mail.smtp.port", EMAIL_SERVER_PORT);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.port", EMAIL_SERVER_PORT);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");

        try {
            Authenticator auth = new SMTPAuthenticator();
            Session session = Session.getInstance(props, auth);
            MimeMessage msg = new MimeMessage(session);

            String filePath = "src/main/resources/user_class_info/recommended_course_code_" + user.getUsername() + ".json";
            String jsonData = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONArray jsonArray = new JSONArray(jsonData);

            String subject = String.format("%s %s's recommended courses", user.getFirstName(), user.getLastName());

            String htmlBody =
                    "<html>" +
                            "<head>" +
                            "<style>" +
                            "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                            "p { margin: 0 0 10px; color: #000000 }" +
                            "table { width: 100%; border-collapse: collapse; margin: 20px 0; }" +
                            "th, td { padding: 10px; text-align: left; border: 1px solid #ddd; }" +
                            "th { background-color: #f4f4f4; color: #333; }" +
                            "tr:nth-child(even) { background-color: #f9f9f9; }" +
                            "strong { color: #285400; }" +
                            "</style>" +
                            "</head>" +
                            "<body>" +
                            "<p>Dear " + counselor.getName() + ",</p>" +
                            "<p><strong>" + user.getFirstName() + " " + user.getLastName() + "</strong> has completed the recommended course application, and here is the list of their desired courses to take:</p>" +
                            "<table>" +
                            "<tr><th>Grade</th><th>Courses</th></tr>";

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int grade = jsonObject.getInt("grade");
                String courses = jsonObject.getString("courses");
                htmlBody += "<tr><td>" + grade + "</td><td>" + courses + "</td></tr>";
            }


            msg.setContent(htmlBody, "text/html");
            msg.setSubject(subject);
            msg.setFrom(new InternetAddress(SENDER_EMAIL));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(counselor.getEmail()));
            Transport.send(msg);
            System.out.println("Message sent successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class SMTPAuthenticator extends javax.mail.Authenticator {
        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
        }
    }

    public static void main(String[] args) {
        SendEmail emailSender = new SendEmail();
        emailSender.send2FA("starlliott@gmail.com", "Elliott","Course Recommender - 2FA Code", "759235");
    }

}
