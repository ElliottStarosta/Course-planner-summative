package org.example.utility;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtility {
    protected static final String FILE_NAME = "src/main/resources/model/CoursesFinal.xlsx";

    public static void loadCourseData() {
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
                    String graduationRequirement = getStringValue(row.getCell(7));

                    Course course = new Course(courseCode, courseName, courseArea, prerequisites, gradeLevel, track, graduationRequirement);
                    CourseAssembly.courseMap.put(courseCode, course);
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

    public static String[] getAllCourseNames() {
        List<String> courseNames = new ArrayList<>();
        try (InputStream inp = new FileInputStream(FILE_NAME)) {
            Workbook workbook = new XSSFWorkbook(inp);
            Sheet sheet = workbook.getSheetAt(0);

            // Iterate through rows skipping header row (if exists)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    String courseCode = getStringValue(row.getCell(0));
                    String courseName = getStringValue(row.getCell(1));
                    if (!courseName.isEmpty() && !courseCode.isEmpty()) {
                        String formattedCourse = courseCode + " - " + courseName;
                        courseNames.add(formattedCourse);
                    }
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
        return courseNames.toArray(new String[0]);
    }

    private static String getStringValue(Cell cell) {
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

    private static int getIntValue(Cell cell) {
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
}
