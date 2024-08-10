package org.example;



import org.example.people.StudentInput;
import org.example.utility.APIClient;
import org.example.utility.Course;
import org.example.utility.CourseAssembly;



/*
NOTE: When signing up ask them for everything in StudentInput class. During this process ask them for their interests, and run the script
NOTE: When logging in, read their file and display info

* */

public class Main {
    public static void main(String[] args) {
        APIClient.deployAPI();
//        new LoginSystem();

        // TODO : FOR FORM QUESTIONS, ASK THEM THE FOLLOWING: THEIR INTERESTS IN SCHOOL, PREVIOUS COURSES, CURRENT GRADE LEVEL, TRACK, HOW THEY LIKE SCHOOL ON A SCALE OF 1 - 10,
        StudentInput student = new StudentInput("physics, biology, enginnering, cooking", "", 9, "University", "es");


        // Checks if the user already have their courses planned out
        boolean hasRecommendations = Course.readRecommendedCoursesFromFile(student.getUsername());

        if(hasRecommendations) {
            System.out.println("Displaying recommendations...");
        } else {
            CourseAssembly.runAssessment(student);
            System.out.println("finished assessment");
        }
    }
}