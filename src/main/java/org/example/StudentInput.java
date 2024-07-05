package org.example;

import java.util.List;

public class StudentInput {
        private String interests;
        private String previousCourses;
        private int grade;
        private String track;
        private boolean isFrench;

        public StudentInput() {
            this.interests = "";
            this.previousCourses = "";
            this.grade = 9;
            this.track = "";
            this.isFrench = false;
        }

        public StudentInput(String interests, String previousCourses, int grade, String track, boolean isFrench) {
            this.interests = interests;
            this.previousCourses = previousCourses;
            this.grade = grade;
            this.track = track;
            this.isFrench = isFrench;
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
        public boolean getIsFrench() {

            return isFrench;
        }

        public void setIsFrench(boolean isFrench) {

            this.isFrench = isFrench;
        }
    }
