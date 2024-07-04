package org.example;

import java.io.File;
import net.sourceforge.tess4j.*;

public class PreviousClassReader {
    public static String getCreditsEarned(String imagePath) {
        // Set the tessdata directory path
        String tessDataDir = "C:\\Program Files\\Tesseract-OCR\\tessdata";

        // Initialize Tesseract instance
        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath(tessDataDir);

        // Specify language data file (eng.traineddata) if necessary
        // tesseract.setLanguage("eng");

        // Set path to image file for OCR
        File imageFile = new File(imagePath);

        String result = null;
        try {
            // Perform OCR on the image
            result = tesseract.doOCR(imageFile);
        } catch (TesseractException e) {
            System.err.println("Error while performing OCR: " + e.getMessage());
        }
        return result;
    }
}
