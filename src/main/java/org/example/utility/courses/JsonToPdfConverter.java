package org.example.utility.courses;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import org.example.gui.manager.NotificationManager;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonToPdfConverter {

    public static void convertJsonToPdf(String name, String username) throws IOException {
        // Prompt user to select the file path for the PDF
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify where to save the PDF file");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // Set default file name
        fileChooser.setSelectedFile(new java.io.File(String.format("%sCourseRecommendation.pdf",name)));

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        String pdfFilePath = fileChooser.getSelectedFile().getAbsolutePath();

        // If the user did not specify a file extension, add ".pdf" to the file name
        if (!pdfFilePath.toLowerCase().endsWith(".pdf")) {
            pdfFilePath += ".pdf";
        }

        String jsonFilePath = "src/main/resources/user_class_info/recommended_course_name_" + username + ".json";

        // Read JSON file
        String jsonString = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
        JSONArray jsonArray = new JSONArray(jsonString);

        // Initialize PDF writer and document
        PdfWriter writer = new PdfWriter(pdfFilePath);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Add the EOM logo
        String imagePath = "src/main/resources/assets/EOM_Logo.png";
        ImageData imageData = ImageDataFactory.create(imagePath);
        Image image = new Image(imageData);

        float maxWidth = 100;
        float maxHeight = 100;

        float originalWidth = image.getImageWidth();
        float originalHeight = image.getImageHeight();
        float aspectRatio = originalWidth / originalHeight;

        // Calculate the new dimensions while preserving the aspect ratio
        float newWidth, newHeight;
        if (originalWidth > originalHeight) {
            newWidth = Math.min(maxWidth, originalWidth);
            newHeight = newWidth / aspectRatio;
            if (newHeight > maxHeight) {
                newHeight = maxHeight;
                newWidth = newHeight * aspectRatio;
            }
        } else {
            newHeight = Math.min(maxHeight, originalHeight);
            newWidth = newHeight * aspectRatio;
            if (newWidth > maxWidth) {
                newWidth = maxWidth;
                newHeight = newWidth / aspectRatio;
            }
        }

        image.setWidth(newWidth);
        image.setHeight(newHeight);

        // Center the image
        image.setHorizontalAlignment(HorizontalAlignment.CENTER);

        document.add(image);

        // Add a title
        document.add(new Paragraph(String.format("%s's Course Recommendations", name))
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER));

        // Define table structure
        Table table = new Table(new float[]{3, 2}); // Adjust column widths as needed
        table.addHeaderCell(new Cell().add(new Paragraph("Grade")).setBackgroundColor(new DeviceRgb(0, 128, 0))); // Green background for header
        table.addHeaderCell(new Cell().add(new Paragraph("Courses")).setBackgroundColor(new DeviceRgb(0, 128, 0))); // Green background for header

        // Parse JSON and add to table
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String courses = jsonObject.getString("courses");
            int grade = jsonObject.getInt("grade");

            table.addCell(new Paragraph(String.valueOf(grade)));
            table.addCell(new Paragraph(courses));
        }

        document.add(table);

        // Close document
        document.close();

        NotificationManager.showNotification(NotificationManager.NotificationType.SUCCESS, "Successfully exported courses");
    }
}
