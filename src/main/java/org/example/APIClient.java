package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class APIClient {

    private static final int CONNECTION_TIMEOUT = 10000; // 10 seconds
    private static final int READ_TIMEOUT = 10000; // 10 seconds

    public static ArrayList<String> getAPIData(String interests) {
        ArrayList<String> courses = new ArrayList<>();

        // First URL
        String firstUrl = "https://coursesapi-84sd.onrender.com/recommend-courses/";

        // Second URL (backup)
        String secondUrl = "http://127.0.0.1:8000/recommend-courses/";

        try {
            String encodedInterests = URLEncoder.encode(interests, "UTF-8");

            // Attempt to fetch data from the first URL
            String responseData = fetchDataFromUrl(firstUrl, encodedInterests);

            // If first attempt fails, wait and try the second URL
            if (responseData == null) {
                Thread.sleep(10000); // Wait for 10 seconds before retrying
                responseData = fetchDataFromUrl(secondUrl, encodedInterests);
            }

            // Handle JSON parsing
            if (responseData != null) {
                JSONArray jsonArray = new JSONArray(responseData);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String courseCode = jsonObject.getString("Course Code");
                    courses.add(courseCode);
                }
            } else {
                System.out.println("Failed to fetch data from both URLs.");
            }

        } catch (Exception e) {
            System.out.println("");
//            e.printStackTrace();
        }
        return courses;
    }

    private static String fetchDataFromUrl(String apiUrl, String encodedInterests) {
        try {
            URL url = new URL(String.format("%s?interests=%s", apiUrl, encodedInterests));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Set timeouts
            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);

            conn.connect();

            // Read the response
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();

        } catch (Exception e) {
            // Handle connection or read timeout
            System.out.println("Failed to fetch data from URL: " + apiUrl);
            return null;
        }
    }
}
