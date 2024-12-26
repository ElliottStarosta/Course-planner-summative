package org.example.gui.manager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * The {@code DynamicFormLoader} class is responsible for dynamically loading
 * and instantiating form classes based on the provided question number and
 * user responses. The form class is expected to be named in the format
 * "Form{question}" and should have a constructor that accepts a
 * {@link HashMap} of user responses and an integer representing the question.
 */
public class DynamicFormLoader {

    /**
     * Loads and instantiates a form based on the provided question number
     * and user responses.
     *
     * <p>The form class is expected to be in the package
     * {@code org.example.gui.pages.quiz} and should follow the naming
     * convention "Form{question}" where {question} is the integer representing
     * the question number. The form class must have a constructor that accepts
     * a {@link HashMap} of user responses and an integer question number.
     *
     * @param question      The question number, which is used to determine
     *                      the form class to load.
     * @param userResponses A {@link HashMap} containing the user responses
     *                      to be passed to the form constructor.
     * @return An instance of the dynamically loaded form, or {@code null} if
     *         the form could not be loaded due to an error.
     */
    public static Object loadForm(int question, HashMap<String, String> userResponses) {
        String formClassName = "org.example.gui.pages.quiz.Form" + question;
        try {
            // Load the class dynamically
            Class<?> formClass = Class.forName(formClassName);

            // Get the constructor with parameters (List<String>, String)
            Constructor<?> constructor = formClass.getConstructor(HashMap.class, int.class);

            // Create an instance of the form
            return constructor.newInstance(userResponses, question);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace(); // Handle exceptions appropriately
            return null; // Or handle error appropriately
        }
    }
}