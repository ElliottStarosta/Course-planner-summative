package org.example.people;

public class StudentInput {
        private String interests;
        private String previousCourses;
        private int grade;
        private String track;
        private String username;

        public StudentInput(String username) {
            this.interests = "";
            this.previousCourses = "";
            this.grade = 9;
            this.track = "";
            this.username = username;
        }

        public StudentInput(String interests, String previousCourses, int grade, String track, String username) {
            this.interests = interests;
            this.previousCourses = previousCourses;
            this.grade = grade;
            this.track = track;
            this.username = username;
        }
        public String getInterests() {

            return interests;
        }

        public void setInterests(String interests) {

            this.interests = interests;
        }

        public int getGrade() {

            return grade;
        }

        public void setGrade(int grade) {

            this.grade = grade;
        }

        public String getTrack() {

            return track;
        }

        public void setTrack(String track) {

            this.track = track;
        }
        public String getPreviousCourses() {

            return previousCourses;
        }

        public void setPreviousCourses(String previousCourses) {

            this.previousCourses = previousCourses;
        }

    public String getUsername() {

        return username;
    }

    public void setUsername(String username) {

        this.username = username;
    }
}
