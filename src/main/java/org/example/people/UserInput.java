package org.example.people;

/**
 * The UserInput class represents the information inputted by the user (student),
 * including their interests, previous courses, grade level, track, and username.
 */
public class UserInput {
    private String interests;
    private String previousCourses;
    private int grade;
    private String track;
    private String username;

    /**
     * Constructs a new UserInput object with a given username.
     * The interests, previous courses, track, and grade are set to default values.
     *
     * @param username The username of the student.
     */
    public UserInput(String username) {
        this.interests = "";
        this.previousCourses = "";
        this.grade = 9;
        this.track = "";
        this.username = username;
    }

    /**
     * Constructs a new UserInput object with all the provided details.
     *
     * @param interests       The student's interests.
     * @param previousCourses The student's previous courses.
     * @param grade           The grade level of the student.
     * @param track           The student's track (e.g., science, arts, etc.).
     * @param username        The student's username.
     */
    public UserInput(String interests, String previousCourses, int grade, String track, String username) {
        this.interests = interests;
        this.previousCourses = previousCourses;
        this.grade = grade;
        this.track = track;
        this.username = username;
    }

    /**
     * Returns the interests of the student.
     *
     * @return The student's interests.
     */
    public String getInterests() {
        return interests;
    }

    /**
     * Sets the interests of the student.
     *
     * @param interests The student's interests to set.
     */
    public void setInterests(String interests) {
        this.interests = interests;
    }

    /**
     * Returns the grade level of the student.
     *
     * @return The student's grade.
     */
    public int getGrade() {
        return grade;
    }

    /**
     * Sets the grade level of the student.
     *
     * @param grade The grade level to set.
     */
    public void setGrade(int grade) {
        this.grade = grade;
    }

    /**
     * Returns the track of the student.
     *
     * @return The student's track.
     */
    public String getTrack() {
        return track;
    }

    /**
     * Sets the track of the student.
     *
     * @param track The track to set.
     */
    public void setTrack(String track) {
        this.track = track;
    }

    /**
     * Returns the previous courses taken by the student.
     *
     * @return The student's previous courses.
     */
    public String getPreviousCourses() {
        return previousCourses;
    }

    /**
     * Sets the previous courses taken by the student.
     *
     * @param previousCourses The previous courses to set.
     */
    public void setPreviousCourses(String previousCourses) {
        this.previousCourses = previousCourses;
    }

    /**
     * Returns the username of the student.
     *
     * @return The student's username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the student.
     *
     * @param username The username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }
}