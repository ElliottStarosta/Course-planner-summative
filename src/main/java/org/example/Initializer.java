package org.example;

public class Initializer {
    public static void startUp() {
        // Starts up the API on start as without inactivity there is a ~50s bootup time
        APIClient.getAPIData("");
    }
}
