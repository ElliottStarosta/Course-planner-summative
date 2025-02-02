package org.example.utility.api;

import java.io.IOException;

/**
 * The {@code PythonAPI} class implements the {@code Deployment} interface and provides
 * functionality to run a Python script as an external process from a Java application.
 *
 * <p>This class is responsible for invoking a Python script located on the system and
 * managing the process. It ensures that the script is started and terminated correctly,
 * even if the Java application ends unexpectedly.</p>
 */
public class PythonAPI implements Deployment {

    /**
     * Runs the Python script using the ProcessBuilder.
     * <p>
     * This method starts a Python script located at a specified path. The Python
     * script is executed as an external process, and a shutdown hook is added to ensure
     * that the process is terminated when the Java application is stopped or exits.
     * </p>
     *
     */
    public static void runAPI() {
        // Used https://www.baeldung.com/java-lang-processbuilder-api to run py script
        try {
            // Specify the command to run the Python script
            ProcessBuilder processBuilder = new ProcessBuilder("python", "main.py"); // location of the script

            // Set the directory where the Python script is located
            processBuilder.directory(new java.io.File("C:\\Users\\fence\\Desktop\\APICourseSummative\\CoursesSummative"));

            // Start the Python script as an external process
            Process process = processBuilder.start();

            // Add a shutdown hook to terminate the Python script when the Java application ends
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                process.destroy(); // Terminate the Python process
            }));
        } catch (IOException e) {
            // Print any errors encountered while starting the Python process
            e.printStackTrace();
        }
    }
}