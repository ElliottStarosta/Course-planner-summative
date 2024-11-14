package org.example.utility.api;

import java.io.IOException;

public class PythonAPI {

    public static void runPythonAPI() {
        try {

            ProcessBuilder processBuilder = new ProcessBuilder("py","main.py"); // location of the script
            processBuilder.directory(new java.io.File("C:\\Users\\fence\\Desktop\\APICourseSummative\\CoursesSummative")); // change to the directory
            Process process = processBuilder.start(); // Start the script

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                process.destroy(); // End script when java application ends
            }));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
