package org.example.utility.courses;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.gui.manager.FormsManager;
import org.example.gui.pages.quiz.FillCourses;
import org.example.people.UserInput;
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


/**
 * The Course class represents a course within the system, providing functionality
 * for managing course prerequisites, fulfilling graduation requirements,
 * and tracking courses per grade level. It is a key part of a larger system
 * that recommends courses based on the student's grade, track, and previous
 * course history.
 */
public class Course {
    /**
     * The code uniquely identifying the course.
     */
    private String courseCode;

    /**
     * The name of the course.
     */
    private String courseName;

    /**
     * The academic area or department to which the course belongs.
     */
    private String courseArea;

    /**
     * The prerequisites required to enroll in the course.
     */
    private String prerequisites;

    /**
     * The grade level for which the course is designed.
     */
    private int gradeLevel;

    /**
     * The track associated with the course (e.g., standard, honors, AP).
     */
    private String track;

    /**
     * Indicates whether the course satisfies a graduation requirement.
     */
    private String graduationRequirement;

    /**
     * The maximum number of courses a student can take per grade level.
     */
    private static final int MAX_COURSES_PER_GRADE = 8;

    /**
     * A list of courses retrieved via an API. Initially set to null.
     */
    private static ArrayList<String> apiCourses = null;

    /**
     * A flag indicating whether the API has been accessed.
     */
    private static AtomicBoolean hasAPI = new AtomicBoolean(false);

    /**
     * Constructor to initialize a course with the specified details.
     *
     * @param courseCode The code representing the course.
     * @param courseName The name of the course.
     * @param courseArea The area or category the course belongs to (e.g., Math, Science).
     * @param prerequisites A comma-separated list of prerequisite course codes.
     * @param gradeLevel The grade level required to take the course.
     * @param track The track the course belongs to (e.g., Science, Arts).
     * @param graduationRequirement The graduation requirement that this course fulfills.
     */
    public Course(String courseCode, String courseName, String courseArea, String prerequisites, int gradeLevel, String track, String graduationRequirement) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.courseArea = courseArea;
        this.prerequisites = prerequisites;
        this.gradeLevel = gradeLevel;
        this.track = track;
        this.graduationRequirement = graduationRequirement;
    }

    /**
     * Retrieves the course code of this course.
     *
     * @return The course code as a string.
     */
    public String getCourseCode() {
        return courseCode;
    }

    /**
     * Retrieves the name of this course.
     *
     * @return The course name as a string.
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * Retrieves the area or category this course belongs to.
     *
     * @return The course area as a string.
     */
    public String getCourseArea() {
        return courseArea;
    }

    /**
     * Retrieves the prerequisites required for this course.
     *
     * @return The prerequisites as a string, or an empty string if none exist.
     */
    public String getPrerequisites() {
        return prerequisites;
    }

    /**
     * Retrieves the grade level this course is designed for.
     *
     * @return The grade level as an integer.
     */
    public int getGradeLevel() {
        return gradeLevel;
    }

    /**
     * Retrieves the track of this course (e.g., University, College, Open).
     *
     * @return The course track as a string.
     */
    public String getTrack() {
        return track;
    }

    /**
     * Retrieves the graduation requirement this course fulfills, if any.
     *
     * @return The graduation requirement as a string, or an empty string if none exist.
     */
    public String getGraduationRequirement() {
        return graduationRequirement;
    }


    /**
     * This method checks if a course meets the prerequisites and grade level requirements
     * for a student, and adds the course to the student's course list if applicable. This is the main algorithm to assign courses to the user.
     *
     * @param student The student whose course eligibility is being checked.
     */
    private void engine(UserInput student) {

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

    /**
     * Adds a course to the student's list of recommended courses if the student
     * meets the grade level requirements and there is space in the grade-level list.
     *
     * @param student The student to add the course to.
     */
    private void addCourse(UserInput student) {
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


    /**
     * Fulfills graduation requirements by adding recommended courses to the student's
     * course list based on their grade and the graduation requirements.
     */
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



    /**
     * Finds the next course that has no prerequisites for a specified course area
     * and grade level, avoiding duplicates in the recommended courses and graduation
     * credits lists.
     *
     * @param courseArea The area of study (e.g., Math, Science).
     * @param courseGrade The grade level of the course.
     * @param recommendedCourses A set of already recommended courses.
     * @param recommendedGradCredits A set of graduation requirements already fulfilled.
     * @return The next available course without prerequisites.
     */
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

    /**
     * Finds the grades in which there are open spots in the recommended courses list.
     * An open spot is represented by a null value in the course array for a specific grade.
     *
     * @return A list of grades that have at least one open spot for courses.
     */
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

    /**
     * Identifies which credit areas have unfulfilled requirements.
     * A credit is considered unfulfilled if its value is greater than zero in the credits map.
     *
     * @return An array of credit names that still need to be fulfilled.
     */
    private static String[] findUnfulfilledCredits() {
        ArrayList<String> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : credits.entrySet()) {
            if (entry.getValue() > 0) {
                result.add(entry.getKey());
            }
        }
        return result.toArray(new String[0]);
    }

    /**
     * Displays a form to allow the user to fill in missing classes in their recommended course list.
     * If there are no missing courses, the method exits without action.
     *
     * @param student The student object containing user input data.
     */
    public static void addNonFilledClasses(UserInput student) {
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

    /**
     * Processes the response from the user to fill missing classes in the recommended course list.
     *
     * @param student         The student object containing user input data.
     * @param studentResponse The response provided by the student to fill the missing courses.
     */
    public static void getNonFilledClassesResponse(UserInput student, String studentResponse) {
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

    /**
     * Fills a course for a specific grade and updates the recommended courses.
     * Tries to retrieve courses from the API or selects a random class if no valid API courses are available.
     *
     * @param grade                  The grade level for the course to be filled.
     * @param recommendedCourses     A set of already recommended courses.
     * @param recommendedCourseArea  A set of course areas that have been recommended.
     * @param courses                The array of courses for the grade.
     * @param response               The user's response for filling courses.
     * @param studentInput           The student's input containing details like track.
     * @return The course code of the filled course, or "E404" if no suitable course is found.
     */
    private static String fillCourse(int grade, Set<String> recommendedCourses, Set<String> recommendedCourseArea, String[] courses, String response, UserInput studentInput) {
        if (!response.isEmpty()) {
            if (!hasAPI.get()) {
                apiCourses = APIClient.getAPIDataClasses(response);
                hasAPI.set(!apiCourses.isEmpty());
            }

            List<String> filteredApiCourses = apiCourses.stream()
                    .filter(courseCode -> {
                        Course course = getCourse(courseCode);
                        return course != null && course.getGradeLevel() == grade
                                && !Arrays.asList(courses).contains(courseCode)
                                && (course.getTrack().equalsIgnoreCase(studentInput.getTrack()) || course.getTrack().equalsIgnoreCase("Open"));
                    })
                    .toList();

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

        Random random = new Random();
        List<String> filteredKeys = courseMap.entrySet().stream()
                .filter(entry -> entry.getValue().getGradeLevel() == grade)
                .filter(entry -> (entry.getValue().getTrack().equalsIgnoreCase(studentInput.getTrack()) || entry.getValue().getTrack().equalsIgnoreCase("Open")))
                .filter(entry -> !Arrays.asList(courses).contains(entry.getValue().getCourseCode()))
                .map(Map.Entry::getKey)
                .toList();

        if (filteredKeys.isEmpty()) {
            return "E404";
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


    /**
     * Writes the recommended courses for a student to a file in JSON format.
     *
     * @param studentInput The student whose recommended courses are being written.
     */
    public static void writeRecommendedCoursesToFileCourseName(UserInput studentInput) {
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
                String coursesString = String.join(",** ", courseNames);
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

    /**
     * Sorts each String array in the recommendedCoursesByGrade map in alphabetical order using Insertion Sort.
     */
    public static void sortCoursesByGrade() {
        // Iterate through each entry in the map
        for (Map.Entry<Integer, String[]> entry : recommendedCoursesByGrade.entrySet()) {
            String[] courses = entry.getValue();
            // Sort the array using Insertion Sort
            insertionSort(courses);
        }
    }

    /**
     * Insertion Sort algorithm to sort a String array in alphabetical order.
     *
     * @param array The String array to be sorted.
     */
    public static void insertionSort(String[] array) {
        for (int i = 1; i < array.length; i++) {
            String key = array[i];
            int j = i - 1;

            // Move elements of array[0..i-1] that are greater than key, to one position ahead
            while (j >= 0 && array[j].compareTo(key) > 0) {
                array[j + 1] = array[j];
                j = j - 1;
            }

            // Place the key in the correct position
            array[j + 1] = key;
        }
    }

    /**
     * Reads the recommended courses for a student from a JSON file.
     * The file is named based on the student's username and is located in
     * the "src/main/resources/user_class_info" directory. The method populates
     * the `recommendedCoursesByGrade` map with the data from the file.
     *
     * @param username The student's username used to construct the file path.
     * @return true if the file was read successfully and contains valid data, false otherwise.
     */
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

    /**
     * Runs the engine for each course in the provided list of courses from the user interests.
     * The engine processes the student's input for each course.
     *
     * @param courses The list of course names to be processed.
     * @param student The student whose input will be used in the engine.
     */
    public static void runEngine(ArrayList<String> courses, UserInput student) {
        for (String c : courses) {
            Course course = getCourse(c);
            if (course != null) {
                course.engine(student);
            }
        }
    }

    /**
     * A helper class to represent the course data in JSON format.
     * This class is used for reading and writing course data in/from JSON files.
     */
    public static class FileCourseData {
        private String courses;
        private int grade;

        /**
         * Gets the courses for the grade.
         *
         * @return A comma-separated string of courses.
         */
        public String getCourses() {
            return courses;
        }

        /**
         * Sets the courses for the grade.
         *
         * @param courses A comma-separated string of courses.
         */
        public void setCourses(String courses) {
            this.courses = courses;
        }

        /**
         * Gets the grade associated with the courses.
         *
         * @return The grade level.
         */
        public int getGrade() {
            return grade;
        }

    }

    /**
     * Cleans a string representing previous courses by removing surrounding
     * brackets and quotes, then returns the cleaned courses as an array of strings.
     *
     * @param previousClasses A string containing previous course names, possibly surrounded by brackets and quotes.
     * @return An array of course names extracted from the input string.
     */
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