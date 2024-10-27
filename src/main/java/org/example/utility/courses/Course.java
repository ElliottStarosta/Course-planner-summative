package org.example.utility.courses;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.gui.manager.FormsManager;
import org.example.gui.pages.quiz.FillCourses;
import org.example.people.StudentInput;
import org.example.utility.api.APIClient;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.example.utility.courses.CourseAssembly.*;
import java.util.concurrent.CountDownLatch;



public class Course {
    private String courseCode;
    private String courseName;
    private String courseArea;
    private String prerequisites;
    private int gradeLevel;
    private String track;
    private String graduationRequirement;
    private static final int MAX_COURSES_PER_GRADE = 8;

    private static ArrayList<String> apiCourses = null;
    private static AtomicBoolean hasAPI = new AtomicBoolean(false);

    public Course(String courseCode, String courseName, String courseArea, String prerequisites, int gradeLevel, String track, String graduationRequirement) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.courseArea = courseArea;
        this.prerequisites = prerequisites;
        this.gradeLevel = gradeLevel;
        this.track = track;
        this.graduationRequirement = graduationRequirement;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getCourseArea() {
        return courseArea;
    }

    public String getPrerequisites() {
        return prerequisites;
    }

    public int getGradeLevel() {
        return gradeLevel;
    }

    public String getTrack() {
        return track;
    }

    public String getGraduationRequirement() {
        return graduationRequirement;
    }

    private void engine(StudentInput student) {

        // Checks if course is at or above your grade and if it is on your track
        if (track.equals("Open")) {
            if (student.getGrade() > gradeLevel) {
                return; // does not meet the requirements
            }
        } else {
            if (student.getGrade() > gradeLevel || !student.getTrack().equals(track)) {
                return; // does not meet the requirements
            }
        }

        // Checks if your course has been already taken
        String[] previousCoursesArray = student.getPreviousCourses().split("\\s*,\\s*");

        if (Arrays.asList(previousCoursesArray).contains(courseCode)) {
            return; // does not meet the requirements
        }

        // Adds the course if it passes the first stage, and then it finds all the prerequisites with recursion
        addCourse(student);
        if (!"none".equals(prerequisites)) {
            Course prerequisiteCourse = getCourse(prerequisites);
            if (prerequisiteCourse != null) {
                prerequisiteCourse.engine(student);
            }
        }
    }

    private void addCourse(StudentInput student) {
        int studentGrade = student.getGrade();
        int courseGrade = getGradeLevel();

        String courseCode = getCourseCode();

        if (courseGrade >= studentGrade) {
            String[] coursesForGrade = recommendedCoursesByGrade.get(courseGrade);

            if (Arrays.asList(coursesForGrade).contains(courseCode)) {
                return; // does not add duplicates
            }

            // Adds the course to the empty slot
            for (int i = 0; i < MAX_COURSES_PER_GRADE; i++) {
                if (coursesForGrade[i] == null) {
                    coursesForGrade[i] = courseCode;
                    break;
                }
            }
        }
    }


    public static void fulfillGradRequirements() {

        String[] courses = findUnfulfilledCredits();

        // Track recommended courses and grad credits to avoid duplicates
        Set<String> recommendedCourses = new HashSet<>();
        Set<String> recommendedGradCredits = new HashSet<>();

        List<Integer> openSpots = findOpenSpotsInRecommendedCourses(); // Start with the initial course grades

        for (String course : courses) {
            Course addedCourse = null;
            int courseGrade = 9;

            // Loop to find an available grade level with an open spot
            for (int index = 0; index < openSpots.size(); index++) {
                int currentCourseGrade = openSpots.get(index);

                // Attempt to add the course without prerequisites at the current grade level
                addedCourse = findNextCourseWithNoPrerequisites(course, currentCourseGrade, recommendedCourses, recommendedGradCredits);

                if (addedCourse != null) {
                    courseGrade = currentCourseGrade;
                    break; // Exit the loop if we successfully add a course
                }
            }

            if (addedCourse == null) {
                System.out.println("No available grade level found for course " + course);
                continue; // Move to the next course if this one can't be added
            }

            // Get courses for the selected grade level
            String[] coursesForGrade = recommendedCoursesByGrade.get(courseGrade);

            // Add to recommended courses set
            recommendedCourses.add(addedCourse.getCourseCode());
            recommendedGradCredits.add(course);

            // Subtract credit for the added course
            if (credits.containsKey(addedCourse.getCourseArea()) && credits.get(addedCourse.getCourseArea()) > 0) {
                credits.put(addedCourse.getCourseArea(), credits.get(addedCourse.getCourseArea()) - 1);
            } else if (credits.containsKey(addedCourse.getGraduationRequirement()) && credits.get(addedCourse.getGraduationRequirement()) > 0) {
                credits.put(addedCourse.getGraduationRequirement(), credits.get(addedCourse.getGraduationRequirement()) - 1);
            }

            // Adds the course to the first available slot in the grade
            boolean added = false;
            for (int i = 0; i < MAX_COURSES_PER_GRADE; i++) {
                if (coursesForGrade[i] == null) {
                    coursesForGrade[i] = addedCourse.getCourseCode();
                    added = true;
                    break;
                }
            }

            if (!added) {
                System.out.println("No space available to add course for grade level " + courseGrade);
            }
        }
    }



    private static Course findNextCourseWithNoPrerequisites(String courseArea, int courseGrade, Set<String> recommendedCourses, Set<String> recommendedGradCredits) {
        for (Map.Entry<String, Course> entry : courseMap.entrySet()) {
            Course course = entry.getValue();
            if ((course.getCourseArea().equalsIgnoreCase(courseArea) || course.getGraduationRequirement().equalsIgnoreCase(courseArea))
                    && course.getPrerequisites().equalsIgnoreCase("none")
                    && !recommendedCourses.contains(course.getCourseCode())
                    && !recommendedGradCredits.contains(course.getCourseArea())
                    && courseGrade == course.getGradeLevel()) {
                return course;
            }
        }
        return null;
    }


    private static List<Integer> findOpenSpotsInRecommendedCourses() {
        List<Integer> openGrades = new ArrayList<>();

        for (Map.Entry<Integer, String[]> entry : recommendedCoursesByGrade.entrySet()) {
            String[] courses = entry.getValue();

            for (String course : courses) {
                if (course == null) {
                    openGrades.add(entry.getKey());
                    break; // Move to the next grade after finding an open spot in the current one
                }
            }
        }

        return openGrades;
    }


    private static String[] findUnfulfilledCredits() {
        ArrayList<String> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : credits.entrySet()) {
            if (entry.getValue() > 0) {
                result.add(entry.getKey());
            }
        }
        return result.toArray(new String[0]);
    }

    public static void addNonFilledClasses(StudentInput student) {

        boolean hasNull = recommendedCoursesByGrade.values().stream()
                .flatMap(Arrays::stream)
                .anyMatch(Objects::isNull);

        if (!hasNull) {
            return;
        }

        CountDownLatch latch = new CountDownLatch(1);

        FormsManager.getInstance().showForm(new FillCourses(student, latch));

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }

    }

    public static void getNonFilledClassesResponse(StudentInput student, String studentResponse) {
        System.out.println("filling");

        recommendedCoursesByGrade.forEach((grade, courses) -> {
            Set<String> recommendedCourses = new HashSet<>();
            Set<String> recommendedCourseArea = Arrays.stream(courses)
                    .map(CourseAssembly::getCourse) // Get course object for each code
                    .filter(Objects::nonNull) // Filter out null courses
                    .map(Course::getCourseArea) // Get the area
                    .collect(Collectors.toSet()); // Collect areas to a set

            for (int i = 0; i < courses.length; i++) {
                if (courses[i] == null) {
                    courses[i] = fillCourse(grade, recommendedCourses, recommendedCourseArea, courses, studentResponse, student);
                }
            }
        });
    }

    private static String fillCourse(int grade, Set<String> recommendedCourses, Set<String> recommendedCourseArea, String[] courses, String response, StudentInput studentInput) {
        if (!response.isEmpty()) {
            // Initialize apiCourses if it's null
            if (!hasAPI.get()) {
                apiCourses = APIClient.getAPIDataClasses(response);
                if (apiCourses.isEmpty()) {
                    // If API returns no courses, we will provide random classes
                    hasAPI.set(true);
                } else {
                    hasAPI.set(true);
                }
            }

            // If no courses are returned from the API
            List<String> filteredApiCourses = apiCourses.stream()
                    .filter(courseCode -> {
                        Course course = getCourse(courseCode);
                        return course != null && course.getGradeLevel() == grade
                                && !Arrays.asList(courses).contains(courseCode)
                                && (course.getTrack().equalsIgnoreCase(studentInput.getTrack()) || course.getTrack().equalsIgnoreCase("Open"));
                    })
                    .toList();

            // If filtered API courses are available, return one
            if (!filteredApiCourses.isEmpty()) {
                for (String courseCode : filteredApiCourses) {
                    Course course = getCourse(courseCode);
                    if (course != null && !recommendedCourses.contains(course.getCourseCode())) {
                        recommendedCourses.add(course.getCourseCode());
                        return course.getCourseCode();
                    }
                }
            }
        }

        // Provide a random class if no valid API courses found
        Random random = new Random();
        List<String> filteredKeys = courseMap.entrySet().stream()
                .filter(entry -> entry.getValue().getGradeLevel() == grade)
                .filter(entry -> (entry.getValue().getTrack().equalsIgnoreCase(studentInput.getTrack()) || entry.getValue().getTrack().equalsIgnoreCase("Open")))
                .filter(entry -> !Arrays.asList(courses).contains(entry.getValue().getCourseCode()))
                .map(Map.Entry::getKey)
                .toList();

        if (filteredKeys.isEmpty()) {
            return "E404"; // Handle case where no courses match the grade level
        }

        String randomKey;
        String courseArea;
        do {
            randomKey = filteredKeys.get(random.nextInt(filteredKeys.size()));
            courseArea = courseMap.get(randomKey).getCourseArea();
        } while (recommendedCourses.contains(randomKey) || recommendedCourseArea.contains(courseArea));

        recommendedCourses.add(randomKey);
        recommendedCourseArea.add(courseArea);
        return randomKey;
    }

    public static void writeRecommendedCoursesToFileCourseName(StudentInput studentInput) {
        try {
            String username = studentInput.getUsername();

            // Construct file path based on the username
            String filePath = "src/main/resources/user_class_info/recommended_course_name_" + username + ".json";

            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);

            List<Map<String, Object>> coursesList = new ArrayList<>();
            for (Map.Entry<Integer, String[]> entry : recommendedCoursesByGrade.entrySet()) {
                Map<String, Object> courseMap = new HashMap<>();
                List<String> courseNames = new ArrayList<>();
                for (String c : entry.getValue()) {
                    Course course = getCourse(c);
                    if (course != null) {
                        courseNames.add(String.format("%s - %s",course.getCourseCode(), course.getCourseName()));

                    }
                }
                String coursesString = String.join(", ", courseNames);
                courseMap.put("grade", entry.getKey());
                courseMap.put("courses", coursesString);
                coursesList.add(courseMap);
            }

            // Write to the JSON file based on the constructed file path
            mapper.writeValue(new File(filePath), coursesList);
            System.out.println("Recommended courses written to " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing recommended courses to file: " + e.getMessage());
        }
    }

    public static boolean readRecommendedCoursesFromFile(String username) {
        String filePath = "src/main/resources/user_class_info/recommended_course_name_" + username + ".json";
        File file = new File(filePath);

        if (!file.exists()) {
            return false;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            List<FileCourseData> courseDataList = mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, FileCourseData.class));

            if (courseDataList == null || courseDataList.isEmpty()) {
                System.out.println("File is empty or contains no courses: " + filePath);
                return false;
            }

            for (FileCourseData courseData : courseDataList) {
                recommendedCoursesByGrade.put(courseData.getGrade(), courseData.getCourses().split(", "));
            }

        } catch (IOException e) {
            System.out.println("Error reading file: " + filePath);
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static void runEngine(ArrayList<String> courses, StudentInput student) {
        for (String c : courses) {
            Course course = getCourse(c);
            if (course != null) {
                course.engine(student);
            }
        }
    }



    // Class made to do wrap the JSON data
    public static class FileCourseData {
        private String courses;
        private int grade;

        public String getCourses() {
            return courses;
        }

        public void setCourses(String courses) {
            this.courses = courses;
        }

        public int getGrade() {
            return grade;
        }

    }

    public static String[] cleanPreviousCourses(String previousClasses) {
        // Remove the surrounding brackets and quotes
        String cleanedString = previousClasses.replace("[", "").replace("]", "");

        Pattern pattern = Pattern.compile("\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(cleanedString);

        // Store each matched item in a list
        ArrayList<String> classList = new ArrayList<>();
        while (matcher.find()) {
            classList.add(matcher.group(1));
        }

        // Convert list to array if needed
        return classList.toArray(new String[0]);

    }
}