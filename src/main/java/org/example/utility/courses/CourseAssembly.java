package org.example.utility.courses;

import org.example.people.UserInput;
import org.example.utility.api.APIClient;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

/**
 * The CourseAssembly class is responsible for managing course data,
 * including recommending courses based on user input, reading credentials,
 * and running course assessments.
 */
public class CourseAssembly {
    /**
     * Map that stores course codes and their corresponding Course objects.
     */
    protected static Map<String, Course> courseMap = new HashMap<>();
    public static Map<Integer, String[]> recommendedCoursesByGrade = new HashMap<>();
    public static final String CREDENTIALS_FILE = "C:\\Users\\fence\\OneDrive\\Desktop\\credentials.txt";

    // Required Credits to Graduate
    public static HashMap<String, Integer> credits = new HashMap<>() {{
        put("Arts", 1); // 1 credit in the Arts
        put("Health & Physical Education", 1); // 1 credit in Health and Physical Education
        put("French", 1); // 1 credit in French as a Second Language

        // Group credits
        put("1.0", 1); // 1 additional credit from Group 1
        put("2.0", 1); // 1 additional credit from Group 2
        put("3.0", 1); // 1 additional credit from Group 3
    }};

    /**
     * Adds initial courses to the recommended courses list based on the user's track
     * (e.g., university or college) and previously completed courses.
     *
     * @param student The UserInput object representing the student.
     */
    public static void addInitialCourses(UserInput student) {
        if ("university".equals(student.getTrack().toLowerCase())) {
            recommendedCoursesByGrade = new HashMap<>() {{
                put(9, new String[]{"ENL1W", "MTH1W", "SNC1W", "CGC1W", null, null, null, null});
                put(10, new String[]{"ENG2D", "MPM2D", "SNC2D", "CHC2D", "CHV2O", null, null, null});
                put(11, new String[]{"NBE3U", "MCR3U", null, null, null, null, null, null});
                put(12, new String[]{"ENG4U", "MHF4U", "MCV4U", null, null, null, null, null});
            }};
        } else { // College
            recommendedCoursesByGrade = new HashMap<>() {{
                put(9, new String[]{"ENL1W", "MTH1W", "SNC1W", "CGC1W", null, null, null, null});
                put(10, new String[]{"ENG2D", "MPM2D", "SNC2D", "CHC2D", "CHV2O", null, null, null});
                put(11, new String[]{"NBE3C", "MBF3C", null, null, null, null, null, null});
                put(12, new String[]{"ENG4C", null, null, null, null, null, null, null});
            }};
        }

        List<String> previousCoursesTemp = new ArrayList<>(Arrays.asList(Course.cleanPreviousCourses(student.getPreviousCourses())));
        ArrayList<String> previousCourses = new ArrayList<>();
        for (String course : previousCoursesTemp) {
            String courseCode = course.split(" - ")[0];
            previousCourses.add(courseCode);
        }
        previousCourses.forEach(courseCode -> {
            Course course = getCourse(courseCode);

            if (course != null) {
                int courseGradeLevel = course.getGradeLevel();
                String courseType = course.getCourseArea();
                String gradRequirement = course.getGraduationRequirement();

                String[] coursesArray = recommendedCoursesByGrade.get(courseGradeLevel);
                if (coursesArray != null) {
                    boolean courseAlreadyAdded = Arrays.stream(coursesArray)
                            .filter(Objects::nonNull)
                            .anyMatch(existingCourseCode -> existingCourseCode.equals(course.getCourseCode()));

                    if (!courseAlreadyAdded) {
                        IntStream.range(0, coursesArray.length)
                                .filter(i -> coursesArray[i] == null)
                                .findFirst()
                                .ifPresent(index -> coursesArray[index] = course.getCourseCode());

                        if (credits.containsKey(courseType) && credits.get(courseType) > 0) {
                            credits.put(courseType, credits.get(courseType) - 1);
                        } else if (credits.containsKey(gradRequirement) && credits.get(gradRequirement) > 0) {
                            credits.put(gradRequirement, credits.get(gradRequirement) - 1);
                        }
                    }
                }
            }
        });
    }

    /**
     * Constructor for the CourseAssembly class.
     * Loads the course data from the ExcelUtility.
     */
    public CourseAssembly() {
        ExcelUtility.loadCourseData();
    }

    /**
     * Retrieves a course from the course map based on its course code.
     *
     * @param courseCode The course code to search for.
     * @return The Course object, or null if not found.
     */
    public static Course getCourse(String courseCode) {
        return courseMap.get(courseCode);
    }

    /**
     * Reads credentials (e.g., API key and password) from a file.
     *
     * @return An array containing the password and API key, or null values if an error occurs.
     */
    public static String[] readCredentialsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CREDENTIALS_FILE))) {
            String passwordLine = reader.readLine();
            String APILine = reader.readLine();

            String password = passwordLine.split(":")[1].trim();
            String[] API = APILine.split(":");
            String APIJoined = API[1].trim() + ":" + API[2].trim();

            return new String[]{password, APIJoined};
        } catch (IOException | NullPointerException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return new String[]{null, null};
        }
    }

    /**
     * Runs the course recommendation and assessment process for a student.
     *
     * @param student The UserInput object representing the student.
     */
    public static void runAssessment(UserInput student) {
        new CourseAssembly();

        ArrayList<String> courses = APIClient.getAPIDataClasses(student.getInterests());
        CourseAssembly.addInitialCourses(student);
        Course.fulfillGradRequirements();
        Course.runEngine(courses, student);
        Course.addNonFilledClasses(student);
        Course.writeRecommendedCoursesToFileCourseName(student);
    }
}