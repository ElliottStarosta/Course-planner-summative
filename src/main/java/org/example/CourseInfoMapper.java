package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

public class CourseInfoMapper {
    private static final String FILE_NAME = "src/main/model/CoursesFinal.xlsx";
    private Map<String, Course> courseMap = new HashMap<>();

    public CourseInfoMapper() {
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
                    String courseArea = getStringValue(row.getCell(3));
                    String prerequisites = getStringValue(row.getCell(4));

                    String[] prerequisitesArray = prerequisites.split("\\s*,\\s*");

                    int gradeLevel = getIntValue(row.getCell(5));
                    String track = getStringValue(row.getCell(6));
                    int graduationRequirement = getIntValue(row.getCell(7));

                    Course course = new Course(courseCode, courseArea, prerequisitesArray, gradeLevel, track, graduationRequirement);
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

    public static void main(String[] args) {
        CourseInfoMapper mapper = new CourseInfoMapper();
        String courseCode = "PPL4O";

        Course course = mapper.getCourse(courseCode);
        String[] prerequisites = course.getPrerequisites(); // Get the prerequisites array
        System.out.println(Arrays.toString(prerequisites)); // Print the array

    }

    // Define Course class encapsulating all attributes
    static class Course {
        private String courseCode;
        private String courseArea;
        private String[] prerequisites;
        private int gradeLevel;
        private String track;
        private int graduationRequirement;

        public Course(String courseCode, String courseArea, String[] prerequisites, int gradeLevel, String track, int graduationRequirement) {
            this.courseCode = courseCode;
            this.courseArea = courseArea;
            this.prerequisites = prerequisites;
            this.gradeLevel = gradeLevel;
            this.track = track;
            this.graduationRequirement = graduationRequirement;
        }

        public String getCourseCode() {
            return courseCode;
        }

        public String getCourseArea() {
            return courseArea;
        }

        public String[] getPrerequisites() {
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
        // Writes classes to file, maybe return arraylist, i dont know
        public void engine(String[] prerequisites, int level, String track, int gradRequirement, String courseCode, StudentInput student) {
            String interests = student.getInterests();
//            System.out.println(interests);
        }
    }
}
