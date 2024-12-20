package org.example.people;

import java.util.HashMap;
import java.util.Map;

/**
 * The abstract class `StudentCounselor` is responsible for managing the mapping of student last names
 * to their respective counselor information.
 * It provides a method to find the correct counselor based on the student's last name.
 */
public abstract class StudentCounselor {

    /**
     * Finds the appropriate counselor for a student based on their last name.
     * The counselors are mapped by ranges of last names, such that a student's last name
     * determines which counselor they are assigned to.
     *
     * @param lastName The last name of the student.
     * @return The counselor assigned to the student, or {@code null} if no counselor is found.
     */
    public static Counselor findCounselor(String lastName) {
        // Initialize the map with counselors' information
        Map<String, Counselor> counselors = new HashMap<>() {{
            /*
            Actual counselor's email addresses...
            put("A-Elgo", new Counselor("Mr. Bobby Howe", "robert.howe@ocdsb.ca"));
            put("Elha-Lin", new Counselor("Mr. Scheepers", "greg.scheepers@ocdsb.ca"));
            put("Ling-Shar", new Counselor("Ms. Walter", "michelle.walter@ocdsb.ca"));
            put("Shaw-Z", new Counselor("Ms. Lisak", "dubravka.lisak@ocdsb.ca"));
             */

            // Example counselor mapping
            put("A-Elgo", new Counselor("Mr. Bobby Howe", "fenceryounger@gmail.com"));
            put("Elha-Lin", new Counselor("Mr. Scheepers", "fenceryounger@gmail.com"));
            put("Ling-Shar", new Counselor("Ms. Walter", "fenceryounger@gmail.com"));
            put("Shaw-Z", new Counselor("Ms. Lisak", "fenceryounger@gmail.com"));
        }};

        // Determine the correct counselor based on the last name's range
        if (lastName.compareToIgnoreCase("A") >= 0 && lastName.compareToIgnoreCase("Elgo") <= 0) {
            return counselors.get("A-Elgo");
        } else if (lastName.compareToIgnoreCase("Elha") >= 0 && lastName.compareToIgnoreCase("Lin") <= 0) {
            return counselors.get("Elha-Lin");
        } else if (lastName.compareToIgnoreCase("Ling") >= 0 && lastName.compareToIgnoreCase("Shar") <= 0) {
            return counselors.get("Ling-Shar");
        } else if (lastName.compareToIgnoreCase("Shaw") >= 0 && lastName.compareToIgnoreCase("Z") <= 0) {
            return counselors.get("Shaw-Z");
        } else {
            return null;  // Return null if no counselor is found for the given last name range
        }
    }
}