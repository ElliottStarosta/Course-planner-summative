package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        StudentInput student = new StudentInput("music", "", 9, "university", false);
        ArrayList<String> courses = APIClient.getAPIData(student.getInterests());
        CourseAssembly mapper = new CourseAssembly();

        for (String c : courses) {
            CourseAssembly.Course course = mapper.getCourse(c);
            if (course != null) {
                course.engine(student);
            }
        }

        CourseAssembly.Course.writeRecommendedCoursesToFile();
    }

}
