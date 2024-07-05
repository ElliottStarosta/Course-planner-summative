package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

public class CourseAssembly {
    private static final String FILE_NAME = "src/main/model/CoursesFinal.xlsx";
    private Map<String, Course> courseMap = new HashMap<>();
    private static Map<Integer, List<String>> recommendedCoursesByGrade = new HashMap<>();

    public CourseAssembly() {
        loadCourseData();
    }

    private void loadCourseData() {
        try (InputStream inp = new FileInputStream(FILE_NAME)) {
            Workbook workbook = new XSSFWorkbook(inp); // Use XSSFWorkbook for .xlsx files

            Sheet sheet = workbook.getSheetAt(0); // Assuming data is on the first sheet

            // Iterate through rows skipping header row (if exists)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    String courseCode = getStringValue(row.getCell(0));
                    String courseName = getStringValue(row.getCell(1));
                    String courseArea = getStringValue(row.getCell(3));
                    String prerequisites = getStringValue(row.getCell(4));

                    int gradeLevel = getIntValue(row.getCell(5));
                    String track = getStringValue(row.getCell(6));
                    int graduationRequirement = getIntValue(row.getCell(7));

                    Course course = new Course(courseCode, courseName, courseArea, prerequisites, gradeLevel, track, graduationRequirement);
                    courseMap.put(courseCode, course);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + FILE_NAME);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error reading file: " + FILE_NAME);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error:");
            e.printStackTrace();
        }
    }

    public Course getCourse(String courseCode) {
        return courseMap.get(courseCode);
    }

    private String getStringValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue()).trim();
            default:
                return "";
        }
    }

    private int getIntValue(Cell cell) {
        if (cell == null) {
            return 0;
        }
        switch (cell.getCellType()) {
            case NUMERIC:
                return (int) cell.getNumericCellValue();
            default:
                return 0;
        }
    }

    class Course {
        private String courseCode;
        private String courseName;
        private String courseArea;
        private String prerequisites;
        private int gradeLevel;
        private String track;
        private int graduationRequirement;

        public Course(String courseCode, String courseName, String courseArea, String prerequisites, int gradeLevel, String track, int graduationRequirement) {
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

        public int getGraduationRequirement() {
            return graduationRequirement;
        }

        public void engine(StudentInput student) {
            if (student.getGrade() > gradeLevel && !student.getTrack().equals(track.toLowerCase())) {
                return;
            }


            String[] previousCoursesArray = student.getPreviousCourses().split("\\s*,\\s*");
            if (Arrays.asList(previousCoursesArray).contains(courseCode)) {
                return; // course already taken, skip
            }

            addCourse(courseCode, student);
            if (!"none".equals(prerequisites)) {
                Course prerequisiteCourse = getCourse(prerequisites);
                prerequisiteCourse.engine(student);
            }
        }

        private void addCourse(String courseCode, StudentInput student) {
            int studentGrade = student.getGrade();
            int courseGrade = getGradeLevel();

            if (courseGrade >= studentGrade) {
                List<String> coursesForGrade = recommendedCoursesByGrade.computeIfAbsent(courseGrade, k -> new ArrayList<>());
                if (!coursesForGrade.contains(courseCode)) {
                    coursesForGrade.add(courseCode);
//                    coursesForGrade.add(this.getCourseName());
                }
            }
        }

        // Writes the recommended courses by grade to a file
        public static void writeRecommendedCoursesToFile() {
            try (FileWriter writer = new FileWriter("src/main/model/recommendedcourses.txt")) {
                for (Map.Entry<Integer, List<String>> entry : recommendedCoursesByGrade.entrySet()) {
                    writer.write(entry.getKey() + ": " + entry.getValue() + "\n");
                }
                System.out.println("Recommended courses written to recommendedcourses.txt");
            } catch (IOException e) {
                System.err.println("Error writing recommended courses to file: " + e.getMessage());
            }
        }
    }
}
