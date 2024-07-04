package org.example;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        // Program startup
//        Initializer.startUp();

        StudentInput student = new StudentInput("coding, history, math, physics, biology, chemistry", "", 9, "", false);
        ArrayList<String> courses = APIClient.getAPIData(student.getInterests());
        CourseInfoMapper mapper = new CourseInfoMapper();

        for(String i : courses) {

            CourseInfoMapper.Course course = mapper.getCourse(i);
            String[] prerequisites = course.getPrerequisites();
            int level = course.getGradeLevel();
            String track = course.getTrack();
            int gradRequirement = course.getGraduationRequirement();
            String courseCode = course.getCourseCode();
            course.engine(prerequisites, level, track, gradRequirement, courseCode, student);
        }


        if (courses.isEmpty()) {
            System.out.print("No courses found, enter more interests or do you want a random generated class schedule?");
        } else {
            System.out.println("[" + String.join(", ", courses) + "]");
        }
    }
}
