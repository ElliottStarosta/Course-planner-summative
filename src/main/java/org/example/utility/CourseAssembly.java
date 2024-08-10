package org.example.utility;

import org.example.people.Counselor;
import org.example.people.StudentInput;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;



//TODO: Take out french immersion classes from workbook

public class CourseAssembly {
    protected static Map<String, Course> courseMap = new HashMap<>();
    public static Map<Integer, String[]> recommendedCoursesByGrade = new HashMap<Integer, String[]>();
    static final String CREDENTIALS_FILE = "C:\\Users\\fence\\OneDrive\\Desktop\\credentials.txt";


    private static AtomicBoolean coursesInitialized = new AtomicBoolean(false);


    // Required Credits to Graduate
    public static HashMap<String, Integer> credits = new HashMap<>() {{

        put("Arts", 1);                          // 1 credit in the Arts
        put("Health & Physical Education", 1); // 1 credit in Health and Physical Education
        put("French", 1);                        // 1 credit in French as a Second Language

        // Group credits
        put("1.0", 1); // 1 additional credit from Group 1
        put("2.0", 1); // 1 additional credit from Group 2
        put("3.0", 1); // 1 additional credit from Group 3
    }};


    public static void addInitialCourses(StudentInput student) {
        if (!coursesInitialized.get()) {
            // Add courses based on track
            if ("university".equals(student.getTrack().toLowerCase())) {

                // University
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


            List<String> previousCourses = new ArrayList<>(Arrays.asList(student.getPreviousCourses().split("\\s*,\\s*|\\s+")));


            previousCourses.forEach(courseCode -> {
                Course course = getCourse(courseCode);

                if (course != null) {
                    int courseGradeLevel = course.getGradeLevel();
                    String courseType = course.getCourseArea();
                    String gradRequirement = course.getGraduationRequirement();

                    // Find the corresponding grade key and array
                    String[] coursesArray = recommendedCoursesByGrade.get(courseGradeLevel);

                    if (coursesArray != null) {
                        // Check if the course is already in the array
                        boolean courseAlreadyAdded = Arrays.stream(coursesArray)
                                .filter(Objects::nonNull)
                                .anyMatch(existingCourseCode -> existingCourseCode.equals(course.getCourseCode()));


                        if (!courseAlreadyAdded) {
                            // Find the first available slot and add the course
                            IntStream.range(0, coursesArray.length)
                                    .filter(i -> coursesArray[i] == null)
                                    .findFirst()
                                    .ifPresent(index -> coursesArray[index] = course.getCourseCode());

                            // Update credits
                            if (credits.containsKey(courseType) && credits.get(courseType) > 0) {
                                credits.put(courseType, credits.get(courseType) - 1); // Subtract one credit for the course type
                            } else if (credits.containsKey(gradRequirement) && credits.get(gradRequirement) > 0) {
                                credits.put(gradRequirement, credits.get(gradRequirement) - 1); // Subtract one credit for the graduation requirement
                            }
                        }

                    }

                }
            });

            coursesInitialized.set(true);

        }
    }


    public CourseAssembly() {
        ExcelUtility.loadCourseData();
    }


    public static Course getCourse(String courseCode) {
        return courseMap.get(courseCode);
    }

    public static String[] readCredentialsFromFile() {

        try (BufferedReader reader = new BufferedReader(new FileReader(CREDENTIALS_FILE))) {
            String passwordLine = reader.readLine();
            String APILine = reader.readLine();

            // Extract username and password from their respective lines
            String password = passwordLine.split(":")[1].trim();
            String[] API = APILine.split(":");
            String APIJoined = API[1].trim() + ":" + API[2].trim();

            return new String[] {password, APIJoined};
        } catch (IOException | NullPointerException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return new String[] {null, null};
        }
    }


    public static void runAssessment(StudentInput student) {

        new CourseAssembly();

        ArrayList<String> courses = APIClient.getAPIDataClasses(student.getInterests());

        CourseAssembly.addInitialCourses(student);

        Course.fulfillGradRequirements(); // If graduation requirements have not been meet, fill them with classes
        Course.runEngine(courses, student); // Find the recommended classes

        Course.addNonFilledClasses(student); // Add non filled classes with random classes

        // TODO : Make it so that the user enters data here for it for the acutal GUI
//        String cc = "Sph3u"; // course code
//        String rcp = "AMI3M"; // replacement course code
//
//        Course.findAndReplaceCourse(cc, rcp);

        Course.writeRecommendedCoursesToFileCourseName(student); // write course name to respective JSON file
        Course.writeRecommendedCoursesToFileCourseCode(student); // write course code to respective JSON file

        // Send email w/ classes to the student's counselor
        Counselor.sendCounselorEmail(student, new SendEmail());


    }
}